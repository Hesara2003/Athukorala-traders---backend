package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.InventoryReservation;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.repository.InventoryReservationRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryReservationRepository reservationRepository;

    public InventoryService(ProductRepository productRepository, 
                           InventoryReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Reserve stock for an order (during checkout)
     * @param orderId Order ID
     * @param productId Product ID
     * @param quantity Quantity to reserve
     * @param customerId Customer ID
     * @param expiryMinutes Minutes until reservation expires
     * @return Reservation object
     */
    @Transactional
    public InventoryReservation reserveStock(String orderId, String productId, int quantity, 
                                             String customerId, int expiryMinutes) {
        // Verify product exists
        productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Calculate available stock (physical stock - reserved stock)
        int availableStock = getAvailableStock(productId);

        if (availableStock < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + availableStock + ", Requested: " + quantity);
        }

        InventoryReservation reservation = new InventoryReservation();
        reservation.setProductId(productId);
        reservation.setOrderId(orderId);
        reservation.setCustomerId(customerId);
        reservation.setQuantity(quantity);
        reservation.setStatus("RESERVED");
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));

        return reservationRepository.save(reservation);
    }

    /**
     * Confirm reservation (payment successful)
     */
    @Transactional
    public void confirmReservation(String orderId) {
        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        
        for (InventoryReservation reservation : reservations) {
            if ("RESERVED".equals(reservation.getStatus())) {
                reservation.setStatus("CONFIRMED");
                reservation.setReason("PAYMENT_CONFIRMED");
                reservationRepository.save(reservation);

                // Deduct actual stock
                Product product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
                product.setStock(product.getStock() - reservation.getQuantity());
                productRepository.save(product);
            }
        }
    }

    /**
     * Release reservation (payment failed or cancelled)
     */
    @Transactional
    public void releaseReservation(String orderId, String reason) {
        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        
        for (InventoryReservation reservation : reservations) {
            if ("RESERVED".equals(reservation.getStatus())) {
                reservation.setStatus("RELEASED");
                reservation.setReleasedAt(LocalDateTime.now());
                reservation.setReason(reason);
                reservationRepository.save(reservation);
            }
        }
    }

    /**
     * Get available stock (physical stock - reserved stock)
     */
    public int getAvailableStock(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Get all active reservations
        List<InventoryReservation> reservations = 
            reservationRepository.findByProductIdAndStatus(productId, "RESERVED");

        int reservedQuantity = reservations.stream()
            .mapToInt(InventoryReservation::getQuantity)
            .sum();

        return product.getStock() - reservedQuantity;
    }

    /**
     * Get stock status for a product
     */
    public Map<String, Object> getStockStatus(String productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        int available = getAvailableStock(productId);
        List<InventoryReservation> reservations = 
            reservationRepository.findByProductIdAndStatus(productId, "RESERVED");
        
        int reserved = reservations.stream()
            .mapToInt(InventoryReservation::getQuantity)
            .sum();

        Map<String, Object> status = new HashMap<>();
        status.put("productId", productId);
        status.put("productName", product.getName());
        status.put("physicalStock", product.getStock());
        status.put("reservedStock", reserved);
        status.put("availableStock", available);
        status.put("isAvailable", available > 0);
        status.put("activeReservations", reservations.size());

        return status;
    }

    /**
     * Auto-release expired reservations (runs every minute)
     */
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void releaseExpiredReservations() {
        List<InventoryReservation> expired = 
            reservationRepository.findByStatusAndExpiresAtBefore("RESERVED", LocalDateTime.now());

        for (InventoryReservation reservation : expired) {
            reservation.setStatus("EXPIRED");
            reservation.setReleasedAt(LocalDateTime.now());
            reservation.setReason("TIMEOUT");
            reservationRepository.save(reservation);
            
            System.out.println("Released expired reservation: " + reservation.getId() + 
                             " for order: " + reservation.getOrderId());
        }
    }

    /**
     * Check if product has sufficient stock
     */
    public boolean checkStock(String productId, int quantity) {
        return getAvailableStock(productId) >= quantity;
    }

    /**
     * Bulk stock check for multiple products
     */
    public Map<String, Boolean> checkStockBulk(Map<String, Integer> items) {
        Map<String, Boolean> results = new HashMap<>();
        
        items.forEach((productId, quantity) -> {
            results.put(productId, checkStock(productId, quantity));
        });
        
        return results;
    }

    /**
     * Update stock alert thresholds
     */
    public void checkLowStockAlerts() {
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            int available = getAvailableStock(product.getId());
            
            if (available <= 10 && available > 0) {
                System.out.println("LOW STOCK ALERT: " + product.getName() + 
                                 " - Available: " + available);
            } else if (available <= 0) {
                System.out.println("OUT OF STOCK: " + product.getName());
            }
        }
    }
}

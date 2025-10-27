package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.EligibleOrderDTO;
import com.example.hardwaremanagement.model.*;
import com.example.hardwaremanagement.repository.OrderRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import com.example.hardwaremanagement.repository.ReturnExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReturnExchangeService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReturnExchangeRepository returnExchangeRepository;

    @Autowired
    private ProductRepository productRepository;

    // Return policy: 30 days from delivery
    private static final int RETURN_WINDOW_DAYS = 30;

    /**
     * Fetch all orders eligible for return/exchange
     * Orders are eligible if:
     * 1. Status is DELIVERED
     * 2. Within 30 days of delivery
     * 3. No active return/exchange already exists
     */
    public List<EligibleOrderDTO> getEligibleOrdersForReturn() {
        // Get all delivered orders
        List<Order> allOrders = orderRepository.findAll();
        
        List<EligibleOrderDTO> eligibleOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            EligibleOrderDTO dto = evaluateOrderEligibility(order);
            eligibleOrders.add(dto);
        }
        
        // Sort by order date (most recent first)
        eligibleOrders.sort((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()));
        
        return eligibleOrders;
    }

    /**
     * Fetch eligible orders for a specific customer
     */
    public List<EligibleOrderDTO> getEligibleOrdersForCustomer(String customerId) {
        List<Order> customerOrders = orderRepository.findByCustomerIdOrderByPlacedAtDesc(customerId);
        
        return customerOrders.stream()
                .map(this::evaluateOrderEligibility)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order's eligibility details
     */
    public EligibleOrderDTO getOrderEligibility(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        return evaluateOrderEligibility(order);
    }

    /**
     * Evaluate if an order is eligible for return/exchange
     */
    private EligibleOrderDTO evaluateOrderEligibility(Order order) {
        EligibleOrderDTO dto = new EligibleOrderDTO();
        dto.setOrderId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setOrderDate(order.getPlacedAt());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        // Check if order is delivered
        boolean isDelivered = order.getStatus() == OrderStatus.DELIVERED;
        
        // Calculate days since delivery
        long daysSinceOrder = ChronoUnit.DAYS.between(order.getPlacedAt(), LocalDateTime.now());
        int daysRemaining = RETURN_WINDOW_DAYS - (int) daysSinceOrder;
        
        // Check if within return window
        boolean withinReturnWindow = daysSinceOrder <= RETURN_WINDOW_DAYS;
        
        // Check if there's already an active return/exchange
        List<ReturnExchange> existingReturns = returnExchangeRepository.findByOrderId(order.getId());
        boolean hasActiveReturn = existingReturns.stream()
                .anyMatch(r -> r.getStatus() != ReturnExchangeStatus.COMPLETED 
                            && r.getStatus() != ReturnExchangeStatus.REJECTED
                            && r.getStatus() != ReturnExchangeStatus.CANCELLED);

        // Determine eligibility
        boolean eligible = isDelivered && withinReturnWindow && !hasActiveReturn;
        
        dto.setEligibleForReturn(eligible);
        dto.setDaysUntilReturnExpires(Math.max(0, daysRemaining));
        
        // Set eligibility reason
        if (!isDelivered) {
            dto.setEligibilityReason("Order must be delivered before return/exchange");
        } else if (!withinReturnWindow) {
            dto.setEligibilityReason("Return window expired (" + RETURN_WINDOW_DAYS + " days limit)");
        } else if (hasActiveReturn) {
            dto.setEligibilityReason("Active return/exchange already exists for this order");
        } else {
            dto.setEligibilityReason("Eligible for return/exchange");
        }

        // Map order items
        if (order.getItems() != null) {
            List<EligibleOrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream()
                    .map(item -> {
                        EligibleOrderDTO.OrderItemDTO itemDTO = new EligibleOrderDTO.OrderItemDTO();
                        itemDTO.setProductId(item.getProductId());
                        itemDTO.setProductName(item.getName());
                        itemDTO.setQuantity(item.getQuantity());
                        itemDTO.setPrice(item.getUnitPrice());
                        itemDTO.setTotal(item.getUnitPrice() * item.getQuantity());
                        itemDTO.setReturnable(eligible);
                        itemDTO.setReturnReason(eligible ? "Available for return" : dto.getEligibilityReason());
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
            
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    /**
     * Get all return/exchange requests
     */
    public List<ReturnExchange> getAllReturnExchanges() {
        return returnExchangeRepository.findAll();
    }

    /**
     * Get return/exchange requests by status
     */
    public List<ReturnExchange> getReturnExchangesByStatus(ReturnExchangeStatus status) {
        return returnExchangeRepository.findByStatus(status);
    }

    /**
     * Get pending return/exchange requests for staff review
     */
    public List<ReturnExchange> getPendingReturnExchanges() {
        List<ReturnExchangeStatus> pendingStatuses = Arrays.asList(
            ReturnExchangeStatus.PENDING,
            ReturnExchangeStatus.APPROVED,
            ReturnExchangeStatus.IN_TRANSIT,
            ReturnExchangeStatus.RECEIVED,
            ReturnExchangeStatus.INSPECTING
        );
        return returnExchangeRepository.findByStatusInOrderByRequestedAtDesc(pendingStatuses);
    }

    /**
     * Get return/exchange by ID
     */
    public ReturnExchange getReturnExchangeById(String id) {
        return returnExchangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return/Exchange not found: " + id));
    }

    /**
     * Create a new return/exchange request
     */
    public ReturnExchange createReturnExchange(ReturnExchange returnExchange) {
        // Validate order exists and is eligible
        String orderId = returnExchange.getOrderId();
        EligibleOrderDTO eligibility = getOrderEligibility(orderId);
        
        if (!eligibility.isEligibleForReturn()) {
            throw new RuntimeException("Order is not eligible for return/exchange: " + eligibility.getEligibilityReason());
        }

        // Set initial status and timestamp
        returnExchange.setStatus(ReturnExchangeStatus.PENDING);
        returnExchange.setRequestedAt(LocalDateTime.now());

        // Calculate refund amount
        double refundAmount = returnExchange.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        returnExchange.setRefundAmount(refundAmount);

        return returnExchangeRepository.save(returnExchange);
    }

    /**
     * Process a return/exchange (for staff)
     * Updates status and adds items back to inventory
     */
    public ReturnExchange processReturnExchange(String id, ReturnExchangeStatus newStatus, String processedBy) {
        ReturnExchange returnExchange = getReturnExchangeById(id);
        
        ReturnExchangeStatus oldStatus = returnExchange.getStatus();
        returnExchange.setStatus(newStatus);
        returnExchange.setProcessedBy(processedBy);
        returnExchange.setProcessedAt(LocalDateTime.now());

        // If completed, add items back to inventory
        if (newStatus == ReturnExchangeStatus.COMPLETED) {
            returnExchange.setCompletedAt(LocalDateTime.now());
            addItemsBackToInventory(returnExchange.getItems());
        }

        return returnExchangeRepository.save(returnExchange);
    }

    /**
     * Add returned items back to inventory
     */
    private void addItemsBackToInventory(List<ReturnExchange.ReturnItem> items) {
        for (ReturnExchange.ReturnItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElse(null);
            
            if (product != null) {
                // Add quantity back to stock
                int currentStock = product.getStock();
                product.setStock(currentStock + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    /**
     * Cancel a return/exchange request
     */
    public ReturnExchange cancelReturnExchange(String id) {
        ReturnExchange returnExchange = getReturnExchangeById(id);
        returnExchange.setStatus(ReturnExchangeStatus.CANCELLED);
        return returnExchangeRepository.save(returnExchange);
    }
}

package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.CreateOrderRequest;
import com.example.hardwaremanagement.dto.OrderDetailDTO;
import com.example.hardwaremanagement.dto.OrderItemDTO;
import com.example.hardwaremanagement.model.OrderStatus;
import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.repository.OrderRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import com.example.hardwaremanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderService {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    public OrderDetailDTO getById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return toDetail(order);
    }


    public List<OrderDetailDTO> listByCustomer(String customerId) {
        return orderRepository.findByCustomerIdOrderByPlacedAtDesc(customerId)
                .stream().map(this::toDetail).collect(Collectors.toList());
    }

    public List<OrderDetailDTO> listPendingOrders() {
        // Pending orders are those that are PLACED or PROCESSING
        List<OrderStatus> pendingStatuses = List.of(OrderStatus.PLACED, OrderStatus.PROCESSING);
        return orderRepository.findByStatusInOrderByPlacedAtDesc(pendingStatuses)
                .stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
    }

    public List<OrderDetailDTO> listByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByPlacedAtDesc(status)
                .stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
    }


    public OrderDetailDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(resolveCustomerId(request));
        order.setCustomerEmail(emptyToNull(request.getCustomerEmail()));
        order.setCustomerReference(emptyToNull(request.getCustomerReference()));
        order.setItems(request.getItems());
        order.setTotals(mapTotals(request.getTotals(), request.getTotalAmount()));
        order.setTotalAmount(resolveTotalAmount(request, order.getTotals()));
        order.setStatus(resolveStatus(request));
        order.setPlacedAt(resolvePlacedAt(request.getPlacedAt()));
        order.setBilling(mapBilling(request.getBilling()));
        order.setShipping(mapShipping(request.getShipping()));
        order.setShippingSameAsBilling(Boolean.TRUE.equals(request.getShippingSameAsBilling()));
        order.setPayment(mapPayment(request.getPayment(), order.getTotalAmount(), order.getTotals()));
        return persist(order);
    }

    private OrderDetailDTO persist(Order order) {
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PROCESSING);
        }
        if (order.getPlacedAt() == null) {
            order.setPlacedAt(LocalDateTime.now());
        }
        Order saved = orderRepository.save(order);
        return toDetail(saved);
    }


    public OrderDetailDTO updateStatus(String id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);
        
        // Deduct inventory when order is marked as PICKED
        if (status == OrderStatus.PICKED && previousStatus != OrderStatus.PICKED) {
            deductInventoryForOrder(order);
        }
        
        // Automatically update to READY_TO_DISPATCH when order is PACKED
        if (status == OrderStatus.PACKED) {
            order.setStatus(OrderStatus.READY_TO_DISPATCH);
        }
        
        Order saved = orderRepository.save(order);
        return toDetail(saved);
    }

    /**
     * Deduct picked quantities from available stock
     * Called when an order is marked as "PICKED"
     */
    private void deductInventoryForOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            String productId = item.getProductId();
            int quantityToDeduct = item.getQuantity();

            // Find the product
            Product product = productRepository.findById(productId).orElse(null);
            
            if (product == null) {
                throw new RuntimeException("Product not found: " + productId + " (Order: " + order.getId() + ")");
            }

            // Check if sufficient stock is available
            int currentStock = product.getStock();
            if (currentStock < quantityToDeduct) {
                throw new RuntimeException(
                    String.format("Insufficient stock for product '%s'. Available: %d, Required: %d", 
                        product.getName(), currentStock, quantityToDeduct)
                );
            }

            // Deduct stock
            product.setStock(currentStock - quantityToDeduct);
            productRepository.save(product);
        }
    }


    private OrderDetailDTO toDetail(Order order) {
        List<OrderItemDTO> items = order.getItems() == null ? List.of() : order.getItems().stream()
                .map(this::toItem)
                .collect(Collectors.toList());
        String fallbackEmail = order.getCustomerEmail();
        if (fallbackEmail == null && order.getCustomerId() != null) {
            fallbackEmail = userRepository.findById(order.getCustomerId())
                    .map(u -> u.getEmail())
                    .orElse(null);
        }

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setCustomerReference(order.getCustomerReference());
        dto.setPlacedAt(order.getPlacedAt());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setItems(items);
        dto.setCustomerEmail(fallbackEmail);
        dto.setTotals(order.getTotals());
        dto.setBilling(order.getBilling());
        dto.setShipping(order.getShipping());
        dto.setShippingSameAsBilling(order.isShippingSameAsBilling());
        dto.setPayment(order.getPayment());
        return dto;
    }


    private OrderItemDTO toItem(OrderItem i) {
        return new OrderItemDTO(i.getProductId(), i.getName(), i.getQuantity(), i.getUnitPrice());
    }

    private String resolveCustomerId(CreateOrderRequest request) {
        if (request == null) {
            return null;
        }
        String customerId = emptyToNull(request.getCustomerId());
        if (customerId != null) {
            return customerId;
        }
        String email = emptyToNull(request.getCustomerEmail());
        if (email != null) {
            return userRepository.findByEmail(email)
                    .map(u -> u.getId())
                    .orElse(null);
        }
        return null;
    }

    private OrderStatus resolveStatus(CreateOrderRequest request) {
        if (request == null) {
            return OrderStatus.PROCESSING;
        }

        if (StringUtils.hasText(request.getPaymentStatus())) {
            try {
                return OrderStatus.valueOf(request.getPaymentStatus().trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fall through and try to infer from payment details
            }
        }

        CreateOrderRequest.PaymentInfo payment = request.getPayment();
        if (payment != null && StringUtils.hasText(payment.getStatus())) {
            String normalized = payment.getStatus().trim().toUpperCase();
            switch (normalized) {
                case "PAID":
                case "AUTHORIZED":
                case "CAPTURED":
                case "COMPLETED":
                case "SUCCESS":
                    return OrderStatus.PLACED;
                case "FAILED":
                case "DECLINED":
                case "CANCELLED":
                    return OrderStatus.CANCELLED;
                default:
                    break;
            }
        }

        return OrderStatus.PROCESSING;
    }

    private LocalDateTime resolvePlacedAt(String placedAtIso) {
        if (!StringUtils.hasText(placedAtIso)) {
            return LocalDateTime.now();
        }
        try {
            Instant instant = Instant.parse(placedAtIso);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDateTime.parse(placedAtIso);
            } catch (DateTimeParseException ignored) {
                return LocalDateTime.now();
            }
        }
    }

    private Order.Totals mapTotals(CreateOrderRequest.Totals source, double fallbackTotal) {
        if (source == null && fallbackTotal <= 0) {
            return null;
        }
        Order.Totals totals = new Order.Totals();
        if (source != null) {
            totals.setSubtotal(source.getSubtotal());
            totals.setTax(source.getTax());
            totals.setShipping(source.getShipping());
            totals.setGrandTotal(source.getGrandTotal() != null ? source.getGrandTotal() : fallbackTotal);
            totals.setCurrency(emptyToNull(source.getCurrency()));
        } else {
            totals.setGrandTotal(fallbackTotal);
        }
        return totals;
    }

    private double resolveTotalAmount(CreateOrderRequest request, Order.Totals totals) {
        if (request != null && request.getTotalAmount() > 0) {
            return request.getTotalAmount();
        }
        if (totals != null && totals.getGrandTotal() != null) {
            return totals.getGrandTotal();
        }
        return 0.0;
    }

    private Order.BillingInfo mapBilling(CreateOrderRequest.BillingInfo source) {
        if (source == null) {
            return null;
        }
        Order.BillingInfo billing = new Order.BillingInfo();
        billing.setFirstName(emptyToNull(source.getFirstName()));
        billing.setLastName(emptyToNull(source.getLastName()));
        billing.setCompany(emptyToNull(source.getCompany()));
        billing.setEmail(emptyToNull(source.getEmail()));
        billing.setPhone(emptyToNull(source.getPhone()));
        billing.setAddress(emptyToNull(source.getAddress()));
        billing.setCity(emptyToNull(source.getCity()));
        billing.setPostal(emptyToNull(source.getPostal()));
        billing.setCountry(emptyToNull(source.getCountry()));
        return billing;
    }

    private Order.ShippingInfo mapShipping(CreateOrderRequest.ShippingInfo source) {
        if (source == null) {
            return null;
        }
        Order.ShippingInfo shipping = new Order.ShippingInfo();
        shipping.setContact(emptyToNull(source.getContact()));
        shipping.setPhone(emptyToNull(source.getPhone()));
        shipping.setAddress(emptyToNull(source.getAddress()));
        shipping.setCity(emptyToNull(source.getCity()));
        shipping.setPostal(emptyToNull(source.getPostal()));
        shipping.setCountry(emptyToNull(source.getCountry()));
        shipping.setInstructions(emptyToNull(source.getInstructions()));
        return shipping;
    }

    private Order.PaymentSummary mapPayment(CreateOrderRequest.PaymentInfo source, double totalAmount, Order.Totals totals) {
        if (source == null) {
            return null;
        }
        Order.PaymentSummary payment = new Order.PaymentSummary();
        payment.setMethod(emptyToNull(source.getMethod()));
        payment.setStatus(emptyToNull(source.getStatus()));
        payment.setTransactionId(emptyToNull(source.getTransactionId()));
        payment.setAmount(source.getAmount() != null ? source.getAmount() : (totals != null ? totals.getGrandTotal() : totalAmount));
        payment.setCurrency(emptyToNull(source.getCurrency()));
        payment.setMessage(emptyToNull(source.getMessage()));
        payment.setFailureReason(emptyToNull(source.getFailureReason()));
        payment.setProcessedAt(emptyToNull(source.getProcessedAt()));
        payment.setMetadata(source.getMetadata());
        return payment;
    }

    private String emptyToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}



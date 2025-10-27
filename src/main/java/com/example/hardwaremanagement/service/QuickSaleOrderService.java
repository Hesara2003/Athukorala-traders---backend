package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.QuickSaleOrder;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.dto.QuickSaleRequestDTO;
import com.example.hardwaremanagement.dto.QuickSaleItemDTO;
import com.example.hardwaremanagement.repository.QuickSaleOrderRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuickSaleOrderService {

    @Autowired
    private QuickSaleOrderRepository quickSaleOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public QuickSaleOrder createOrder(QuickSaleRequestDTO request) {
        QuickSaleOrder order = new QuickSaleOrder();
        order.setStaffId(request.getStaffId());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("COMPLETED"); // Quick sales are immediately completed

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (var itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            // Check stock availability and deduct stock
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new IllegalStateException(
                    "Insufficient stock for product " + product.getName() + 
                    ". Required: " + itemDTO.getQuantity() + ", Available: " + product.getStock());
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setName(product.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());

            orderItems.add(item);
            totalAmount += item.getUnitPrice() * item.getQuantity();

            // Deduct stock after validation
            productService.deductStock(product.getId(), itemDTO.getQuantity());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return quickSaleOrderRepository.save(order);
    }

    public List<QuickSaleOrder> getAllOrders() {
        return quickSaleOrderRepository.findAll();
    }

    public QuickSaleOrder getOrder(String id) {
        return quickSaleOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quick sale order not found: " + id));
    }
}
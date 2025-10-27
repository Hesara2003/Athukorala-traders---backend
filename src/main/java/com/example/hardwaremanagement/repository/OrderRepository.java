package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerIdOrderByPlacedAtDesc(String customerId);
    List<Order> findByStatusOrderByPlacedAtDesc(OrderStatus status);
    List<Order> findByStatusInOrderByPlacedAtDesc(List<OrderStatus> statuses);
    List<Order> findByDeliveryStaffIdAndStatus(String deliveryStaffId, OrderStatus status);
    List<Order> findByDeliveryStaffIdAndStatusOrderByPlacedAtDesc(String deliveryStaffId, OrderStatus status);
}
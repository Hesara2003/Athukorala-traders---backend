package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.*;
import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.model.OrderStatus;
import com.example.hardwaremanagement.repository.OrderRepository;
import com.example.hardwaremanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public SalesReportDTO generateSalesReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get all orders within the date range
        List<Order> allOrders = orderRepository.findAll();
        List<Order> ordersInRange = allOrders.stream()
                .filter(order -> order.getPlacedAt() != null && 
                        order.getPlacedAt().isAfter(startDateTime) && 
                        order.getPlacedAt().isBefore(endDateTime) &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        SalesReportDTO report = new SalesReportDTO();
        report.setReportDate(LocalDateTime.now());

        // Calculate totals
        double totalRevenue = ordersInRange.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        int totalOrders = ordersInRange.size();
        
        int totalProducts = ordersInRange.stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(OrderItem::getQuantity)
                .sum();

        report.setTotalRevenue(totalRevenue);
        report.setTotalSales(totalRevenue); // Same as revenue for this context
        report.setTotalOrders(totalOrders);
        report.setTotalProducts(totalProducts);

        // Generate top selling products
        Map<String, ProductSalesData> productSalesMap = new HashMap<>();
        
        for (Order order : ordersInRange) {
            for (OrderItem item : order.getItems()) {
                String productId = item.getProductId();
                productSalesMap.computeIfAbsent(productId, k -> new ProductSalesData())
                        .addSale(item.getQuantity(), item.getUnitPrice() * item.getQuantity());
            }
        }

        List<ProductSalesDTO> topSellingProducts = productSalesMap.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    ProductSalesData salesData = entry.getValue();
                    
                    // Try to get product details
                    Optional<Product> productOpt = productRepository.findById(productId);
                    String productName = productOpt.map(Product::getName).orElse("Unknown Product");
                    String category = productOpt.map(Product::getCategory).orElse("Unknown");
                    double unitPrice = productOpt.map(Product::getPrice).orElse(0.0);
                    
                    ProductSalesDTO dto = new ProductSalesDTO(productId, productName, 
                            salesData.getQuantitySold(), salesData.getTotalRevenue());
                    dto.setCategory(category);
                    dto.setUnitPrice(unitPrice);
                    return dto;
                })
                .sorted((a, b) -> Integer.compare(b.getQuantitySold(), a.getQuantitySold()))
                .limit(10) // Top 10 products
                .collect(Collectors.toList());

        report.setTopSellingProducts(topSellingProducts);

        // Generate daily sales breakdown
        List<DailySalesDTO> dailySales = generateDailySalesBreakdown(ordersInRange, startDate, endDate);
        report.setDailySales(dailySales);

        return report;
    }

    public InventoryReportDTO generateInventoryReport() {
        List<Product> allProducts = productRepository.findAll();
        
        InventoryReportDTO report = new InventoryReportDTO();
        report.setReportDate(LocalDateTime.now());
        report.setTotalProducts(allProducts.size());

        List<InventoryItemDTO> inventoryItems = new ArrayList<>();
        List<InventoryItemDTO> lowStockItems = new ArrayList<>();
        List<InventoryItemDTO> outOfStockItems = new ArrayList<>();
        
        double totalStockValue = 0.0;
        int lowStockCount = 0;
        int outOfStockCount = 0;

        for (Product product : allProducts) {
            InventoryItemDTO item = new InventoryItemDTO(
                    product.getId(),
                    product.getName(),
                    product.getSku(),
                    product.getStock(),
                    product.getPrice()
            );
            
            item.setCategory(product.getCategory());
            item.setBrand(product.getBrand());
            
            inventoryItems.add(item);
            totalStockValue += item.getStockValue();

            if (item.isOutOfStock()) {
                outOfStockItems.add(item);
                outOfStockCount++;
            } else if (item.isLowStock()) {
                lowStockItems.add(item);
                lowStockCount++;
            }
        }

        report.setTotalStockValue((int) totalStockValue);
        report.setLowStockCount(lowStockCount);
        report.setOutOfStockCount(outOfStockCount);
        report.setInventoryItems(inventoryItems);
        report.setLowStockItems(lowStockItems);
        report.setOutOfStockItems(outOfStockItems);

        return report;
    }

    private List<DailySalesDTO> generateDailySalesBreakdown(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, DailySalesData> dailyDataMap = new HashMap<>();
        
        // Initialize all dates in range
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < daysBetween; i++) {
            LocalDate date = startDate.plusDays(i);
            dailyDataMap.put(date, new DailySalesData());
        }

        // Populate with actual sales data
        for (Order order : orders) {
            LocalDate orderDate = order.getPlacedAt().toLocalDate();
            if (dailyDataMap.containsKey(orderDate)) {
                DailySalesData dailyData = dailyDataMap.get(orderDate);
                dailyData.addOrder(order.getTotalAmount(), 
                        order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
            }
        }

        return dailyDataMap.entrySet().stream()
                .map(entry -> new DailySalesDTO(entry.getKey(), 
                        entry.getValue().getTotalRevenue(),
                        entry.getValue().getTotalOrders(),
                        entry.getValue().getTotalItems()))
                .sorted(Comparator.comparing(DailySalesDTO::getDate))
                .collect(Collectors.toList());
    }

    // Helper classes
    private static class ProductSalesData {
        private int quantitySold = 0;
        private double totalRevenue = 0.0;

        public void addSale(int quantity, double revenue) {
            this.quantitySold += quantity;
            this.totalRevenue += revenue;
        }

        public int getQuantitySold() { return quantitySold; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    private static class DailySalesData {
        private double totalRevenue = 0.0;
        private int totalOrders = 0;
        private int totalItems = 0;

        public void addOrder(double revenue, int items) {
            this.totalRevenue += revenue;
            this.totalOrders++;
            this.totalItems += items;
        }

        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public int getTotalItems() { return totalItems; }
    }
}

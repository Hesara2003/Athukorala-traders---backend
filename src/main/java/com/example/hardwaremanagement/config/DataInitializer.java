package com.example.hardwaremanagement.config;

import com.example.hardwaremanagement.model.*;
import com.example.hardwaremanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("seed")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private com.example.hardwaremanagement.repository.OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            initializeSampleCategoriesAndProducts();
            initializeSamplePromotions();
        }
        initializeSampleSuppliers();
        initializeSamplePurchaseOrders();
        initializeSampleCustomerOrders();
        System.out.println("Sample data initialized successfully!");
    }

    private void initializeSampleCategoriesAndProducts() {
        Category tools = createCategory("Tools", "Hand tools and sets");
        Category powerTools = createCategory("Power Tools", "Cordless & corded power tools");
        Category safety = createCategory("Safety Equipment", "Safety and protective gear");
        Category electrical = createCategory("Electrical", "Electrical tools and accessories");
        categoryRepository.saveAll(Arrays.asList(tools, powerTools, safety, electrical));

        List<Product> sampleProducts = Arrays.asList(
                createProduct("Hammer", tools, "Heavy-duty claw hammer with steel handle", 25.99, 50, Arrays.asList("hammer1.jpg")),
                createProduct("Screwdriver Set", tools, "6-piece Phillips and flathead screwdriver set", 18.50, 35, Arrays.asList("screwdriver_set1.jpg")),
                createProduct("Power Drill", powerTools, "18V cordless drill with battery and charger", 89.99, 20, Arrays.asList("power_drill1.jpg")),
                createProduct("Safety Goggles", safety, "Clear polycarbonate safety goggles", 8.75, 75, Arrays.asList("safety_goggles1.jpg")),
                createProduct("Extension Cord", electrical, "50-foot heavy-duty outdoor extension cord", 32.50, 25, Arrays.asList("extension_cord1.jpg"))
        );
        productRepository.saveAll(sampleProducts);
    }

    private Category createCategory(String name, String description) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        return c;
    }

    private Product createProduct(String name, Category category, String description, double price, int stock, List<String> images) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category.getName());
        p.setCategoryId(category.getId());
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setImages(images);
        return p;
    }

    private void initializeSamplePromotions() {
        if (promotionRepository.count() > 0) return;
        Category tools = categoryRepository.findByName("Tools").orElse(null);
        if (tools != null) {
            Promotion catPromo = new Promotion();
            catPromo.setName("Tools 10% Off");
            catPromo.setScope(PromotionScope.CATEGORY);
            catPromo.setCategoryId(tools.getId());
            catPromo.setDiscountPercent(10);
            catPromo.setStartDate(java.time.LocalDateTime.now().minusDays(1));
            catPromo.setEndDate(java.time.LocalDateTime.now().plusDays(30));
            promotionRepository.save(catPromo);
        }
        Promotion global = new Promotion();
        global.setName("Storewide 5% Off");
        global.setScope(PromotionScope.GLOBAL);
        global.setDiscountPercent(5);
        global.setStartDate(java.time.LocalDateTime.now().minusDays(1));
        global.setEndDate(java.time.LocalDateTime.now().plusDays(7));
        promotionRepository.save(global);
    }

    private void initializeSamplePurchaseOrders() {
        if (purchaseOrderRepository.count() > 0) return;
        // Use first two products for example POs
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return;

        var supplier1 = supplierRepository.findAll().stream().findFirst().orElse(null);
        var supplier2 = supplierRepository.findAll().stream().skip(1).findFirst().orElse(supplier1);

        PurchaseOrder po1 = new PurchaseOrder();
        po1.setSupplierId(supplier1 != null ? supplier1.getId() : "supplier-1");
        po1.setItems(List.of(
                createItem(products.get(0).getId(), 10),
                products.size() > 1 ? createItem(products.get(1).getId(), 5) : createItem(products.get(0).getId(), 5)
        ));
        po1.setProductIds(po1.getItems().stream().map(i -> i.getProductId()).toList());
        po1.setDeliveryDate(java.time.LocalDate.now().plusDays(7));
        po1.setStatus("CREATED");
        po1.setCreatedAt(java.time.LocalDateTime.now().minusDays(2));

        PurchaseOrder po2 = new PurchaseOrder();
        po2.setSupplierId(supplier2 != null ? supplier2.getId() : "supplier-2");
        po2.setItems(List.of(
                products.size() > 1 ? createItem(products.get(1).getId(), 3) : createItem(products.get(0).getId(), 3),
                products.size() > 2 ? createItem(products.get(2).getId(), 8) : createItem(products.get(0).getId(), 8)
        ));
        po2.setProductIds(po2.getItems().stream().map(i -> i.getProductId()).toList());
        po2.setDeliveryDate(java.time.LocalDate.now().plusDays(10));
        po2.setStatus("CREATED");
        po2.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));

        purchaseOrderRepository.saveAll(Arrays.asList(po1, po2));
    }

    private PurchaseOrderItem createItem(String productId, int qty) {
        PurchaseOrderItem i = new PurchaseOrderItem();
        i.setProductId(productId);
        i.setQuantity(qty);
        return i;
    }

    private void initializeSampleSuppliers() {
        if (supplierRepository.count() > 0) return;
        Supplier s1 = new Supplier(); s1.setName("Global Tools Co"); s1.setContactEmail("sales@globaltools.example"); s1.setPhone("+94 11 234 5678"); s1.setAddress("123 Tool St, Colombo");
        Supplier s2 = new Supplier(); s2.setName("PowerWare Supplies"); s2.setContactEmail("orders@powerware.example"); s2.setPhone("+94 11 876 5432"); s2.setAddress("45 Industrial Rd, Kandy");
        supplierRepository.saveAll(Arrays.asList(s1, s2));
    }

    private void initializeSampleCustomerOrders() {
        if (orderRepository.count() > 0) return;
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) return;

        String customerId = "customer-123"; // placeholder customer id (maps to User.id in real app)

        com.example.hardwaremanagement.model.Order o1 = new com.example.hardwaremanagement.model.Order();
        o1.setCustomerId(customerId);
        o1.setItems(List.of(
                makeOrderItem(products.get(0), 2),
                products.size() > 1 ? makeOrderItem(products.get(1), 1) : makeOrderItem(products.get(0), 1)
        ));
        o1.setTotalAmount(o1.getItems().stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum());
        o1.setStatus(com.example.hardwaremanagement.model.OrderStatus.DELIVERED);
        o1.setPlacedAt(java.time.LocalDateTime.now().minusDays(5));

        com.example.hardwaremanagement.model.Order o2 = new com.example.hardwaremanagement.model.Order();
        o2.setCustomerId(customerId);
        o2.setItems(List.of(
                products.size() > 2 ? makeOrderItem(products.get(2), 1) : makeOrderItem(products.get(0), 3)
        ));
        o2.setTotalAmount(o2.getItems().stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum());
        o2.setStatus(com.example.hardwaremanagement.model.OrderStatus.SHIPPED);
        o2.setPlacedAt(java.time.LocalDateTime.now().minusDays(2));

        orderRepository.saveAll(Arrays.asList(o1, o2));
    }

    private com.example.hardwaremanagement.model.OrderItem makeOrderItem(Product p, int qty) {
        com.example.hardwaremanagement.model.OrderItem i = new com.example.hardwaremanagement.model.OrderItem();
        i.setProductId(p.getId());
        i.setName(p.getName());
        i.setQuantity(qty);
        i.setUnitPrice(p.getPrice());
        return i;
    }
}

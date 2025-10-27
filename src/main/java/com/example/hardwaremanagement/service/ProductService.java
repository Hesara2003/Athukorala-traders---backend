package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.ProductDTO;
import com.example.hardwaremanagement.dto.ProductDetailDTO;
import com.example.hardwaremanagement.model.Category;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.model.Promotion;
import com.example.hardwaremanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PromotionService promotionService;

    @Transactional
    public void deductStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
                
        if (product.getStock() < quantity) {
            throw new IllegalStateException(
                "Insufficient stock for product " + product.getName() + 
                ". Required: " + quantity + ", Available: " + product.getStock());
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product updateProduct(String id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setImages(updatedProduct.getImages());
        existing.setBrand(updatedProduct.getBrand());
        existing.setModel(updatedProduct.getModel());
        existing.setSpecifications(updatedProduct.getSpecifications());
        existing.setWarranty(updatedProduct.getWarranty());
        existing.setWeight(updatedProduct.getWeight());
        existing.setDimensions(updatedProduct.getDimensions());
        existing.setColor(updatedProduct.getColor());
        existing.setMaterial(updatedProduct.getMaterial());
        existing.setAvailable(updatedProduct.isAvailable());
        existing.setSku(updatedProduct.getSku());

        return productRepository.save(existing);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductDetailDTO getProductDetails(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Get related products
        List<ProductDTO> relatedProducts = getRelatedProducts(id, product.getCategory(), product.getBrand());
        
        // Determine stock status
        String stockStatus = determineStockStatus(product.getStock());

        // Category name resolution
        String categoryName = product.getCategory();
        if (categoryName == null && product.getCategoryId() != null) {
            try { Category cat = categoryService.get(product.getCategoryId()); categoryName = cat.getName(); } catch (RuntimeException ignored) {}
        }

        // Promotion application
        Promotion best = promotionService.getBestPromotionFor(product.getId(), product.getCategoryId()).orElse(null);
        double discountedPrice = product.getPrice();
        Double discountPercent = null; String promotionName = null;
        if (best != null) {
            discountPercent = best.getDiscountPercent();
            promotionName = best.getName();
            discountedPrice = product.getPrice() * (1 - (discountPercent / 100.0));
        }
        
        return new ProductDetailDTO(
                product.getId(),
                product.getName(),
                categoryName,
                product.getDescription(),
                product.getPrice(),
                discountedPrice,
                discountPercent,
                promotionName,
                product.getStock(),
                product.getImages(),
                product.getBrand(),
                product.getModel(),
                product.getSpecifications(),
                product.getWarranty(),
                product.getWeight(),
                product.getDimensions(),
                product.getColor(),
                product.getMaterial(),
                product.isAvailable(),
                product.getSku(),
                stockStatus,
                relatedProducts
        );
    }

    public List<ProductDTO> getRelatedProducts(String excludeId, String category, String brand) {
        List<Product> relatedProducts;
        
        // First try to find products from same category
        relatedProducts = productRepository.findByCategoryAndIdNot(category, excludeId);
        
        // If not enough products from same category, add products from same brand
        if (relatedProducts.size() < 4 && brand != null) {
            List<Product> brandProducts = productRepository.findByBrandAndIdNot(brand, excludeId);
            relatedProducts.addAll(brandProducts.stream()
                    .filter(p -> !relatedProducts.contains(p))
                    .collect(Collectors.toList()));
        }
        
        // Limit to 4 related products
        return relatedProducts.stream()
                .limit(4)
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToProductDTO(Product product) {
        Promotion best = promotionService.getBestPromotionFor(product.getId(), product.getCategoryId()).orElse(null);
        double discountedPrice = product.getPrice();
        Double discountPercent = null; String promotionName = null;
        if (best != null) {
            discountPercent = best.getDiscountPercent();
            promotionName = best.getName();
            discountedPrice = product.getPrice() * (1 - (discountPercent / 100.0));
        }
        String categoryName = product.getCategory();
        if (categoryName == null && product.getCategoryId() != null) {
            try { Category cat = categoryService.get(product.getCategoryId()); categoryName = cat.getName(); } catch (RuntimeException ignored) {}
        }
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), discountedPrice, discountPercent, promotionName, product.getImages(), categoryName, product.getStock());
    }

    private String determineStockStatus(int stock) {
        if (stock == 0) {
            return "Out of Stock";
        } else if (stock <= 5) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    // Search methods
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchByNameCategoryOrSku(searchTerm.trim());
    }

    public List<Product> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByNameContaining(name.trim());
    }

    public List<Product> searchBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findBySkuContaining(sku.trim());
    }

    public List<Product> advancedSearch(String searchTerm, Double minPrice, Double maxPrice, Boolean availableOnly) {
        // Set default values
        double min = (minPrice != null && minPrice >= 0) ? minPrice : 0;
        double max = (maxPrice != null && maxPrice > min) ? maxPrice : Double.MAX_VALUE;
        boolean available = (availableOnly != null) ? availableOnly : false;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            searchTerm = "";
        }
        
        return productRepository.advancedSearch(searchTerm.trim(), min, max, available);
    }

    // Convert products to DTOs with promotion info
    public List<ProductDTO> searchProductsAsDTO(String searchTerm) {
        return searchProducts(searchTerm).stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> advancedSearchAsDTO(String searchTerm, Double minPrice, Double maxPrice, Boolean availableOnly) {
        return advancedSearch(searchTerm, minPrice, maxPrice, availableOnly).stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }
}

package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Find products by category excluding the current product
    List<Product> findByCategoryAndIdNot(String category, String excludeId);
    
    // Find products by brand excluding the current product  
    List<Product> findByBrandAndIdNot(String brand, String excludeId);
    
    // Find products by category
    List<Product> findByCategory(String category);
    
    // Find products that are available (in stock)
    @Query("{ 'stock' : { $gt : 0 } }")
    List<Product> findAvailableProducts();
    
    // Search products by name (case-insensitive)
    @Query("{ 'name' : { $regex : ?0, $options : 'i' } }")
    List<Product> findByNameContaining(String name);
    
    // Search products by SKU (case-insensitive)
    @Query("{ 'sku' : { $regex : ?0, $options : 'i' } }")
    List<Product> findBySkuContaining(String sku);
    
    // Search products by multiple criteria (name, category, sku)
    @Query("{ $or: [ " +
           "{ 'name' : { $regex : ?0, $options : 'i' } }, " +
           "{ 'category' : { $regex : ?0, $options : 'i' } }, " +
           "{ 'sku' : { $regex : ?0, $options : 'i' } } " +
           "] }")
    List<Product> searchByNameCategoryOrSku(String searchTerm);
    
    // Advanced search with multiple filters
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "  { 'name' : { $regex : ?0, $options : 'i' } }, " +
           "  { 'category' : { $regex : ?0, $options : 'i' } }, " +
           "  { 'sku' : { $regex : ?0, $options : 'i' } } " +
           "] }, " +
           "{ 'price' : { $gte : ?1, $lte : ?2 } }, " +
           "{ $expr: { $cond: { if: ?3, then: { $gt: ['$stock', 0] }, else: true } } } " +
           "] }")
    List<Product> advancedSearch(String searchTerm, double minPrice, double maxPrice, boolean availableOnly);
}

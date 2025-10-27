package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.service.ProductService;
import com.example.hardwaremanagement.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload-images")
    public ResponseEntity<Map<String, Object>> uploadProductImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> fileNames = fileStorageService.storeFiles(files);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileNames", fileNames);
            response.put("message", "Images uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload images: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.createProduct(product);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Search endpoints for admin
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String searchTerm) {
        List<Product> products = productService.searchProducts(searchTerm);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<List<Product>> advancedSearch(
            @RequestParam(value = "q", required = false, defaultValue = "") String searchTerm,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "availableOnly", required = false, defaultValue = "false") Boolean availableOnly) {
        
        List<Product> products = productService.advancedSearch(searchTerm, minPrice, maxPrice, availableOnly);
        return ResponseEntity.ok(products);
    }
}

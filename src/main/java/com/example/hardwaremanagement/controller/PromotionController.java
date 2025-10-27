package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Promotion;
import com.example.hardwaremanagement.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public ResponseEntity<Promotion> create(@RequestBody Promotion promotion) {
        return new ResponseEntity<>(promotionService.create(promotion), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Promotion> list() { return promotionService.list(); }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> get(@PathVariable String id) {
        return promotionService.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Promotion update(@PathVariable String id, @RequestBody Promotion promotion) { return promotionService.update(id, promotion); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { promotionService.delete(id); return ResponseEntity.noContent().build(); }
}

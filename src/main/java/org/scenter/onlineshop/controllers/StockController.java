package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/stock")
@AllArgsConstructor
public class StockController {
    StockService stockService;

    @GetMapping("/allProducts")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok().body(stockService.getAllProducts());
    }

    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok().body(stockService.getAllCategories());
    }
}

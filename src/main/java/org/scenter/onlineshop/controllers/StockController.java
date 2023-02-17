package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/stock")
@AllArgsConstructor
public class StockController {
    StockService stockService;

    // ====================== Comment management =========================
    @PostMapping("/{product}/postComment")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> postComment(@PathVariable String product,
                                         @Valid @RequestPart CommentRequest commentRequest,
                                         @RequestPart MultipartFile[] files) {
        if (!stockService.isAuthorized(commentRequest.getUserEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        return stockService.postComment(commentRequest,product,files);
    }

    @DeleteMapping("/{product}/deleteComment")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable String product,
                                           @Valid @RequestBody CommentRequest commentRequest) {
        if (!stockService.isAuthorized(commentRequest.getUserEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        return stockService.deleteComment(commentRequest.getCommentId(),product);
    }

    @DeleteMapping("/{product}/forceDeleteComment")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> forceDeleteComment(@PathVariable String product,
                                           @Valid @RequestBody CommentRequest commentRequest) {
        return stockService.deleteComment(commentRequest.getCommentId(),product);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        return stockService.getFile(id);
    }

    @GetMapping ("/{userEmail}/getUserComments")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getCommentsByUserId(@PathVariable String userEmail) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByUserEmail(userEmail));
    }
    @GetMapping ("/{product}/getComments")
    public ResponseEntity<?> getCommentsByProduct(@PathVariable String product) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByProduct(product));
    }

    // ====================== Products/Categories management =========================
    @GetMapping("/allProducts")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok().body(stockService.getAllProducts());
    }

    @GetMapping("/{category}/products")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok().body(stockService.getProductsByCategory(category));
    }
    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok().body(stockService.getAllCategories());
    }
}

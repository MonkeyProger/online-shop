package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.scenter.onlineshop.exception.IllegalFormatException;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.services.StockService;
import org.springframework.expression.AccessException;
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
    private StockService stockService;

    // ====================== Comment management =========================

    @PostMapping("/{product}/postComment")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> postComment(@PathVariable String product,
                                         @Valid @RequestPart CommentRequest commentRequest,
                                         @RequestPart MultipartFile[] files) throws IllegalFormatException, FileUploadException {
        return stockService.postComment(commentRequest, product, files);
    }

    @DeleteMapping("/{product}/deleteComment")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable String product,
                                           @Valid @RequestBody CommentRequest commentRequest) throws AccessException {

        return stockService.deleteComment(commentRequest.getCommentId(), product);
    }
    @GetMapping ("/{product}/getComments")
    public ResponseEntity<?> getCommentsByProduct(@PathVariable String product) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByProduct(product));
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        return stockService.getFile(id);
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

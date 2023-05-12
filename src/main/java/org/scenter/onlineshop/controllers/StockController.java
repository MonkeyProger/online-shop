package org.scenter.onlineshop.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.dto.ProductDTO;
import org.scenter.onlineshop.exception.IllegalFormatException;
import org.scenter.onlineshop.mapping.ProductMapping;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/stock")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", maxAge = 3600)
public class StockController {

    private final StockService stockService;

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
        List<Product> products = stockService.getAllProducts();
        List<ProductDTO> productDTO = products.stream().map(ProductMapping::convertProductToDTO).collect(Collectors.toList());
        return ResponseEntity.ok().body(productDTO);
    }

    @GetMapping("/{category}/products")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        List<Product> products = stockService.getProductsByCategory(category);
        List<ProductDTO> productDTO = products.stream().map(ProductMapping::convertProductToDTO).collect(Collectors.toList());
        return ResponseEntity.ok().body(productDTO);
    }

    @GetMapping("/allCategories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok().body(stockService.getAllCategories());
    }

    @GetMapping("/allCharacteristics")
    public ResponseEntity<?> setCharacteristic() {
        return ResponseEntity.ok().body(stockService.getAllCharacteristic());
    }
}

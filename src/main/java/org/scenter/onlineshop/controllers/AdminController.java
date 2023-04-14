package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import org.scenter.onlineshop.requests.*;
import org.scenter.onlineshop.services.ShopService;
import org.scenter.onlineshop.services.StockService;
import org.scenter.onlineshop.services.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/manage")
@AllArgsConstructor
public class AdminController {
    private StockService stockService;
    private ShopService shopService;
    private UserDetailsServiceImpl userDetailsService;
    private AuthController authController;

//  ====================================        User management         ================================================

    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userDetailsService.getAllUsers());
    }

    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody AdminSignupRequest adminSignupRequest) {
        return authController.registerUser(adminSignupRequest);
    }

    @PostMapping("/updateUser/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody AdminSignupRequest adminSignupRequest,
                                        @PathVariable Long userId) {
        return authController.updateUser(adminSignupRequest, userId);
    }

    @DeleteMapping("/deleteUser/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return userDetailsService.deleteAppUser(userId);
    }

    //  =================================        Comments management         ===========================================

    @DeleteMapping("/deleteComment/{product}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> forceDeleteComment(@PathVariable String product,
                                                @Valid @RequestBody CommentRequest commentRequest) {
        return stockService.deleteComment(commentRequest.getCommentId(), product);
    }

    @DeleteMapping("/deleteComments/{product}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> forceDeleteComments(@PathVariable String product) {
        return stockService.deleteProductComments(product);
    }

    @DeleteMapping("/deleteCommentPhotos/{commentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCommentPhotos(@PathVariable Long commentId) {
        return stockService.deleteCommentPhotos(commentId);
    }

    @GetMapping("/getUserComments/{userEmail}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getCommentsByUserEmail(@PathVariable String userEmail) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByUserEmail(userEmail));
    }

    //  =============================        Products management         ====================================

    @PostMapping("/placeProduct")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> placeProduct(@Valid @RequestPart PlaceProductRequest placeProductRequest,
                                          @RequestPart MultipartFile[] files) {
        return stockService.placeProduct(placeProductRequest, files);
    }

    @PostMapping("/updateProduct/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @Valid @RequestBody PlaceProductRequest placeProductRequest) {
        return stockService.updateProduct(productId, placeProductRequest);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        return stockService.removeProduct(productId);
    }

    @PostMapping("/postCharact/{productName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setCharacteristic(@PathVariable String productName,
                                               @Valid @RequestBody CharacteristicRequest charactRequest) {
        return stockService.setCharacteristic(productName, charactRequest);
    }

    @PostMapping("/deleteCharact/{productName}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCharacteristic(@PathVariable String productName,
                                                  @Valid @RequestBody CharacteristicRequest charactRequest) {
        return stockService.deleteCharacteristic(productName, charactRequest.getName());
    }

    //  ===================================        Categories management         =======================================

    @PostMapping("/placeCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> placeCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return stockService.placeCategory(categoryRequest);
    }

    @PostMapping("/updateCategory/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @Valid @RequestBody CategoryRequest categoryRequest) {
        return stockService.updateCategory(categoryId, categoryRequest);
    }

    @PostMapping("/deleteCategory/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        return stockService.deleteCategory(categoryId);
    }

    @PostMapping("/link{childId}/to{parentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> linkCategoryToParent(@PathVariable Long childId, @PathVariable Long parentId) {
        return ResponseEntity.ok().body(stockService.saveParentToCategory(childId, parentId));
    }

    @PostMapping("/link{productId}/to{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> linkProductToCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
        return ResponseEntity.ok().body(stockService.saveProductToCategory(productId, categoryId));
    }
    //  ===================================        Files management         ============================================

    //  ===================================        Order management         ============================================

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok().body(shopService.getAllOrders());
    }

    @GetMapping("/saleProducts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSaleProducts() {
        return ResponseEntity.ok().body(shopService.getAllSaleProducts());
    }

    @GetMapping("/allActiveOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllActiveOrders() {
        return ResponseEntity.ok().body(shopService.getAllActiveOrders());
    }

    @GetMapping("/allOrders/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrdersByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(shopService.getAllOrdersByEmail(email));
    }

    @PostMapping("/updateOrder/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest,
                                         @PathVariable Long orderId) {
        return ResponseEntity.ok().body(shopService.updateOrder(placeOrderRequest, orderId));
    }

    @DeleteMapping("/deleteOrder")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrder(@Valid @RequestBody CloseOrderRequest closeOrderRequest) {
        return ResponseEntity.ok().body(shopService.closeOrder(closeOrderRequest));
    }
}
package org.scenter.onlineshop.web.controllers;

import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.scenter.onlineshop.common.requests.*;
import org.scenter.onlineshop.common.exception.ElementIsPresentedException;
import org.scenter.onlineshop.common.exception.IllegalFormatException;
import org.scenter.onlineshop.service.services.ShopService;
import org.scenter.onlineshop.service.services.StockService;
import org.scenter.onlineshop.service.services.UserDetailsServiceImpl;
import org.springframework.expression.AccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/manage")
@AllArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    private final StockService stockService;
    private final ShopService shopService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthController authController;

//  ====================================        User management         ================================================

    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userDetailsService.getAllUsers());
    }

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@Valid @RequestBody AdminSignupRequest adminSignupRequest) throws ElementIsPresentedException {
        return authController.registerUser(adminSignupRequest);
    }

    @PostMapping("/updateUser/{userId}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody AdminSignupRequest adminSignupRequest,
                                        @PathVariable Long userId) {
        return authController.updateUser(adminSignupRequest, userId);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        return userDetailsService.deleteAppUser(userId);
    }

    //  =================================        Comments management         ===========================================

    @DeleteMapping("/deleteComment/{product}")
    public ResponseEntity<?> forceDeleteComment(@PathVariable String product,
                                                @Valid @RequestBody CommentRequest commentRequest) throws AccessException {
        return stockService.deleteComment(commentRequest.getCommentId(), product);
    }

    @DeleteMapping("/deleteComments/{product}")
    public ResponseEntity<?> forceDeleteComments(@PathVariable String product) {
        return stockService.deleteProductComments(product);
    }

    @DeleteMapping("/deleteCommentPhotos/{commentId}")
    public ResponseEntity<?> deleteCommentPhotos(@PathVariable Long commentId) {
        return stockService.deleteCommentPhotos(commentId);
    }

    @GetMapping("/getUserComments/{userEmail}")
    public ResponseEntity<?> getCommentsByUserEmail(@PathVariable String userEmail) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByUserEmail(userEmail));
    }

    //  =============================        Products management         ====================================

    @PostMapping("/placeProduct")
    public ResponseEntity<?> placeProduct(@Valid @RequestPart PlaceProductRequest placeProductRequest,
                                          @RequestPart MultipartFile[] files) throws IllegalFormatException, FileUploadException {
        return stockService.placeProduct(placeProductRequest, files);
    }

    @PostMapping("/updateProduct/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @Valid @RequestBody PlaceProductRequest placeProductRequest) {
        return stockService.updateProduct(productId, placeProductRequest);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        return stockService.removeProduct(productId);
    }

    //  ===================================        Characteristics management         ==================================

    @GetMapping("/allCharacteristics")
    public ResponseEntity<?> getAllCharacteristics() {
        return ResponseEntity.ok(stockService.getAllCharacteristics());
    }

    @PostMapping("/createCharact")
    public ResponseEntity<?> createCharact(@RequestBody @Valid CharacteristicRequest characteristicRequest) {
        return ResponseEntity.ok(stockService.createCharacteristic(characteristicRequest));
    }

    @PostMapping("/addValue/{charactId}")
    public ResponseEntity<?> addValueForCharact(@RequestBody String newValue,
                                                @PathVariable Long charactId) {
        return ResponseEntity.ok(stockService.addValueForCharact(newValue, charactId));
    }

    @PostMapping("/deleteValue/{valueId}")
    public ResponseEntity<?> deleteValue(@PathVariable Long valueId) {
        return ResponseEntity.ok(stockService.deleteCharacteristicValue(valueId));
    }

    @PostMapping("/deleteCharacteristic/{charactId}")
    public ResponseEntity<?> deleteCharacteristic(@PathVariable Long charactId) {
        return ResponseEntity.ok(stockService.deleteCharacteristic(charactId));
    }

    @PostMapping("/postCharact/{productName}")
    public ResponseEntity<?> setCharacteristic(@PathVariable String productName,
                                               @Valid @RequestBody PlaceCharactOnProductRequest placeCharactRequest) {
        return stockService.setCharacteristic(productName, placeCharactRequest);
    }

    @PostMapping("/deleteCharact/{productName}")
    public ResponseEntity<?> deleteCharacteristic(@PathVariable String productName,
                                                  @Valid @RequestBody PlaceCharactOnProductRequest charactRequest) {
        return stockService.deleteCharacteristicFromProduct(productName, charactRequest.getValue());
    }

    //  ===================================        Categories management         =======================================

    @PostMapping("/placeCategory")
    public ResponseEntity<?> placeCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return stockService.placeCategory(categoryRequest);
    }

    @PostMapping("/updateCategory/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId,
                                            @Valid @RequestBody CategoryRequest categoryRequest) {
        return stockService.updateCategory(categoryId, categoryRequest);
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        return stockService.deleteCategory(categoryId);
    }

    @PostMapping("/linkCategory/{childId}/to/{parentId}")
    public ResponseEntity<?> linkCategoryToParent(@PathVariable Long childId,
                                                  @PathVariable Long parentId) {
        return ResponseEntity.ok().body(stockService.saveParentToCategory(childId, parentId));
    }

    @PostMapping("/linkProduct/{productId}/to/{categoryId}")
    public ResponseEntity<?> linkProductToCategory(@PathVariable Long productId,
                                                   @PathVariable Long categoryId) {
        return ResponseEntity.ok().body(stockService.saveProductToCategory(productId, categoryId));
    }

    @PostMapping("/removeProduct/{productId}/from/{categoryId}")
    public ResponseEntity<?> removeFromCategory(@PathVariable Long productId,
                                                @PathVariable Long categoryId) {
        return ResponseEntity.ok().body(stockService.removeFromCategory(productId, categoryId));
    }
    //  ===================================        Files management         ============================================

    //  ===================================        Order management         ============================================

    @GetMapping("/allOrders")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok().body(shopService.getAllOrders());
    }

    @GetMapping("/saleProducts")
    public ResponseEntity<?> getSaleProducts() {
        return ResponseEntity.ok().body(shopService.getAllSaleProducts());
    }

    @GetMapping("/allActiveOrders")
    public ResponseEntity<?> getAllActiveOrders() {
        return ResponseEntity.ok().body(shopService.getAllActiveOrders());
    }

    @GetMapping("/allOrders/{email}")
    public ResponseEntity<?> getAllOrdersByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(shopService.getAllOrdersByEmail(email));
    }

    @PostMapping("/updateOrder/{orderId}")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest,
                                         @PathVariable Long orderId) {
        return ResponseEntity.ok().body(shopService.updateOrder(placeOrderRequest, orderId));
    }

    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@Valid @RequestBody CloseOrderRequest closeOrderRequest) throws AccessException {
        return ResponseEntity.ok().body(shopService.closeOrder(closeOrderRequest));
    }
}
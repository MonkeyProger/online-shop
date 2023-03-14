package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.services.AdminService;
import org.scenter.onlineshop.services.ShopService;
import org.scenter.onlineshop.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/manage")
@AllArgsConstructor
public class AdminController {
    private StockService stockService;
    private ShopService shopService;
    private AdminService adminService;
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    // TODO User/Role: add update delete ban unban

    // TODO Stock: Comments {deleteComment getByUser getAll}; Products/Categories
    @DeleteMapping("/{product}/deleteComment")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> forceDeleteComment(@PathVariable String product,
                                                @Valid @RequestBody CommentRequest commentRequest) {
        return stockService.deleteComment(commentRequest.getCommentId(),product);
    }

    @GetMapping ("/{userEmail}/getUserComments")
    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getCommentsByUserEmail(@PathVariable String userEmail) {
        return ResponseEntity.ok().body(stockService.getAllCommentsByUserEmail(userEmail));
    }

    // TODO Shop: Orders{getAll delete update}
    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrders(){
        return ResponseEntity.ok().body(shopService.getAllOrders());
    }

    @GetMapping("/saleProducts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSaleProducts(){
        return ResponseEntity.ok().body(shopService.getAllSaleProducts());
    }
    @GetMapping("/allActiveOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllActiveOrders(){
        return ResponseEntity.ok().body(shopService.getAllActiveOrders());
    }
    @GetMapping("/allOrders={email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrdersByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(shopService.getAllOrdersByEmail(email));
    }
}
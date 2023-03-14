package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.*;
import org.scenter.onlineshop.repo.CategoryRepo;
import org.scenter.onlineshop.repo.CommentRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class StockService {
    private CategoryRepo categoryRepo;
    private ProductRepo productRepo;
    private CommentRepo commentRepo;
    private UserRepo userRepo;
    private FileStorageService fileStorageService;

    // ====================== Product management =========================
    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepo.findById(productId);
        if (product.isEmpty()){
            log.error("Product with id " + productId + "not found");
            throw new NoSuchElementException("Product with id " + productId + "not found");
        }
        log.info("Product '{}' found in the database", product.get().getName());
        return product.get();
    }
    public Product getProductByName(String productName) {
        Optional<Product> product = productRepo.findByName(productName);
        if (product.isEmpty()){
            log.error("Product with name " + productName + "not found");
            throw new NoSuchElementException("Product with name " + productName + "not found");
        }
        log.info("Product '{}' found in the database", productName);
        return product.get();
    }

    // ====================== Comment management =========================

    public boolean isAuthorized(String email){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Objects.equals(email, username);
    }

    public ResponseEntity<?> postComment(CommentRequest commentRequest,
                                         String productName,
                                         MultipartFile[] files){
        if (!userRepo.existsByEmail(commentRequest.getUserEmail())){
            log.error("User with id " + commentRequest.getUserEmail() + "not found");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with email " + commentRequest.getUserEmail() + "not found"));
        }

        Comment comment = new Comment(commentRequest.getComment(),
                commentRequest.getRating(),commentRequest.getUserEmail());
        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        if (comments.contains(comment)){
            log.error("Comment has already been added to product: "+productName);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment has already been added to product: "+productName));
        }

        // Сохранение файлов в бд
        if (!fileStorageService.checkImages(files)){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Wrong format of images."));
        }

        if (files.length > 0 && files.length <= 10)
        {
            List<FileDB> filesDB;
            try {
                filesDB = fileStorageService.saveFilesDB(files);
            } catch (Exception e) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Fail to upload files :" + e.getMessage()));
            }
            // Задание списка пользовательских фотографий комментарию
            List<ResponseFile> commentFiles = new ArrayList<>();
            filesDB.forEach(file -> commentFiles.add(fileStorageService.saveResponsefile(file)));
            comment.setImages(commentFiles);
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Too many files to upload."));
        }

        comments.add(comment);
        product.setComments(comments);
        saveComment(comment);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Comment "+comment.getId().toString()+
                " added to product "+productName+" successfully"));
    }

    public ResponseEntity<?> getFile(String id){
        FileDB fileDB = fileStorageService.getFile(id);
        if (fileDB == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("File with id "+id+" is not presented"));;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    };

    public ResponseEntity<?>  deleteComment(Long commentId, String productName){
        Optional<Comment> comment = commentRepo.findById(commentId);
        if (comment.isEmpty()) {
            log.error("Comment with id "+commentId+" is not presented");
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment with id "+commentId+" is not presented"));
        }

        Comment repoComment = comment.get();
        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        if (!comments.contains(repoComment)){
            log.error("Comment is not presented in product: "+productName);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment is not presented in product: "+productName));
        }

        comments.remove(repoComment);
        product.setComments(comments);
        deleteComment(repoComment);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Comment "+repoComment.getId().toString()+
                " deleted successfully"));
    }

    public List<Comment> getAllCommentsByUserEmail(String email){
        return commentRepo.findAllByUserEmail(email);
    }

    public List<Comment> getAllCommentsByProduct(String productName){
        Product product = getProductByName(productName);
        return product.getComments();
    }

    // ====================== Category management =========================
    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }
    public Category getCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (category.isEmpty()){
            log.error("Category with id " + categoryId + "not found");
            throw new NoSuchElementException("Category with id " + categoryId + "not found");
        }
        log.info("Category '{}' found in the database", category.get().getName());
        return category.get();
    }
    public Category getCategoryByName(String categoryName) {
        Optional<Category> category = categoryRepo.findByName(categoryName);
        if (category.isEmpty()){
            log.error("Category with name " + categoryName + "not found");
            throw new NoSuchElementException("Category with name " + categoryName + "not found");
        }
        log.info("Category '{}' found in the database", categoryName);
        return category.get();
    }

    public void saveProductToCategory(String productName, String categoryName){
        Category category = getCategoryByName(categoryName);
        Product product = getProductByName(productName);
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.error("Product '{}' is already in category '{}'", productName,categoryName);
            throw new IllegalArgumentException("Product is already in category");
        }
        oldProducts.add(product);
        category.setProducts(oldProducts);
        saveCategory(category);
    }
    public void saveParentToCategory(String child, String parent){
        Category parentCategory = getCategoryByName(parent);
        Category childCategory = getCategoryByName(child);
        childCategory.setParentId(parentCategory.getId());
        saveCategory(childCategory);
    }
    public List<Product> getProductsByCategory(String categoryName){
        Category category = getCategoryByName(categoryName);
        return category.getProducts();
    }


    @Transactional
    public void saveComment(Comment comment) {commentRepo.save(comment);}
    @Transactional
    public void deleteComment(Comment comment) {commentRepo.delete(comment);}
    @Transactional
    public void saveProduct(Product product) {productRepo.save(product);}
    @Transactional
    public void saveAllProducts(Set<Product> products) {productRepo.saveAll(products);}
    @Transactional
    public void saveCategory(Category category) {categoryRepo.save(category);}
}

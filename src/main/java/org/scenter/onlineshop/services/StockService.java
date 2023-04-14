package org.scenter.onlineshop.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.*;
import org.scenter.onlineshop.repo.*;
import org.scenter.onlineshop.requests.CategoryRequest;
import org.scenter.onlineshop.requests.CharacteristicRequest;
import org.scenter.onlineshop.requests.CommentRequest;
import org.scenter.onlineshop.requests.PlaceProductRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final CommentRepo commentRepo;
    private final FileStorageService fileStorageService;
    private final CharacteristicRepo characteristicRepo;
    private final CharacteristicValueRepo characteristicValueRepo;


    // ====================== Product management =========================

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Long productId) {
        Optional<Product> product = productRepo.findById(productId);
        if (!product.isPresent()) {
            log.error("Product with id " + productId + "not found");
            throw new NoSuchElementException("Product with id " + productId + "not found");
        }
        log.info("Product '{}' found in the database", product.get().getName());
        return product.get();
    }

    public Product getProductByName(String productName) {
        Optional<Product> product = productRepo.findByName(productName);
        if (!product.isPresent()) {
            log.error("Product with name " + productName + "not found");
            throw new NoSuchElementException("Product with name " + productName + "not found");
        }
        log.info("Product '{}' found in the database", productName);
        return product.get();
    }

    public ResponseEntity<?> placeProduct(PlaceProductRequest placeProductRequest,
                                          MultipartFile[] files) {
        Product product = new Product(
                null,
                placeProductRequest.getName(),
                placeProductRequest.getTitle(),
                null,
                placeProductRequest.getPrice(),
                placeProductRequest.getSalePrice(),
                placeProductRequest.getAmount(),
                null,
                null);

        product = saveProduct(product);
        addPhotosToProduct(product.getId(), files);
        return ResponseEntity.ok(new MessageResponse("Product added successfully"));
    }

    public ResponseEntity<?> addPhotosToProduct(Long productId, MultipartFile[] files) {
        Product product = getProductById(productId);
        if (files[0].getContentType() != null) {
            if (files.length <= 10) {
                if (!fileStorageService.checkImages(files)) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Wrong format of images."));
                }
                List<FileDB> filesDB;
                try {
                    filesDB = fileStorageService.saveFilesDB(files);
                } catch (Exception e) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Fail to upload files :" + e.getMessage()));
                }
                List<ProductFile> productFiles = new ArrayList<>();
                filesDB.forEach(file -> productFiles.add(fileStorageService.saveProductFile(file)));
                product.setImages(productFiles);
            } else {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Too many files to upload."));
            }
            saveProduct(product);
        }
        return ResponseEntity.ok(new MessageResponse("Photos added successfully"));
    }

    public ResponseEntity<?> updateProduct(Long productId, PlaceProductRequest placeProductRequest) {
        Product product = getProductById(productId);
        Boolean saveComments = placeProductRequest.getSaveComments();
        if (saveComments == null || !saveComments) {
            List<Comment> comments = product.getComments();
            product.setComments(new ArrayList<>());
            deleteComments(comments);
        }
        product.setAmount(placeProductRequest.getAmount());
        product.setName(placeProductRequest.getName());
        product.setPrice(placeProductRequest.getPrice());
        product.setTitle(placeProductRequest.getTitle());
        product.setSalePrice(placeProductRequest.getSalePrice());
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Product updated successfully"));
    }

    public ResponseEntity<?> removeProduct(Long productId) {
        Product product = getProductById(productId);
        if (product == null) {
            return ResponseEntity.ok(new MessageResponse("Product with id " + productId + " is not found"));
        }
        List<Comment> productComments = product.getComments();
        Set<Category> categories = getCategoriesByProduct(product);
        for (Category category : categories) {
            List<Product> productList = category.getProducts();
            productList.remove(product);
            category.setProducts(productList);
            saveCategory(category);
        }
        removeProduct(product);
        deleteComments(productComments);
        return ResponseEntity.ok(new MessageResponse("Product: " + product.getName() + " deleted successfully"));
    }

    // ===================== Characteristic management ===================

    public ResponseEntity<?> setCharacteristic(Long productId, CharacteristicRequest characteristicRequest) {
        Optional<Characteristic> optionalCharacteristic =
                characteristicRepo.findByName(characteristicRequest.getName());

        Characteristic characteristic;
        if (!optionalCharacteristic.isPresent()) {
            characteristic = new Characteristic();
            characteristic.setName(characteristic.getName());

        } else {
            characteristic = optionalCharacteristic.get();
        }

        Optional<CharacteristicValue> optionalCharacteristicValue =
                characteristicValueRepo.findByValue(characteristicRequest.getValue());

        CharacteristicValue characteristicValue;
        if (!optionalCharacteristicValue.isPresent()) {
            characteristicValue = new CharacteristicValue();
            characteristicValue.setValue(characteristicRequest.getValue());
        } else {
            characteristicValue = optionalCharacteristicValue.get();
        }


        characteristic.setValue(characteristicValue);

        Product product = productRepo.getReferenceById(productId);
        List<Characteristic> productCharacteristic = product.getCharacteristics();
        productCharacteristic.add(characteristic);

        product.setCharacteristics(productCharacteristic);
        saveCharacteristicValue(characteristicValue);
        saveCharacteristic(characteristic);
        saveProduct(product);

        return ResponseEntity.ok(new MessageResponse("Characteristic " + characteristic.getName() + " = " +
                characteristicValue.getValue() + " added to product " + product.getName() + " successfully"));
    }

    // ====================== Comment management =========================
    public ResponseEntity<?> postComment(CommentRequest commentRequest,
                                         String productName,
                                         MultipartFile[] files) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = new Comment(
                commentRequest.getComment(),
                commentRequest.getRating(),
                userEmail);

        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        if (comments.contains(comment)) {
            log.error("Comment has already been added to product: " + productName);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment has already been added to product: " + productName));
        }

        log.info(Arrays.toString(files));

        // Сохранение файлов в бд
        if (files[0].getContentType() != null) {
            if (files.length <= 10) {

                if (!fileStorageService.checkImages(files)) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Wrong format of images."));
                }
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
        }

        comments.add(comment);
        product.setComments(comments);
        saveComment(comment);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Comment " + comment.getId().toString() +
                " added to product " + productName + " successfully"));
    }

    public ResponseEntity<?> getFile(String id) {
        FileDB fileDB = fileStorageService.getFile(id);
        if (fileDB == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("File with id " + id + " is not presented"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }

    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepo.findById(commentId);
        if (!comment.isPresent()) {
            log.error("Comment with id " + commentId + " is not presented");
            return null;
        }
        return comment.get();
    }

    public ResponseEntity<?> deleteComment(Long commentId, String productName) {
        Comment repoComment = getCommentById(commentId);
        if (repoComment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment with id " + commentId + " is not presented"));
        }

        if (SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .noneMatch(a -> a.getAuthority().equals("ADMIN"))) {

            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!repoComment.getUserEmail().equals(userEmail)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Access denied: To delete the comment, you need to be under your account"));
            }
        }

        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        if (!comments.contains(repoComment)) {
            log.error("Comment is not presented in product: " + productName);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Comment is not presented in product: " + productName));
        }

        comments.remove(repoComment);
        product.setComments(comments);
        saveProduct(product);

        List<ResponseFile> commentFiles = repoComment.getImages();
        deleteComment(repoComment);
        if (!commentFiles.isEmpty()) {
            fileStorageService.deleteResponseFiles(commentFiles);
        }
        return ResponseEntity.ok(new MessageResponse("Comment " + repoComment.getId().toString() +
                " deleted successfully"));
    }

    public ResponseEntity<?> deleteProductComments(String productName) {
        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        product.setComments(new ArrayList<>());
        deleteComments(comments);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Comments of the product " + productName +
                " cleared successfully"));
    }

    public ResponseEntity<?> deleteCommentPhotos(Long commentId) {
        Comment comment = getCommentById(commentId);
        List<ResponseFile> commentPhotos = comment.getImages();
        comment.setImages(new ArrayList<>());
        fileStorageService.deleteResponseFiles(commentPhotos);
        saveComment(comment);
        return ResponseEntity.ok(new MessageResponse("Photos of the comment " + commentId +
                " cleared successfully"));
    }

    public List<Comment> getAllCommentsByUserEmail(String email) {
        return commentRepo.findAllByUserEmail(email);
    }

    public List<Comment> getAllCommentsByProduct(String productName) {
        Product product = getProductByName(productName);
        return product.getComments();
    }

    // ====================== Category management =========================
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (!category.isPresent()) {
            log.error("Category with id " + categoryId + "not found");
            throw new NoSuchElementException("Category with id " + categoryId + "not found");
        }
        log.info("Category '{}' found in the database", category.get().getName());
        return category.get();
    }

    public Category getCategoryByName(String categoryName) {
        Optional<Category> category = categoryRepo.findByName(categoryName);
        if (!category.isPresent()) {
            log.error("Category with name " + categoryName + "not found");
            throw new NoSuchElementException("Category with name " + categoryName + "not found");
        }
        log.info("Category '{}' found in the database", categoryName);
        return category.get();
    }

    public void saveProductToCategory(String productName, String categoryName) {
        Category category = getCategoryByName(categoryName);
        Product product = getProductByName(productName);
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.error("Product '{}' is already in category '{}'", productName, categoryName);
            throw new IllegalArgumentException("Product is already in category");
        }
        oldProducts.add(product);
        category.setProducts(oldProducts);
        saveCategory(category);
    }

    public Category saveProductToCategory(Long productId, Long categoryId) {
        Category category = getCategoryById(categoryId);
        Product product = getProductById(productId);
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.error("Product '{}' is already in category '{}'", productId, categoryId);
            throw new IllegalArgumentException("Product is already in category");
        }
        oldProducts.add(product);
        category.setProducts(oldProducts);
        return saveCategory(category);
    }

    public void saveParentToCategory(String child, String parent) {
        Category parentCategory = getCategoryByName(parent);
        Category childCategory = getCategoryByName(child);
        childCategory.setParentId(parentCategory.getId());
        saveCategory(childCategory);
    }

    public Category saveParentToCategory(Long child, Long parent) {
        Category parentCategory = getCategoryById(parent);
        Category childCategory = getCategoryById(child);
        childCategory.setParentId(parentCategory.getId());
        return saveCategory(childCategory);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        Category category = getCategoryByName(categoryName);
        return category.getProducts();
    }

    public Set<Category> getCategoriesByProduct(Product product) {
        return categoryRepo.findAllByProductsContains(product);
    }

    public ResponseEntity<?> placeCategory(CategoryRequest categoryRequest) {
        Category category = new Category(null, categoryRequest.getName(), categoryRequest.getTitle(),
                categoryRequest.getParentId(), null);
        List<Product> products = new ArrayList<>();
        for (String productName : categoryRequest.getProducts()) {
            Product product = getProductByName(productName);
            if (product != null) products.add(product);
        }
        category.setProducts(products);
        saveCategory(category);
        return ResponseEntity.ok(new MessageResponse("Category: " + category.getName() + " created successfully"));
    }

    public ResponseEntity<?> updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category category = getCategoryById(categoryId);
        List<Product> products = new ArrayList<>();
        for (String productName : categoryRequest.getProducts()) {
            Product product = getProductByName(productName);
            if (product != null) products.add(product);
        }
        category.setName(categoryRequest.getName());
        category.setTitle(categoryRequest.getTitle());
        category.setParentId(categoryRequest.getParentId());
        category.setProducts(products);
        saveCategory(category);
        return ResponseEntity.ok(new MessageResponse("Category: " + category.getName() + " created successfully"));
    }

    public ResponseEntity<?> deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        Long parentId = category.getParentId();
        Set<Category> childCategories = categoryRepo.findAllByParentId(categoryId);
        for (Category childCategory : childCategories) {
            childCategory.setParentId(parentId);
            saveCategory(childCategory);
        }
        deleteCategory(category);
        return ResponseEntity.ok(new MessageResponse("Category: " + category.getName() + " deleted successfully"));
    }

    @Transactional
    public void saveCharacteristicValue(CharacteristicValue characteristicValue) {
        characteristicValueRepo.save(characteristicValue);
    }

    @Transactional
    public void saveCharacteristic(Characteristic characteristic) {
        characteristicRepo.save(characteristic);
    }

    @Transactional
    public void saveComment(Comment comment) {
        commentRepo.save(comment);
    }

    @Transactional
    public void deleteComment(Comment comment) {
        commentRepo.delete(comment);
    }

    @Transactional
    public void deleteComments(List<Comment> comments) {
        commentRepo.deleteAll(comments);
    }

    @Transactional
    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    @Transactional
    public void saveAllProducts(Set<Product> products) {
        productRepo.saveAll(products);
    }

    @Transactional
    public void deleteCategory(Category category) {
        categoryRepo.delete(category);
    }

    @Transactional
    public void removeProduct(Product product) {
        productRepo.delete(product);
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }
}

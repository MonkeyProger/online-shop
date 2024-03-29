package org.scenter.onlineshop.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.scenter.onlineshop.common.exception.IllegalFormatException;
import org.scenter.onlineshop.common.requests.*;
import org.scenter.onlineshop.common.responses.MessageResponse;
import org.scenter.onlineshop.domain.*;
import org.scenter.onlineshop.dto.CategoryDTO;
import org.scenter.onlineshop.dto.CharacteristicDTO;
import org.scenter.onlineshop.dto.ProductDTO;
import org.scenter.onlineshop.repo.*;
import org.scenter.onlineshop.service.mapping.CategoryMapping;
import org.scenter.onlineshop.service.mapping.CharacteristicMapping;
import org.scenter.onlineshop.service.mapping.ProductMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.scenter.onlineshop.service.mapping.CharacteristicMapping.characteristicToDTO;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockService {

    private final ProductFileRepo productFileRepo;
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
        log.info("Product '{}' found in the database", product.get().getDescription());
        return product.get();
    }

    public Map<Long, Product> getProductsByIds(List<Long> productIds) {
        List<Product> products = productRepo.findAllById(productIds);
        if (products.size() != productIds.size()) {
            Set<Long> foundIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            List<Long> notFoundIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new NoSuchElementException("Products with ids " + notFoundIds + " not found");
        }
        log.info("Found {} products in the database", products.size());
        return products
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }


    public Product getProductByName(String productName) {
        Optional<Product> product = productRepo.findByTitle(productName);
        if (!product.isPresent()) {
            log.error("Product with name " + productName + " not found");
            throw new NoSuchElementException("Product with name " + productName + "not found");
        }
        log.info("Product '{}' found in the database", productName);
        return product.get();
    }

    public ResponseEntity<?> placeProduct(PlaceProductRequest placeProductRequest,
                                          MultipartFile[] files) throws IllegalFormatException, FileUploadException {
        Product product = new Product(
                null,
                placeProductRequest.getName(),
                placeProductRequest.getTitle(),
                null,
                placeProductRequest.getPrice(),
                placeProductRequest.getSalePrice(),
                placeProductRequest.getAmount(),
                null,
                new ArrayList<>(),
                null);
        addPhotosToProduct(product, files);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Product added successfully"));
    }

    private void addPhotosToProduct(Product product, MultipartFile[] files)
            throws FileUploadException, IllegalFormatException {
        if (files[0].getContentType() != null) {
            List<FileDB> filesDB = saveFilesToDB(files);
            List<ProductFile> productFiles = Optional.ofNullable(product.getImages()).orElse(new ArrayList<>());
            filesDB.forEach(file -> productFiles.add(fileStorageService.saveProductFile(file)));
            product.setImages(productFiles);
        }
    }

    private List<FileDB> saveFilesToDB(MultipartFile[] files) throws FileUploadException, IllegalFormatException {
        if (files.length > 10) {
            throw new FileUploadException("Error: Too many files to upload.");
        }
        if (!fileStorageService.checkImages(files)) {
            throw new IllegalFormatException("Error: Wrong format of images.");
        }
        List<FileDB> filesDB;
        try {
            filesDB = fileStorageService.saveFilesDB(files);
        } catch (Exception e) {
            throw new FileUploadException("Error: Fail to upload files: " + e.getMessage());
        }
        return filesDB;
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
        product.setDescription(placeProductRequest.getName());
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
        return ResponseEntity.ok(new MessageResponse("Product: " + product.getDescription() + " deleted successfully"));
    }

    public ProductDTO placePhotoOnProduct(Long productId, MultipartFile[] files)
            throws IllegalFormatException, FileUploadException {
        Product product = getProductById(productId);
        addPhotosToProduct(product, files);
        saveProduct(product);
        return ProductMapping.convertProductToDTO(product);
    }

    public ProductDTO deletePhotoFromProduct(Long productId, Long fileId) {
        Product product = getProductById(productId);
        ProductFile productFile = productFileRepo.findById(fileId)
                .orElseThrow(() -> new NoSuchElementException("No file with id: " + fileId));

        log.info(productFile.toString());

        List<ProductFile> productFiles = product.getImages();

        log.info(productFiles.toString());

        productFiles.remove(productFile);
        saveProduct(product);
        return ProductMapping.convertProductToDTO(product);
    }

    public ResponseEntity<?> deleteFileFromStorage(String fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.ok(new MessageResponse("File deleted successfully"));
    }

    // ===================== Characteristic management ===================

    public CharacteristicDTO createCharacteristic(CharacteristicRequest characteristicRequest) {
        final Characteristic newCharact = new Characteristic();
        newCharact.setName(characteristicRequest.getName());

        List<CharacteristicValue> values = characteristicRequest.getValues().stream()
                .map(v -> {
                    CharacteristicValue cv = new CharacteristicValue();
                    cv.setCharacteristic(newCharact);
                    cv.setValue(v);
                    return cv;
                })
                .collect(Collectors.toList());

        newCharact.setValues(values);
        return characteristicToDTO(characteristicRepo.save(newCharact));
    }

    public CharacteristicDTO addValueForCharact(String newValue, Long charactId) {
        Characteristic charactToUpdate = characteristicRepo.findById(charactId).orElseThrow(
                () -> new RuntimeException("No such characteristic"));

        CharacteristicValue cv = new CharacteristicValue();
        cv.setCharacteristic(charactToUpdate);
        cv.setValue(newValue);

        List<CharacteristicValue> values = charactToUpdate.getValues();
        values.add(cv);

        return characteristicToDTO(characteristicRepo.save(charactToUpdate));
    }

    public List<CharacteristicDTO> getAllCharacteristics() {
        return characteristicRepo.findAll()
                .stream()
                .map(CharacteristicMapping::characteristicToDTO)
                .collect(Collectors.toList());
    }

    public List<CharacteristicDTO> deleteCharacteristicValue(Long valueId) {
        CharacteristicValue valueToDelete = characteristicValueRepo.findById(valueId)
                .orElseThrow(() -> new RuntimeException("No characteristic value with id: " + valueId));

        deleteCharacteristicValueFromProducts(valueToDelete);
        characteristicValueRepo.delete(valueToDelete);
        return getAllCharacteristics();
    }

    public List<CharacteristicDTO> deleteCharacteristic(Long charactId) {
        Characteristic characteristicToDelete = characteristicRepo.findById(charactId)
                .orElseThrow(() -> new RuntimeException("No characteristic value with id: " + charactId));

        characteristicToDelete.getValues()
                .forEach(this::deleteCharacteristicValueFromProducts);

        characteristicRepo.deleteById(charactId);
        return getAllCharacteristics();
    }

    private void deleteCharacteristicValueFromProducts(CharacteristicValue valueToDelete) {
        List<Product> products = productRepo.findAllByCharacteristicValuesContains(valueToDelete);
        for (Product product : products) {
            Set<CharacteristicValue> values = product.getCharacteristicValues();
            values.remove(valueToDelete);
            product.setCharacteristicValues(values);
            productRepo.save(product);
        }
    }

    public ResponseEntity<?> setCharacteristic(String productName, PlaceCharactOnProductRequest pcpr) {

        Characteristic characteristic = characteristicRepo.findByName(pcpr.getName())
                .orElseThrow(() -> new RuntimeException("No characteristic with name: " + pcpr.getName()));

        CharacteristicValue value = characteristicValueRepo.findByValue(pcpr.getValue())
                .orElseThrow(() -> new RuntimeException("No such value: " + pcpr.getValue()));

        if (!characteristic.getValues().contains(value)) {
            throw new RuntimeException("Сharacteristic" + characteristic.getName()
                    + "does not contain value" + value.getValue());
        }

        Product product = getProductByName(productName);

        Set<CharacteristicValue> productCharacteristic = product.getCharacteristicValues();
        productCharacteristic.add(value);
        product.setCharacteristicValues(productCharacteristic);
        saveProduct(product);

        return ResponseEntity.ok(new MessageResponse("Characteristic " + characteristic.getName() + " = " +
                value.getValue() + " added to product " + product.getDescription() + " successfully"));
    }

    public ResponseEntity<?> deleteCharacteristicFromProduct(String productName, String characteristicValue) {
        CharacteristicValue value = characteristicValueRepo.findByValue(characteristicValue)
                .orElseThrow(() -> new RuntimeException("No such value: " + characteristicValue));

        Product product = getProductByName(productName);

        Set<CharacteristicValue> productCharacteristic = product.getCharacteristicValues();
        productCharacteristic.remove(value);
        product.setCharacteristicValues(productCharacteristic);
        saveProduct(product);

        return ResponseEntity.ok(new MessageResponse("Characteristic " + value.getCharacteristic().getName() + " = " +
                value.getValue() + " deleted from product " + product.getDescription() + " successfully"));
    }

    // ====================== Comment management =========================
    public ResponseEntity<?> postComment(CommentRequest commentRequest,
                                         String productName,
                                         MultipartFile[] files)
            throws IllegalFormatException, FileUploadException {

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
        addPhotosToComment(comment, files);

        comments.add(comment);
        product.setComments(comments);
        saveComment(comment);
        saveProduct(product);
        return ResponseEntity.ok(new MessageResponse("Comment " + comment.getId().toString() +
                " added to product " + productName + " successfully"));
    }

    private void addPhotosToComment(Comment comment, MultipartFile[] files)
            throws IllegalFormatException, FileUploadException {
        if (files[0].getContentType() != null) {
            List<FileDB> filesDB = saveFilesToDB(files);
            List<ResponseFile> commentFiles = Optional.ofNullable(comment.getImages()).orElse(new ArrayList<>());
            filesDB.forEach(file -> commentFiles.add(fileStorageService.saveResponsefile(file)));
            comment.setImages(commentFiles);
        }
    }

    public ResponseEntity<?> getFile(String id) {
        FileDB fileDB = fileStorageService.getFile(id);
        if (fileDB == null) {
            throw new NoSuchElementException("File with id " + id + " is not presented");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }

    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepo.findById(commentId);
        if (!comment.isPresent()) {
            log.error("Comment with id " + commentId + " is not presented");
            throw new NoSuchElementException("Comment with id " + commentId + " is not presented");
        }
        return comment.get();
    }

    public ResponseEntity<?> deleteComment(Long commentId, String productName) throws AccessException {
        Comment repoComment = getCommentById(commentId);

        if (SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .noneMatch(a -> a.getAuthority().equals("ADMIN"))) {

            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!repoComment.getUserEmail().equals(userEmail)) {
                throw new AccessException("Access denied: To delete the comment, you need to be under your account");
            }
        }

        Product product = getProductByName(productName);
        List<Comment> comments = product.getComments();
        if (!comments.contains(repoComment)) {
            log.error("Comment is not presented in product: " + productName);
            throw new NoSuchElementException("Comment is not presented in product: " + productName);
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
        Set<Category> oldCategories = product.getCategories();
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.error("Product '{}' is already in category '{}'", productName, categoryName);
            throw new IllegalArgumentException("Product is already in category");
        }
        if (oldCategories.contains(category)) {
            log.error("Category '{}' is already in product '{}'", categoryName, productName);
            throw new IllegalArgumentException("Category is already in Product");
        }
        oldCategories.add(category);
        oldProducts.add(product);
        category.setProducts(oldProducts);
        product.setCategories(oldCategories);
        saveCategory(category);
        saveProduct(product);
    }

    public CategoryDTO saveProductToCategoryByIds(Long productId, Long categoryId) {
        Category category = getCategoryById(categoryId);
        Product product = getProductById(productId);
        Set<Category> oldCategories = product.getCategories();
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.error("Product '{}' is already in category '{}'", productId, categoryId);
            throw new IllegalArgumentException("Product is already in category");
        }
        if (oldCategories.contains(category)) {
            log.error("Category '{}' is already in product '{}'", categoryId, productId);
            throw new IllegalArgumentException("Category is already in Product");
        }
        oldCategories.add(category);
        oldProducts.add(product);
        category.setProducts(oldProducts);
        product.setCategories(oldCategories);
        saveCategory(category);
        return CategoryMapping.convertCategoryToDTO(saveCategory(category));
    }

    public CategoryDTO deleteProductFromCategoryByIds(Long productId, Long categoryId) {
        Category category = getCategoryById(categoryId);
        Product product = getProductById(productId);
        Set<Category> oldCategories = product.getCategories();
        List<Product> oldProducts = category.getProducts();
        if (!oldProducts.contains(product)) {
            log.error("Product '{}' is not in category '{}'", productId, categoryId);
            throw new IllegalArgumentException("Product is not presented in category");
        }
        if (!oldCategories.contains(category)) {
            log.error("Category '{}' is not in product '{}'", categoryId, productId);
            throw new IllegalArgumentException("Category is not presented in product");
        }
        oldCategories.remove(category);
        oldProducts.remove(product);
        category.setProducts(oldProducts);
        product.setCategories(oldCategories);
        saveProduct(product);
        return CategoryMapping.convertCategoryToDTO(saveCategory(category));
    }

    public void saveParentToCategory(String child, String parent) {
        Category parentCategory = getCategoryByName(parent);
        Category childCategory = getCategoryByName(child);
        childCategory.setParentId(parentCategory.getId());
        saveCategory(childCategory);
    }

    public CategoryDTO saveParentToCategory(Long child, Long parent) {
        Category parentCategory = getCategoryById(parent);
        Category childCategory = getCategoryById(child);
        childCategory.setParentId(parentCategory.getId());
        return CategoryMapping.convertCategoryToDTO(saveCategory(childCategory));
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
        deepDeleteCategory(category);
        return ResponseEntity.ok(new MessageResponse("Category: " + category.getName() + " deleted successfully"));
    }

    public void deepDeleteCategory(Category category) {
        Long categoryId = category.getId();
        Set<Category> childCategories = categoryRepo.findAllByParentId(categoryId);
        for (Category childCategory : childCategories) {
            deepDeleteCategory(childCategory);
        }

        List<Product> products = category.getProducts();
        products.forEach(
                product -> {
                    Set<Category> categories = product.getCategories();
                    categories.remove(category);
                    product.setCategories(categories);
                }
        );
        productRepo.saveAll(products);

        deleteCategory(category);
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
    public void saveProduct(Product product) {
        productRepo.save(product);
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

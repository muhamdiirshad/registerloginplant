package com.example.registerloginplant.service;


import com.example.registerloginplant.entity.Product;
import com.example.registerloginplant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    public void updateProduct(Product existingProduct) {
        productRepository.save(existingProduct);
    }

    public String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String fileName = UUID.randomUUID().toString() + "-" + StringUtils.cleanPath(imageFile.getOriginalFilename());
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try {
            Path destinationFile = uploadPath.resolve(fileName).normalize();
            Files.copy(imageFile.getInputStream(), destinationFile);
            return "/images/" + fileName; // Path relative to static directory
        } catch (IOException ex) {
            throw new IOException("Failed to save image file: " + fileName, ex);
        }
    }
}
package com.hcl.inventory.service;

import com.hcl.inventory.model.Category;
import com.hcl.inventory.model.Product;
import com.hcl.inventory.repository.CategoryRepository;
import com.hcl.inventory.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product getProductById(Long id){
        return productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found with Id : " + id ));
    }
    public Product createProduct(Product product){
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("A product with SKU '" + product.getSku() + "' already exists");
        }
        return productRepository.save(product);
    }
    public Product updateProduct(Long id,Product updatedProduct){
        Product existing = getProductById(id);

        if (!existing.getSku().equals(updatedProduct.getSku())
                && productRepository.existsBySku(updatedProduct.getSku())) {
            throw new IllegalArgumentException("A product with SKU '" + updatedProduct.getSku() + "' already exists");
        }

        existing.setName(updatedProduct.getName());
        existing.setSku(updatedProduct.getSku());
        existing.setPrice(updatedProduct.getPrice());
        existing.setReorderLevel(updatedProduct.getReorderLevel());
        existing.setCategoryId(updatedProduct.getCategoryId());

        return productRepository.save(existing);

    }
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId is required");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));
    }
}

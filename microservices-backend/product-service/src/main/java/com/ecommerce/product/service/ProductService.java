package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDtos.*;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public PagedResponse<ProductSummary> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return toPagedResponse(productRepository.findByActiveTrue(pageable));
    }

    public PagedResponse<ProductSummary> findByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return toPagedResponse(productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable));
    }

    public PagedResponse<ProductSummary> search(
            String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return toPagedResponse(productRepository.search(keyword, categoryId, minPrice, maxPrice, pageable));
    }

    public ProductResponse findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));
        Product product = mapper.toEntity(request);
        product.setCategory(category);
        return mapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.categoryId()));
        mapper.updateEntity(request, product);
        product.setCategory(category);
        return mapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product getOrThrow(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private PagedResponse<ProductSummary> toPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
            page.getContent().stream().map(mapper::toSummary).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}

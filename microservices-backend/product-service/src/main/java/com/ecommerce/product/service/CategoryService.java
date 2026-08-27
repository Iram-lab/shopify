package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDtos.*;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
            .map(mapper::toDto)
            .toList();
    }

    public CategoryResponse findById(Long id) {
        return mapper.toDto(getOrThrow(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category already exists: " + request.name());
        }
        Category category = mapper.toEntity(request);
        return mapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOrThrow(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return mapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.delete(getOrThrow(id));
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }
}

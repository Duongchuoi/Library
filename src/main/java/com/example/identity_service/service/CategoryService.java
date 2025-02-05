package com.example.identity_service.service;

import com.example.identity_service.dto.request.CategoryRequest;
import com.example.identity_service.dto.request.PermissionRequest;
import com.example.identity_service.dto.response.CategoryResponse;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Category;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.mapper.CategoryMapper;
import com.example.identity_service.mapper.PermissionMapper;
import com.example.identity_service.respository.CategoryRepository;
import com.example.identity_service.respository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAll() {
        var categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public void delete(String category) {
        categoryRepository.deleteById(category);
    }
}

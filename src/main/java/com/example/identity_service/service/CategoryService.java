package com.example.identity_service.service;

import com.example.identity_service.dto.request.CategoryRequest;
import com.example.identity_service.dto.request.PermissionRequest;
import com.example.identity_service.dto.response.CategoryResponse;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Category;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.CategoryMapper;
import com.example.identity_service.mapper.PermissionMapper;
import com.example.identity_service.respository.CategoryRepository;
import com.example.identity_service.respository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
        log.info("Creating category: {}", request.getName());

        // Kiểm tra xem danh mục đã tồn tại chưa
        if (categoryRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        try {
            Category category = categoryMapper.toCategory(request);
            category = categoryRepository.save(category);
            return categoryMapper.toCategoryResponse(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating category: {}", e.getMessage());
            throw new AppException(ErrorCode.CATEGORY_CREATION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error while creating category: {}", e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<CategoryResponse> getAll() {
        try {
            var categories = categoryRepository.findAll();
            return categories.stream()
                    .map(categoryMapper::toCategoryResponse)
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
            throw new AppException(ErrorCode.GET_CATEGORY_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(String categoryName, CategoryRequest request) {
        log.info("Updating category: {}", categoryName);
        try {
            Category category = categoryRepository.findById(categoryName)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            // Cập nhật thông tin từ request
            category.setDescription(request.getDescription()); // Cập nhật mô tả
            category = categoryRepository.save(category);

            return categoryMapper.toCategoryResponse(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating category: {}", e.getMessage());
            throw new AppException(ErrorCode.CATEGORY_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error while updating category: {}", e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String categoryName) {
        log.info("Deleting category: {}", categoryName);

        if (!categoryRepository.existsById(categoryName)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        try {
            categoryRepository.deleteById(categoryName);
        } catch (DataIntegrityViolationException e) {
            log.error("Error deleting category: {}", e.getMessage());
            throw new AppException(ErrorCode.CATEGORY_DELETION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error while deleting category: {}", e.getMessage());
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

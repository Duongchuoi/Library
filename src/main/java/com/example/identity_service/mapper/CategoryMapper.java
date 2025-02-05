package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.CategoryRequest;
import com.example.identity_service.dto.request.PermissionRequest;
import com.example.identity_service.dto.response.CategoryResponse;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Category;
import com.example.identity_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);
}

package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.CategoryRequest;
import com.example.identity_service.dto.request.PermissionRequest;
import com.example.identity_service.dto.response.CategoryResponse;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.service.CategoryService;
import com.example.identity_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    ApiResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .Result(categoryService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .Result(categoryService.getAll())
                .build();
    }

    @DeleteMapping("/{category}")
    ApiResponse<Void> delete(@PathVariable("category") String permission) {
        categoryService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}

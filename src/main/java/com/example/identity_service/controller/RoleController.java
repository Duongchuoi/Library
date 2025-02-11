package com.example.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.RoleRequest;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    private final RestClient.Builder builder;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .Result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .Result(roleService.getAll())
                .build();
    }

    @PutMapping("/{roleName}")
    ApiResponse<RoleResponse> update(
            @PathVariable("roleName") String roleName,
            @RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .Result(roleService.updateRole(roleName, request))
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable("permission") String permission) {
        roleService.delete(permission);
        return ApiResponse.<Void>builder().build();
    }
}

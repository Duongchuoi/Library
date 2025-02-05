package com.example.identity_service.controller;

import java.util.List;
import java.util.Map;

import com.example.identity_service.entity.User;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .Result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .Result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().Result("User has been deleted").build();
    }

    @GetMapping("/me")
    ResponseEntity<User> getCurrentUser() {
        // Delegate the logic to the UserService
        return ResponseEntity.ok(userService.getCurrentUser());
    }


    @PostMapping("/{userId}/revoke-permission")
    ApiResponse<String> revokePermission(@PathVariable String userId, @RequestBody Map<String, String> requestBody) {
        String permissionName = requestBody.get("permissionName");
        userService.revokePermission(userId, permissionName);
        return ApiResponse.<String>builder()
                .Result("Permission revoked successfully")
                .build();
    }

    @PostMapping("/{userId}/restore-permission")
    ApiResponse<String> restorePermission(@PathVariable String userId, @RequestBody Map<String, String> requestBody) {
        String permissionName = requestBody.get("permissionName");
        userService.restorePermission(userId, permissionName);
        return ApiResponse.<String>builder()
                .Result("Permission restored successfully")
                .build();
    }

}

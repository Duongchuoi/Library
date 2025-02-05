package com.example.identity_service.mapper;

import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.User;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    default Set<RoleResponse> mapRoles(User user, Set<Role> roles) {
        if (roles == null) return Collections.emptySet();

        return roles.stream()
                .map(role -> mapRole(user, role))
                .collect(Collectors.toSet());
    }

    default RoleResponse mapRole(User user, Role role) {
        if (role == null) return null;

        // Lấy danh sách permissions hợp lệ (không bị revoked)
        Set<PermissionResponse> activePermissions = role.getPermissions().stream()
                .map(Permission::getName)
                .filter(permission -> !isPermissionRevoked(user, permission))
                .map(PermissionResponse::new)
                .collect(Collectors.toSet());

        return RoleResponse.builder()
                .name(role.getName())
                .description(role.getDescription())
                .permissions(activePermissions)
                .build();
    }

    default boolean isPermissionRevoked(User user, String permissionName) {
        return user.getRevokedPermissions().stream()
                .anyMatch(rp -> rp.getPermissionName().equals(permissionName));
    }

    default UserResponse toUserResponseWithPermissions(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .dob(user.getDob())
                .roles(mapRoles(user, user.getRoles())) // Gọi hàm mapRoles để loại bỏ permissions bị revoked
                .build();
    }
}

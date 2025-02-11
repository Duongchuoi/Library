package com.example.identity_service.service;

import java.util.HashSet;
import java.util.List;

import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.identity_service.dto.request.RoleRequest;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.mapper.RoleMapper;
import com.example.identity_service.respository.PermissionRepository;
import com.example.identity_service.respository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }
        try {
            var role = roleMapper.toRole(request);

            // Lấy permissions từ request và set vào role
            var permissions = permissionRepository.findAllById(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));

            // Lưu role vào cơ sở dữ liệu
            role = roleRepository.save(role);

            return roleMapper.toRoleResponse(role);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROLE_CREATION_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        try {
            return roleRepository.findAll().stream()
                    .map(roleMapper::toRoleResponse)
                    .toList();
        } catch (Exception e) {
            throw new AppException(ErrorCode.GET_ROLE_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(String roleName, RoleRequest request) {
        try {
            // Kiểm tra role có tồn tại không
            var role = roleRepository.findById(roleName)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

            // Cập nhật thông tin role
            role.setName(request.getName());
            role.setDescription(request.getDescription());

            // Cập nhật permissions nếu có trong request
            if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
                var permissions = permissionRepository.findAllById(request.getPermissions());
                role.setPermissions(new HashSet<>(permissions));
            }

            // Lưu lại role đã cập nhật
            role = roleRepository.save(role);
            return roleMapper.toRoleResponse(role);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROLE_UPDATE_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String roleId) {
        try {
            roleRepository.deleteById(roleId);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}

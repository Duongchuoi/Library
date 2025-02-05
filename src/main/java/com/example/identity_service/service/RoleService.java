package com.example.identity_service.service;

import java.util.HashSet;
import java.util.List;

import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
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

    public RoleResponse create(RoleRequest request) {
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

    public List<RoleResponse> getAll() {
        try {
            return roleRepository.findAll().stream()
                    .map(roleMapper::toRoleResponse)
                    .toList();
        } catch (Exception e) {
            throw new AppException(ErrorCode.GET_ROLE_FAILED);
        }
    }

    public void delete(String roleId) {
        try {
            roleRepository.deleteById(roleId);
        } catch (Exception e) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
    }
}

package com.example.identity_service.service;

import java.util.List;

import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.identity_service.dto.request.PermissionRequest;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.mapper.PermissionMapper;
import com.example.identity_service.respository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        log.info("Creating permission: {}", request.getName());

        // Kiểm tra xem quyền đã tồn tại chưa
        if (permissionRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }

        try {
            Permission permission = permissionMapper.toPermission(request);
            permission = permissionRepository.save(permission);
            return permissionMapper.toPermissionResponse(permission);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.PERMISSION_CREATION_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permissionName) {
        log.info("Deleting permission: {}", permissionName);

        if (!permissionRepository.existsById(permissionName)) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        try {
            permissionRepository.deleteById(permissionName);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PERMISSION_DELETION_FAILED);
        }
    }
}

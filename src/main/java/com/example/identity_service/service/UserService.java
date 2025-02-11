package com.example.identity_service.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.example.identity_service.dto.request.BookRequest;
import com.example.identity_service.entity.*;
import com.example.identity_service.respository.RevokedPermissionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.identity_service.constant.PredefinedRole;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.respository.RoleRepository;
import com.example.identity_service.respository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RevokedPermissionRepository revokedPermissionRepository;

    public UserResponse createUser(UserCreationRequest request) {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try{
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.USER_EXISTED);
        }


        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
//    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<UserResponse> getUsers() {

        return userRepository.findAll().stream().map(userMapper::toUserResponseWithPermissions).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponseWithPermissions(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse updateUser(String userID, UserUpdateRequest request) {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userID) {
        userRepository.deleteById(userID);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponseWithPermissions(user);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Thu hồi quyền của user
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void revokePermission(String userId, String permissionName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRevokedPermissionNames().contains(permissionName)) {
            throw new RuntimeException("Permission already revoked");
        }

        RevokedPermission revokedPermission = new RevokedPermission();
        revokedPermission.setUser(user);
        revokedPermission.setPermissionName(permissionName);
        revokedPermissionRepository.save(revokedPermission);
    }

    /**
     * Khôi phục quyền đã thu hồi
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void restorePermission(String userId, String permissionName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        revokedPermissionRepository.deleteByUserAndPermissionName(user, permissionName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User saveAdUser(UserCreationRequest request) {
        try {
            // Xử lý danh mục role
            Set<Role> roles = request.getRoles().stream()
                    .map(name -> roleRepository.findById(name)
                            .orElseGet(() -> roleRepository.save(
                                    Role.builder().name(name).description("").build()
                            )))
                    .collect(Collectors.toSet());

            User user = userMapper.toUser(request);
            user.setRoles(roles);

            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public byte[] exportUsersToExcel() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new AppException(ErrorCode.NO_USERS_FOUND);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Tạo hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Username", "Full Name", "Email", "Phone", "DOB", "Roles"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Ghi dữ liệu người dùng
            int rowIndex = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getFullname());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getPhone());
                row.createCell(5).setCellValue(user.getDob() != null ? user.getDob().toString() : "");
                row.createCell(6).setCellValue(user.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Lỗi khi tạo file Excel", e);
            throw new AppException(ErrorCode.EXPORT_EXCEL_FAILED);
        }
    }
}

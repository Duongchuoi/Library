package com.example.identity_service.respository;

import com.example.identity_service.entity.RevokedPermission;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedPermissionRepository extends JpaRepository<RevokedPermission, String> {
    void deleteByUserAndPermissionName(User user, String permissionName);

    //chua dung
    boolean existsByUserAndPermissionName(User user, String permissionName);
}

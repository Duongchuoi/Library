package com.example.identity_service.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identity_service.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {}

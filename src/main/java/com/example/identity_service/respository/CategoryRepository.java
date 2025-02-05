package com.example.identity_service.respository;

import com.example.identity_service.entity.Category;
import com.example.identity_service.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {}

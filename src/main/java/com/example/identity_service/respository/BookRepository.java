package com.example.identity_service.respository;

import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {}

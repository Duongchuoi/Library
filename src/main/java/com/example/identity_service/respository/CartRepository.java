package com.example.identity_service.respository;

import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    List<Cart> findByUserId(String userId);
    List<Cart> findByBookId(String bookId);
    void deleteByUserId(String userId);
}

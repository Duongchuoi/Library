package com.example.identity_service.respository;

import com.example.identity_service.entity.Cart;
import com.example.identity_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findByBookId(String bookId);
}

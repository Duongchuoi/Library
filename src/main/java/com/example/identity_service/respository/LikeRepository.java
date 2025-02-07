package com.example.identity_service.respository;

import com.example.identity_service.entity.Comment;
import com.example.identity_service.entity.Like;
import com.example.identity_service.entity.Post;
import com.example.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    Optional<Like> findByPostAndUser(Post post, User user);
    Optional<Like> findByCommentAndUser(Comment comment, User user);
    void deleteByPostAndUser(Post post, User user);
    void deleteByCommentAndUser(Comment comment, User user);
}

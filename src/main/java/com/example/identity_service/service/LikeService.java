package com.example.identity_service.service;

import com.example.identity_service.entity.Comment;
import com.example.identity_service.entity.Like;
import com.example.identity_service.entity.Post;
import com.example.identity_service.entity.User;
import com.example.identity_service.enums.LikeType;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.respository.CommentRepository;
import com.example.identity_service.respository.LikeRepository;
import com.example.identity_service.respository.PostRepository;
import com.example.identity_service.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public String toggleLike(String type, String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (type.equalsIgnoreCase("post")) {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

            Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

            if (existingLike.isPresent()) {
                likeRepository.deleteByPostAndUser(post, user);
                post.setLikeCount(post.getLikeCount() - 1); // Giảm số like
                postRepository.save(post); // Lưu lại thay đổi
                return "Unlike bài viết thành công!";
            } else {
                Like like = Like.builder().type(LikeType.LIKE_POST).post(post).user(user).build();
                likeRepository.save(like);
                post.setLikeCount(post.getLikeCount() + 1); // Tăng số like
                postRepository.save(post); // Lưu lại thay đổi
                return "Like bài viết thành công!";
            }

        } else if (type.equalsIgnoreCase("comment")) {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

            Optional<Like> existingLike = likeRepository.findByCommentAndUser(comment, user);

            if (existingLike.isPresent()) {
                likeRepository.deleteByCommentAndUser(comment, user);
                comment.setLikeCount(comment.getLikeCount() - 1); // Giảm số like
                commentRepository.save(comment); // Lưu lại thay đổi
                return "Unlike bình luận thành công!";
            } else {
                Like like = Like.builder().type(LikeType.LIKE_COMMENT).comment(comment).user(user).build();
                likeRepository.save(like);
                comment.setLikeCount(comment.getLikeCount() + 1); // Tăng số like
                commentRepository.save(comment); // Lưu lại thay đổi
                return "Like bình luận thành công!";
            }
        } else {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
    }
}


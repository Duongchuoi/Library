package com.example.identity_service.service;

import com.example.identity_service.entity.Comment;
import com.example.identity_service.entity.Post;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.respository.BookRepository;
import com.example.identity_service.respository.CommentRepository;
import com.example.identity_service.respository.PostRepository;
import com.example.identity_service.respository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    PostRepository postRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;


    @PreAuthorize("hasAuthority('USER_APPROVECOMMENT')")
    public Comment addComment(String postId, Comment comment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Lấy user từ token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy post từ ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Thiết lập thông tin comment
        comment.setPost(post);
        comment.setUser(user);

        try {
            return commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.COMMENT_SAVE_FAILED);
        }
    }

    public List<Comment> getAllCommentsByPost(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
        return commentRepository.findByPostId(postId);
    }

    @PreAuthorize("hasAuthority('USER_APPROVECOMMENT')")
    public Comment updateComment(String commentId, Comment updatedComment) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Chỉ cho phép người đăng bình luận chỉnh sửa
        if (!comment.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        try {
            comment.setContent(updatedComment.getContent());
            return commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.COMMENT_UPDATE_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('USER_APPROVECOMMENT') or hasAuthority('mod_deletecomment')")
    public void deleteComment(String commentId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // Chỉ cho phép người đăng hoặc mod xóa bình luận
        if (!comment.getUser().getUsername().equals(username) &&
                !SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("mod_deletecomment"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        try {
            commentRepository.delete(comment);
        } catch (Exception e) {
            throw new AppException(ErrorCode.COMMENT_DELETION_FAILED);
        }
    }

}

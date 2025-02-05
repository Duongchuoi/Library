package com.example.identity_service.service;

import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Comment;
import com.example.identity_service.entity.Post;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.respository.BookRepository;
import com.example.identity_service.respository.PostRepository;
import com.example.identity_service.respository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    BookRepository bookRepository;
    UserRepository userRepository;

    public List<Post> getAllReviewsByBook(String bookId) {
        return postRepository.findByBookId(bookId);
    }

    public Post getPostById(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('USER_APPROVEPOST')")
    public Post createPost(String bookId, Post post) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        try {
            post.setBook(book);
            post.setUser(user);
            return postRepository.save(post);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.POST_CREATION_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('USER_APPROVEPOST')")
    public Post updatePost(String postId, Post updatedPost) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Chỉ cho phép người đăng bài mới có thể chỉnh sửa
        if (!post.getUser().getUsername().equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        try {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            return postRepository.save(post);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.POST_UPDATE_FAILED);
        }
    }

    @PreAuthorize("hasAuthority('USER_APPROVEPOST') or hasAuthority('MOD_DELETEPOST')")
    public void deletePost(String postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Chỉ cho phép người đăng hoặc người có quyền xóa
        if (!post.getUser().getUsername().equals(username) &&
                !SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("MOD_DELETEPOST"))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        try {
            postRepository.delete(post);
        } catch (Exception e) {
            throw new AppException(ErrorCode.POST_DELETION_FAILED);
        }
    }

}

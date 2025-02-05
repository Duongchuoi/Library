package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.entity.Post;
import com.example.identity_service.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    private PostService postService;

    @GetMapping("/{bookId}")
    public ApiResponse<List<Post>> getAllReviews(@PathVariable String bookId) {
        return ApiResponse.<List<Post>>builder()
                .Result(postService.getAllReviewsByBook(bookId))
                .build();
    }

    @GetMapping("/postDetail/{postId}")
    public ApiResponse<Post> getPost(@PathVariable String postId) {
        return ApiResponse.<Post>builder()
                .Result(postService.getPostById(postId))
                .build();
    }

    @PostMapping("/{bookId}")
    public ApiResponse<Post> createPost(@PathVariable String bookId, @RequestBody Post post) {
        return ApiResponse.<Post>builder()
                .Result(postService.createPost(bookId, post))
                .build();
    }

    @PutMapping("/{postId}")
    public ApiResponse<Post> updatePost(@PathVariable String postId, @RequestBody Post updatedPost) {
        return ApiResponse.<Post>builder()
                .Result(postService.updatePost(postId, updatedPost))
                .build();
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ApiResponse.<Void>builder().build();
    }
}

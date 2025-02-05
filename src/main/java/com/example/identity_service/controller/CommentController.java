package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.entity.Comment;
import com.example.identity_service.entity.Post;
import com.example.identity_service.service.CommentService;
import com.example.identity_service.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    private CommentService commentService;

    @PostMapping("/{postId}")
    public ApiResponse<Comment> addComment(@PathVariable String postId, @RequestBody Comment comment) {
        return ApiResponse.<Comment>builder()
                .Result(commentService.addComment(postId, comment))
                .build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<List<Comment>> getAllComments(@PathVariable String postId) {
        return ApiResponse.<List<Comment>>builder()
                .Result(commentService.getAllCommentsByPost(postId))
                .build();
    }

    @PutMapping("/{commentId}")
    public ApiResponse<Comment> updateComment(@PathVariable String commentId, @RequestBody Comment updatedComment) {
        return ApiResponse.<Comment>builder()
                .Result(commentService.updateComment(commentId, updatedComment))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.<Void>builder().build();
    }
}

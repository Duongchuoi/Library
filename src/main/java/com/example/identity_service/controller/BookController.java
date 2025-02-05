package com.example.identity_service.controller;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.BookResponse;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Category;
import com.example.identity_service.service.BookService;
import com.example.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    BookService bookService;


    @GetMapping("/{bookId}")
    ApiResponse<BookResponse> getBook(@PathVariable("bookId") String bookId) {
        log.info("Get Book by id: {}", bookId);
        return ApiResponse.<BookResponse>builder()
                .Result(bookService.getBook(bookId))
                .build();
    }

    @PutMapping(value = "/{bookId}")
    ApiResponse<Book> updateBook(@PathVariable("bookId") String bookId,
                                 @ModelAttribute BookUpdateRequest request) throws IOException {
        log.info("Update Book by id: {}", bookId);
        Book updatedBook = bookService.updateBook(bookId, request);
        return ApiResponse.<Book>builder()
                .Result(updatedBook)
                .build();
    }

    @DeleteMapping("/{bookId}")
    ApiResponse<String> deleteBook(@PathVariable String bookId) {
        bookService.deleteBook(bookId);
        return ApiResponse.<String>builder()
                .Result("Book has been deleted successfully")
                .build();
    }

    @PostMapping(value = "/save", consumes = "multipart/form-data")
    ApiResponse<Book> addBook(@ModelAttribute @Valid BookRequest request) throws IOException {
        Book savedBook = bookService.saveBook(request);
        return ApiResponse.<Book>builder()
                .Result(savedBook)
                .build();
    }

    @GetMapping()
    ApiResponse<List<Book>> getBooks(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String search) {
        List<Book> books = bookService.searchBooks(search, categoryName);
        return ApiResponse.<List<Book>>builder()
                .Result(books)
                .build();
    }
}

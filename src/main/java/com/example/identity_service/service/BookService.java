package com.example.identity_service.service;

import com.example.identity_service.dto.request.BookRequest;
import com.example.identity_service.dto.request.BookUpdateRequest;
import com.example.identity_service.dto.response.BookResponse;
import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Category;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.BookMapper;
import com.example.identity_service.respository.BookRepository;
import com.example.identity_service.respository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {
    BookRepository bookRepository;
    CategoryRepository categoryRepository;
    BookMapper bookMapper;

    public List<Book> getAllBooks() {

        return bookRepository.findAll();
    }

    public BookResponse getBook(String bookId) {
        return bookMapper.toBookResponse(
                bookRepository.findById(bookId)
                        .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book updateBook(String bookId, BookUpdateRequest request) {
        log.info("Updating book with ID: {}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        try {
            // Nếu có ảnh, xử lý và cập nhật
            String imageUrl = Optional.ofNullable(request.getImage())
                    .filter(image -> !image.isEmpty())
                    .map(image -> {
                        try {
                            return Base64.getEncoder().encodeToString(image.getBytes());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.ERROR_PROCESSING_IMAGE);
                        }
                    }).orElse(book.getImage());

            bookMapper.updateBook(book, request);
            book.setImage(imageUrl);

            return bookRepository.save(book);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.BOOK_UPDATE_FAILED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(String bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        }
        bookRepository.deleteById(bookId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Book saveBook(BookRequest request) {
        try {
            // Xử lý ảnh nếu có
            String imageUrl = Optional.ofNullable(request.getImage())
                    .filter(image -> !image.isEmpty())
                    .map(image -> {
                        try {
                            return Base64.getEncoder().encodeToString(image.getBytes());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.ERROR_PROCESSING_IMAGE);
                        }
                    }).orElse(null);

            // Xử lý danh mục sách
            Set<Category> categories = request.getCategories().stream()
                    .map(name -> categoryRepository.findById(name)
                            .orElseGet(() -> categoryRepository.save(
                                    Category.builder().name(name).description("").build()
                            )))
                    .collect(Collectors.toSet());

            // Tạo đối tượng Book
            Book book = bookMapper.toBook(request);
            book.setImage(imageUrl);
            book.setCategories(categories);

            return bookRepository.save(book);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.BOOK_ALREADY_EXISTS);
        }
    }

    public List<Book> searchBooks(String searchTerm, String categoryName) {
        return bookRepository.findAll().stream()
                .filter(book -> {
                    boolean matchesTitle = Optional.ofNullable(searchTerm)
                            .map(term -> book.getTitle().toLowerCase().contains(term.toLowerCase()))
                            .orElse(true);

                    boolean matchesCategory = Optional.ofNullable(categoryName)
                            .map(name -> book.getCategories().stream()
                                    .anyMatch(category -> category.getName().toLowerCase().contains(name.toLowerCase())))
                            .orElse(true);

                    return matchesTitle && matchesCategory;
                })
                .collect(Collectors.toList());
    }

}

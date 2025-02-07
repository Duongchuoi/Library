package com.example.identity_service.service;

import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Cart;
import com.example.identity_service.entity.Rental;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.respository.BookRepository;
import com.example.identity_service.respository.CartRepository;
import com.example.identity_service.respository.RentalRepository;
import com.example.identity_service.respository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalService {
    UserRepository userRepository;
    BookRepository bookRepository;
    CartRepository cartRepository;
    RentalRepository rentalRepository;

    @Transactional
    public void rentAllBooksInCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Cart> cartItems = cartRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        List<Rental> rentals = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            Book book = cartItem.getBook();

            // Kiểm tra sách còn đủ để thuê không
            if (book.getStock_quantity() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.BOOK_NOT_ENOUGH_STOCK);
            }

            // Giảm số lượng sách trong kho
            book.setStock_quantity(book.getStock_quantity() - cartItem.getQuantity());
            bookRepository.save(book);

            // Lưu thông tin thuê sách
            Rental rental = Rental.builder()
                    .user(user)
                    .book(book)
                    .quantity(cartItem.getQuantity())
                    .rentalDate(LocalDate.now())
                    .dueDate(LocalDate.now().plusDays(14)) // 14 ngày thuê
                    .build();
            rentals.add(rental);
        }

        // Lưu danh sách thuê
        rentalRepository.saveAll(rentals);

        // Xóa giỏ hàng sau khi thuê
        cartRepository.deleteByUserId(user.getId());
    }

    public List<Rental> getAllRentals() {
        try {
            return rentalRepository.findAll();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Rental> getCurrentUserRentals() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            return rentalRepository.findByUserId(user.getId());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, List<Map<String, Object>>> getMostRentedBooksByCategory() {
        List<Object[]> results = rentalRepository.findMostRentedBooksByCategory();
        Map<String, List<Map<String, Object>>> categorizedBooks = new LinkedHashMap<>();

        for (Object[] row : results) {
            String categoryName = (String) row[0];
            Book book = (Book) row[1];
            Long totalRented = (Long) row[2];

            // Định dạng dữ liệu sách
            Map<String, Object> bookData = new HashMap<>();
            bookData.put("bookId", book.getId());
            bookData.put("title", book.getTitle());
            bookData.put("totalRented", totalRented);

            // Nhóm sách theo thể loại
            categorizedBooks.putIfAbsent(categoryName, new ArrayList<>());
            categorizedBooks.get(categoryName).add(bookData);
        }

        return categorizedBooks;
    }
}

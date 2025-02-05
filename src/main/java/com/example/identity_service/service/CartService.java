package com.example.identity_service.service;

import com.example.identity_service.dto.request.CartRequest;
import com.example.identity_service.dto.response.CartResponse;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.Cart;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.CartMapper;
import com.example.identity_service.respository.BookRepository;
import com.example.identity_service.respository.CartRepository;
import com.example.identity_service.respository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    UserRepository userRepository;
    CartRepository cartRepository;
    CartMapper cartMapper;
    private final BookRepository bookRepository;

    public Cart updateQuantity(String cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }

        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public List<CartResponse> getCartItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Cart> carts = cartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getId().equals(user.getId()))
                .toList();

        return carts.stream()
                .map(cart -> CartResponse.builder()
                        .id(cart.getId())
                        .title(cart.getBook().getTitle())
                        .author(cart.getBook().getAuthor())
                        .image(cart.getBook().getImage())
                        .quantity(cart.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    public Cart addToCart(String bookId) {
        log.info("request: {}", bookId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Cart> userCarts = cartRepository.findAll();
        boolean isBookInCart = userCarts.stream()
                .anyMatch(cart -> cart.getUser().equals(user) && cart.getBook().getId().equals(bookId));

        if (isBookInCart) {
            throw new AppException(ErrorCode.BOOK_ALREADY_IN_CART);
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setBook(book);
        cart.setQuantity(1);
        log.info("add to cart: {}", cart);

        try {
            return cartRepository.save(cart);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CART_ITEM_EXISTED);
        }
    }

    public void removeFromCart(String id) {
        if (!cartRepository.existsById(id)) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }
        cartRepository.deleteById(id);
    }

}

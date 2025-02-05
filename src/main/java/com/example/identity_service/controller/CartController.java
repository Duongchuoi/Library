package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.CartRequest;
import com.example.identity_service.dto.response.CartResponse;
import com.example.identity_service.entity.Cart;
import com.example.identity_service.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping
    public ApiResponse<List<CartResponse>> getCartItems() {
        List<CartResponse> cartItems = cartService.getCartItems();
        return ApiResponse.<List<CartResponse>>builder()
                .Result(cartItems)
                .build();
    }

    @PostMapping
    public ApiResponse<Cart> addToCart(@RequestBody @Valid String bookId) {
        Cart cart = cartService.addToCart(bookId);
        return ApiResponse.<Cart>builder()
                .Result(cart)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> removeFromCart(@PathVariable String id) {
        cartService.removeFromCart(id);
        return ApiResponse.<String>builder()
                .Result("Xóa sản phẩm khỏi giỏ hàng thành công.")
                .build();
    }

    @PutMapping("/{bookId}")
    public ApiResponse<Cart> updateCartQuantity(@PathVariable String bookId,
                                                @RequestBody Map<String, Integer> requestBody) {
        Integer quantity = requestBody.get("quantity");
        if (quantity == null) {
            throw new IllegalArgumentException("Missing 'quantity' in request body");
        }

        Cart updatedCart = cartService.updateQuantity(bookId, quantity);
        return ApiResponse.<Cart>builder()
                .Result(updatedCart)
                .build();
    }
}

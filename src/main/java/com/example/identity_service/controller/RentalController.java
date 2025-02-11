package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResponse;
import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.Rental;
import com.example.identity_service.service.RentalService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentalController {
    RentalService rentalService;
    @PostMapping
    public ApiResponse<String> rentAllBooks() {
        rentalService.rentAllBooksInCart();
        return ApiResponse.<String>builder()
                .Result("Thuê sách thành công!")
                .build();
    }
    @GetMapping
    public ApiResponse<List<Rental>> getAllRentals() {
        return ApiResponse.<List<Rental>>builder()
                .Result(rentalService.getAllRentals())
                .build();
    }

    @GetMapping("/my-rentals")
    public ApiResponse<List<Rental>> getCurrentUserRentals() {
        return ApiResponse.<List<Rental>>builder()
                .Result(rentalService.getCurrentUserRentals())
                .build();
    }

    @GetMapping("/top-categories")
    public ApiResponse<Map<String, List<Map<String, Object>>>> getMostRentedBooksByCategory() {
        return ApiResponse.<Map<String, List<Map<String, Object>>>>builder()
                .Result(rentalService.getMostRentedBooksByCategory())
                .build();
    }

    @PutMapping("/{rentalId}/return")
    ApiResponse<String> markAsReturned(@PathVariable String rentalId) {
        rentalService.markAsReturned(rentalId);
        return ApiResponse.<String>builder()
                .Result("Rental marked as returned successfully")
                .build();
    }
}

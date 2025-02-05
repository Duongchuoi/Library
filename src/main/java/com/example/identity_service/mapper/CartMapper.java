package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.CartRequest;
import com.example.identity_service.dto.response.CartResponse;
import com.example.identity_service.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartRequest request);

    CartResponse toCartResponse(Cart cart);
}

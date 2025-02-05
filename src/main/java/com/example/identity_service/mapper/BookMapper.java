package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.BookRequest;
import com.example.identity_service.dto.request.BookUpdateRequest;
import com.example.identity_service.dto.response.BookResponse;
import com.example.identity_service.entity.Book;
import com.example.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Book toBook(BookRequest request);

    BookResponse toBookResponse(Book Book);

    @Mapping(target = "image", ignore = true)
    void updateBook(@MappingTarget Book book, BookUpdateRequest request);
}

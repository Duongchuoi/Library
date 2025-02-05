package com.example.identity_service.dto.request;

import com.example.identity_service.entity.Category;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookRequest {
    String title;
    String author;
    Integer stock_quantity;
    MultipartFile image;

    Set<String> categories;
}

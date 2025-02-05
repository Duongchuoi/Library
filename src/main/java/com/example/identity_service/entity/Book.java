package com.example.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.awt.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;
    String author;
    int stock_quantity;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String image;

    @ManyToMany
    Set<Category> categories;
}

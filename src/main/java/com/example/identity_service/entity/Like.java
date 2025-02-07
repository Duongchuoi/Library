package com.example.identity_service.entity;

import com.example.identity_service.enums.LikeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    LikeType type; // LIKE_POST hoặc LIKE_COMMENT

    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post; // Có thể null nếu like là của comment

    @ManyToOne
    @JoinColumn(name = "comment_id")
    Comment comment; // Có thể null nếu like là của post

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
}


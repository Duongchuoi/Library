package com.example.identity_service.dto.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Size;

import com.example.identity_service.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    String fullname;
    String email;
    String phone;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
    Set<String> roles;
}

package com.berkayyetis.store.dtos;

import com.berkayyetis.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank
    @Email(message = "valid email is required")
    @Lowercase(message = "email is must be in lowercase")
    private String email;

    @NotBlank
    @Size(min = 6, max = 36)
    private String password;
}

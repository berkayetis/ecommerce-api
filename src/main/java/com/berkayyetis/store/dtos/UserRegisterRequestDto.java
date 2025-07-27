package com.berkayyetis.store.dtos;

import com.berkayyetis.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterRequestDto {
        @NotBlank(message = "name is required.")
        @Size(max = 255)
        private String name;

        @NotBlank(message = "email is required.")
        @Email(message = "email is not valid.")
        @Lowercase(message = "email is must be in lowercase")
        private String email;

        @NotBlank(message = "password is required.")
        @Size(min = 6, max = 25, message = "password must be min 6 and max 25 character.")
        private String password;
}

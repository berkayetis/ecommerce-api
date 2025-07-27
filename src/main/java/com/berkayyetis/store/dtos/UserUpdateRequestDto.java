package com.berkayyetis.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String name;
    private String email;
    private String password;
}

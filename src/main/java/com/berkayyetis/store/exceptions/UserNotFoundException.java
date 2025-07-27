package com.berkayyetis.store.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("User not found with ID : '%s'", userId));
    }
}

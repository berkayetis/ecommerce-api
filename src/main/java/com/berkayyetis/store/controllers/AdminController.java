package com.berkayyetis.store.controllers;

import com.berkayyetis.store.dtos.UserDto;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@RestController
public class AdminController {

    @PostMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}

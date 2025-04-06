package com.tometracker.controller;

import com.tometracker.dto.UserDTO;
import com.tometracker.data_template.UserInfo;
import com.tometracker.service.UserBookService;
import com.tometracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserBookService userBookService;
    private final TokenBasedRememberMeServices rememberMeServices;

    public UserController(UserService userService, UserBookService userBookService, TokenBasedRememberMeServices rememberMeServices) {
        this.userService = userService;
        this.userBookService = userBookService;
        this.rememberMeServices = rememberMeServices;
    }

    @PostMapping("/registration")
    public String register(@RequestBody UserDTO user) {
        return userService.addUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        rememberMeServices.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/info")
    public UserInfo getUserInfo() {
        return userBookService.getUserInfo();
    }
}

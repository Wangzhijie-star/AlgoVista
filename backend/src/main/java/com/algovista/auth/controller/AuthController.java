package com.algovista.auth.controller;

import com.algovista.auth.dto.AuthRequest;
import com.algovista.auth.dto.UserProfile;
import com.algovista.auth.security.SessionKeys;
import com.algovista.auth.service.AuthService;
import com.algovista.common.result.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<UserProfile> register(@Valid @RequestBody AuthRequest request, HttpSession session) {
        UserProfile profile = authService.register(request);
        session.setAttribute(SessionKeys.CURRENT_USER_ID, profile.id());
        return ApiResponse.ok(profile);
    }

    @PostMapping("/login")
    public ApiResponse<UserProfile> login(@Valid @RequestBody AuthRequest request, HttpSession session) {
        UserProfile profile = authService.login(request);
        session.setAttribute(SessionKeys.CURRENT_USER_ID, profile.id());
        return ApiResponse.ok(profile);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<UserProfile> me(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionKeys.CURRENT_USER_ID);
        return ApiResponse.ok(authService.findProfile(userId));
    }
}

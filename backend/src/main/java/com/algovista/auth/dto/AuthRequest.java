package com.algovista.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Size(min = 3, max = 32) String username,
        @NotBlank @Size(min = 6, max = 64) String password,
        @Size(max = 32) String nickname
) {
}

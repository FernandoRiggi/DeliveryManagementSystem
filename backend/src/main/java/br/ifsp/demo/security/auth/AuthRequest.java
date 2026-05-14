package br.ifsp.demo.security.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Schema(description = "Email to be used as login", example = "know.nothing@snow.com")
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid")
        String email,

        @Schema(description = "Password", example = "n3243#kFdj$")
        @NotBlank(message = "Password is required")
        String password
) {}

package br.ifsp.demo.security.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @Schema(description = "Name", example = "John")
        @NotBlank(message = "Name is required") @Size(min = 2, max = 100)
        String name,

        @Schema(description = "Lastname", example = "Snow")
        @NotBlank(message = "Lastname is required") @Size(min = 2, max = 100)
        String lastname,

        @Schema(description = "Email to be used as login", example = "know.nothing@snow.com")
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 255)
        String email,

        @Schema(description = "Password", example = "n3243#kFdj$")
        @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password
) {}

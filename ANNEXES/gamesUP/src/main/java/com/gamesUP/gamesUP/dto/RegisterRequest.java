package com.gamesUP.gamesUP.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") String displayName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères") String password
) {
}

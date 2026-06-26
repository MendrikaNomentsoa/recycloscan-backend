// dto/RegisterRequestDto.java
package com.recycloscan.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "Le username est obligatoire")
    @Size(min = 3, max = 20, message = "Le username doit faire entre 3 et 20 caractères")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;
}
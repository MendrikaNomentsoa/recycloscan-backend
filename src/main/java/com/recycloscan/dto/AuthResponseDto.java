// dto/AuthResponseDto.java
package com.recycloscan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;      // JWT retourné au client
    private String username;
    private String email;
    private Integer totalPoints;
}
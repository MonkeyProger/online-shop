package org.scenter.onlineshop.responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JWTResponse {
    private String token;
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
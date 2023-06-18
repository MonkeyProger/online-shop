package org.scenter.onlineshop.common.responses;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JWTResponse {
    private String token;
    private final String type = "Bearer";
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<String> roles;
}
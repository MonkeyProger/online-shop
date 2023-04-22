package org.scenter.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    String path;
    String message;
    int statusCode;
    LocalDateTime localDateTime;
}

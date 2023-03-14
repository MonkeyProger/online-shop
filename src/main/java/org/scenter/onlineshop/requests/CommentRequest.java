package org.scenter.onlineshop.requests;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {
    private Long commentId;
    @NotBlank
    private String comment;
    @Range(min = 1, max = 5)
    private Integer rating;
    @NotBlank
    private String userEmail;
}

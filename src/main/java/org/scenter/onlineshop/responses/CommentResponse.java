package org.scenter.onlineshop.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.scenter.onlineshop.domain.ResponseFile;

import java.util.List;

@Data
@AllArgsConstructor
public class CommentResponse {
    private String text;
    private Integer rating;
    private String fullName;
    private List<ResponseFile> images;
}

package org.scenter.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scenter.onlineshop.domain.ResponseFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long id;

    private String text;

    private Integer rating;

    private String userEmail;

    private List<ResponseFile> images = new ArrayList<>();
}

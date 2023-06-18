package org.scenter.onlineshop.service.mapping;

import org.scenter.onlineshop.domain.Comment;
import org.scenter.onlineshop.dto.CommentDTO;

public class CommentMapping {
    public static CommentDTO commentToDTO(Comment comment){
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setImages(comment.getImages());
        dto.setRating(comment.getRating());
        dto.setUserEmail(comment.getUserEmail());
        return dto;
    }
}

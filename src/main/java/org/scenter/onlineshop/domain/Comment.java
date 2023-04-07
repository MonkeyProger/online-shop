package org.scenter.onlineshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String text;

    @Range(min = 1, max = 5)
    private Integer rating;

    private String userEmail;

    @ManyToMany
    private List<ResponseFile> images = new ArrayList<>();

    public Comment (String text, Integer rating, String userEmail) {
        this.text = text;
        this.rating = rating;
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!text.equals(comment.text)) return false;
        if (!rating.equals(comment.rating)) return false;
        return userEmail.equals(comment.userEmail);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + rating.hashCode();
        result = 31 * result + userEmail.hashCode();
        return result;
    }
}

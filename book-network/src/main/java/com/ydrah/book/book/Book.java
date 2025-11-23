package com.ydrah.book.book;

import com.ydrah.book.common.BaseEntity;
import com.ydrah.book.feedback.Feedback;
import com.ydrah.book.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinTable(name="user_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        // Return 4.0 if roundedRate is less than 4.5, otherwise return 4.5
        return Math.round(rate * 10.0) / 10.0;
    }
}

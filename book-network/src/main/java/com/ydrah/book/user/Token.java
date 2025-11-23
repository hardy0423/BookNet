package com.ydrah.book.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer Id;

    @Column(unique = true)
    private String token;

    private LocalDate createdAt;
    private LocalDate expiresAt;
    private LocalDate validatedAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}

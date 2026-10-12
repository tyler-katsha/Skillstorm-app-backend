package com.skillstorm.skillstorm.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "difficulty", nullable = false)
    private String difficulty; // "Easy", "Medium", "Hard"

    @ManyToMany
    @JoinTable(
            name = "topic_quiz",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Question> questions = new HashSet<>();

    private String email;
}

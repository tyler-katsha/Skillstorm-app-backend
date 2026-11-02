package com.skillstorm.skillstorm.model;

import com.skillstorm.skillstorm.enums.Kind;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badge")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Integer id;

    // Maps to the frontend definition id (e.g., 'first', 'champion', 'flawless')
    @Column(name = "definition_id", nullable = false)
    private String definitionId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "kind", nullable = false)
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Column(name = "goal", nullable = false)
    private Integer goal;

}
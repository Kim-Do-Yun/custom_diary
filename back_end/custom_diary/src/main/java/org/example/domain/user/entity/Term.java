package org.example.domain.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@Setter
@Data
@NoArgsConstructor
public class Term {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;             // 예: "personal_info", "marketing"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

package org.example.domain.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "art_styles")
@Getter
@Setter
@Data
@NoArgsConstructor
public class ArtStyle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String prompt; ;              // JSON 형태 문자열로 저장

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

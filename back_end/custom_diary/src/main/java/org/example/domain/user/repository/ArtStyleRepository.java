package org.example.domain.user.repository;
import org.example.domain.user.entity.ArtStyle;
import org.example.domain.user.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface ArtStyleRepository extends JpaRepository<ArtStyle, Long> {
    ArtStyle findTopByUserOrderByCreatedAtDesc(User user);
}

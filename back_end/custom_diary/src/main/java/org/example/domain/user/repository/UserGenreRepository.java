package org.example.domain.user.repository;
import org.example.domain.user.entity.User;
import org.example.domain.user.entity.UserGenre;
import org.example.domain.user.entity.UserGenreKey;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGenreRepository extends JpaRepository<UserGenre, UserGenreKey> {
    List<UserGenre> findByUser(User user);
}

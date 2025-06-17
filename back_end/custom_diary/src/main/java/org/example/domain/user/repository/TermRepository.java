package org.example.domain.user.repository;

import org.example.domain.user.entity.Term;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByTermId(Long termId);
}

package com.project.cuecards.repositories;

import com.project.cuecards.entities.CueCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CueCardRepository extends JpaRepository<CueCard, Long> {
}

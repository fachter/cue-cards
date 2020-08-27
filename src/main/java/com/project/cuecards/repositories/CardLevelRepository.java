package com.project.cuecards.repositories;

import com.project.cuecards.entities.CardLevel;
import com.project.cuecards.entities.CueCard;
import com.project.cuecards.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardLevelRepository extends JpaRepository<CardLevel, Long> {

    CardLevel findCardLevelByCueCardAndUser(CueCard cueCard, User user);
}

package com.psyched.game.repository;

import com.psyched.game.model.GameMode;
import com.psyched.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByGameMode(GameMode gameMode);
}


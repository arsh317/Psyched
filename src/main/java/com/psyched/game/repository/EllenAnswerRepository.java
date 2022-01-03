package com.psyched.game.repository;

import com.psyched.game.model.EllenAnswer;
import com.psyched.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EllenAnswerRepository extends JpaRepository<EllenAnswer, Long> {

    public List<EllenAnswer> findByQuestion(Question question);

    public List<EllenAnswer> findByQuestionAndAnswer(Question question, String answer);
}

package com.psyched.game;

import com.psyched.game.controller.QuestionsList;
import com.psyched.game.model.EllenAnswer;
import com.psyched.game.model.Question;
import com.psyched.game.model.Round;
import com.psyched.game.repository.EllenAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RandomTopfiveAnswers implements EllenStrategy{

    @Autowired
    private EllenAnswerRepository ellenAnswerRepository;

    private EllenAnswer getRandomAnswer(Question question){
        String answer = QuestionsList.getInstance().getRandomQuestion(question.getGameMode()).getCorrectAnswer();
        EllenAnswer ellenAnswer = new EllenAnswer.EllenAnswerBuilder().answer(answer)
                .question(question).votes(0L).build();

        ellenAnswerRepository.save(ellenAnswer);
        return ellenAnswer;
    }

    @Override
    public EllenAnswer getAnswer(Round round) {

        Question question = round.getQuestion();
        List<EllenAnswer> possibleAnswers = ellenAnswerRepository.findByQuestion(question);
        if(possibleAnswers.isEmpty())
            return getRandomAnswer(question);

        // set the indexes right for this to work as expected
        // Also test this once
        return possibleAnswers.get(0);
    }
}

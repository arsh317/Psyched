package com.psyched.game;

import com.psyched.game.model.EllenAnswer;
import com.psyched.game.model.Question;
import com.psyched.game.repository.EllenAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UpdateEllenAnswers {

    @Autowired
    private static EllenAnswerRepository ellenAnswerRepository;

    public static void increaseVoteCount(Question question, String answer, long val){
        List<EllenAnswer> ellenAnswers = ellenAnswerRepository.findByQuestionAndAnswer(question, answer);

        EllenAnswer answerToUpdate;
        if(ellenAnswers.isEmpty()) {
            answerToUpdate = new EllenAnswer.EllenAnswerBuilder().answer(answer)
                    .question(question).votes(0L).build();
            ellenAnswerRepository.save(answerToUpdate);
        }
        else {
            answerToUpdate = ellenAnswers.get(0);
        }

        answerToUpdate.increaseVote(val);
    }
}

package com.psyched.game.model;

import com.psyched.game.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "stats")
public class Stats extends Auditable {
    @Getter
    @Setter
    private long correctAnswers = 0;
    @Getter
    @Setter
    private long gotPsychedCount = 0;
    @Getter
    @Setter
    private long psychedOthersCount = 0;

    public void incrementCorrectAnswers(){
        correctAnswers += 1;
    }

    public void incrementGotPsychedCount(){
        gotPsychedCount += 1;
    }

    public void incrementPsychedOthersCount(){
        psychedOthersCount += 1;
    }

    public void increaseCorrectAnswers(long val){
        correctAnswers += val;
    }

    public void increaseGotPsychedCount(long val){
        gotPsychedCount += val;
    }

    public void increasePsychedOthersCount(long val){
        psychedOthersCount += val;
    }

    public long getScores() {
        return (correctAnswers * Constants.CORRECT_ANSWER_SCORE) +
                (psychedOthersCount * Constants.PSYCHED_OTHER_SCORE);
    }
}
package com.psyched.game.model;

import com.psyched.game.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "ellen_answers")
public class EllenAnswer extends Auditable {
    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @Getter
    @Setter
    @NotBlank
    @Column(length = Constants.MAX_ANSWER_LENGTH)
    private String answer;

    @Getter
    @Setter
    private Long votes = 0L;

    public void increaseVote() {
        increaseVote(1);
    }

    public void increaseVote(long val) {
        votes += val;
    }

    public static final class EllenAnswerBuilder {
        private Question question;
        private String answer;
        private Long votes = 0L;

        public EllenAnswerBuilder question(Question question) {
            this.question = question;
            return this;
        }

        public EllenAnswerBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public EllenAnswerBuilder votes(Long votes) {
            this.votes = votes;
            return this;
        }

        public EllenAnswer build() {
            EllenAnswer ellenAnswer = new EllenAnswer();
            ellenAnswer.setQuestion(question);
            ellenAnswer.setAnswer(answer);
            ellenAnswer.setVotes(votes);
            return ellenAnswer;
        }
    }
}


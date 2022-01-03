package com.psyched.game.model;

import com.psyched.game.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "player_answers")
public class PlayerAnswer extends Auditable {
    @Getter
    @Setter
    @NotBlank
    @Column(length = Constants.MAX_ANSWER_LENGTH)
    private String answer;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "player_id")
    @NotNull
    private Player player;

    public static final class PlayerAnswerBuilder {
        private String answer;
        private Round round;
        private Player player;

        public PlayerAnswerBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public PlayerAnswerBuilder round(Round round) {
            this.round = round;
            return this;
        }

        public PlayerAnswerBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public PlayerAnswer build() {
            PlayerAnswer playerAnswer = new PlayerAnswer();
            playerAnswer.setAnswer(answer);
            playerAnswer.setRound(round);
            playerAnswer.setPlayer(player);
            return playerAnswer;
        }
    }
}

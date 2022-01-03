package com.psyched.game.model;

import com.psyched.game.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "player_selections")
public class PlayerSelection extends Auditable{
    @Getter
    @Setter
    @NotBlank
    @Column(length = Constants.MAX_ANSWER_LENGTH)
    private Boolean isCorrect = false;

    @Getter
    @Setter
    @NotBlank
    @Column(length = Constants.MAX_ANSWER_LENGTH)
    private Boolean isEllenAnswer = false;

    @Getter
    @Setter
    @ManyToOne
    private PlayerAnswer playerAnswer = null;

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

    public static final class PlayerSelectionBuilder {
        private Boolean isCorrect = false;
        private Boolean isEllenAnswer = false;
        private PlayerAnswer playerAnswer = null;
        private Round round;
        private Player player;

        public PlayerSelectionBuilder isCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public PlayerSelectionBuilder isEllenAnswer(Boolean isEllenAnswer) {
            this.isEllenAnswer = isEllenAnswer;
            return this;
        }

        public PlayerSelectionBuilder playerAnswer(PlayerAnswer playerAnswer) {
            this.playerAnswer = playerAnswer;
            return this;
        }

        public PlayerSelectionBuilder round(Round round) {
            this.round = round;
            return this;
        }

        public PlayerSelectionBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public PlayerSelection build() {
            PlayerSelection playerSelection = new PlayerSelection();
            playerSelection.setIsCorrect(isCorrect);
            playerSelection.setIsEllenAnswer(isEllenAnswer);
            playerSelection.setPlayerAnswer(playerAnswer);
            playerSelection.setRound(round);
            playerSelection.setPlayer(player);
            return playerSelection;
        }
    }
}

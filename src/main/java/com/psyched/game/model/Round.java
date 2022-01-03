package com.psyched.game.model;

import com.psyched.game.Constants;
import com.psyched.game.UpdateEllenAnswers;
import com.psyched.game.Util.OptionToSelect;
import com.psyched.game.Utils;
import com.psyched.game.exceptions.InvalidActionForGameStateExeption;
import com.psyched.game.exceptions.InvalidSelectionException;
import com.psyched.game.exceptions.InvalidSubmissionException;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "rounds")
public class Round extends Auditable {
    @ManyToOne
    @Getter
    @Setter
    @NotNull
    private Game game;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @Getter
    @Setter
    @NotNull
    private int roundNumber;

    @ManyToOne(optional = true)
    @Getter
    @Setter
    private EllenAnswer ellenAnswer = null;

    @ManyToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Map<Player, PlayerAnswer> submittedAnswers = new HashMap<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Map<Player, PlayerSelection> selectedAnswers = new HashMap<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Map<Player, Stats> playerStats = new HashMap<>();

    @ManyToMany()
    @Getter
    @Setter
    private Set<Player> readyPlayers = new HashSet<>();

    public Round(Game game, Question randomQuestion, int roundNumber) {
        this.game = game;
        this.question = randomQuestion;
        this.roundNumber = roundNumber;
    }

    public Round() {
    }

    private boolean isCorrectAnswer(String answer) {
        return Utils.isMatches(answer, question.getCorrectAnswer());
    }

    private boolean alreadySubmitted(String answer) {
        for(PlayerAnswer playerAnswer: submittedAnswers.values()){
            if(Utils.isMatches(answer, playerAnswer.getAnswer())) return true;
        }
        return false;
    }

    public void submitAnswer(Player player, String answer) throws InvalidActionForGameStateExeption, InvalidSubmissionException {
        if(submittedAnswers.containsKey(player))
            throw new InvalidActionForGameStateExeption("Player have already selected answer for this round");
        if(isCorrectAnswer(answer))
            throw new InvalidSubmissionException("You typed right answer. Please type a fake Answer");
        if(alreadySubmitted(answer))
            throw new InvalidSubmissionException("Someone typed the same answer. Please type some other Answer");

        PlayerAnswer playerAnswer = new PlayerAnswer.PlayerAnswerBuilder()
                .player(player).answer(answer).round(this).build();
        submittedAnswers.put(player, playerAnswer);
    }


    public void selectAnswer(Player player, Boolean isCorrect,
                             Boolean isEllenAnswer, PlayerAnswer selectedAnswer) throws InvalidActionForGameStateExeption {

        if(selectedAnswers.containsKey(player))
            throw new InvalidActionForGameStateExeption("Player have already selected answer for this round");

        PlayerSelection playerSelection = new PlayerSelection.PlayerSelectionBuilder()
                .isCorrect(isCorrect).isEllenAnswer(isEllenAnswer).player(player).round(this)
                .playerAnswer(selectedAnswer).build();
        selectedAnswers.put(player, playerSelection);
    }


    private void updateRoundStats() throws InvalidSelectionException {
        for(PlayerSelection selectedAnswer : selectedAnswers.values()){

            if(selectedAnswer.getIsCorrect()) {
                playerStats.get(selectedAnswer.getPlayer()).incrementCorrectAnswers();
                continue;
            }
            if(selectedAnswer.getIsEllenAnswer()) {
                ellenAnswer.increaseVote();
                continue;
            }

            if(selectedAnswer.getPlayerAnswer() == null)
                throw new InvalidSelectionException(String.format("No selection found for player : %s, for latest round",
                        selectedAnswer.getPlayer().getUserName()));

            // update current round stats
            playerStats.get(selectedAnswer.getPlayer()).incrementGotPsychedCount();
            playerStats.get(selectedAnswer.getPlayerAnswer().getPlayer()).incrementPsychedOthersCount();
        }

    }

    public void endRound() throws InvalidSelectionException {
        updateRoundStats();
        game.updateGameStatsFromLastRound();
    }

    public void getReady(Player player) {
        if(readyPlayers.contains(player)) return;

        readyPlayers.add(player);
    }

    public void updateEllenAnswers() {
        for(Player player : playerStats.keySet()){
            UpdateEllenAnswers.increaseVoteCount(question, submittedAnswers.get(player).getAnswer(),
                    playerStats.get(player).getPsychedOthersCount());
        }
    }

    public JSONObject getRoundStats() {
        JSONObject roundStats = new JSONObject();
        for(Player player : playerStats.keySet()){

            JSONObject currPlayerStats = new JSONObject();
            currPlayerStats.put(Constants.SCORES, playerStats.get(player).getScores());
            currPlayerStats.put(Constants.PSYCHED_COUNT, playerStats.get(player).getPsychedOthersCount());
            currPlayerStats.put(Constants.IS_CORRECT_ANS, selectedAnswers.get(player).getIsCorrect());
            currPlayerStats.put(Constants.IS_ELLEN_ANS, selectedAnswers.get(player).getIsEllenAnswer());

            Player psychedBy = null;
            if(selectedAnswers.get(player).getPlayerAnswer() != null)
                selectedAnswers.get(player).getPlayerAnswer().getPlayer();
            currPlayerStats.put(Constants.PSYCHED_BY, psychedBy);

            roundStats.put(player.getUserName(), currPlayerStats);
        }

        return roundStats;
    }

    public List<OptionToSelect> getOptionsForPlayer(Player player) {
        List<OptionToSelect> options = new ArrayList<>();
        for(PlayerAnswer playerAnswer : submittedAnswers.values()){
            // Don't show player his own Answer as a option
            if(playerAnswer.getPlayer() == player)
                continue;

            OptionToSelect option = new OptionToSelect.OptionToSelectBuilder().answerId(playerAnswer.getId())
                    .answerText(playerAnswer.getAnswer()).build();
            options.add(option);
        }

        // Adding the correct option
        OptionToSelect correctOption = new OptionToSelect.OptionToSelectBuilder()
                .answerText(question.getCorrectAnswer()).isCorrect(true).build();
        options.add(correctOption);

        if(ellenAnswer != null){
            OptionToSelect ellenOption = new OptionToSelect.OptionToSelectBuilder()
                    .answerText(ellenAnswer.getAnswer()).isEllenAnswer(true).build();
            options.add(ellenOption);
        }

        return options;
    }

    public static final class RoundBuilder {
        private Round round;

        public RoundBuilder() {
            round = new Round();
        }

        public static RoundBuilder aRound() {
            return new RoundBuilder();
        }

        public RoundBuilder game(Game game) {
            round.setGame(game);
            return this;
        }

        public RoundBuilder question(Question question) {
            round.setQuestion(question);
            return this;
        }

        public RoundBuilder roundNumber(int roundNumber) {
            round.setRoundNumber(roundNumber);
            return this;
        }

        public RoundBuilder ellenAnswer(EllenAnswer ellenAnswer) {
            round.setEllenAnswer(ellenAnswer);
            return this;
        }

        public Round build() {
            for(Player player : round.getGame().getPlayers())
                round.getPlayerStats().put(player, new Stats());
            return round;
        }
    }
}

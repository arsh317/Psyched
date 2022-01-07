package com.psyched.game.model;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.psyched.game.Pair;
import com.psyched.game.RandomTopfiveAnswers;
import com.psyched.game.Util.OptionToSelect;
import com.psyched.game.controller.GameController;
import com.psyched.game.controller.QuestionsList;
import com.psyched.game.exceptions.*;
import com.psyched.game.repository.QuestionRepository;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "games")
public class Game extends Auditable {
    @Getter
    @Setter
    @NotNull
    private int numRounds;

    @Getter
    @Setter
    private Boolean isEllen = Boolean.FALSE;

    @ManyToMany(cascade = CascadeType.ALL)
    @Getter
    @Setter
    private Map<Player, Stats> playerStats = new HashMap<>();

    @ManyToMany
    @Getter
    @Setter
    private Set<Player> players = new HashSet<>();

    @NotNull
    @Getter
    @Setter
    private GameMode gameMode;

    @Getter
    @Setter
    private GameStatus gameStatus = GameStatus.JOINING;

    @ManyToOne
    @NotNull
    @Getter
    @Setter
    private Player leader;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Getter @Setter
    private List<Round> rounds = new ArrayList<>();

    public static final class GameBuilder {
        private int numRounds;
        private Boolean isEllen = Boolean.FALSE;
        private List<Player> players;
        private GameMode gameMode;
        private Player leader;

        public GameBuilder numRounds(int numRounds) {
            this.numRounds = numRounds;
            return this;
        }

        public GameBuilder isEllen(Boolean isEllen) {
            this.isEllen = isEllen;
            return this;
        }

        public GameBuilder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public GameBuilder gameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            return this;
        }

        public GameBuilder leader(Player leader) {
            this.leader = leader;
            return this;
        }

        public Game build() {
            Game game = new Game();
            game.setNumRounds(numRounds);
            game.setIsEllen(isEllen);
            game.setGameMode(gameMode);
            game.setLeader(leader);
            game.addPlayer(leader);
            return game;
        }
    }

    public Map<String, Object> getCurrRoundStats() {
        if(gameStatus != GameStatus.OVER && gameStatus != GameStatus.GETTING_READY) return null;
        return currentRound().getRoundStats();
    }

    public Map<String, Object> getGameStats() {
        if(gameStatus != GameStatus.OVER && gameStatus != GameStatus.GETTING_READY) return null;

        Map<String, Object> scores = new HashMap<>();
        for(Player player : players)
            scores.put(player.getUserName(), playerStats.get(player).getScores());

        return scores;
    }

    public List<OptionToSelect> getOptionsForPlayer(Player player) throws InvalidActionForGameStateExeption {
        if(gameStatus != GameStatus.SELECTING_ANSWERS)
            throw new InvalidActionForGameStateExeption("Game is currently not in selecting mode");

        return currentRound().getOptionsForPlayer(player);
    }


    public void addPlayer(Player player){
        if(gameStatus != GameStatus.JOINING){
            new InvalidActionForGameStateExeption("Game has already started");
        }

        if(players.contains(player)) return;

        players.add(player);
        playerStats.put(player, new Stats());
    }

    public void start(Player player, QuestionRepository questionRepository) throws Exception {
        if(player.getId() != leader.getId())
            throw new InvalidPlayerForStartingGameException("Player not leader of given Game");
        else if(players.size() <= 1)
            throw new PlayersNotEnoughException("Needs atleast 2 players to start a Game");
        else if(gameStatus != GameStatus.JOINING)
            throw new InvalidActionForGameStateExeption("Game already started");

        startNewRound();
    }

    public void startNewRound(){
        Question question = QuestionsList.getInstance().getRandomQuestion(gameMode);
        EllenAnswer ellenAnswer = null;

        Round round = new Round.RoundBuilder().game(this)
                .question(question)
                .roundNumber(rounds.size())
                .build();

        if(isEllen)
            ellenAnswer = new RandomTopfiveAnswers().getAnswer(round);
        round.setEllenAnswer(ellenAnswer);

        rounds.add(round);
        gameStatus = GameStatus.SUBMITTING_ANSWERS;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public Round currentRound(){
        return rounds.get(rounds.size()-1);
    }

    public void submitAnswer(Player player, String answer)
            throws InvalidActionForGameStateExeption, InvalidSubmissionException {

        if(gameStatus != GameStatus.SUBMITTING_ANSWERS)
            throw new InvalidActionForGameStateExeption("Not accepting answers at this point");
        Round round = currentRound();
        round.submitAnswer(player, answer);
        if(round.getSubmittedAnswers().size() == players.size())
            gameStatus = GameStatus.SELECTING_ANSWERS;
    }

    public void selectAnswer(Player player, Boolean isCorrect,
                             Boolean isEllen ,PlayerAnswer selectedAnswer)
        throws InvalidActionForGameStateExeption, InvalidSelectionException {

        if(gameStatus != GameStatus.SELECTING_ANSWERS)
            throw new InvalidActionForGameStateExeption("Not allowing selections at this point");
        Round round = currentRound();
        round.selectAnswer(player, isCorrect, isEllen, selectedAnswer);
        if(round.getSelectedAnswers().size() >= players.size()) {
            gameStatus = GameStatus.GETTING_READY;
            round.endRound();
        }
    }

    private Player getRandomGamePlayer(){
        Random rand = new Random();
        int randInd = rand.nextInt(players.size());

        return ((List<Player>) players).get(randInd);
    }

    public void leavePlayer(Player player) {
        players.remove(player);
        playerStats.remove(player);

        if(player == leader)
            leader = getRandomGamePlayer();
    }

    public void updateGameStatsFromLastRound(){
        Round round = currentRound();
        for(Player player : players){
            Stats playerRoundStats = round.getPlayerStats().get(player);
            playerStats.get(player).increaseCorrectAnswers(playerRoundStats.getCorrectAnswers());
            playerStats.get(player).increaseGotPsychedCount(playerRoundStats.getGotPsychedCount());
            playerStats.get(player).increasePsychedOthersCount(playerRoundStats.getPsychedOthersCount());
        }
    }

    private void updatePlayersStatsFromGame(){
        for(Player player : players)
            player.updatePlayerStats(playerStats.get(player));
    }

    private void updateEllenAnswers(){
        for(Round round : rounds){
            round.updateEllenAnswers();
        }
    }

    public void endGame(){
        updatePlayersStatsFromGame();
        updateEllenAnswers();
    }

    public void getReady(Player player) throws InvalidActionForGameStateExeption, InvalidSelectionException {
        if(gameStatus != GameStatus.GETTING_READY)
            throw new InvalidActionForGameStateExeption(String.format("Game is currently in %s State", gameStatus));
        Round round = currentRound();
        round.getReady(player);

        if(round.getReadyPlayers().size() == players.size()){
            gameStatus = GameStatus.OVER;
            if(rounds.size() == numRounds)
                endGame();
            else
                startNewRound();
        }

    }

}
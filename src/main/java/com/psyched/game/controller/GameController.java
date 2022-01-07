package com.psyched.game.controller;

import com.psyched.game.Constants;
import com.psyched.game.Pair;
import com.psyched.game.Util.OptionToSelect;
import com.psyched.game.Utils;
import com.psyched.game.exceptions.*;
import com.psyched.game.model.*;
import com.psyched.game.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private StatsRepository statsRepository;

    @Autowired
    private PlayerAnswerRepository playerAnswerRepository;

    @Autowired
    private EllenAnswerRepository ellenAnswerRepository;

    private Map<GameMode, List<Question>> gameModeToQuestions = null;

    public Question getRandomQuestion(GameMode gameMode){
        if (gameModeToQuestions == null){
            gameModeToQuestions = new HashMap<>();
            for(GameMode gm : GameMode.values()){
                gameModeToQuestions.put(gm, questionRepository.findByGameMode(gm));
            }
        }
        Random rand = new Random();
        int randInd = rand.nextInt(gameModeToQuestions.get(gameMode).size());
        return gameModeToQuestions.get(gameMode).get(randInd);
    }

    @GetMapping("/create/{username}/{gm}/{nmr}/{isEllen}")
    public String createGame(@PathVariable(value = "username") String username,
                             @PathVariable(value = "gm") int gameModeId,
                             @PathVariable(value = "nmr") int numRounds,
                             @PathVariable(value = "isEllen") Boolean isEllen) throws Exception {
        Player leader = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        Game game = new Game.GameBuilder()
                .numRounds(numRounds)
                .gameMode(Constants.GAME_MODE_MAP.get(gameModeId))
                .isEllen(isEllen)
                .leader(leader)
                .build();

        gameRepository.save(game);
        return game.getId() + "-" + Utils.getSecretCodeFromGameId(game.getId());
    }

    @GetMapping("/join/{username}/{gc}")
    public String joinGame(@PathVariable(value = "username") String username,
                           @PathVariable(value = "gc") String gameCode) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game gameToJoin = gameRepository.findById(gameId)
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        gameToJoin.addPlayer(player);
        gameRepository.save(gameToJoin);
        return "Joined Successfully";
    }

//    check if player is leader
//    check if gameCode is Valid
//    check if numPlayers > 1
//    game is not already started
    @GetMapping("/start/{username}/{gc}")
    public String startGame(@PathVariable(value = "username") String username,
                            @PathVariable(value = "gc") String gameCode) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game gameToStart = gameRepository.findById(gameId)
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        gameToStart.start(player, questionRepository);
        gameRepository.save(gameToStart);
        return "Started Successfully";
    }

    @GetMapping("/round/{gc}")
    public String startRound(@PathVariable(value = "gc") String gameCode) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));

        game.startNewRound();
        gameRepository.save(game);
        return "Round started successfully";
    }

    @GetMapping("/question/{gc}")
    public Question getCurrRoundQuestion(@PathVariable(value = "gc") String gameCode) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));

        return game.getRounds().get(game.getRounds().size() - 1).getQuestion();
    }

    @GetMapping("/test/{gc}")
    public int test(@PathVariable(value = "gc") String gameCode) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new Exception("Invalid GameCode"));

        Player player = playerRepository.findByUserName("nico").orElseThrow(() -> new Exception("Player not found"));

//        Round currRound = game.getRounds().get(game.getCurrentRound());
//        System.out.println(currRound.getRoundNumber());
//        System.out.println(currRound.getPlayerStats().get(player).getCorrectAnswers());
//        System.out.println(currRound.getPlayerStats().get(player).getPsychedOthersCount());

        return game.getRounds().size();
    }

    @GetMapping("/submit/{username}/{answer}/{gc}")
    public String submitAnswer(@PathVariable(value = "username") String username,
                               @PathVariable(value = "answer") String answer,
                               @PathVariable(value = "gc") String gameCode) throws Exception {

        Game game = gameRepository.findById(Utils.getGameIdFromSecretCode(gameCode))
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        if(!game.hasPlayer(player)) throw new IllegalGameException("Player is not part of given game");

        game.submitAnswer(player, answer);
        gameRepository.save(game);
        return "saved successfully";
    }

    @GetMapping("/options/{gc}/{username}")
    public List<OptionToSelect> getRoundOptions(@PathVariable(value = "gc") String gameCode,
                                                @PathVariable(value = "username") String username) throws Exception {

        Long gameId = Utils.getGameIdFromSecretCode(gameCode);
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new Exception("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username).orElseThrow(() -> new Exception("Player not found"));

        return game.getOptionsForPlayer(player);
    }

    @GetMapping("/select/{gc}/{username}/{isCorrectAnswer}/{isEllenAnswer}/{selectedAnswerId}")
    public String selectAnswer(@PathVariable(value = "gc") String gameCode,
                               @PathVariable(value = "username") String username,
                               @PathVariable(value = "isCorrectAnswer") Boolean isCorrectAnswer,
                               @PathVariable(value = "isEllenAnswer") Boolean isEllenAnswer,
                               @PathVariable(value = "selectedAnswerId") Long selectedAnswerId
                               ) throws Exception {

        Game game = gameRepository.findById(Utils.getGameIdFromSecretCode(gameCode))
                .orElseThrow(() -> new Exception("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        PlayerAnswer selectedAnswer = null;
        try {
            selectedAnswer = playerAnswerRepository.findById(selectedAnswerId).get();
        } catch (Exception err){ selectedAnswer = null; }

        game.selectAnswer(player, isCorrectAnswer, isEllenAnswer, selectedAnswer);
        gameRepository.save(game);
        return "selected successfully";
    }

    @GetMapping("/game-state/{gc}")
    public Map<String, Object> getGameState(@PathVariable(value = "gc") String gameCode) throws Exception {
        Game game = gameRepository.findById(Utils.getGameIdFromSecretCode(gameCode))
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));

        Map<String, Object> gameState = new HashMap<>();
        gameState.put(Constants.CURR_ROUND_NUM, game.currentRound().getRoundNumber());
        gameState.put(Constants.ROUND_STATE, game.getGameStatus());
        gameState.put(Constants.ROUND_STATS, game.getCurrRoundStats());
        gameState.put(Constants.GAME_STATS, game.getGameStats());

        return gameState;
    }


    @GetMapping("/leave/{username}/{gc}")
    public void leaveGame(@PathVariable(value = "username") String username,
                          @PathVariable(value = "gc") String gameCode)
            throws InvalidGameException, PlayerNotFoundException {

        Game game = gameRepository.findById(Utils.getGameIdFromSecretCode(gameCode))
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        game.leavePlayer(player);
        gameRepository.save(game);
    }

    @GetMapping("/get-ready/{username}/{gc}")
    public String getReady(@PathVariable(value = "username") String username,
                         @PathVariable(value = "gc") String gameCode)
            throws InvalidGameException, PlayerNotFoundException, InvalidActionForGameStateExeption, InvalidSelectionException {

        Game game = gameRepository.findById(Utils.getGameIdFromSecretCode(gameCode))
                .orElseThrow(() -> new InvalidGameException("Invalid GameCode"));
        Player player = playerRepository.findByUserName(username)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));

        game.getReady(player);
        gameRepository.save(game);

        return "Success";
    }

}

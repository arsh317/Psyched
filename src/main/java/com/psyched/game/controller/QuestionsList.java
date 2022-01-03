package com.psyched.game.controller;

import com.psyched.game.model.GameMode;
import com.psyched.game.model.Question;
import com.psyched.game.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestionsList {
    @Autowired
    private QuestionRepository questionRepository;

    private static QuestionsList INST = null;
    private Map<GameMode, List<Question>> gameModeToQuestions = new HashMap<>();

    private QuestionsList(){
        for(GameMode gameMode : GameMode.values()){
            gameModeToQuestions.put(gameMode, questionRepository.findByGameMode(gameMode));
        }
    }

    public static QuestionsList getInstance(){
        if(INST != null){
            return INST;
        }
        return new QuestionsList();
    }

    public Question getRandomQuestion(GameMode gameMode){
        Random rand = new Random();
        int randInd = rand.nextInt(gameModeToQuestions.get(gameMode).size());
        return gameModeToQuestions.get(gameMode).get(randInd);
    }
}

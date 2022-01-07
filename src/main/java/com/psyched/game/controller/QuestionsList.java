package com.psyched.game.controller;

import com.psyched.game.model.GameMode;
import com.psyched.game.model.Question;
import com.psyched.game.repository.QuestionRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class QuestionsList {

    private QuestionRepository questionRepository;

    private static QuestionsList INST = null;
    private Map<GameMode, List<Question>> gameModeToQuestions = new HashMap<>();

    public QuestionsList(){
    }

    private QuestionsList(QuestionsListBuilder questionsListBuilder){
        this.questionRepository = questionsListBuilder.questionRepository;
        this.INST = this;
        this.initialize();
    }

    private void initialize(){
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

    public static final class QuestionsListBuilder {
        private QuestionRepository questionRepository;

        public QuestionsListBuilder questionRepository(QuestionRepository questionRepository) {
            this.questionRepository = questionRepository;
            return this;
        }

        public QuestionsList build() {
            QuestionsList questionsList = new QuestionsList(this);
            return questionsList;
        }
    }
}

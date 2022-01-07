package com.psyched.game.controller;

import com.psyched.game.model.GameMode;
import com.psyched.game.model.Question;
import com.psyched.game.repository.QuestionRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dev")
public class QuestionController {
    @Autowired
    @Getter
    private QuestionRepository questionRepository;

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @GetMapping("/questions/{id}")
    public Question getQuestionsById(@PathVariable(value = "id") Long id) throws Exception {
        return questionRepository.findById(id).orElseThrow(Exception::new);
    }

    @GetMapping("/initialize-questions")
    public String initializeQuestionsList() {
        new QuestionsList.QuestionsListBuilder().questionRepository(questionRepository)
                .build();
        return "Initialized successfully";
    }
}

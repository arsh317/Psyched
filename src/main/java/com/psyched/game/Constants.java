package com.psyched.game;

import com.psyched.game.model.GameMode;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int MAX_ROUNDS = 100;
    public static final int MAX_QUESTION_LENGTH = 1000;
    public static final int MAX_ANSWER_LENGTH = 1000;
    public static final int MAX_QUESTIONS_TO_READ = 100;
    public static final int CORRECT_ANSWER_SCORE = 2;
    public static final int PSYCHED_OTHER_SCORE = 1;

    public static final String SCORES = "scores";
    public static final String ROUND_STATS = "round_stats";
    public static final String ROUND_STATE = "round_state";
    public static final String CURR_ROUND_NUM = "curr_round_num";
    public static final String GAME_STATS = "game_stats";
    public static final String PSYCHED_COUNT = "psyched_count";
    public static final String PSYCHED_BY = "psyched_by";
    public static final String IS_CORRECT_ANS = "is_correct_ans";
    public static final String IS_ELLEN_ANS = "is_ellen_ans";

    public static final String WORDS_FILE = "words.txt";

    public static final Map<String, GameMode> QA_FILES = new HashMap<>();
    public static final Map<Integer, GameMode> GAME_MODE_MAP = new HashMap<>();

    public static GameMode gameModeIdToGameMode(int gameModeId){
        return GAME_MODE_MAP.get(gameModeId);
    }

    static {
        GAME_MODE_MAP.put(1, GameMode.IS_THIS_A_FACT);
        GAME_MODE_MAP.put(2, GameMode.UNSCRAMBLE);
        GAME_MODE_MAP.put(3, GameMode.WORD_UP);

        QA_FILES.put("qa_facts.txt", GameMode.IS_THIS_A_FACT);
        QA_FILES.put("qa_unscramble.txt", GameMode.UNSCRAMBLE);
        QA_FILES.put("qa_word_up.txt", GameMode.WORD_UP);
    }
}
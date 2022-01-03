package com.psyched.game;

import com.psyched.game.model.GameMode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    private static List<String> wordsList = new ArrayList<>();
    private static Map<String, Integer> wordsMap = new HashMap<>();

    static {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.WORDS_FILE));
            String word;

            int ind = 0;
            do{
                word = bufferedReader.readLine();
                wordsList.add(word);
                wordsMap.put(word, ind);

                ind++;
            }while(word != null);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Pair<String, String>> readQAFile(String filename) {

        String question, answer;
        List<Pair<String, String>> question_answers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            do {
                question = br.readLine();
                answer = br.readLine();
                if (question == null || answer == null || question.length() > Constants.MAX_QUESTION_LENGTH -1 || answer.length() > Constants.MAX_ANSWER_LENGTH -1) {
                    System.out.println("Skipping question: " + question);
                    System.out.println("Skipping answer: " + answer);
                    continue;
                }
                question_answers.add(new Pair<>(question, answer));
            } while (question != null & answer != null);
        } catch (IOException ignored) {
        }
        return question_answers;
    }

    public static String getSecretCodeFromGameId(Long gameId){
        String code = "";
        while(gameId > 0){
            code += wordsList.get((int) (gameId % wordsList.size()));
            gameId /= wordsList.size();
        }

        return code;
    }

    public static Long getGameIdFromSecretCode(String code){
        List<String> codeWords = List.of(code.split(" "));

        Long gameId = 0L;
        for(String codeWord : codeWords){
            gameId = gameId * wordsList.size() + wordsMap.get(codeWord);
        }

        return gameId;
    }

    public static boolean isMatches(String a, String b) {
        return a.toLowerCase().equals(b.toLowerCase());
    }
}


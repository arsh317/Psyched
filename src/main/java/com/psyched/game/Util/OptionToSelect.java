package com.psyched.game.Util;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class OptionToSelect {

    @Getter
    @Setter
    Long answerId;

    @Getter
    @Setter
    @NonNull
    String answerText;

    @Getter
    @Setter
    Boolean isCorrect = false;

    @Getter
    @Setter
    Boolean isEllenAnswer = false;

    public static final class OptionToSelectBuilder {
        Long answerId = null;
        String answerText;
        Boolean isCorrect = false;
        Boolean isEllenAnswer = false;

        public OptionToSelectBuilder answerId(Long answerId) {
            this.answerId = answerId;
            return this;
        }

        public OptionToSelectBuilder answerText(String answerText) {
            this.answerText = answerText;
            return this;
        }

        public OptionToSelectBuilder isCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public OptionToSelectBuilder isEllenAnswer(Boolean isEllenAnswer) {
            this.isEllenAnswer = isEllenAnswer;
            return this;
        }

        public OptionToSelect build() {
            OptionToSelect optionToSelect = new OptionToSelect();
            optionToSelect.setAnswerId(answerId);
            optionToSelect.setAnswerText(answerText);
            optionToSelect.setIsCorrect(isCorrect);
            optionToSelect.setIsEllenAnswer(isEllenAnswer);
            return optionToSelect;
        }
    }
}

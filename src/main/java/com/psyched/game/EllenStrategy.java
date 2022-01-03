package com.psyched.game;

import com.psyched.game.model.EllenAnswer;
import com.psyched.game.model.Round;

public interface EllenStrategy {
    EllenAnswer getAnswer(Round round);
}

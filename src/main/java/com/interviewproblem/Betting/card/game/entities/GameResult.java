package com.interviewproblem.Betting.card.game.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResult {
    private Card drawnCard;
    private boolean win;
    private int balance;
}

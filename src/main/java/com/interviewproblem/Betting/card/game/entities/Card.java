package com.interviewproblem.Betting.card.game.entities;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private String rank;
    private String suit;

    @Override
    public String toString() {
        return "Card {" +
                "rank='" + rank + '\'' +
                ", suit='" + suit + '\'' +
                '}';
    }
}

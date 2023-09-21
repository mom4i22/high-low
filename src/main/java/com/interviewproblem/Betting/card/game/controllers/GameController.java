package com.interviewproblem.Betting.card.game.controllers;

import com.interviewproblem.Betting.card.game.entities.Card;
import com.interviewproblem.Betting.card.game.entities.Deck;
import com.interviewproblem.Betting.card.game.entities.GameResult;
import com.interviewproblem.Betting.card.game.entities.Player;
import com.interviewproblem.Betting.card.game.models.PlaceBetRequest;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class GameController {

    private Deck deck;
    private Player player;

    @GetMapping("/start")
    public ResponseEntity<?> startGame(@RequestParam int initialBalance) {
        if(initialBalance <= 0) {
            return ResponseEntity.badRequest().body("You can't start the game with a negative balance or a balance that equals 0!");
        }
        deck = new Deck();
        deck.shuffle();

        Card drawnCard = deck.drawTopCard();
        player = new Player(initialBalance, drawnCard);
        return ResponseEntity.ok(drawnCard);
    }

    @PostMapping("/shuffle")
    public ResponseEntity<?> shuffleDeck() {
        if(player == null) {
            return ResponseEntity.badRequest().body("Start the game first to be able to shuffle the deck!");
        }
        if(player.getBalance() <= 0) {
            return ResponseEntity.badRequest().body("Player balance must be more than 0 to shuffle the deck!");
        }
        deck = new Deck();
        deck.shuffle();

        Card drawnCard = deck.drawTopCard();
        player.setDrawnCard(drawnCard);

        return ResponseEntity.ok("Used cards have been put back into the deck and the whole deck has been shuffled. The first drawn card is: " + drawnCard.toString());
    }

    @PostMapping("/bet")
    public ResponseEntity<?> placeBet(@RequestBody PlaceBetRequest placeBetRequest) {
        if(player == null) {
            return ResponseEntity.badRequest().body("Start the game first to be able to place a bet!");
        }
        if(placeBetRequest.getBetAmount() < 0) {
            return ResponseEntity.badRequest().body("You can't bet a negative amount!");
        }
        if(placeBetRequest.getBetAmount() > player.getBalance()) {
            return ResponseEntity.badRequest().body("Your balance is lower than the bet you want to place! Try placing a lower bet or start a new game with a new balance!");
        }
        Card currentCard = deck.drawTopCard();
        if(currentCard == null) {
            return ResponseEntity.badRequest().body("Deck is empty. Shuffle to continue with current balance or start a new game with a new balance!");
        }
        // Check if the player's guess is "higher," "lower," or "same" as the previous card
        String comparison = compareCards(player.getDrawnCard(), currentCard);
        int outcome;
        if (placeBetRequest.getGuess().equalsIgnoreCase(comparison)) {
            outcome = placeBetRequest.getBetAmount();
        } else if (comparison.equalsIgnoreCase("same")) {
            outcome = 0;
        } else {
            outcome = -placeBetRequest.getBetAmount();
        }

        int currentBalance = player.getBalance();
        int newBalance = currentBalance + outcome;
        player.setBalance(newBalance);

        GameResult gameResult = new GameResult(currentCard, outcome > 0, newBalance);
        player.setDrawnCard(currentCard);

        return ResponseEntity.ok(gameResult);
    }

    // Helper method to compare two cards based on their rank
    private String compareCards(Card card1, Card card2) {
        String[] rankOrder = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int rankIndex1 = Arrays.asList(rankOrder).indexOf(card1.getRank());
        int rankIndex2 = Arrays.asList(rankOrder).indexOf(card2.getRank());

        if (rankIndex2 > rankIndex1) {
            return "higher";
        } else if (rankIndex1 > rankIndex2) {
            return "lower";
        } else {
            return "same";
        }
    }
}

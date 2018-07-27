package com.example.dominion;

public class BureaucratAttack {

    String playerName;
    int playerNumber;
    boolean blocked;
    boolean victoryInHand;
    String cardOnDeck;

    public BureaucratAttack(int playerNumber, String playerName){
        this.playerName = playerName;
        this.playerNumber = playerNumber;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setVictoryInHand(boolean victoryInHand) {
        this.victoryInHand = victoryInHand;
    }

    public void setCardOnDeck(String cardOnDeck) {
        this.cardOnDeck = cardOnDeck;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isVictoryInHand() {
        return victoryInHand;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getCardOnDeck() {
        return cardOnDeck;
    }
}

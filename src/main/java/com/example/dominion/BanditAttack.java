package com.example.dominion;

public class BanditAttack {
    private String playerName;
    private int playerNumber;
    private String card1;
    private String card2;
    private int trashed = 2;
    private boolean blocked = false;

    BanditAttack(int playerNumber, String playerName){
        this.playerName = playerName;
        this.playerNumber = playerNumber;
    }

    public void setCard1(String card1) {
        this.card1 = card1;
    }

    public void setCard2(String card2) {
        this.card2 = card2;
    }

    public void setTrashed(int trashed) {
        this.trashed = trashed;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getCard1() {
        return card1;
    }

    public String getCard2() {
        return card2;
    }

    public int getTrashed() {
        return trashed;
    }

    public boolean isBlocked() {
        return blocked;
    }
}

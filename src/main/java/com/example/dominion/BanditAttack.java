package com.example.dominion;

public class BanditAttack {
    private String playerName;
    private String card1;
    private String card2;
    private int trashed = 2;

    BanditAttack(String playerName){
        this.playerName = playerName;
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

    public String getPlayerName() {
        return playerName;
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
}

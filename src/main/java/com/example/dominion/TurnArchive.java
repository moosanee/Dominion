package com.example.dominion;

public class TurnArchive {

    Player player;
    Turn turn;
    int turnNumber;

    TurnArchive(Player player, Turn turn, int turnNumber){
        this.player = player;
        this.turn = turn;
        this.turnNumber = turnNumber;
    }

}

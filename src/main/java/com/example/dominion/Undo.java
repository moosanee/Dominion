package com.example.dominion;

import android.view.View;

import java.util.ArrayList;

public class Undo {

    private String description;
    private Player player;
    private Turn turn;
    private int trashed;
    private int undoPhase;
    private ArrayList<CardData> cardDataList;
    private ListenerSwitches listenerSwitches;
    private View.OnTouchListener onTouchListener;

    public Undo (Player player, String description, Turn turn, ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (Player player, String description, Turn turn, int trashed, ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.trashed = trashed;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (Player player, String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.cardDataList = cardDataList;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (Player player, String description, Turn turn, int undoPhase, View.OnTouchListener onTouchListener,
                 ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.onTouchListener = onTouchListener;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (Player player, String description, Turn turn, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.cardDataList = cardDataList;
        this.onTouchListener = onTouchListener;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (Player player, String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, ListenerSwitches listenerSwitches){
        this.player = player;
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.cardDataList = cardDataList;
        this.onTouchListener = onTouchListener;
        this.listenerSwitches = listenerSwitches;
    }

    public void undoAction(){
        String[] words = description.split("\\s+");
        String source = "";
        if (words[0].equals("moved")) {
            description = words[0] + " " + words[2] + " " + words[3];
            source = words[1];
        }
        switch (description){
            case "start buying phase":
                turn.startActionPhase(listenerSwitches);
                break;
            case "start clean up phase":
                turn.startOpenBankPhase(listenerSwitches);
                break;
            case "play all treasures":
                turn.unplayTreasures(cardDataList, onTouchListener, listenerSwitches);
                turn.startBuyingPhase(listenerSwitches);
                break;
            case "finish chapel":
                turn.returnToChapelTrashing(trashed, listenerSwitches);
                break;
            case "moved to discard":
                turn.undoNewCardInDiscard(source, undoPhase, onTouchListener, listenerSwitches);
                break;
            case "moved to inPlay":
                turn.undoNewCardInPlay(source, undoPhase, onTouchListener, listenerSwitches);
                break;
            case "moved to trash":
                turn.undoNewCardInTrash(source, undoPhase, onTouchListener, listenerSwitches);
                break;
        }
    }

}

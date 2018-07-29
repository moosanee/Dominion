package com.example.dominion;

import android.view.View;

import java.util.ArrayList;

public class Undo {

    private String description;
    private Turn turn;
    private int cardsMoved;
    private int undoPhase;
    private ArrayList<CardData> cardDataList;
    private ListenerSwitches listenerSwitches;
    private View.OnTouchListener onTouchListener;

    public Undo (String description, Turn turn, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int cardsMoved, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.cardsMoved = cardsMoved;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.cardDataList = cardDataList;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int undoPhase, View.OnTouchListener onTouchListener,
                 ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.onTouchListener = onTouchListener;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.cardDataList = cardDataList;
        this.onTouchListener = onTouchListener;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, ListenerSwitches listenerSwitches){
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
                turn.returnToChapelTrashing(cardsMoved, listenerSwitches);
                break;
            case "put deck in discard":
                turn.undoDeckToDiscard(cardsMoved, listenerSwitches);
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
            case "moved to deck":
                turn.undoNewCardOnDeck(source, undoPhase, onTouchListener, listenerSwitches);
                break;
        }
    }

}

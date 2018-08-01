package com.example.dominion;

import android.view.View;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class Undo {

    private String description;
    private Turn turn;
    private int cardsMoved;
    private int undoPhase;
    private ArrayList<CardData> cardDataList;
    private ArrayList<ArrayList<String>> pileLists;
    boolean[] playerToggles;
    private ListenerSwitches listenerSwitches;
    private View.OnTouchListener onTouchListener;
    private BasicCards basicCardSet;

    public Undo (String description, Turn turn, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.listenerSwitches = listenerSwitches;
        this.basicCardSet = basicCardSet;
    }
    public Undo (String description, Turn turn, int cardsMoved, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.cardsMoved = cardsMoved;
        this.listenerSwitches = listenerSwitches;
        this.basicCardSet = basicCardSet;
    }
    public Undo (String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.cardDataList = cardDataList;
        this.basicCardSet = basicCardSet;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, boolean[] playerToggles,
                 View.OnTouchListener onTouchListener, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.playerToggles = playerToggles;
        this.onTouchListener = onTouchListener;
        this.basicCardSet = basicCardSet;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int undoPhase, View.OnTouchListener onTouchListener,
                 BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.onTouchListener = onTouchListener;
        this.basicCardSet = basicCardSet;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.cardDataList = cardDataList;
        this.onTouchListener = onTouchListener;
        this.basicCardSet = basicCardSet;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, Turn turn, int undoPhase, ArrayList<CardData> cardDataList,
                 View.OnTouchListener onTouchListener, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.cardDataList = cardDataList;
        this.onTouchListener = onTouchListener;
        this.basicCardSet = basicCardSet;
        this.listenerSwitches = listenerSwitches;
    }
    public Undo (String description, int undoPhase, Turn turn, ArrayList<ArrayList<String>> pileLists,
                 View.OnTouchListener onTouchListener, BasicCards basicCardSet, ListenerSwitches listenerSwitches){
        this.description = description;
        this.turn = turn;
        this.undoPhase = undoPhase;
        this.pileLists = pileLists;
        this.onTouchListener = onTouchListener;
        this.basicCardSet = basicCardSet;
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
                basicCardSet.returnToChapelTrashing(cardsMoved, turn, listenerSwitches);
                break;
            case "put deck in discard":
                basicCardSet.undoDeckToDiscard(cardsMoved, turn, listenerSwitches);
                break;
            case "other players drew a card":
                basicCardSet.undoCouncilRoom(playerToggles, turn, onTouchListener, listenerSwitches);
                break;
            case "played a feast":
                turn.undoNewCardInPlay("feast", FEAST, onTouchListener, listenerSwitches);
                break;
            case "played a library":
                basicCardSet.undoLibrary(pileLists, turn, onTouchListener, listenerSwitches);
                break;
            case "browsed discard":
                turn.undoNewCardInPlay("harbinger", HARBINGER, onTouchListener, listenerSwitches);
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

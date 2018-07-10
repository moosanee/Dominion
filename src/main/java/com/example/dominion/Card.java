package com.example.dominion;

import java.io.Serializable;

public class Card implements Serializable{

    private String name;
    private String type;
    private String expansionName;
    private int cost;
    private int value;
    private int drawCards;
    private int actions;
    private int extraBuys;
    private int extraCoins;
    private String instructions;
    private int victoryPoints;
    private boolean trasher;
    private static final long serialVersionUID = 100L;

    Card(String name, String type, String expansionName, int cost, int value, int drawCards,
                int actions, int extraBuys, int extraCoins, String instructions, int victoryPoints,
                boolean trasher){
        this.name = name;
        this.type = type;
        this.expansionName = expansionName;
        this.cost = cost;
        this.value = value;
        this.drawCards = drawCards;
        this.actions = actions;
        this.extraBuys = extraBuys;
        this.extraCoins = extraCoins;
        this.instructions = instructions;
        this.victoryPoints = victoryPoints;
        this.trasher = trasher;
    }

    Card() {

    }

    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
    public String getExpansionName(){
        return expansionName;
    }
    public int getCost(){
        return cost;
    }
    public int getValue(){
        return value;
    }
    public int getDrawCards(){
        return drawCards;
    }
    public int getActions(){
        return actions;
    }
    public int getExtraBuys(){
        return extraBuys;
    }
    public int getExtraCoins(){
        return extraCoins;
    }
    public String getInstructions(){
        return instructions;
    }
    public int getVictoryPoints(){
        return victoryPoints;
    }
    public void setVictoryPoints(int vp){
        victoryPoints = vp;
    }
    public boolean isTrasher() {return trasher;}

}

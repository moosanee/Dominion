package com.example.dominion;

import java.io.Serializable;
import java.util.ArrayList;

public class GameCardStats implements Serializable{
    private int[] cardCostTally = new int[15];
    private int[] cardDrawTally = new int[5];
    private int[] extraActionTally = new int[5];
    private int[] extraBuysTally = new int[5];
    private int[] extraCoinTally = new int[5];
    private int trashTally = 0;
    private ArrayList<CardTypeTally> cardTypeTally= new ArrayList<>();
    private ArrayList<ExpansionTally> expansionTally= new ArrayList<>();


    GameCardStats(){

        CardTypeTally treasure = new CardTypeTally("treasure", 0);
        cardTypeTally.add(treasure);
        CardTypeTally victory = new CardTypeTally("victory", 0);
        cardTypeTally.add(victory);
        CardTypeTally action = new CardTypeTally("action", 0);
        cardTypeTally.add(action);
        CardTypeTally actionAttack = new CardTypeTally("action - attack", 0);
        cardTypeTally.add(actionAttack);
        CardTypeTally curse = new CardTypeTally("curse", 0);
        cardTypeTally.add(curse);
        CardTypeTally actionReaction = new CardTypeTally("action - reaction", 0);
        cardTypeTally.add(actionReaction);
        ExpansionTally basic = new ExpansionTally("basic", 0);
        expansionTally.add(basic);
        ExpansionTally prosperity = new ExpansionTally("prosperity", 0);
        expansionTally.add(prosperity);
        ExpansionTally intrigue = new ExpansionTally("intrigue", 0);
        expansionTally.add(intrigue);
        ExpansionTally darkAges = new ExpansionTally("darkAges", 0);
        expansionTally.add(darkAges);
        ExpansionTally seaside = new ExpansionTally("seaside", 0);
        expansionTally.add(seaside);
        ExpansionTally alchemy = new ExpansionTally("alchemy", 0);
        expansionTally.add(alchemy);
        ExpansionTally cornucopia = new ExpansionTally("cornucopia", 0);
        expansionTally.add(cornucopia);
        ExpansionTally hinterlands = new ExpansionTally("hinterlands", 0);
        expansionTally.add(hinterlands);
        ExpansionTally guilds = new ExpansionTally("guilds", 0);
        expansionTally.add(guilds);
        ExpansionTally adventures = new ExpansionTally("adventures", 0);
        expansionTally.add(adventures);
        ExpansionTally empires = new ExpansionTally("empires", 0);
        expansionTally.add(empires);
        ExpansionTally nocturne = new ExpansionTally("nocturne", 0);
        expansionTally.add(nocturne);
        ExpansionTally promo = new ExpansionTally("promo", 0);
        expansionTally.add(promo);


    }
    public void calculateGameCardStats(ArrayList<Card> gameCards){

        //reset tallies to zero
        for(int i = 0; i < 15; i++) cardCostTally[i] = 0;
        for(int i = 0; i < 5; i++) cardDrawTally[i] = 0;
        for(int i = 0; i < 5; i++) extraActionTally[i] = 0;
        for(int i = 0; i < 5; i++) extraBuysTally[i] = 0;
        for(int i = 0; i < 5; i++) extraCoinTally[i] = 0;
        trashTally = 0;
        for (int i = 0; i < cardTypeTally.size(); i++) cardTypeTally.get(i).setTypeTally(0);
        for (int i = 0; i < expansionTally.size(); i++)expansionTally.get(i).setExpansionTally(0);

        //tally up game card stats
        for (int i = 0; i < gameCards.size(); i++){
            int cost = gameCards.get(i).getCost();
            if (cost > 0) cardCostTally[cost-1] +=1;
            int draw = gameCards.get(i).getDrawCards();
            if (draw > 0) cardDrawTally[draw-1] +=1;
            int cards = gameCards.get(i).getActions();
            if (cards > 0) extraActionTally[cards-1] +=1;
            int buys = gameCards.get(i).getExtraBuys();
            if (buys > 0) extraBuysTally[buys-1] +=1;
            int coins = gameCards.get(i).getExtraCoins();
            if (coins > 0) extraCoinTally[coins-1] +=1;
            if (gameCards.get(i).isTrasher()) trashTally +=1;
            for (int j=0;j<cardTypeTally.size();j++){
                String string = cardTypeTally.get(j).getTypeName();
                if (string.charAt(string.length()-1) == '+' ||
                        string.charAt(string.length()-1) == '-')
                    string = string.substring(0, string.length()-2);
                if(gameCards.get(i).getType().equals(string)) cardTypeTally.get(j).add1TypeTally();
            }
            for (int j=0;j<expansionTally.size();j++){
                if(gameCards.get(i).getType().equals(expansionTally.get(j).getExpansionName())){
                    expansionTally.get(j).add1ExpansionTally();
                }
            }
        }

    }

    public ArrayList<CardTypeTally> getcardTypeTally() {
        return cardTypeTally;
    }

    public ArrayList<ExpansionTally> getexpansionTally() {
        return expansionTally;
    }

    public int getTrashTally() {
        return trashTally;
    }

    public int[] getCardCostTally() {
        return cardCostTally;
    }

    public int[] getCardDrawTally() {
        return cardDrawTally;
    }

    public int[] getExtraActionTally() {
        return extraActionTally;
    }

    public int[] getExtraBuysTally() {
        return extraBuysTally;
    }

    public int[] getExtraCoinTally() {
        return extraCoinTally;
    }
}

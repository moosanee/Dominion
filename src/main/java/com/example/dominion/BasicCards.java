package com.example.dominion;

import java.io.Serializable;
import java.util.ArrayList;

public class BasicCards implements Serializable{

    public Card copper;
    public Card silver;
    public Card gold;
    public Card curse;
    public Card estate;
    public Card duchy;
    public Card province;
    public Card trash;
    public Card adventurer;
    public Card artisan;
    public Card bandit;
    public Card bureaucrat;
    public Card cellar;
    public Card chancellor;
    public Card chapel;
    public Card councilRoom;
    public Card feast;
    public Card festival;
    public Card gardens;
    public Card harbinger;
    public Card laboratory;
    public Card library;
    public Card market;
    public Card merchant;
    public Card militia;
    public Card mine;
    public Card moat;
    public Card moneyLender;
    public Card poacher;
    public Card remodel;
    public Card sentry;
    public Card smithy;
    public Card spy;
    public Card thief;
    public Card throneRoom;
    public Card vassal;
    public Card village;
    public Card witch;
    public Card woodcutter;
    public Card workshop;
    private ArrayList<Card> basicCardList = new ArrayList<>();
    private static final long serialVersionUID = 100L;

    BasicCards(){

        String instructions;

        copper = new Card("copper", "treasure", "basic", 0,
                1, 0, 0, 0, 0, "",
                0, false);
        basicCardList.add(copper);

        silver = new Card("silver", "treasure", "basic", 3,
                2, 0, 0, 0, 0, "",
                0, false);
        basicCardList.add(silver);

        gold = new Card("gold", "treasure", "basic", 6, 3,
                0, 0, 0, 0, "", 0,
                false);
        basicCardList.add(gold);

        curse = new Card("curse", "curse", "basic", 0, 0,
                0, 0, 0, 0, "", -1,
                false);
        basicCardList.add(curse);

        estate = new Card("estate", "victory", "basic", 2,
                0, 0, 0, 0, 0, "",
                1, false);
        basicCardList.add(estate);

        duchy = new Card("duchy", "victory", "basic", 5,
                0, 0, 0, 0, 0, "",
                3, false);
        basicCardList.add(duchy);

        province = new Card("province", "victory", "basic", 8,
                0, 0, 0, 0, 0, "",
                6, false);
        basicCardList.add(province);

        trash = new Card("trash", "none", "basic", 0, 0,
                0, 0, 0, 0, "", 0,
                false);
        basicCardList.add(trash);

        instructions = "Reveal cards from your deck until you reveal 2 treasure cards. " +
                "Put those treasure cards into your hand and discard the other revealed cards.";
        adventurer = new Card("adventurer", "action", "basic-",
                6, 0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(adventurer);

        instructions = "Gain a card to your hand costing up to 5 coins. Put a card " +
                "from your hand onto your deck.";
        artisan = new Card("artisan", "action", "basic+",
                6, 0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(artisan);

        instructions = "Gain a Gold. Each other player reveals the top 2 cards of their deck, " +
                "trashes a revealed Treasure other than Copper, and discards the rest.";
        bandit = new Card("bandit", "action - attack", "basic+",
                5, 0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(bandit);

        instructions = "Gain a silver card; put it on top of your deck. Each other player reveals " +
                "a Victory card from his hand and puts it on his deck (or reaveals a hand with " +
                "no victory cards).";
        bureaucrat = new Card("bureaucrat", "action - attack",
                "basic", 4, 0, 0, 0, 0,
                0, instructions, 0, false);
        basicCardList.add(bureaucrat);

        instructions = "Discard any number of cards. +1 Card per card discarded.";
        cellar = new Card("cellar", "action", "basic", 2,
                0, 0, 1, 0, 0, instructions,
                0, false);
        basicCardList.add(cellar);

        instructions = "You may immediately put your deck into your discard pile.";
        chancellor = new Card("chancellor", "action", "basic-",
                3, 0, 0, 1, 0, 2, instructions,
                0, false);
        basicCardList.add(chancellor);

        instructions = "Trash up to 4 cards from your hand.";
        chapel = new Card("chapel", "action", "basic", 2,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(chapel);

        instructions = "Each other player draws a card.";
        councilRoom = new Card("councilRoom", "action", "basic",
                5, 0, 4, 0, 1, 0, instructions,
                0, false);
        basicCardList.add(councilRoom);

        instructions = "Trash this card. Gain a card costing up to 5 coins.";
        feast = new Card("feast", "action", "basic-", 4,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(feast);

        festival = new Card("festival", "action", "basic", 5,
                0, 0, 2, 1, 2, "",
                0, false);
        basicCardList.add(festival);

        instructions = "Worth 1 VP for every 10 cards in your deck (rounded down).";
        gardens = new Card("gardens", "victory", "basic", 4,
                0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(gardens);

        instructions = "Look through your discard pile. You may put a card from it onto you deck.";
        harbinger = new Card("harbinger", "action", "basic+",
                3, 0, 1, 1, 0, 0, instructions,
                0, false);
        basicCardList.add(harbinger);

        laboratory = new Card("laboratory", "action", "basic", 5,
                0, 2, 1, 0, 0, "",
                0, false);
        basicCardList.add(laboratory);

        instructions = "Draw until you have cards in hand. You may set aside any ction cards " +
                "drawn in this way, as you draw them; discard the set aside cards after you " +
                "finish drawing.";
        library = new Card("library", "action", "basic", 5,
                0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(library);

        market = new Card("market", "action", "basic", 5,
                0, 1, 1, 1, 1, "",
                0, false);
        basicCardList.add(market);

        instructions = "The first time you play a Silver this turn, +1 coin";
        merchant = new Card("merchant", "action", "basic+",
                3, 0, 1, 1, 0, 0, instructions,
                0, false);
        basicCardList.add(merchant);

        instructions = "Each other player discards down to 3 cards in his hand.";
        militia = new Card("militia", "action - attack", "basic", 4,
                0, 0, 0, 0, 2, instructions,
                0, false);
        basicCardList.add(militia);

        instructions = "Trash a Treasure card from your hand. Gain a Treasure card costing up " +
                "to 3 coins more; put it into your hand";
        mine = new Card("mine", "action", "basic", 5,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(mine);

        instructions = "When another player plays an attack card, you may reveal this from " +
                "your hand. If you do, you are unaffected by that attack.";
        moat = new Card("moat", "action - reaction", "basic", 2,
                0, 2, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(moat);

        instructions = "Trash a copperfrom your hand. If you do, +3 coins";
        moneyLender = new Card("moneyLender", "action", "basic", 4,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(moneyLender);

        instructions = "Discard a card per empty Supply pile";
        poacher = new Card("poacher", "action", "basic+",
                4, 0, 1, 1, 0, 1, instructions,
                0, false);
        basicCardList.add(poacher);

        instructions = "Trash a card from your hand. Gain a card costing up to 2 coins more " +
                "than the trashed card.";
        remodel = new Card("remodel", "action", "basic", 4,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(remodel);

        instructions = "Look at the top 2 cards of your deck. Trash and/or discard any number " +
                "of them. Put the rest back in any order.";
        sentry = new Card("sentry", "action", "basic+",
                5, 0, 1, 1, 0, 0, instructions,
                0, true);
        basicCardList.add(sentry);

        smithy = new Card("smithy", "action", "basic", 4,
                0, 3, 0, 0, 0, "",
                0, false);
        basicCardList.add(smithy);

        instructions = "Each player (including you) reveals the top card of his deck and " +
                "either discards it or puts it back, your choice.";
        spy = new Card("spy", "action - attack", "basic-", 4,
                0, 1, 1, 0, 0, instructions,
                0, false);
        basicCardList.add(spy);

        instructions = "Each other player reveals the top two cards of his deck. If they " +
                "revealed any Treasure cards, they trash one of them that you choose. You " +
                "may gain any or all of these trashed cards. They discard the other revealed cards";
        thief = new Card("thief", "action - attack", "basic-", 4,
                0, 0, 0, 0, 0, instructions,
                0, true);
        basicCardList.add(thief);

        instructions = "Choose an Action card in your hand. Play it twice.";
        throneRoom = new Card("throneRoom", "action", "basic", 4,
                0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(throneRoom);

        instructions = "Discard the top card of your deck. If it is an Action card, you may " +
                "play it.";
        vassal = new Card("vassal", "action", "basic+",
                3, 0, 0, 0, 0, 2, instructions,
                0, false);
        basicCardList.add(vassal);

        village = new Card("village", "action", "basic", 3,
                0, 1, 2, 0, 0, "",
                0, false);
        basicCardList.add(village);

        instructions = "Each other player gets a curse card";
        witch = new Card("witch", "action - attack", "basic", 5,
                0, 2, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(witch);

        woodcutter = new Card("woodcutter", "action", "basic-", 3,
                0, 0, 0, 1, 2, "",
                0, false);
        basicCardList.add(woodcutter);

        instructions = "Gain a card costing up to 4 coins.";
        workshop = new Card("workshop", "action", "basic", 3,
                0, 0, 0, 0, 0, instructions,
                0, false);
        basicCardList.add(workshop);

    }

    public Card getCard(String cardName){
        Card soughtCard = new Card();
        for (Card card: basicCardList){
            if (card.getName().equals(cardName)) soughtCard = card;
        }
        return soughtCard;
    }

}

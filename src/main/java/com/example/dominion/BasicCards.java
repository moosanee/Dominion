package com.example.dominion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

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
    private ArrayList<String> postList = new ArrayList<>();
    private ArrayList<BanditAttack> banditAttackResult = new ArrayList<>();
    private ArrayList<BureaucratAttack> bureaucratAttackResult = new ArrayList<>();
    private ArrayList<String> revealedCards = new ArrayList<>();
    private boolean adventurerShuffle = false;
    private int discarded = 0;
    private int trashed = 0;
    private ArrayList<Card> cardsTrashed = new ArrayList<>();
    private ArrayList<Card> cardsDiscarded = new ArrayList<>();
    private int poacherCompliance = 0;
    private int councilRoomDraws = 0;

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
                3, 0, 0, 0, 0, 2, instructions,
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

        instructions = "Draw until you have 7 cards in hand. You may set aside any action cards " +
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

    public void reactToNewCardInPlay(String cardName, Turn turn, View.OnTouchListener handListener,
                                    ListenerSwitches listenerSwitches){
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (cardName) {
            case "adventurer":
                Toast.makeText(turn.context,
                        "Reveal cards from your deck until you reveal 2 treasure cards.",
                        Toast.LENGTH_SHORT).show();
                Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("apply\nadventurer");
                button.setTag(ADVENTURER);
                turn.setListeners(ALL_OFF, listenerSwitches);
                turn.phase = ADVENTURER;
                ((GameBoardActivity) turn.activity).undo
                        = new Undo("moved adventurer to inPlay",
                        turn, turn.phase, handListener, this, listenerSwitches);
                ((GameBoardActivity) turn.activity).undoButton.setClickable(true);
                ((GameBoardActivity) turn.activity).undoButton.setAlpha(1f);
                int treasureFlag = 0;
                revealedCards.clear();
                int deckSize = player.deck.size();
                int discardSize = player.discard.size();
                while (treasureFlag < 2){
                    if (deckSize > 0) {
                        CardData cardData = player.deck.get(deckSize - 1);
                        if (cardData.getCard().getType().equals("treasure")) {
                            treasureFlag += 1;
                            revealedCards.add(cardData.getCardName());
                        } else {
                            revealedCards.add(cardData.getCardName());
                        }
                        deckSize -= 1;
                    } else if (!adventurerShuffle) {
                        player.shufflePile("discard");
                        adventurerShuffle = true;
                    } else if (discardSize > 0) {
                        CardData cardData = player.discard.get(discardSize - 1);
                        if (cardData.getCard().getType().equals("treasure")){
                            treasureFlag +=1;
                            revealedCards.add(cardData.getCardName());
                        } else {
                            revealedCards.add(cardData.getCardName());
                        }
                        discardSize -= 1;
                    } else
                        break;
                }
                //popup reveal of cards with buttons
                ArrayList<String> discardCardsAdventurer = new ArrayList<>();
                Intent intent = new Intent(turn.context, RevealDialogActivity.class);
                intent.putStringArrayListExtra("revealedCardsKey", revealedCards);
                intent.putStringArrayListExtra("discardCardsKey", discardCardsAdventurer);
                intent.putExtra("phaseKey", turn.phase);
                intent.putExtra("cardsToDrawKey", 0);
                turn.activity.startActivity(intent);
                break;
            case "artisan":
                Toast.makeText(turn.context, "gain a card to your hand costing up to 5 coins",
                        Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("gain\ncard");
                button.setTag(ARTISAN1);
                turn.setListeners(BANK_TO_HAND, listenerSwitches);
                turn.phase = ARTISAN1;
                break;
            case "bandit":
                player.addCardToDiscard("gold", turn.activity, turn.context);
                ((GameBoardActivity)turn.activity).removeCardFromBankPile("gold");
                ((GameBoardActivity)turn.activity).undo = new Undo("moved bandit to inPlay",
                        turn, BANDIT, turn.bankPiles, handListener, this, listenerSwitches);
                postList.add("You gained a gold");
                banditAttackResult = reactToBanditAttack(player.getName(),
                        ((GameBoardActivity)turn.activity).playerList, turn);
                for (int i = 0; i < banditAttackResult.size(); i++){
                    if (banditAttackResult.get(i).isBlocked()){
                        postList.add(banditAttackResult.get(i).getPlayerName()
                                + " blocked the attack.");
                    } else {
                        String card1 = banditAttackResult.get(i).getCard1();
                        String card2 = banditAttackResult.get(i).getCard2();
                        if (!(card1.equals("")) && !(card2.equals(""))) {
                            postList.add(banditAttackResult.get(i).getPlayerName()
                                    + " revealed " + card1 + " and " + card2 + ".");
                            if (banditAttackResult.get(i).getTrashed() == 1) {
                                postList.add(banditAttackResult.get(i).getPlayerName()
                                        + " trashed " + card2 + ".");
                            } else if (banditAttackResult.get(i).getTrashed() == 0) {
                                postList.add(banditAttackResult.get(i).getPlayerName()
                                        + " trashed " + card1 + ".");
                            }
                        } else if (!(card1.equals(""))){
                            postList.add(banditAttackResult.get(i).getPlayerName()
                                    + " revealed " + card1 + ".");
                            if (banditAttackResult.get(i).getTrashed() == 0) {
                                postList.add(banditAttackResult.get(i).getPlayerName()
                                        + " trashed " + card1 + ".");
                            }
                        } else if(!(card2.equals(""))) {
                            postList.add(banditAttackResult.get(i).getPlayerName()
                                    + " revealed " + card2 + ".");
                            if (banditAttackResult.get(i).getTrashed() == 1) {
                                postList.add(banditAttackResult.get(i).getPlayerName()
                                        + " trashed " + card2 + ".");
                            }
                        } else {
                            postList.add(banditAttackResult.get(i).getPlayerName()
                                    + " had no cards to reveal.");
                        }
                    }
                }
                intent = new Intent(turn.context, NotificationActivity.class);
                intent.putExtra("postListKey", postList);
                turn.activity.startActivity(intent);
                postList.clear();
                break;
            case "bureaucrat":
                player.addCardToDeck("silver", turn.activity, turn.context);
                ((GameBoardActivity)turn.activity).removeCardFromBankPile("silver");
                ((GameBoardActivity)turn.activity).undo
                        = new Undo("moved bureaucrat to inPlay",
                        turn, BUREAUCRAT, turn.bankPiles, handListener, this,
                        listenerSwitches);
                postList.add("You gained a silver to your deck");
                bureaucratAttackResult = reactToBureaucratAttack(player.getName(),
                        ((GameBoardActivity)turn.activity).playerList, turn);
                for (int i = 0; i < bureaucratAttackResult.size(); i++) {
                    if (bureaucratAttackResult.get(i).isBlocked()) {
                        postList.add(bureaucratAttackResult.get(i).getPlayerName()
                                + " blocked the attack.");
                    } else {
                        String playerName = bureaucratAttackResult.get(i).getPlayerName();
                        cardName = bureaucratAttackResult.get(i).getCardOnDeck();
                        if (bureaucratAttackResult.get(i).isVictoryInHand()){
                            if (cardName.equals("estate"))
                                postList.add(playerName + " returned an estate to their deck.");
                            else postList.add(playerName + " returned a " + cardName
                                    + " to their deck.");
                        } else {
                            postList.add(playerName + " has no victory cards in hand.");
                        }
                    }
                }
                intent = new Intent(turn.context, NotificationActivity.class);
                intent.putExtra("postListKey", postList);
                turn.activity.startActivity(intent);
                postList.clear();
                break;
            case "chancellor":
                intent = new Intent(turn.context, DecisionDialogActivity.class);
                turn.activity.startActivityForResult(intent, CHANCELLOR_ANSWER_CODE);
                break;
            case "chapel":
                Toast.makeText(turn.context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ntrashing");
                button.setTag(CHAPEL);
                turn.setListeners(HAND_TO_TRASH, listenerSwitches);
                turn.phase = CHAPEL;
                break;
            case "cellar":
                Toast.makeText(turn.context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ndiscarding");
                button.setTag(CELLAR);
                turn.setListeners(HAND_TO_DISCARD, listenerSwitches);
                turn.phase = CELLAR;
                break;
            case "councilRoom":
                reactToCouncilRoom(player.getName(), ((GameBoardActivity)turn.activity).playerList,
                        turn, handListener, listenerSwitches);
                Toast.makeText(turn.context, "the other players drew a card",
                        Toast.LENGTH_SHORT).show();
                break;
            case "feast":
                int viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                ((GameBoardActivity)turn.activity).addCardToTrash("feast");
                ((GameBoardActivity)turn.activity).undo = new Undo("played a feast",
                        turn, FEAST, handListener, this, listenerSwitches);
                Toast.makeText(turn.context, "gain a card to your hand costing up to 5 coins",
                        Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("gain\ncard");
                button.setTag(FEAST);
                turn.setListeners(BANK_TO_DISCARD, listenerSwitches);
                turn.phase = FEAST;
                break;
            case "harbinger":
                Toast.makeText(turn.context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("browse\ndiscard");
                button.setTag(HARBINGER);
                turn.setListeners(ALL_OFF, listenerSwitches);
                turn.phase = HARBINGER;
                break;
            case "library":
                Toast.makeText(turn.context, getCard("library").getInstructions(),
                        Toast.LENGTH_SHORT).show();
                turn.setListeners(ALL_OFF, listenerSwitches);
                turn.phase = LIBRARY;
                ArrayList<String> discardCards = new ArrayList<>();
                player.shufflePile("discard");
                for (int i = 0; i < player.discard.size(); i++){
                    discardCards.add(player.discard.get(i).getCardName());
                }
                ArrayList<String> deckCards = new ArrayList<>();
                for (int i = 0; i < player.deck.size(); i++){
                    deckCards.add(player.deck.get(i).getCardName());
                }
                ArrayList<String> handCards = new ArrayList<>();
                for (int i = 0; i < player.hand.size(); i++){
                    handCards.add(player.hand.get(i).getCardName());
                }
                handCards.add("library");
                ArrayList<ArrayList<String>> pileLists = new ArrayList<>();
                pileLists.add(deckCards);
                pileLists.add(handCards);
                pileLists.add(discardCards);
                ((GameBoardActivity)turn.activity).undo = new Undo("played a library",
                        LIBRARY, turn, pileLists, handListener, this, listenerSwitches);
                ((GameBoardActivity)turn.activity).undoButton.setClickable(true);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(1f);
                int cardsToDraw = 7 - player.hand.size();
                intent = new Intent(turn.context, RevealDialogActivity.class);
                intent.putStringArrayListExtra("revealedCardsKey", deckCards);
                intent.putStringArrayListExtra("discardCardsKey", discardCards);
                intent.putExtra("phaseKey", turn.phase);
                intent.putExtra("cardsToDrawKey", cardsToDraw);
                turn.activity.startActivityForResult(intent, LIBRARY_REVEAL_CODE);
                break;
            case "merchant":
                Toast.makeText(turn.context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                break;
            case "poacher":
                if (turn.emptyBankPiles > 0){
                    if (player.hand.size() > 0) {
                        if (turn.emptyBankPiles == 1) {
                            String toast = "there is 1 empty supply pile.\ndiscard 1 card";
                            Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                        } else {
                            String toast = "there are " + turn.emptyBankPiles + " empty supply piles."
                                    + "\ndiscard " + turn.emptyBankPiles + " cards";
                            Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                        }
                        button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("discard\ncard");
                        button.setTag(POACHER);
                        turn.setListeners(HAND_TO_DISCARD, listenerSwitches);
                        turn.phase = POACHER;
                    } else {
                        Toast.makeText(turn.context, "no cards in hand to discard",
                                Toast.LENGTH_SHORT).show();
                        turn.startOpenBankPhase(listenerSwitches);
                    }
                }
                break;
            case "witch":
                reactToWitch(player.getName(), ((GameBoardActivity)turn.activity).playerList, turn);
                break;
        }
    }

    public void undoNewCardInPlay(Card card, int undoPhase, Turn turn, View.OnTouchListener
            onTouchListener,
                                    ListenerSwitches listenerSwitches) {
        Player player = turn.player;
        String source = card.getName();
        switch (undoPhase){
            case ADVENTURER:
                if (revealedCards.size() > player.deck.size()) adventurerShuffle = true;
                revealedCards.clear();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                player.addCardToHand(source, turn.layout, turn.context, turn.activity,
                        onTouchListener);
                turn.numberOfActionsInHand += 1;
                turn.actions += 1;
                TextView textView = ((Activity)turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions+" actions left");
                turn.startActionPhase(listenerSwitches);
                break;
            case ARTISAN1:
                viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                player.addCardToHand(source, turn.layout, turn.context, turn.activity,
                        onTouchListener);
                turn.numberOfActionsInHand += 1;
                turn.actions += 1;
                textView = ((Activity)turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions+" actions left");
                turn.startActionPhase(listenerSwitches);
                break;
            case BANDIT:
                //put bandit back in hand
                viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                player.addCardToHand("bandit", turn.layout, turn.context, turn.activity,
                        onTouchListener);
                turn.numberOfActionsInHand += 1;
                turn.actions += 1;
                textView = ((Activity)turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions+" actions left");
                //return gold to bank
                player.removeCardFromDiscard(player.discard.size()-1, turn.context,
                        turn.activity);
                ((GameBoardActivity)turn.activity).addCardToBankPile("gold");
                //return cards to other players
                for (int i = 0; i < banditAttackResult.size(); i++){
                    if (!banditAttackResult.get(i).isBlocked()) {
                        int playerIndex = banditAttackResult.get(i).getPlayerNumber();
                        if (!banditAttackResult.get(i).getCard2().equals("")) {
                            card = getCard(banditAttackResult.get(i).getCard2());
                            ((GameBoardActivity) turn.activity).playerList.get(playerIndex)
                                    .addOffTurnCard(card.getName(), "deck");
                            if (banditAttackResult.get(i).getTrashed() == 1) {
                                ((GameBoardActivity) turn.activity)
                                        .removeCardFromTrashByName(card.getName());
                            } else {
                                int top = ((GameBoardActivity) turn.activity)
                                        .playerList.get(playerIndex)
                                        .discard.size() - 1;
                                ((GameBoardActivity) turn.activity).playerList.get(playerIndex)
                                        .removeOffTurnCard(top, "discard");
                            }
                        }
                        if (!banditAttackResult.get(i).getCard1().equals("")) {
                            card = getCard(banditAttackResult.get(i).getCard1());
                            ((GameBoardActivity) turn.activity).playerList.get(playerIndex)
                                    .addOffTurnCard(card.getName(), "deck");
                            if (banditAttackResult.get(i).getTrashed() == 0) {
                                ((GameBoardActivity) turn.activity)
                                        .removeCardFromTrashByName(card.getName());
                            } else {
                                int top = ((GameBoardActivity) turn.activity).playerList
                                        .get(playerIndex)
                                        .discard.size() - 1;
                                ((GameBoardActivity) turn.activity).playerList.get(playerIndex)
                                        .removeOffTurnCard(top, "discard");
                            }
                        }
                    }
                }
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity) turn.activity).undoButton.setAlpha(0.5f);
                turn.startActionPhase(listenerSwitches);
                break;
            case BUREAUCRAT:
                //put bureaucrat back in hand
                viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                player.addCardToHand("bureaucrat", turn.layout, turn.context,
                        turn.activity, onTouchListener);
                turn.numberOfActionsInHand += 1;
                turn.actions += 1;
                textView = ((Activity)turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions+" actions left");
                //return silver to bank
                player.removeCardFromDeck(player.deck.size()-1, turn.activity);
                ((GameBoardActivity)turn.activity).addCardToBankPile("silver");
                //return cards to other players
                for (int i = 0; i < bureaucratAttackResult.size(); i++) {
                    if (!bureaucratAttackResult.get(i).isBlocked()) {
                        if (bureaucratAttackResult.get(i).isVictoryInHand()){
                            player = ((GameBoardActivity)turn.activity).playerList
                                    .get(bureaucratAttackResult.get(i).getPlayerNumber());
                            String cardName = bureaucratAttackResult.get(i).getCardOnDeck();
                            int index = player.deck.size()-1;
                            player.addOffTurnCard(cardName, "hand");
                            player.removeOffTurnCard(index, "deck");
                        }
                    }
                }
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity) turn.activity).undoButton.setAlpha(0.5f);
                turn.startActionPhase(listenerSwitches);
                break;
            case FEAST:
                //return feast to hand
                player.addCardToHand("feast", turn.layout, turn.context, turn.activity,
                        onTouchListener);
                ((GameBoardActivity)turn.activity).removeCardFromTrashByName("feast");
                //hand book keeping
                turn.actions +=1;
                textView = ((Activity) turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions + " actions left");
                turn.numberOfActionsInHand +=1;
                //deactivate undo
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity) turn.activity).undoButton.setAlpha(0.5f);
                //return to turn
                turn.startActionPhase(listenerSwitches);
                break;
            default:
                viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
                player.addCardToHand(source, turn.layout, turn.context, turn.activity,
                        onTouchListener);
                turn.updateHandData(card, true);
                turn.actions +=1;
                if (card.getActions() > 0) turn.actions -= card.getActions();
                textView = ((Activity) turn.activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(turn.actions + " actions left");
                if (card.getExtraBuys() > 0) {
                    turn.buys -= card.getExtraBuys();
                    textView = ((Activity) turn.activity).findViewById(BUYS_LEFT_ID);
                    textView.setText(turn.buys + " buys left");
                }
                if ((card.getExtraCoins() > 0) || (card.getValue() > 0)) {
                    turn.coins -= card.getExtraCoins();
                    turn.coins -= card.getValue();
                    textView = ((Activity)turn.activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(turn.coins + "         saved");
                }
                if (card.getDrawCards() > 0) {
                    turn.draws = card.getDrawCards();
                    for (int i = turn.draws; i > 0; i--) {
                        CardData cardData = player.hand.get(player.hand.size()-1);
                        viewId = cardData.getImageViewId();
                        String cardName = cardData.getCardName();
                        player.removeCardFromHand(viewId, turn.activity, turn.layout);
                        turn.updateHandData(card, false);
                        player.addCardToDeck(cardName, turn.activity, turn.context);
                        turn.draws -= 1;
                    }
                }
                turn.continueToTurnPhase(listenerSwitches);
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
                break;
        }
    }

    public void reactToNewCardInTrash(String cardName, Turn turn, View.OnTouchListener handListener,
                                      ListenerSwitches listenerSwitches){
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (turn.phase) {
            case CHAPEL:
                if (trashed < 4){
                    trashed += 1;
                    cardsTrashed.add(card);
                    turn.updateHandData(card, false);
                } else {
                    Toast.makeText(turn.context, "You can only trash 4 cards",
                            Toast.LENGTH_SHORT).show();
                    ((GameBoardActivity) turn.activity).removeCardFromTrashByIndex(
                            ((GameBoardActivity) turn.activity).trash.size()-1);
                    player.addCardToHand(cardName, turn.layout, turn.context, turn.activity,
                            handListener);
                    listenerSwitches.setAllFalse();
                }
                break;
        }
    }

    public void undoNewCardInTrash(String cardName, int undoPhase, Turn turn,
                                   View.OnTouchListener onTouchListener,
                                   ListenerSwitches listenerSwitches) {
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (undoPhase){
            case CHAPEL:
                ((GameBoardActivity)turn.activity).removeCardFromTrashByName(cardName);
                player.addCardToHand(cardName, turn.layout, turn.context, turn.activity,
                        onTouchListener);
                turn.updateHandData(card, true);
                trashed -= 1;
                Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ntrashing");
                button.setTag(CHAPEL);
                turn.setListeners(HAND_TO_TRASH, listenerSwitches);
                turn.phase = CHAPEL;
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
                break;
        }
    }

    public void reactToNewCardInDiscard(String cardName, Turn turn, ListenerSwitches listenerSwitches) {
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (turn.phase) {
            case CELLAR:
                discarded += 1;
                cardsDiscarded.add(card);
                turn.updateHandData(card, false);
                break;
            case FEAST:
                if (card.getCost() > 5) {
                    Toast.makeText(turn.context, "too expensive\ntry something cheaper",
                            Toast.LENGTH_SHORT).show();
                    int index = player.discard.size() - 1;
                    player.removeCardFromDiscard(index, turn.context, turn.activity);
                    int bankPileCounterId = turn.findBankViewId(cardName, turn.bankPiles);
                    TextView textView = ((Activity) turn.activity).findViewById(bankPileCounterId);
                    int count = Integer.parseInt(textView.getText().toString()) + 1;
                    textView.setText(String.valueOf(count));

                } else {
                    String description = "moved " + cardName + " to discard";
                    ((GameBoardActivity) turn.activity).undo = new Undo(description, turn, FEAST,
                            turn.bankPiles, this, listenerSwitches);
                    ((GameBoardActivity) turn.activity).undoButton.setClickable(true);
                    ((GameBoardActivity) turn.activity).undoButton.setAlpha(1f);
                    turn.continueToTurnPhase(listenerSwitches);
                }
                break;
            case POACHER:
                poacherCompliance += 1;
                turn.updateHandData(card, false);
                if (poacherCompliance == turn.emptyBankPiles) {
                    //previousPhase = POACHER;
                    turn.continueToTurnPhase(listenerSwitches);
                    poacherCompliance = 0;
                }
                break;
        }
    }

    public void undoNewCardInDiscard(String cardName, int undoPhase, Turn turn,
                                   View.OnTouchListener handListener,
                                   ListenerSwitches listenerSwitches) {
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (undoPhase) {
            case OPEN_BANK:
                player.removeCardFromDiscard(player.discard.size() - 1, turn.context,
                        turn.activity);
                int bankPileCounterId = turn.findBankViewId(cardName, turn.bankPiles);
                TextView textView = ((Activity) turn.activity).findViewById(bankPileCounterId);
                int count = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(count));
                turn.cardsGained.remove(card);
                turn.coins += card.getCost();
                textView = ((Activity) turn.activity).findViewById(COINS_COLLECTED_ID);
                textView.setText(turn.coins + "         saved");
                turn.buys += 1;
                textView = ((Activity) turn.activity).findViewById(BUYS_LEFT_ID);
                textView.setText(turn.buys + " buys left");
                if (turn.phase == CLEAN_UP_PHASE) turn.startOpenBankPhase(listenerSwitches);
                break;
            case CELLAR:
                player.removeCardFromDiscard(player.discard.size()-1, turn.context,
                        turn.activity);
                player.addCardToHand(cardName, turn.layout, turn.context, turn.activity,
                        handListener);
                turn.updateHandData(card, true);
                discarded -= 1;
                Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ndiscarding");
                button.setTag(CELLAR);
                turn.setListeners(HAND_TO_DISCARD, listenerSwitches);
                turn.phase = CELLAR;
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
                break;
            case FEAST:
                // return gained card
                player.removeCardFromDiscard(player.discard.size()-1, turn.context,
                        turn.activity);
                ((GameBoardActivity)turn.activity).addCardToBankPile(cardName);
                // gain card prompt
                Toast.makeText(turn.context, "gain a card to your hand costing up to 5 coins",
                        Toast.LENGTH_SHORT).show();
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("gain\ncard");
                button.setTag(FEAST);
                turn.setListeners(BANK_TO_DISCARD, listenerSwitches);
                turn.phase = FEAST;
                break;
            case POACHER:
                if (poacherCompliance > 0) poacherCompliance -= 1;
                turn.updateHandData(card, true);
                player.removeCardFromDiscard(player.discard.size() - 1, turn.context,
                        turn.activity);
                player.addCardToHand(cardName, turn.layout, turn.context, turn.activity,
                        handListener);
                int discardsLeft = turn.emptyBankPiles - poacherCompliance;
                if (turn.emptyBankPiles == 1) {
                    String toast = "there is 1 empty supply pile.\ndiscard 1 card";
                    Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                } else {
                    String toast = "there are " + turn.emptyBankPiles + " empty supply piles."
                            + "\ndiscard " + discardsLeft + " cards";
                    Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                }
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("discard\ncard");
                button.setTag(POACHER);
                turn.setListeners(POACHER, listenerSwitches);
                turn.phase = POACHER;
                break;
        }
    }

    public void reactToNewCardInHand(String cardName, Turn turn, ListenerSwitches listenerSwitches){
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (turn.phase) {
            case CELLAR:
                turn.updateHandData(card,true);
                break;
            case ARTISAN1:
                if (card.getCost() > 5){
                    Toast.makeText(turn.context, "too expensive\ntry something cheaper",
                            Toast.LENGTH_SHORT).show();
                    int viewId = player.hand.get(player.hand.size()-1).getImageViewId();
                    player.removeCardFromHand(viewId, turn.activity, turn.layout);
                    int bankPileCounterId = turn.findBankViewId(cardName, turn.bankPiles);
                    TextView textView = ((Activity) turn.activity).findViewById(bankPileCounterId);
                    int count = Integer.parseInt(textView.getText().toString()) + 1;
                    textView.setText(String.valueOf(count));

                } else {
                    turn.updateHandData(card,true);
                    Toast.makeText(turn.context, "put a card from your hand into your deck.",
                            Toast.LENGTH_SHORT).show();
                    Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                    button.setText("card\nto deck");
                    button.setTag(ARTISAN2);
                    turn.setListeners(HAND_TO_DECK, listenerSwitches);
                    turn.phase = ARTISAN2;
                }
                break;
        }
    }

    public void reactToNewCardOnDeck(String cardName, Turn turn, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = getCard(cardName);
        switch (turn.phase) {
            case ARTISAN2:
                String description = "moved " + cardName + " to deck";
                ((GameBoardActivity)turn.activity).undo = new Undo(description, turn, turn.phase,
                        handListener, this, listenerSwitches);
                turn.updateHandData(card,false);
                turn.continueToTurnPhase(listenerSwitches);
                ((GameBoardActivity)turn.activity).undoButton.setClickable(true);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(1f);
                break;
        }
    }

    public void undoNewCardOnDeck(String cardName, int undoPhase, Turn turn, View.OnTouchListener
            onTouchListener, ListenerSwitches listenerSwitches){
        Card card = getCard(cardName);
        Player player = turn.player;
        switch (undoPhase){
            case ARTISAN2:
                //return card to hand
                player.removeCardFromDeck(player.deck.size()-1, turn.activity);
                player.addCardToHand(cardName, turn.layout, turn.context, turn.activity,
                        onTouchListener);
                //book-keeping
                turn.updateHandData(card, true);
                //set phase
                Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("card\nto deck");
                button.setTag(ARTISAN2);
                turn.setListeners(ARTISAN2, listenerSwitches);
                turn.phase = ARTISAN2;
                //disable undo
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
                break;
            case HARBINGER:
                //put card back in discard
                cardName = player.deck.get(player.deck.size()-1).getCardName();
                player.removeCardFromDeck(player.deck.size()-1, turn.activity);
                player.addCardToDiscard(cardName, turn.activity, turn.context);
                //disable undo
                ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
                ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
                //reset phase
                button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
                button.setText("card\nto deck");
                button.setTag(HARBINGER);
                turn.setListeners(ALL_OFF, listenerSwitches);
                turn.phase = HARBINGER;
                break;
        }
    }

    public void undoDeckToDiscard(int cardsMoved, Turn turn, ListenerSwitches listenerSwitches){
        Player player = turn.player;
        int index = player.discard.size()-cardsMoved;
        for (int i = 0; i < cardsMoved; i++){
            String cardName = player.discard.get(index).getCardName();
            player.addCardToDeck(cardName, turn.activity, turn.context);
            player.removeCardFromDiscard(index, turn.context, turn.activity);
        }
        ((GameBoardActivity)turn.activity).undo = new Undo("moved chancellor to inPlay",
                turn, this, listenerSwitches);
        turn.startActionPhase(listenerSwitches);
        ((GameBoardActivity)turn.activity).undoButton.setClickable(true);
        ((GameBoardActivity)turn.activity).undoButton.setAlpha(1f);
        Intent intent = new Intent(turn.context, DecisionDialogActivity.class);
        turn.activity.startActivityForResult(intent, CHANCELLOR_ANSWER_CODE);
    }

    public void finishAdventurer(Turn turn, ListenerSwitches listenerSwitches,
                                 View.OnTouchListener handListener) {
        Player player = turn.player;
        int deckSize = player.deck.size();
        int discardSize = player.discard.size();
        int movedToDiscard = 0;
        adventurerShuffle = false;
        if (deckSize >= revealedCards.size()) {
            for (int i = 0; i < revealedCards.size(); i++) {
                Card card = getCard(revealedCards.get(i));
                if (card.getType().equals("treasure")) {
                    player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                    player.addCardToHand(revealedCards.get(i), turn.layout, turn.context,
                            turn.activity,
                            handListener);
                    turn.numberOfTreasuresInHand += 1;
                } else {
                    player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                    player.addCardToDiscard(revealedCards.get(i), turn.activity, turn.context);
                    movedToDiscard +=1;
                }
            }
        } else {
            for (int i = 0; i < deckSize; i++) {
                Card card = getCard(revealedCards.get(i));
                if (card.getType().equals("treasure")) {
                    player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                    player.addCardToHand(revealedCards.get(i), turn.layout, turn.context,
                            turn.activity, handListener);
                    turn.numberOfTreasuresInHand += 1;
                } else {
                    player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                    player.addCardToDiscard(revealedCards.get(i), turn.activity, turn.context);
                    movedToDiscard +=1;
                }
            }
            //put old discard on deck unshuffled
            int newDiscardSize = player.discard.size();
            for (int i = 0; i < newDiscardSize-movedToDiscard; i++) {
                player.deck.add(player.discard.get(0));
                player.deck.get(player.deck.size() - 1).setCardMultiTagOnMoveToNewPile
                        (player.deck.get(player.deck.size() - 1),
                        (player.deck.size() - 1), player.deckTally, "deck");
                player.deckTally += 1;
                player.removeCardFromDiscard(0, turn.context, turn.activity);
            }

            if ((revealedCards.size() - deckSize) <= discardSize) {
                for (int i = deckSize; i < revealedCards.size(); i++) {
                    Card card = getCard(revealedCards.get(i));
                    if (card.getType().equals("treasure")) {
                        player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                        player.addCardToHand(revealedCards.get(i), turn.layout, turn.context,
                                turn.activity, handListener);
                        turn.numberOfTreasuresInHand +=1;
                    } else {
                        player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                        player.addCardToDiscard(revealedCards.get(i), turn.activity, turn.context);
                    }
                }
            } else {
                for (int i = deckSize; i < discardSize + deckSize; i++) {
                    Card card = getCard(revealedCards.get(i));
                    if (card.getType().equals("treasure")) {
                        player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                        player.addCardToHand(revealedCards.get(i), turn.layout, turn.context,
                                turn.activity, handListener);
                        turn.numberOfTreasuresInHand += 1;
                    } else {
                        player.removeCardFromDeck(player.deck.size() - 1, turn.activity);
                        player.addCardToDiscard(revealedCards.get(i), turn.activity, turn.context);
                    }
                }
            }
        }
        turn.continueToTurnPhase(listenerSwitches);
    }

    public int finishChapel(Turn turn, ListenerSwitches listenerSwitches){
        int trashedSoFar = trashed;
        trashed = 0;
        turn.continueToTurnPhase(listenerSwitches);
        return trashedSoFar;
    }

    public void returnToChapelTrashing(int trashed, Turn turn, ListenerSwitches listenerSwitches){
        this.trashed = trashed;
        Card card = getCard("chapel");
        Toast.makeText(turn.context, card.getInstructions(), Toast.LENGTH_SHORT).show();
        Button button = ((Activity) turn.activity).findViewById(PHASE_BUTTON_ID);
        button.setText("finished\ntrashing");
        button.setTag(CHAPEL);
        turn.setListeners(HAND_TO_TRASH, listenerSwitches);
        turn.phase = CHAPEL;
    }

    public int finishCellar(Turn turn, View.OnTouchListener handListener,
                            ListenerSwitches listenerSwitches){
        int discardedSoFar = discarded;
        discarded = 0;
        for (int i = discardedSoFar; i > 0; i--) {
            String cardName = turn.drawCard(handListener);
            if (!(cardName.equals("null"))) {
                reactToNewCardInHand(cardName, turn, listenerSwitches);
            }
        }
        turn.continueToTurnPhase(listenerSwitches);
        return discardedSoFar;
    }

    public void finishChancellor(boolean choice, Turn turn, ListenerSwitches listenerSwitches){
        Player player = turn.player;
        if (choice){
            int deckSize = player.deck.size();
            ((GameBoardActivity)turn.activity).undo = new Undo("put deck in discard",
                    turn, deckSize, this, listenerSwitches);
            player.putDeckInDiscard(turn.activity);
        }
        turn.continueToTurnPhase(listenerSwitches);
        ((GameBoardActivity)turn.activity).undoButton.setClickable(true);
        ((GameBoardActivity)turn.activity).undoButton.setAlpha(1f);
    }

    public void undoCouncilRoom(boolean[] playerToggles, Turn turn, View.OnTouchListener handListener,
                                ListenerSwitches listenerSwitches){
        Player player = turn.player;
        //return drawn cards
        for (int i = 0; i < councilRoomDraws; i++){
            CardData cardData = player.hand.get(player.hand.size()-1);
            int viewId = cardData.getImageViewId();
            String cardName = cardData.getCardName();
            String cardType = cardData.getCard().getType();
            player.removeCardFromHand(viewId, turn.activity, turn.layout);
            player.addCardToDeck(cardName, turn.activity, turn.context);
            turn.updateHandData(cardData.getCard(), false);
        }
        //return councilRoom
        CardData cardData = player.inPlay.get(player.inPlay.size()-1);
        int viewId = cardData.getImageViewId();
        String cardName = cardData.getCardName();
        player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
        player.addCardToHand(cardName, turn.layout, turn.context, turn.activity, handListener);
        turn.numberOfActionsInHand +=1;
        turn.actions +=1;
        TextView textView = ((Activity) turn.activity).findViewById(ACTIONS_LEFT_ID);
        textView.setText(turn.actions + " actions left");
        turn.buys -=1;
        textView = ((Activity) turn.activity).findViewById(BUYS_LEFT_ID);
        textView.setText(turn.buys + " buys left");
        //return cards drawn by other players
        undoOffTurnCouncilRoom(playerToggles, ((GameBoardActivity)turn.activity).playerList, turn);
        //return to previous state
        turn.startActionPhase(listenerSwitches);

    }

    public void finishLibrary(ArrayList<String> drawnCards, ArrayList<String> discardedCards, Turn turn,
                              View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        Player player = turn.player;
        int drawnCardsSize = drawnCards.size();
        int discardedCardsSize = discardedCards.size();
        int totalDrawn = drawnCardsSize + discardedCardsSize;
        int deckSize = player.deck.size();
        int discardSize = player.discard.size();
        if ((totalDrawn) <= player.deck.size()) { //check if they are all in the deck
            for (int j = 0; j < discardedCardsSize; j++){ //go through the discarded list
                int minIndex = deckSize - totalDrawn;
                for (int i = player.deck.size()-1; i >= minIndex; i--) { //search for the card in the deck, starting at last drawn card
                    if (discardedCards.get(0).equals(player.deck.get(i).getCardName())) { //when found
                        player.removeCardFromDeck(i, turn.activity); // remove from deck
                        player.addCardToDiscard(discardedCards.get(0), turn.activity, turn.context); //add to discard
                        discardedCards.remove(0); // remove from discarded list
                        break;
                    }
                }
            }
            for (int j = 0; j < drawnCardsSize; j++){
                int index = player.deck.size()-1;
                if (drawnCards.get(0).equals(player.deck.get(index).getCardName())) { //when found
                    player.removeCardFromDeck(index, turn.activity); // remove from deck
                    player.addCardToHand(drawnCards.get(0), turn.layout, turn.context, turn.activity,
                            handListener); // add to hand
                    Card card = turn.basicCardSet.getCard(drawnCards.get(0));
                    turn.updateHandData(card, true);
                    drawnCards.remove(0);
                } else {
                    Toast.makeText(turn.context, "the cards don't match",
                            Toast.LENGTH_SHORT).show();
                }

            }
        } else { // some of the cards come from the discard pile
            int cardsFromDiscard = discardedCardsSize + drawnCardsSize - player.deck.size();
            int minIndex = player.discard.size() - cardsFromDiscard -1;
            String discardedCardName;
            int discardedIndex = 0;
            for (int i = deckSize-1; i >= 0; i--){
                if (discardedCards.size() > discardedIndex) discardedCardName
                        = discardedCards.get(discardedIndex);
                else discardedCardName = "";
                if (discardedCardName.equals(player.deck.get(i).getCardName())) {
                    player.removeCardFromDeck(i, turn.activity);
                    //player.addCardToDiscard(discardedCardName, activity, context);
                    discardedIndex += 1;
                } else if (drawnCards.get(0).equals(player.deck.get(i).getCardName())){
                    Card card = getCard(drawnCards.get(0));
                    player.removeCardFromDeck(i, turn.activity);
                    player.addCardToHand(card.getName(), turn.layout, turn.context, turn.activity,
                            handListener);
                    turn.updateHandData(card, true);
                    drawnCards.remove(0);
                } else {
                    Toast.makeText(turn.context, "the cards don't match",
                            Toast.LENGTH_SHORT).show();
                }
            }
            for (int i = discardSize-1; i > minIndex; i--){ //discard or draw remaining cards
                if (discardedCards.size() > discardedIndex) discardedCardName
                        = discardedCards.get(discardedIndex);
                else discardedCardName = "";
                if (discardedCardName.equals(player.discard.get(i).getCardName())) {
                    discardedCards.remove(discardedIndex);
                } else if (drawnCards.get(0).equals(player.discard.get(i).getCardName())){
                    player.removeCardFromDiscard(i, turn.context, turn.activity);
                    player.addCardToHand(drawnCards.get(0), turn.layout, turn.context, turn.activity,
                            handListener);
                    Card card = getCard(drawnCards.get(0));
                    turn.updateHandData(card, true);
                    drawnCards.remove(0);
                }
            }
            player.putDiscardInDeck(turn.activity);
            if (discardedCards.size() > 0) {
                for (int i = 0; i < discardedCards.size(); i++) {
                    player.addCardToDiscard(discardedCards.get(i), turn.activity, turn.context);
                }
            }
        }
        turn.continueToTurnPhase(listenerSwitches);
    }

    public void undoLibrary(ArrayList<ArrayList<String>> pileLists, Turn turn,
                            View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        Player player = turn.player;
        ArrayList<String> deckList = pileLists.get(0);
        ArrayList<String> handList = pileLists.get(1);
        ArrayList<String> discardList = pileLists.get(2);
        player.deck.clear();
        player.deckTally = 0;
        for (int i = 0; i < deckList.size(); i++){
            player.addCardToDeck(deckList.get(i), turn.activity, turn.context);
        }
        player.deckPile.getTextView().setText(String.valueOf(player.deck.size()));
        int handSize = player.hand.size();
        for (int i = 0; i < handSize; i++) {
            int viewId = player.hand.get(player.hand.size()-1).getImageViewId();
            Card card = getCard(player.hand.get(player.hand.size()-1).getCardName());
            player.removeCardFromHand(viewId, turn.activity, turn.layout);
            turn.updateHandData(card, false);
        }
        for (int i = 0; i < handList.size(); i++){
            player.addCardToHand(handList.get(i), turn.layout, turn.context, turn.activity,
                    handListener);
            Card card = getCard(handList.get(i));
            turn.updateHandData(card, true);
        }
        player.discard.clear();
        player.discardTally = 0;
        for (int i = 0; i < discardList.size(); i++){
            player.addCardToDiscard(discardList.get(i), turn.activity, turn.context);
        }
        int viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
        player.removeCardFromInPlay(viewId, turn.activity, turn.layout);
        turn.actions +=1;
        String cardName = player.discard.get(player.discard.size()-1).getCardName();
        Drawable drawable = player.getImageDps(turn.activity, cardName, (cardWidth / 2));
        ImageView imageView1 = ((Activity) turn.activity)
                .findViewById(player.discardPile.getImageViewId());
        imageView1.setImageDrawable(drawable);
        TextView textView = ((GameBoardActivity)turn.activity).findViewById(ACTIONS_LEFT_ID);
        textView.setText(turn.actions + " actions left");
        ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
        ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
        turn.continueToTurnPhase(listenerSwitches);
    }

    public void reactToWitch(String playerName, ArrayList<Player> playerList, Turn turn){
        boolean cardsLeft;
        ArrayList<String> postList = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++){
            if (!playerList.get(i).getName().equals(playerName)){
                String reaction = playerList.get(i).checkForReaction("witch");
                if (!reaction.equals("moat")) {
                    postList.add(playerList.get(i).getName() + " gained a curse.");
                    cardsLeft = ((GameBoardActivity)turn.activity)
                            .removeCardFromBankPile("curse");
                    if (cardsLeft) playerList.get(i).addOffTurnCard("curse",
                            "discard");
                } else {
                    postList.add(playerList.get(i).getName() + " has a moat.");
                }
            }
        }
        Intent intent = new Intent(turn.context, NotificationActivity.class);
        intent.putStringArrayListExtra("postListKey", postList);
        turn.activity.startActivity(intent);
    }

    public ArrayList<BanditAttack> reactToBanditAttack(String playerName,
                                                       ArrayList<Player> playerList, Turn turn) {
        ArrayList<BanditAttack> banditAttackResults = new ArrayList<>();
        Card[] topCard = new Card[2];
        for (int i = 0; i < playerList.size(); i++){
            boolean[] treasures = {true, true};
            if (!playerList.get(i).getName().equals(playerName)) {
                BanditAttack banditAttack = new BanditAttack(i, playerList.get(i).getName());
                String reaction = playerList.get(i).checkForReaction("bandit");
                if (reaction.equals("moat")) {
                    banditAttack.setBlocked(true);
                } else {
                    int index;
                    if (playerList.get(i).deck.size() > 1)
                        index = playerList.get(i).deck.size() - 1;
                    else if (playerList.get(i).deck.size() == 1) {
                        index = 0;
                        playerList.get(i).putDiscardInDeck(turn.activity);
                    } else {
                        playerList.get(i).putDiscardInDeck(turn.activity);
                        index = playerList.get(i).deck.size() - 1;
                    }
                    if (index >= 0) {
                        topCard[0] = playerList.get(i).deck.get(index).getCard();
                        banditAttack.setCard1(topCard[0].getName());
                        playerList.get(i).removeOffTurnCard(index, "deck");
                        if (!topCard[0].getType().equals("treasure") || topCard[0].getName()
                                .equals("copper")) {
                            playerList.get(i).addOffTurnCard(topCard[0].getName(), "discard");
                            treasures[0] = false;
                        }
                        if (playerList.get(i).deck.size() > 0) {
                            index = playerList.get(i).deck.size() - 1;
                            topCard[1] = playerList.get(i).deck.get(index).getCard();
                            banditAttack.setCard2(topCard[1].getName());
                            playerList.get(i).removeOffTurnCard(index, "deck");
                            if (!topCard[1].getType().equals("treasure") || topCard[1].getName()
                                    .equals("copper")) {
                                playerList.get(i).addOffTurnCard(topCard[1].getName(),
                                        "discard");
                                treasures[1] = false;
                            }
                        } else {
                            treasures[1] = false;
                            String toast = "no more cards to reveal";
                            Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                            banditAttack.setCard2("");
                        }
                    } else {
                        String toast = "no more cards to reveal";
                        Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                        banditAttack.setCard1("");
                        banditAttack.setCard2("");
                        treasures[1] = false;
                        treasures[0] = false;
                    }
                    if (treasures[0] && treasures[1]) {
                        int value1 = topCard[0].getValue();
                        int value2 = topCard[1].getValue();
                        if (value1 < value2) {
                            banditAttack.setTrashed(0);
                            ((GameBoardActivity)turn.activity).addCardToTrash(topCard[0].getName());
                            playerList.get(i).addOffTurnCard(topCard[1].getName(), "discard");
                        } else {
                            banditAttack.setTrashed(1);
                            ((GameBoardActivity)turn.activity).addCardToTrash(topCard[1].getName());
                            playerList.get(i).addOffTurnCard(topCard[0].getName(), "discard");
                        }
                    } else if (treasures[0]) {
                        banditAttack.setTrashed(0);
                        ((GameBoardActivity)turn.activity).addCardToTrash(topCard[0].getName());
                    } else if (treasures[1]) {
                        banditAttack.setTrashed(1);
                        ((GameBoardActivity)turn.activity).addCardToTrash(topCard[1].getName());
                    }
                }
                banditAttackResults.add(banditAttack);
            }
        }
        return banditAttackResults;
    }

    public void reactToCouncilRoom(String playerName, ArrayList<Player> playerList, Turn turn,
                                   View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        int size = playerList.size();
        boolean[] cardDrawn = new boolean[size];
        for (int i = 0; i < playerList.size(); i ++){
            if (playerName.equals(playerList.get(i).getName())) {
                cardDrawn[i] = false;
            } else {
                Player player = playerList.get(i);
                if (player.deck.size() == 0){
                    player.putOffTurnDiscardInDeck();
                }
                if (player.deck.size()>0) {
                    String cardName = player.deck.get(player.deck.size() - 1).getCardName();
                    player.addOffTurnCard(cardName, "hand");
                    player.removeOffTurnCard(player.deck.size()-1, "deck");
                    cardDrawn[i] = true;
                } else {
                    String toast = player.getName() + " has no cards to draw";
                    Toast.makeText(turn.context, toast, Toast.LENGTH_SHORT).show();
                    cardDrawn[i] = false;
                }

            }
        }
        ((GameBoardActivity)turn.activity).undo = new Undo("other players drew a card", turn,
                cardDrawn, handListener, this, listenerSwitches);
        ((GameBoardActivity)turn.activity).undoButton.setClickable(true);
        ((GameBoardActivity)turn.activity).undoButton.setAlpha(1f);
    }

    public void undoOffTurnCouncilRoom(boolean[] playerToggles, ArrayList<Player> playerList, Turn turn){
        for (int i = 0; i < playerList.size(); i ++){
            Player player = playerList.get(i);
            if (playerToggles[i]) {
                String cardName = player.hand.get(player.hand.size() - 1).getCardName();
                player.addOffTurnCard(cardName, "deck");
                player.removeOffTurnCard(player.hand.size()-1, "hand");
            }
        }
        ((GameBoardActivity)turn.activity).undoButton.setClickable(false);
        ((GameBoardActivity)turn.activity).undoButton.setAlpha(0.5f);
    }

    public ArrayList<BureaucratAttack> reactToBureaucratAttack(String playerName,
                                                               ArrayList<Player> playerList,
                                                               Turn turn){
        ArrayList<BureaucratAttack> bureaucratAttackResults = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++){
            boolean vpCard = false;
            if (!playerList.get(i).getName().equals(playerName)) {
                BureaucratAttack bureaucratAttack = new BureaucratAttack(i, playerList.get(i).getName());
                String reaction = playerList.get(i).checkForReaction("bureaucrat");
                if (reaction.equals("moat")) {
                    bureaucratAttack.setBlocked(true);
                } else {
                    int vpValue = 100;
                    String replacedCard = "";
                    int index = -1;
                    for (int j = 0; j < playerList.get(i).hand.size(); j++) {
                        Card card = playerList.get(i).hand.get(j).getCard();
                        if (card.getType().equals("victory")) {
                            vpCard = true;
                            if (card.getVictoryPoints() < vpValue) {
                                vpValue = card.getVictoryPoints();
                                replacedCard = card.getName();
                                index = j;
                            }
                        }
                    }
                    bureaucratAttack.setCardOnDeck(replacedCard);
                    bureaucratAttack.setVictoryInHand(vpCard);
                    if (index >= 0) {
                        playerList.get(i).removeOffTurnCard(index, "hand");
                        playerList.get(i).addOffTurnCard(replacedCard, "deck");
                    }
                }
                bureaucratAttackResults.add(bureaucratAttack);
            }
        }
        return bureaucratAttackResults;
    }



}

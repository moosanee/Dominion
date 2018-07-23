package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class Turn {

    Activity activity;
    Context context;
    ConstraintLayout layout;
    Player player;
    int phase = 0;
    int actions = 1;
    int buys = 1;
    int coins = 0;
    int draws = 0;
    int numberOfTreasuresInHand = 0;
    int numberOfActionsInHand = 0;
    int trashed = 0;
    boolean firstSilverInPlayFlag = false;
    int emptyBankPiles;
    int poacherCompliance = 0;
    ArrayList<String> reactions = new ArrayList<>();
    BasicCards basicCardSet = new BasicCards();
    ArrayList<CardData> bankPiles; // = new ArrayList<>();
    ArrayList<Card> actionsPlayed = new ArrayList<>();
    ArrayList<Card> treasuresPlayed = new ArrayList<>();
    ArrayList<Card> cardsGained = new ArrayList<>();
    ArrayList<Card> cardsTrashed = new ArrayList<>();
    ArrayList<String> revealedCards = new ArrayList<>();



    Turn(Player player, int emptyBankPiles, ArrayList<CardData> bankPiles, GameBoardActivity activity,
            Context context, ConstraintLayout layout){
        this.player = player;
        this.emptyBankPiles = emptyBankPiles;
        this.bankPiles = bankPiles;
        this.activity = activity;
        this.context = context;
        this.layout = layout;
    }

    public void startTurn(ListenerSwitches listenerSwitches){
        TextView textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
        textView.setText("1 action left");
        textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
        textView.setText("0        saved");
        textView = ((Activity)activity).findViewById(BUYS_LEFT_ID);
        textView.setText("1 buy left");
        promptTurn(listenerSwitches);
    }

    public void setListeners(int key, ListenerSwitches listenerSwitches){
        switch (key) {
            case ACTION_PHASE:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                break;
            case BUYING_PHASE:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                break;
            case OPEN_BANK:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDiscardDragSwitch(true);
                listenerSwitches.setBankListenerSwitch(true);
                break;
            case CLEAN_UP_PHASE:
                listenerSwitches.setAllFalse();
                listenerSwitches.setInPlayListenerSwitch(true);
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setDiscardDragSwitch(true);
                break;
            case CHAPEL:
                listenerSwitches.setAllFalse();
                listenerSwitches.setTrashDragSwitch(true);
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                break;
            case POACHER:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setDiscardDragSwitch(true);
                break;
            case ARTISAN1:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setBankListenerSwitch(true);
                break;
            case ARTISAN2:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setDeckDragSwitch(true);
                break;
            case ADVENTURER:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDeckListenerSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                listenerSwitches.setDeckDragSwitch(true);
                break;
        }
    }

    public void promptTurn(ListenerSwitches listenerSwitches){
        numberOfActionsInHand = 0;
        numberOfTreasuresInHand = 0;
        firstSilverInPlayFlag = false;
        reactions.clear();
        for (int i=0; i < player.hand.size(); i++){
            Card card = player.hand.get(i).getCard();
            if (card.getType().equals("action")) numberOfActionsInHand += 1;
            else if (card.getType().equals("action - attack"))
                numberOfActionsInHand += 1;
            else if (card.getType().equals("action - reaction")) {
                numberOfActionsInHand += 1;
                reactions.add(card.getName());
            }
            else if (player.hand.get(i).getCard().getType().equals("treasure"))
                numberOfTreasuresInHand += 1;
        }
        if (numberOfActionsInHand > 0) {
            String message = player.getName() + ", it's your turn.\nTake an action";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startActionPhase(listenerSwitches);
        } else {
            String message = player.getName() + ", it's your turn.\n" +
                    "You have no actions.\nCount your coins and make a purchase.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startBuyingPhase(listenerSwitches);
        }
    }

    public void startActionPhase(ListenerSwitches listenerSwitches){
        phase = ACTION_PHASE;
        Button button = ((Activity)activity).findViewById(PHASE_BUTTON_ID);
        button.setTag(ACTION_PHASE);
        button.setText("finished\nactions");
        setListeners(ACTION_PHASE, listenerSwitches);
    }

    public void startBuyingPhase (ListenerSwitches listenerSwitches) {
        phase = BUYING_PHASE;
        Button button = ((Activity)activity).findViewById(PHASE_BUTTON_ID);
        button.setTag(BUYING_PHASE);
        button.setText("play all\ntreasures");
        setListeners(BUYING_PHASE, listenerSwitches);
    }

    public void startOpenBankPhase(ListenerSwitches listenerSwitches){
        Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
        button.setText("finished\nbuying");
        button.setTag(OPEN_BANK);
        setListeners(OPEN_BANK, listenerSwitches);
        phase = OPEN_BANK;
    }

    public void startCleanUpPhase(ListenerSwitches listenerSwitches){
        phase = CLEAN_UP_PHASE;
        setListeners(CLEAN_UP_PHASE, listenerSwitches);
        Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
        button.setTag(CLEAN_UP_PHASE);
        button.setText("finish\nclean up");
    }

    public void reactToNewCardInPlay(String cardName, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        if (phase == ACTION_PHASE) {
            if (!(card.getType().equals("action") || card.getType().equals("action - attack")
                    || card.getType().equals("action - reaction"))){
                Toast.makeText(context, "action card please", Toast.LENGTH_SHORT).show();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            } else if (actions == 0){
                String message = "You are out of actions.\nCount your coins and make a purchase.";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                startBuyingPhase(listenerSwitches);
            } else {
                actionsPlayed.add(card);
                numberOfActionsInHand -= 1;
                actions -= 1;
                if (card.getActions() > 0) actions += card.getActions();
                TextView textView = ((Activity) activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions + " actions left");
                if (card.getExtraBuys() > 0) {
                    buys += card.getExtraBuys();
                    textView = ((Activity) activity).findViewById(BUYS_LEFT_ID);
                    textView.setText(buys + " buys left");
                }
                if ((card.getExtraCoins() > 0) || (card.getValue() > 0)) {
                    coins += card.getExtraCoins();
                    coins += card.getValue();
                    textView = ((Activity) activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(coins + "         saved");
                }
                if (card.getDrawCards() > 0) {
                    draws += card.getDrawCards();
                }
                for (int i = draws; i > 0; i--) {
                    String cardName1 = drawCard(handListener);
                    if (!(cardName1.equals("null"))) {
                        reactToNewCardInHand(cardName1, listenerSwitches);
                    }
                    draws -= 1;
                }
                switch (cardName) {
                    case "adventurer":
                        Toast.makeText(context, "Reveal cards from your deck until you reveal 2 treasure cards.",
                                Toast.LENGTH_SHORT).show();
                        Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("apply\nadventurer");
                        button.setTag(ADVENTURER);
                        setListeners(ADVENTURER, listenerSwitches);
                        phase = ADVENTURER;
                        break;
                    case "artisan":
                        Toast.makeText(context, "gain a card to your hand costing up to 5 coins",
                                Toast.LENGTH_SHORT).show();
                        button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("gain\ncard");
                        button.setTag(ARTISAN1);
                        setListeners(ARTISAN1, listenerSwitches);
                        phase = ARTISAN1;
                        break;
                    case "chapel":
                        Toast.makeText(context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                        button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("finished\ntrashing");
                        button.setTag(CHAPEL);
                        setListeners(CHAPEL, listenerSwitches);
                        phase = CHAPEL;
                        break;
                    case "merchant":
                        Toast.makeText(context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                        firstSilverInPlayFlag = true;
                        break;
                    case "poacher":
                        if (emptyBankPiles > 0){
                            if (player.hand.size() > 0) {
                                if (emptyBankPiles == 1) {
                                    String toast = "there is 1 empty supply pile.\ndiscard 1 card";
                                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                                } else {
                                    String toast = "there are " + emptyBankPiles + " empty supply piles."
                                            + "\ndiscard " + emptyBankPiles + " cards";
                                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                                }
                                button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                                button.setText("discard\ncard");
                                button.setTag(POACHER);
                                setListeners(POACHER, listenerSwitches);
                                phase = POACHER;
                            } else {
                                Toast.makeText(context, "no cards in hand to discard",
                                        Toast.LENGTH_SHORT).show();
                                startOpenBankPhase(listenerSwitches);
                            }
                        }
                        break;
                    case "witch":
                        ((GameBoardActivity)activity).reactToWitch(player.getName());
                        break;
                }
            }
        }
        if (phase == BUYING_PHASE) {
            if (!card.getType().equals("treasure")){
                Toast.makeText(context, "treasure cards please", Toast.LENGTH_SHORT).show();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            } else {
                treasuresPlayed.add(card);
                if (card.getName().equals("silver") && firstSilverInPlayFlag){
                    coins += 1;
                    Toast.makeText(context, "extra coin from merchant", Toast.LENGTH_SHORT).show();
                    firstSilverInPlayFlag = false;
                }
                if ((card.getExtraCoins()> 0)||(card.getValue() > 0)) {
                    coins += card.getExtraCoins();
                    coins += card.getValue();
                    TextView textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(coins+"         saved");
                }
                numberOfTreasuresInHand -= 1;
            }
            if (numberOfTreasuresInHand == 0) {
                startOpenBankPhase(listenerSwitches);
            }
        }
        if (phase == ADVENTURER){
            int treasureFlag = 0;
            revealedCards = new ArrayList<>();
            int deckSize = player.deck.size();
            int discardSize = player.discard.size();
            boolean shuffled = false;
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
                } else if (!shuffled) {
                    player.shufflePile("discard");
                    shuffled = true;
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
            Intent intent = new Intent(context, RevealDialogActivity.class);
            intent.putStringArrayListExtra("revealedCardsKey", revealedCards);
            intent.putExtra("phaseKey", phase);
            activity.startActivityForResult(intent, REVEAL_DIALOG_KEY);
        }
        ((GameBoardActivity) activity).refreshInPlay();
    }


    public void reactToNewCardInTrash(String cardName, View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);

        if (phase == CHAPEL){
            if (trashed < 4){
                trashed += 1;
                cardsTrashed.add(card);
                if (card.getType().equals("treasure")) numberOfTreasuresInHand -=1;
                if (card.getType().equals("action") || card.getType().equals("action - attack")
                        || card.getType().equals("action - reaction"))
                    numberOfActionsInHand -= 1;
            } else {
                Toast.makeText(context, "You can only trash 4 cards", Toast.LENGTH_SHORT).show();
                ((GameBoardActivity) activity).removeCardFromTrash(((GameBoardActivity) activity).trash.size()-1);
                player.addCardToHand(cardName, layout, context, activity, handListener);
                listenerSwitches.setAllFalse();
            }
        }
    }

    public void reactToNewCardInDiscard(String cardName, ArrayList<CardData> bankPiles,
                                        ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        if (phase == OPEN_BANK){
            if (card.getCost()> coins){
                Toast.makeText(context, "too expensive\ntry something cheaper", Toast.LENGTH_SHORT).show();
                player.removeCardFromDiscard(player.discard.size()-1, context, activity);
                int bankPileCounterId = findBankViewId(cardName, bankPiles);
                TextView textView = ((Activity) activity).findViewById(bankPileCounterId);
                int count = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(count));
            } else {
                cardsGained.add(card);
                coins -= card.getCost();
                TextView textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
                textView.setText(coins+"         saved");
                buys -= 1;
                textView = ((Activity)activity).findViewById(BUYS_LEFT_ID);
                textView.setText(buys+" buys left");
                if (buys == 0) {
                    Toast.makeText(context, "you are out of buys", Toast.LENGTH_SHORT).show();
                    startCleanUpPhase(listenerSwitches);
                }
            }
        }
        if (phase == CLEAN_UP_PHASE){
            //CardData cardData = player.discard.get(player.discard.size()-1);
        }
        if (phase == POACHER){
            poacherCompliance +=1;
            if (poacherCompliance == emptyBankPiles){
                if (numberOfActionsInHand > 0){
                    startActionPhase(listenerSwitches);
                } else if (numberOfTreasuresInHand > 0){
                    Toast.makeText(context, "you are out of actions", Toast.LENGTH_SHORT).show();
                    startBuyingPhase(listenerSwitches);
                } else {
                    Toast.makeText(context, "you are out of actions and treasures",
                            Toast.LENGTH_SHORT).show();
                    startOpenBankPhase(listenerSwitches);
                }
                poacherCompliance = 0;
            }
        }
        ((GameBoardActivity)activity).refreshDiscard();
    }

    public void reactToNewCardInHand(String cardName, ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String cardType = card.getType();
        if (phase == ACTION_PHASE){
            if (cardType.equals("action") || cardType.equals("action - attack")) numberOfActionsInHand += 1;
            if (cardType.equals("action - reaction")){
                numberOfActionsInHand += 1;
                reactions.add(card.getName());
            }
            if (cardType.equals("treasure")) numberOfTreasuresInHand += 1;
        }
        if (phase == ARTISAN1){
            if (card.getCost() > 5){
                Toast.makeText(context, "too expensive\ntry something cheaper", Toast.LENGTH_SHORT).show();
                int viewId = player.hand.get(player.hand.size()-1).getImageViewId();
                player.removeCardFromHand(viewId, activity, layout);
                int bankPileCounterId = findBankViewId(cardName, bankPiles);
                TextView textView = ((Activity) activity).findViewById(bankPileCounterId);
                int count = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(count));

            } else {
                if (cardType.equals("action") || cardType.equals("action - attack"))
                    numberOfActionsInHand += 1;
                if (cardType.equals("action - reaction")) {
                    numberOfActionsInHand += 1;
                    reactions.add(card.getName());
                }
                if (cardType.equals("treasure")) numberOfTreasuresInHand += 1;
                Toast.makeText(context, "put a card from your hand into your deck.",
                        Toast.LENGTH_SHORT).show();
                Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("card\nto deck");
                button.setTag(ARTISAN2);
                setListeners(ARTISAN2, listenerSwitches);
                phase = ARTISAN2;
            }
        }
    }

    public void reactToNewCardOnDeck(String cardName, ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        if (phase == ARTISAN2){
            if (card.getType().equals("action") || card.getType().equals("action - attack"))
                numberOfActionsInHand -= 1;
            if (card.getType().equals("action - reaction")) {
                numberOfActionsInHand -= 1;
                reactions.remove(card.getName());
            }
            if (card.getType().equals("treasure")) numberOfTreasuresInHand -= 1;
            if (actions > 0 && numberOfActionsInHand > 0) {
                Toast.makeText(context, "take another action", Toast.LENGTH_SHORT).show();
                startActionPhase(listenerSwitches);
            }
            else if (numberOfTreasuresInHand > 0) {
                Toast.makeText(context, "you are out of actions", Toast.LENGTH_SHORT).show();
                startBuyingPhase(listenerSwitches);
            }
            else {
                Toast.makeText(context, "you are out of actions\nyou have no treasures in hand",
                                Toast.LENGTH_SHORT).show();
                startOpenBankPhase(listenerSwitches);
            }
        }
        ((GameBoardActivity)activity).refreshDeck();
    }

    public String drawCard(View.OnTouchListener handListener){
        String cardName = "null";
        if (player.deck.size() > 0) {
            CardData cardData = player.deck.get(player.deck.size() - 1);
            cardName = cardData.getCardName();
            player.removeCardFromDeck(player.deck.size() - 1, activity);
            player.addCardToHand(cardName, layout, context, activity, handListener);
        } else if (player.discard.size() > 0) {
            player.putDiscardInDeck(activity);
            CardData cardData = player.deck.get(player.deck.size() - 1);
            cardName = player.deck.get(player.deck.size() - 1).getCardName();
            player.removeCardFromDeck(player.deck.size() - 1, activity);
            player.addCardToHand(cardName, layout, context, activity, handListener);
        } else
            Toast.makeText(context, "no cards left to draw", Toast.LENGTH_SHORT).show();
        return cardName;
    }

    public void finishAdventurer(ListenerSwitches listenerSwitches, View.OnTouchListener handListener) {
        int deckSize = player.deck.size();
        int discardSize = player.discard.size();
        int movedToDiscard = 0;
        if (deckSize >= revealedCards.size()) {
            for (int i = 0; i < revealedCards.size(); i++) {
                Card card = basicCardSet.getCard(revealedCards.get(i));
                /*Card card1 = player.deck.get(player.deck.size()-1).getCard();
                if (!(card.getName() == card1.getName())) System.out.println("NOT EQUAL in part 1");*/
                if (card.getType().equals("treasure")) {
                    player.removeCardFromDeck(player.deck.size() - 1, activity);
                    player.addCardToHand(revealedCards.get(i), layout, context, activity, handListener);
                    numberOfTreasuresInHand += 1;
                } else {
                    player.removeCardFromDeck(player.deck.size() - 1, activity);
                    player.addCardToDiscard(revealedCards.get(i), activity, context);
                    movedToDiscard +=1;
                }
            }
        } else {
            for (int i = 0; i < deckSize; i++) {
                Card card = basicCardSet.getCard(revealedCards.get(i));
                /*Card card1 = player.deck.get(player.deck.size()-1).getCard();
                if (!(card.getName() == card1.getName())) System.out.println("NOT EQUAL in part 2");*/
                if (card.getType().equals("treasure")) {
                    player.removeCardFromDeck(player.deck.size() - 1, activity);
                    player.addCardToHand(revealedCards.get(i), layout, context, activity, handListener);
                    numberOfTreasuresInHand += 1;
                } else {
                    player.removeCardFromDeck(player.deck.size() - 1, activity);
                    player.addCardToDiscard(revealedCards.get(i), activity, context);
                    movedToDiscard +=1;
                }
            }
            //put old discard on deck unshuffled
            if (player.deck.size() != 0) System.out.println("deck not empty!");
            int newDiscardSize = player.discard.size();
            for (int i = 0; i < newDiscardSize-movedToDiscard; i++) {
                player.deck.add(player.discard.get(0));
                player.deck.get(player.deck.size() - 1).setCardMultiTagOnMoveToNewPile(player.deck.get(player.deck.size() - 1),
                        (player.deck.size() - 1), player.deckTally, "deck");
                player.deckTally += 1;
                player.removeCardFromDiscard(0, context, activity);
            }

            if ((revealedCards.size() - deckSize) <= discardSize) {
                for (int i = deckSize; i < revealedCards.size(); i++) {
                    Card card = basicCardSet.getCard(revealedCards.get(i));
                    /*Card card1 = player.deck.get(player.deck.size()-1).getCard();
                    if (!(card.getName() == card1.getName())) System.out.println("NOT EQUAL in part 3");*/
                    if (card.getType().equals("treasure")) {
                        player.removeCardFromDeck(player.deck.size() - 1, activity);
                        player.addCardToHand(revealedCards.get(i), layout, context, activity, handListener);
                        numberOfTreasuresInHand +=1;
                    } else {
                        player.removeCardFromDeck(player.deck.size() - 1, activity);
                        player.addCardToDiscard(revealedCards.get(i), activity, context);
                    }
                }
            } else {
                for (int i = deckSize; i < discardSize + deckSize; i++) {
                    Card card = basicCardSet.getCard(revealedCards.get(i));
                    /*Card card1 = player.deck.get(player.deck.size()-1).getCard();
                    if (!(card.getName() == card1.getName())) System.out.println("NOT EQUAL in part 4");*/
                    if (card.getType().equals("treasure")) {
                        player.removeCardFromDeck(player.deck.size() - 1, activity);
                        player.addCardToHand(revealedCards.get(i), layout, context, activity, handListener);
                        numberOfTreasuresInHand += 1;
                    } else {
                        player.removeCardFromDeck(player.deck.size() - 1, activity);
                        player.addCardToDiscard(revealedCards.get(i), activity, context);
                    }
                }
            }
        }
        if (actions > 0 && numberOfActionsInHand > 0) {
            Toast.makeText(context, "Take an action", Toast.LENGTH_SHORT).show();
            startActionPhase(listenerSwitches);
        }
        else if (numberOfTreasuresInHand > 0){
            String message = "You are out of actions.\nCount your coins and make a purchase.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startBuyingPhase(listenerSwitches);
        } else {
            String message = "You have no more treasures.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startOpenBankPhase(listenerSwitches);
        }
    }


    public void finishChapel(ListenerSwitches listenerSwitches){
        if (actions > 0 && numberOfActionsInHand > 0) {
            Toast.makeText(context, "Take an action", Toast.LENGTH_SHORT).show();
            startActionPhase(listenerSwitches);
        }
        else if (numberOfTreasuresInHand > 0){
            String message = "You are out of actions.\nCount your coins and make a purchase.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startBuyingPhase(listenerSwitches);
        } else {
            String message = "You have no more treasures.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            startOpenBankPhase(listenerSwitches);
        }
    }

    public int findBankViewId(String cardName, ArrayList<CardData> bankPiles){
        int textViewId = -1;
        for (int i=0; i < bankPiles.size(); i++){
            if (bankPiles.get(i).getCardName().equals(cardName)){
                textViewId = bankPiles.get(i).getTextViewId();
            }
        }
        return textViewId;
    }

    public void playAllTreasures(View.OnTouchListener inPlayListener, View.OnTouchListener handListener,
                                 ListenerSwitches listenerSwitches){
        int listLength = player.hand.size()-1;
        boolean treasureFlag = false;
        for (int i = listLength; i >=0; i--){
            Card card = player.hand.get(i).getCard();
            if (card.getType().equals("treasure")){
                treasureFlag = true;
                int viewId = player.hand.get(i).getImageViewId();
                String cardName = player.hand.get(i).getCardName();
                player.removeCardFromHand(viewId, activity, layout);
                player.addCardToPlayArea(cardName, layout, context,
                        activity, inPlayListener);
                reactToNewCardInPlay(cardName, handListener, listenerSwitches);
            }
        }
        if (!treasureFlag){
            Toast.makeText(context, "no treasures left.", Toast.LENGTH_SHORT).show();
            startOpenBankPhase(listenerSwitches);
        }
    }

    public void cleanUp(ListenerSwitches listenerSwitches, View.OnTouchListener handListener){
        for (int i = player.inPlay.size()-1; i >= 0; i --){
            String cardName = player.inPlay.get(i).getCardName();
            int viewId = player.inPlay.get(i).getImageViewId();
            player.addCardToDiscard(cardName, activity, context);
            player.removeCardFromInPlay(viewId, activity, layout);
        }
        for (int i = player.hand.size()-1; i >= 0; i --){
            String cardName = player.hand.get(i).getCardName();
            int viewId = player.hand.get(i).getImageViewId();
            player.addCardToDiscard(cardName, activity, context);
            player.removeCardFromHand(viewId, activity, layout);
        }
        int deckSize = player.drawHand(layout, context, activity, handListener);
        player.deckPile.getTextView().setText(String.valueOf(deckSize));
        ((GameBoardActivity) activity).startNextTurn();
    }
}

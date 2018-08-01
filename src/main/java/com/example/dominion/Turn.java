package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
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
    boolean firstSilverInPlayFlag = false;
    int emptyBankPiles;
    int councilRoomDraws = 0;
    ArrayList<String> reactions = new ArrayList<>();
    BasicCards basicCardSet;
    ArrayList<CardData> bankPiles;
    ArrayList<Card> actionsPlayed = new ArrayList<>();
    ArrayList<Card> treasuresPlayed = new ArrayList<>();
    ArrayList<Card> cardsGained = new ArrayList<>();


    Turn(Player player, int emptyBankPiles, ArrayList<CardData> bankPiles, GameBoardActivity activity,
            Context context, ConstraintLayout layout, BasicCards basicCardSet){
        this.player = player;
        this.emptyBankPiles = emptyBankPiles;
        this.bankPiles = bankPiles;
        this.activity = activity;
        this.context = context;
        this.layout = layout;
        this.basicCardSet = basicCardSet;
    }

    public void startTurn(ListenerSwitches listenerSwitches){
        TextView textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
        textView.setText("1 action left");
        textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
        textView.setText("0        saved");
        textView = ((Activity)activity).findViewById(BUYS_LEFT_ID);
        textView.setText("1 buy left");
        ((GameBoardActivity)activity).undoButton.setClickable(false);
        ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
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
            case HAND_TO_TRASH:
                listenerSwitches.setAllFalse();
                listenerSwitches.setTrashDragSwitch(true);
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                break;
            case HAND_TO_DISCARD:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDiscardDragSwitch(true);
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                break;
            case HAND_TO_DECK:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setDeckDragSwitch(true);
                break;
            case BANK_TO_HAND:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setBankListenerSwitch(true);
                break;
            case BANK_TO_DISCARD:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDiscardDragSwitch(true);
                listenerSwitches.setBankListenerSwitch(true);
                break;
            case DECK_TO_INPLAY:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDeckListenerSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                listenerSwitches.setDeckDragSwitch(true);
                break;
            case ALL_OFF:
                listenerSwitches.setAllFalse();
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
            updateHandData(card, true);
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
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
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
                councilRoomDraws = 0;
                for (int i = draws; i > 0; i--) {
                    String cardName1 = drawCard(handListener);
                    if (!(cardName1.equals("null"))) {
                        if (cardName.equals("councilRoom")) councilRoomDraws +=1;
                        reactToNewCardInHand(cardName1, listenerSwitches);
                    }
                    draws -= 1;
                }
                String expansion = card.getExpansionName();
                if (expansion.equals("basic+")) expansion = "basic";
                if (expansion.equals("basic-")) expansion = "basic";
                switch (expansion){
                    case "basic":
                        if (cardName.equals("merchant")) firstSilverInPlayFlag = true;
                        basicCardSet.reactToNewCardInPlay(cardName, this, handListener,
                                listenerSwitches);
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
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
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
        ((GameBoardActivity) activity).refreshInPlay();
    }


    public void undoNewCardInPlay(String source, int undoPhase, View.OnTouchListener onTouchListener,
                                  ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(source);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.undoNewCardInPlay(card, undoPhase,this, onTouchListener,
                        listenerSwitches);
                break;
        }
    }


    public void reactToNewCardInTrash(String cardName, View.OnTouchListener handListener,
                                      ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.reactToNewCardInTrash(cardName, this, handListener, listenerSwitches);
                break;
        }
    }

    public void undoNewCardInTrash(String cardName, int undoPhase, View.OnTouchListener onTouchListener,
            ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.undoNewCardInTrash(cardName, undoPhase,  this, onTouchListener,
                        listenerSwitches);
                break;
        }
    }

    public void reactToNewCardInDiscard(String cardName, ArrayList<CardData> bankPiles,
                                        ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (phase) {
            case OPEN_BANK:
                if (card.getCost() > coins) {
                    Toast.makeText(context, "too expensive\ntry something cheaper",
                            Toast.LENGTH_SHORT).show();
                    player.removeCardFromDiscard(player.discard.size() - 1, context, activity);
                    int bankPileCounterId = findBankViewId(cardName, bankPiles);
                    TextView textView = ((Activity) activity).findViewById(bankPileCounterId);
                    int count = Integer.parseInt(textView.getText().toString()) + 1;
                    textView.setText(String.valueOf(count));
                } else {
                    cardsGained.add(card);
                    coins -= card.getCost();
                    TextView textView = ((Activity) activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(coins + "         saved");
                    buys -= 1;
                    textView = ((Activity) activity).findViewById(BUYS_LEFT_ID);
                    textView.setText(buys + " buys left");
                    if (buys == 0) {
                        Toast.makeText(context, "you are out of buys", Toast.LENGTH_SHORT).show();
                        startCleanUpPhase(listenerSwitches);
                    }
                }
                break;
            default:
                String expansion = card.getExpansionName();
                if (expansion.equals("basic+")) expansion = "basic";
                if (expansion.equals("basic-")) expansion = "basic";
                switch (expansion) {
                    case "basic":
                        basicCardSet.reactToNewCardInDiscard(cardName, this, listenerSwitches);
                        break;
                }
                break;
        }
        ((GameBoardActivity)activity).refreshDiscard();
    }


    public void undoNewCardInDiscard(String cardName, int undoPhase, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.undoNewCardInDiscard(cardName, undoPhase,this, handListener,
                        listenerSwitches);
                break;
        }
    }


    public void reactToNewCardInHand(String cardName, ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (phase){
            case ACTION_PHASE:
                updateHandData(card, true);
                break;
            default:
                String expansion = card.getExpansionName();
                if (expansion.equals("basic+")) expansion = "basic";
                if (expansion.equals("basic-")) expansion = "basic";
                switch (expansion) {
                    case "basic":
                        basicCardSet.reactToNewCardInHand(cardName, this, listenerSwitches);
                        break;
                }
                break;
        }
    }

    public void reactToNewCardOnDeck(String cardName, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.reactToNewCardOnDeck(cardName, this, handListener, listenerSwitches);
                break;
        }
        ((GameBoardActivity)activity).refreshDeck();
    }

    public void undoNewCardOnDeck(String cardName, int undoPhase, View.OnTouchListener onTouchListener,
                                  ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        String expansion = card.getExpansionName();
        if (expansion.equals("basic+")) expansion = "basic";
        if (expansion.equals("basic-")) expansion = "basic";
        switch (expansion) {
            case "basic":
                basicCardSet.undoNewCardOnDeck(cardName, undoPhase, this, onTouchListener,
                        listenerSwitches);
                break;
        }
    }


    public void continueToTurnPhase(ListenerSwitches listenerSwitches){
        if (numberOfActionsInHand > 0 && actions > 0) {
            startActionPhase(listenerSwitches);
        } else if (numberOfTreasuresInHand > 0) {
            Toast.makeText(context, "you are out of actions", Toast.LENGTH_SHORT).show();
            startBuyingPhase(listenerSwitches);
        } else {
            Toast.makeText(context, "you are out of actions and treasures",
                    Toast.LENGTH_SHORT).show();
            startOpenBankPhase(listenerSwitches);
        }
    }

    public void updateHandData(Card card, boolean add) {
        String cardType = card.getType();
        String cardName = card.getName();
        if (add){
            if (cardType.equals("action") || cardType.equals("action - attack"))
                numberOfActionsInHand += 1;
            if (cardType.equals("action - reaction")) {
                numberOfActionsInHand += 1;
                reactions.add(cardName);
            }
            if (cardType.equals("treasure")) numberOfTreasuresInHand += 1;
        } else {
            if (cardType.equals("action") || cardType.equals("action - attack"))
                numberOfActionsInHand -= 1;
            if (cardType.equals("action - reaction")) {
                numberOfActionsInHand -= 1;
                reactions.remove(cardName);
            }
            if (cardType.equals("treasure")) numberOfTreasuresInHand -= 1;
        }
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
            cardName = player.deck.get(player.deck.size() - 1).getCardName();
            player.removeCardFromDeck(player.deck.size() - 1, activity);
            player.addCardToHand(cardName, layout, context, activity, handListener);
        } else
            Toast.makeText(context, "no cards left to draw", Toast.LENGTH_SHORT).show();
        return cardName;
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


    public ArrayList<CardData> playAllTreasures(View.OnTouchListener inPlayListener,
                                                View.OnTouchListener handListener,
                                                ListenerSwitches listenerSwitches){
        int listLength = player.hand.size()-1;
        ArrayList<CardData> treasureList = new ArrayList<>();
        for (int i = listLength; i >=0; i--){
            Card card = player.hand.get(i).getCard();
            if (card.getType().equals("treasure")){
                int viewId = player.hand.get(i).getImageViewId();
                String cardName = player.hand.get(i).getCardName();
                player.removeCardFromHand(viewId, activity, layout);
                player.addCardToPlayArea(cardName, layout, context,
                        activity, inPlayListener);
                treasureList.add(player.inPlay.get(player.inPlay.size()-1));
                reactToNewCardInPlay(cardName, handListener, listenerSwitches);
            }
        }
        startOpenBankPhase(listenerSwitches);
        return treasureList;
    }


    public void unplayTreasures(ArrayList<CardData> treasureList, View.OnTouchListener handListener,
                                ListenerSwitches listenerSwitches){
        for (int i = 0; i < treasureList.size(); i++){
            int viewId = treasureList.get(i).getImageViewId();
            String cardName = treasureList.get(i).getCardName();
            player.removeCardFromInPlay(viewId, activity, layout);
            player.addCardToHand(cardName, layout, context, activity, handListener);
            numberOfTreasuresInHand +=1;
            coins -= treasureList.get(i).getCard().getValue();
        }
        TextView textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
        textView.setText(coins+"         saved");
        startBuyingPhase(listenerSwitches);
    }


    public void cleanUp(){
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
        player.drawOffTurnHand();
        ((GameBoardActivity) activity).startNextTurn();
    }
}

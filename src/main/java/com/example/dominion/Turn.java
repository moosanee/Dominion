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
    BasicCards basicCardSet = new BasicCards();


    Turn(Player player, Activity activity, Context context, ConstraintLayout layout){
        this.player = player;
        this.activity = activity;
        this.context = context;
        this.layout = layout;
    }

    public void startTurn(ListenerSwitches listenerSwitches){
        phase = BEGIN_TURN;
        setListeners(BEGIN_TURN, listenerSwitches);
        promptTurn();
    }

    public void setListeners(int key, ListenerSwitches listenerSwitches){
        switch (key) {
            case BEGIN_TURN:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                break;
            case ACTION_PHASE:
                listenerSwitches.setAllFalse();
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                listenerSwitches.setInPlayDragSwitch(true);
                break;
            case OPEN_BANK:
                listenerSwitches.setAllFalse();
                listenerSwitches.setInPlayDragSwitch(true);
                listenerSwitches.setBankListenerSwitch(true);
                break;
        }
    }

    public void promptTurn(){
        int actionCardsInHand = 0;
        for (int i=0; i < player.hand.size(); i++){
            if (player.hand.get(i).getCard().getType().equals("action")) actionCardsInHand += 1;
            else if (player.hand.get(i).getCard().getType().equals("action - attack"))
                actionCardsInHand += 1;
            else if (player.hand.get(i).getCard().getType().equals("treasure"))
                numberOfTreasuresInHand += 1;
        }
        if (actionCardsInHand > 0) {
            String message = player.getName() + ", it's your turn.\nTake an action";
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            phase = ACTION_PHASE;
        } else {
            String message = player.getName() + ", it's your turn.\n" +
                    "You have no actions.\nCount your coins and make a purchase.";
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            phase = BUYING_PHASE;
        }
    }

    public void reactToNewCardInPlay(String cardName, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches, ArrayList<CardData> bankPiles){
        Card card = basicCardSet.getCard(cardName);
        if (phase == ACTION_PHASE) {
            if (!(card.getType().equals("action") || card.getType().equals("action - attack"))){
                Toast.makeText(context, "action card please", Toast.LENGTH_SHORT).show();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            }
            if (card.getActions()> 0) {
                actions += card.getActions();
                TextView textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions+" actions left");
            }
            if (card.getExtraBuys()> 0) {
                buys += card.getExtraBuys();
                TextView textView = ((Activity)activity).findViewById(BUYS_LEFT_ID);
                textView.setText(buys+" buys left");
            }
            if ((card.getExtraCoins()> 0)||(card.getValue() > 0)) {
                coins += card.getExtraCoins();
                coins += card.getValue();
                TextView textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
                textView.setText(coins+"        saved");
            }
            if (card.getDrawCards() > 0) {
                draws += card.getDrawCards();
                player.removeCardFromDeck(player.deck.size()-1, activity);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            }
            if (!(card.getInstructions().equals(""))){
                Toast.makeText(context, card.getInstructions(), Toast.LENGTH_LONG).show();
            }
            for (int i = 0; i < draws; i++){
                player.removeCardFromDeck(player.deck.size()-1, activity);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            }
        }
        if (phase == BUYING_PHASE) {
            if (!card.getType().equals("treasure")){
                Toast.makeText(context, "treasure cards please", Toast.LENGTH_SHORT).show();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(cardName, layout, context, activity, handListener);
            } else {
                if ((card.getExtraCoins()> 0)||(card.getValue() > 0)) {
                    coins += card.getExtraCoins();
                    coins += card.getValue();
                    TextView textView = ((Activity)activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(coins+"        saved");
                }
                numberOfTreasuresInHand -= 1;
            }
            if (numberOfTreasuresInHand == 0) {
                Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\nbuying");
                setListeners(OPEN_BANK, listenerSwitches);
                phase = OPEN_BANK;
            }
        }
        if (phase == OPEN_BANK){
            if (card.getCost()> coins){
                Toast.makeText(context, "too expensive\ntry something cheaper", Toast.LENGTH_SHORT).show();
                int viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                int bankPileCounterId = findBankViewId(cardName, bankPiles);
                TextView textView = ((Activity) activity).findViewById(bankPileCounterId);
                int count = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(count));
            }
        }

    }

    public int findBankViewId(String cardName, ArrayList<CardData> bankPiles){
        int textViewId = -1;
        for (int i=0; i < bankPiles.size()-1; i++){
            if (bankPiles.get(i).getCardName().equals(cardName)){
                textViewId = bankPiles.get(i).getTextViewId();
            }
        }
        return textViewId;
    }

}

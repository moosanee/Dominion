package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import static com.example.dominion.MyConstants.*;

public class Turn {

    Activity activity;
    Context context;
    Player player;
    int actions = 1;
    int buys = 1;
    int virtualCoins = 0;
    boolean[] listenerSwitches = {false, false, false, false, false, false, false};
                                // hand, inPlay, deck, discard, bank, trash, opponents
    boolean[] dragSwitches = {false, false, false, false, false, false};
                            // hand, inPlay, deck, discard, bank, trash

    Turn(Player player, Activity activity, Context context){
        this.player = player;
        this.activity = activity;
        this.context = context;
    }

    public void takeTurn(){
        setListeners(BEGIN_TURN);
        promptTurn();
    }

    public void setListeners(int key){
        if (key == BEGIN_TURN) {
            listenerSwitches[0] = true; //can drag from hand
            dragSwitches[1] = true; //can drag to inPlay
        }
    }

    public void promptTurn(){
        int actionCardsInHand = 0;
        for (int i=0; i < player.hand.size(); i++){
            if (player.hand.get(i).getCard().getType().equals("action")) actionCardsInHand += 1;
            if (player.hand.get(i).getCard().getType().equals("action - attack"))
                actionCardsInHand += 1;
        }
        if (actionCardsInHand > 0) {
            String message = player.getName() + ", it's your turn.\nTake an action";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            String message = player.getName() + ",it's your turn.\n" +
                    "You have no actons.\nCount your coins and make a purchase.";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean[] getListenerSwitches() {
        return listenerSwitches;
    }
}

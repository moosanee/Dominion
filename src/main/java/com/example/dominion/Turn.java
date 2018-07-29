package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    int previousPhase;
    int actions = 1;
    int buys = 1;
    int coins = 0;
    int draws = 0;
    int numberOfTreasuresInHand = 0;
    int numberOfActionsInHand = 0;
    int trashed = 0;
    int discarded = 0;
    boolean firstSilverInPlayFlag = false;
    int emptyBankPiles;
    int poacherCompliance = 0;
    boolean adventurerShuffle = false;
    ArrayList<String> reactions = new ArrayList<>();
    BasicCards basicCardSet = new BasicCards();
    ArrayList<CardData> bankPiles; // = new ArrayList<>();
    ArrayList<Card> actionsPlayed = new ArrayList<>();
    ArrayList<Card> treasuresPlayed = new ArrayList<>();
    ArrayList<Card> cardsGained = new ArrayList<>();
    ArrayList<Card> cardsTrashed = new ArrayList<>();
    ArrayList<Card> cardsDiscarded = new ArrayList<>();
    ArrayList<String> revealedCards = new ArrayList<>();
    ArrayList<String> postList = new ArrayList<>();
    ArrayList<BanditAttack> banditAttackResult = new ArrayList<>();
    ArrayList<BureaucratAttack> bureaucratAttackResult = new ArrayList<>();



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
            case CHAPEL:
                listenerSwitches.setAllFalse();
                listenerSwitches.setTrashDragSwitch(true);
                listenerSwitches.setHandListenerSwitch(true);
                listenerSwitches.setHandDragSwitch(true);
                break;
            case CELLAR:
                listenerSwitches.setAllFalse();
                listenerSwitches.setDiscardDragSwitch(true);
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
                    case "bandit":
                        player.addCardToDiscard("gold", activity, context);
                        ((GameBoardActivity)activity).removeCardFromBankPile("gold");
                        ((GameBoardActivity)activity).undo = new Undo("moved bandit to inPlay",
                                this, BANDIT, bankPiles, handListener, listenerSwitches);
                        postList.add("You gained a gold");
                        banditAttackResult = ((GameBoardActivity)activity)
                                .reactToBanditAttack(player.getName());
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
                        Intent intent = new Intent(context, NotificationActivity.class);
                        intent.putExtra("postListKey", postList);
                        activity.startActivity(intent);
                        postList.clear();
                        break;
                    case "bureaucrat":
                        player.addCardToDeck("silver", activity, context);
                        ((GameBoardActivity)activity).removeCardFromBankPile("silver");
                        ((GameBoardActivity)activity).undo = new Undo(
                                "moved bureaucrat to inPlay", this,
                                BUREAUCRAT, bankPiles, handListener, listenerSwitches);
                        postList.add("You gained a silver to your deck");
                        bureaucratAttackResult = ((GameBoardActivity)activity)
                                .reactToBureaucratAttack(player.getName());
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
                        intent = new Intent(context, NotificationActivity.class);
                        intent.putExtra("postListKey", postList);
                        activity.startActivity(intent);
                        postList.clear();
                        break;
                    case "chancellor":
                        intent = new Intent(context, DecisionDialogActivity.class);
                        activity.startActivityForResult(intent, CHANCELLOR_ANSWER_CODE);
                        break;
                    case "chapel":
                        Toast.makeText(context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                        button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("finished\ntrashing");
                        button.setTag(CHAPEL);
                        setListeners(CHAPEL, listenerSwitches);
                        phase = CHAPEL;
                        break;
                    case "cellar":
                        Toast.makeText(context, card.getInstructions(), Toast.LENGTH_SHORT).show();
                        button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                        button.setText("finished\ndiscarding");
                        button.setTag(CELLAR);
                        setListeners(CELLAR, listenerSwitches);
                        phase = CELLAR;
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
        if (phase == ADVENTURER){
            ((GameBoardActivity) activity).undo = new Undo("moved adventurer to inPlay",
                    this, phase, handListener, listenerSwitches);
            ((GameBoardActivity) activity).undoButton.setClickable(true);
            ((GameBoardActivity) activity).undoButton.setAlpha(1f);
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
            Intent intent = new Intent(context, RevealDialogActivity.class);
            intent.putStringArrayListExtra("revealedCardsKey", revealedCards);
            intent.putExtra("phaseKey", phase);
            activity.startActivity(intent);
        }
        ((GameBoardActivity) activity).refreshInPlay();
    }


    public void undoNewCardInPlay(String source, int undoPhase, View.OnTouchListener onTouchListener,
                                  ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(source);
        switch (undoPhase){
            case ADVENTURER:
                if (revealedCards.size() > player.deck.size()) adventurerShuffle = true;
                revealedCards.clear();
                int viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(source, layout, context, activity, onTouchListener);
                numberOfActionsInHand += 1;
                actions += 1;
                TextView textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions+" actions left");
                startActionPhase(listenerSwitches);
                break;
            case ARTISAN1:
                viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand(source, layout, context, activity, onTouchListener);
                numberOfActionsInHand += 1;
                actions += 1;
                textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions+" actions left");
                startActionPhase(listenerSwitches);
                break;
            case BANDIT:
                //put bandit back in hand
                viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand("bandit", layout, context, activity, onTouchListener);
                numberOfActionsInHand += 1;
                actions += 1;
                textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions+" actions left");
                //return gold to bank
                player.removeCardFromDiscard(player.discard.size()-1, context, activity);
                ((GameBoardActivity)activity).addCardToBankPile("gold");
                //return cards to other players
                for (int i = 0; i < banditAttackResult.size(); i++){
                    if (!banditAttackResult.get(i).isBlocked()) {
                        int playerIndex = banditAttackResult.get(i).getPlayerNumber();
                        if (!banditAttackResult.get(i).getCard2().equals("")) {
                            card = basicCardSet.getCard(banditAttackResult.get(i).getCard2());
                            ((GameBoardActivity) activity).playerList.get(playerIndex)
                                    .addOffTurnCard(card.getName(), "deck");
                            if (banditAttackResult.get(i).getTrashed() == 1) {
                                ((GameBoardActivity) activity).removeCardFromTrashByName(card.getName());
                            } else {
                                int top = ((GameBoardActivity) activity).playerList.get(playerIndex)
                                        .discard.size() - 1;
                                ((GameBoardActivity) activity).playerList.get(playerIndex)
                                        .removeOffTurnCard(top, "discard");
                            }
                        }
                        if (!banditAttackResult.get(i).getCard1().equals("")) {
                            card = basicCardSet.getCard(banditAttackResult.get(i).getCard1());
                            ((GameBoardActivity) activity).playerList.get(playerIndex)
                                    .addOffTurnCard(card.getName(), "deck");
                            if (banditAttackResult.get(i).getTrashed() == 0) {
                                ((GameBoardActivity) activity).removeCardFromTrashByName(card.getName());
                            } else {
                                int top = ((GameBoardActivity) activity).playerList.get(playerIndex)
                                        .discard.size() - 1;
                                ((GameBoardActivity) activity).playerList.get(playerIndex)
                                        .removeOffTurnCard(top, "discard");
                            }
                        }
                    }
                }
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity) activity).undoButton.setAlpha(0.5f);
                startActionPhase(listenerSwitches);
                break;
            case BUREAUCRAT:
                //put bureaucrat back in hand
                viewId = player.inPlay.get(player.inPlay.size()-1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                player.addCardToHand("bureaucrat", layout, context, activity, onTouchListener);
                numberOfActionsInHand += 1;
                actions += 1;
                textView = ((Activity)activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions+" actions left");
                //return silver to bank
                player.removeCardFromDeck(player.deck.size()-1, activity);
                ((GameBoardActivity)activity).addCardToBankPile("silver");
                //return cards to other players
                for (int i = 0; i < bureaucratAttackResult.size(); i++) {
                    if (!bureaucratAttackResult.get(i).isBlocked()) {
                        if (bureaucratAttackResult.get(i).isVictoryInHand()){
                            Player player = ((GameBoardActivity)activity).playerList
                                    .get(bureaucratAttackResult.get(i).getPlayerNumber());
                            String cardName = bureaucratAttackResult.get(i).getCardOnDeck();
                            int index = player.deck.size()-1;
                            player.addOffTurnCard(cardName, "hand");
                            player.removeOffTurnCard(index, "deck");
                        }
                    }
                }
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity) activity).undoButton.setAlpha(0.5f);
                startActionPhase(listenerSwitches);
                break;
            default:
                viewId = player.inPlay.get(player.inPlay.size() - 1).getImageViewId();
                player.removeCardFromInPlay(viewId, activity, layout);
                if (card.getType().equals("action") || card.getType().equals("action - attack")) {
                    numberOfActionsInHand += 1;
                    actions += 1;
                    if (source.equals("merchant")) firstSilverInPlayFlag = false;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand += 1;
                    actions += 1;
                    reactions.add(card.getName());
                } else if (card.getType().equals("treasure")) numberOfTreasuresInHand += 1;
                if (card.getActions() > 0) actions -= card.getActions();
                textView = ((Activity) activity).findViewById(ACTIONS_LEFT_ID);
                textView.setText(actions + " actions left");
                if (card.getExtraBuys() > 0) {
                    buys -= card.getExtraBuys();
                    textView = ((Activity) activity).findViewById(BUYS_LEFT_ID);
                    textView.setText(buys + " buys left");
                }
                if ((card.getExtraCoins() > 0) || (card.getValue() > 0)) {
                    coins -= card.getExtraCoins();
                    coins -= card.getValue();
                    textView = ((Activity) activity).findViewById(COINS_COLLECTED_ID);
                    textView.setText(coins + "         saved");
                }
                if (card.getDrawCards() > 0) {
                    draws = card.getDrawCards();
                    for (int i = draws; i > 0; i--) {
                        CardData cardData = player.hand.get(player.hand.size()-1);
                        viewId = cardData.getImageViewId();
                        String cardName = cardData.getCardName();
                        player.removeCardFromHand(viewId, activity, layout);
                        if (cardData.getCard().getType().equals("treasure")) numberOfTreasuresInHand -= 1;
                        if (cardData.getCard().getType().equals("action") ||
                                cardData.getCard().getType().equals("action - attack")){
                            numberOfActionsInHand -= 1;
                        } else if (cardData.getCard().getType().equals("action - reaction")) {
                            reactions.remove(card.getName());
                            numberOfActionsInHand -= 1;
                        }
                        player.addCardToDeck(cardName, activity, context);
                        draws -= 1;
                    }
                }
                player.addCardToHand(source, layout, context, activity, onTouchListener);
                if (card.getType().equals("action") || card.getType().equals("action - attack") ||
                        card.getType().equals("action - reaction")) startActionPhase(listenerSwitches);
                if (card.getType().equals("treasure")) startBuyingPhase(listenerSwitches);
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
                break;
        }
    }


    public void reactToNewCardInTrash(String cardName, View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);

        switch (phase) {
            case CHAPEL:
            if (trashed < 4){
                trashed += 1;
                cardsTrashed.add(card);
                if (card.getType().equals("treasure")) numberOfTreasuresInHand -=1;
                if (card.getType().equals("action") || card.getType().equals("action - attack")){
                    numberOfActionsInHand -= 1;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand -= 1;
                    reactions.remove(card.getName());
                }
            } else {
                Toast.makeText(context, "You can only trash 4 cards", Toast.LENGTH_SHORT).show();
                ((GameBoardActivity) activity).removeCardFromTrashByIndex(((GameBoardActivity) activity).trash.size()-1);
                player.addCardToHand(cardName, layout, context, activity, handListener);
                listenerSwitches.setAllFalse();
            }
            break;
        }
    }

    public void undoNewCardInTrash(String cardName, int undoPhase, View.OnTouchListener onTouchListener,
            ListenerSwitches listenerSwitches){
        switch (undoPhase){
            case CHAPEL:
                Card card = basicCardSet.getCard(cardName);
                ((GameBoardActivity)activity).removeCardFromTrashByName(cardName);
                player.addCardToHand(cardName, layout, context, activity, onTouchListener);
                if (card.getType().equals("action") || card.getType().equals("action - attack")){
                    numberOfActionsInHand += 1;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand += 1;
                    reactions.add(card.getName());
                } else if (card.getType().equals("treasure")) numberOfTreasuresInHand += 1;
                trashed -= 1;
                Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ntrashing");
                button.setTag(CHAPEL);
                setListeners(CHAPEL, listenerSwitches);
                phase = CHAPEL;
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
                break;
        }
    }

    public void reactToNewCardInDiscard(String cardName, ArrayList<CardData> bankPiles,
                                        ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (phase) {
            case OPEN_BANK:
                if (card.getCost() > coins) {
                    Toast.makeText(context, "too expensive\ntry something cheaper", Toast.LENGTH_SHORT).show();
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
            case CLEAN_UP_PHASE:
                //CardData cardData = player.discard.get(player.discard.size()-1);
                break;
            case CELLAR:
                discarded += 1;
                cardsDiscarded.add(card);
                if (card.getType().equals("treasure")) numberOfTreasuresInHand -= 1;
                else if (card.getType().equals("action") || card.getType().equals("action - attack")) {
                    numberOfActionsInHand -= 1;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand -= 1;
                    reactions.remove(card.getName());
                }

                break;
            case POACHER:
                poacherCompliance += 1;
                if (card.getType().equals("action") || card.getType().equals("action - attack")){
                    numberOfActionsInHand -= 1;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand -= 1;
                    reactions.remove(card.getName());
                }
                if (card.getType().equals("treasure")) numberOfTreasuresInHand -= 1;
                if (poacherCompliance == emptyBankPiles) {
                    previousPhase = POACHER;
                    if (numberOfActionsInHand > 0) {
                        startActionPhase(listenerSwitches);
                    } else if (numberOfTreasuresInHand > 0) {
                        Toast.makeText(context, "you are out of actions", Toast.LENGTH_SHORT).show();
                        startBuyingPhase(listenerSwitches);
                    } else {
                        Toast.makeText(context, "you are out of actions and treasures",
                                Toast.LENGTH_SHORT).show();
                        startOpenBankPhase(listenerSwitches);
                    }
                    poacherCompliance = 0;
                }
                break;
        }
        ((GameBoardActivity)activity).refreshDiscard();
    }


    public void undoNewCardInDiscard(String cardName, int phaseOfUndo, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (phaseOfUndo) {
            case OPEN_BANK:
                player.removeCardFromDiscard(player.discard.size() - 1, context, activity);
                int bankPileCounterId = findBankViewId(cardName, bankPiles);
                TextView textView = ((Activity) activity).findViewById(bankPileCounterId);
                int count = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(count));
                cardsGained.remove(card);
                coins += card.getCost();
                textView = ((Activity) activity).findViewById(COINS_COLLECTED_ID);
                textView.setText(coins + "         saved");
                buys += 1;
                textView = ((Activity) activity).findViewById(BUYS_LEFT_ID);
                textView.setText(buys + " buys left");
                if (phase == CLEAN_UP_PHASE) startOpenBankPhase(listenerSwitches);
                break;
            case CELLAR:
                card = basicCardSet.getCard(cardName);
                player.removeCardFromDiscard(player.discard.size()-1, context, activity);
                player.addCardToHand(cardName, layout, context, activity, handListener);
                if (card.getType().equals("action") || card.getType().equals("action - attack")){
                    numberOfActionsInHand += 1;
                } else if (card.getType().equals("action - reaction")){
                    numberOfActionsInHand += 1;
                    reactions.add(card.getName());
                } else if (card.getType().equals("treasure")) numberOfTreasuresInHand += 1;
                discarded -= 1;
                Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("finished\ndiscarding");
                button.setTag(CELLAR);
                setListeners(CELLAR, listenerSwitches);
                phase = CELLAR;
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
                break;
            case POACHER:
                if (poacherCompliance > 0) poacherCompliance -= 1;
                if (card.getType().equals("action") || card.getType().equals("action - attack")){
                    numberOfActionsInHand += 1;
                } else if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand += 1;
                    reactions.add(card.getName());
                } else if (card.getType().equals("treasure")) numberOfTreasuresInHand += 1;
                player.removeCardFromDiscard(player.discard.size() - 1, context, activity);
                player.addCardToHand(cardName, layout, context, activity, handListener);
                int discardsLeft = emptyBankPiles - poacherCompliance;
                if (emptyBankPiles == 1) {
                    String toast = "there is 1 empty supply pile.\ndiscard 1 card";
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                } else {
                    String toast = "there are " + emptyBankPiles + " empty supply piles."
                            + "\ndiscard " + discardsLeft + " cards";
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                }
                button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("discard\ncard");
                button.setTag(POACHER);
                setListeners(POACHER, listenerSwitches);
                phase = POACHER;
                break;


        }
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
        if (phase == CELLAR){
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

    public void reactToNewCardOnDeck(String cardName, View.OnTouchListener handListener,
                                     ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (phase){
            case ARTISAN2:
                String description = "moved " + cardName + " to deck";
                ((GameBoardActivity)activity).undo = new Undo(description, this, phase,
                        handListener, listenerSwitches);
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
                ((GameBoardActivity)activity).undoButton.setClickable(true);
                ((GameBoardActivity)activity).undoButton.setAlpha(1f);
            break;
        }
        ((GameBoardActivity)activity).refreshDeck();
    }

    public void undoNewCardOnDeck(String cardName, int undoPhase, View.OnTouchListener onTouchListener,
                                  ListenerSwitches listenerSwitches){
        Card card = basicCardSet.getCard(cardName);
        switch (undoPhase){
            case ARTISAN2:
                //return card to hand
                player.removeCardFromDeck(player.deck.size()-1, activity);
                player.addCardToHand(cardName, layout, context, activity, onTouchListener);
                //book-keeping
                if (card.getType().equals("action") || card.getType().equals("action - attack"))
                    numberOfActionsInHand += 1;
                if (card.getType().equals("action - reaction")) {
                    numberOfActionsInHand += 1;
                    reactions.add(card.getName());
                }
                if (card.getType().equals("treasure")) numberOfTreasuresInHand += 1;
                //set phase
                Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
                button.setText("card\nto deck");
                button.setTag(ARTISAN2);
                setListeners(ARTISAN2, listenerSwitches);
                phase = ARTISAN2;
                //disable undo
                ((GameBoardActivity)activity).undoButton.setClickable(false);
                ((GameBoardActivity)activity).undoButton.setAlpha(0.5f);
                break;
        }
    }

    public void undoDeckToDiscard(int cardsMoved, ListenerSwitches listenerSwitches){
        int index = player.discard.size()-cardsMoved;
        for (int i = 0; i < cardsMoved; i++){
            String cardName = player.discard.get(index).getCardName();
            player.addCardToDeck(cardName, activity, context);
            player.removeCardFromDiscard(index, context, activity);
        }
        ((GameBoardActivity)activity).undo = new Undo("moved chancellor to inPlay",
                this, listenerSwitches);
        startActionPhase(listenerSwitches);
        ((GameBoardActivity)activity).undoButton.setClickable(true);
        ((GameBoardActivity)activity).undoButton.setAlpha(1f);
        Intent intent = new Intent(context, DecisionDialogActivity.class);
        activity.startActivityForResult(intent, CHANCELLOR_ANSWER_CODE);
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

    public void finishAdventurer(ListenerSwitches listenerSwitches, View.OnTouchListener handListener) {
        int deckSize = player.deck.size();
        int discardSize = player.discard.size();
        int movedToDiscard = 0;
        adventurerShuffle = false;
        if (deckSize >= revealedCards.size()) {
            for (int i = 0; i < revealedCards.size(); i++) {
                Card card = basicCardSet.getCard(revealedCards.get(i));
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


    public int finishChapel(ListenerSwitches listenerSwitches){
        int trashedSoFar = trashed;
        trashed = 0;
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
        return trashedSoFar;
    }

    public void returnToChapelTrashing(int trashed, ListenerSwitches listenerSwitches){
        this.trashed = trashed;
        Card card = basicCardSet.getCard("chapel");
        Toast.makeText(context, card.getInstructions(), Toast.LENGTH_SHORT).show();
        Button button = ((Activity) activity).findViewById(PHASE_BUTTON_ID);
        button.setText("finished\ntrashing");
        button.setTag(CHAPEL);
        setListeners(CHAPEL, listenerSwitches);
        phase = CHAPEL;
    }

    public int finishCellar(View.OnTouchListener handListener, ListenerSwitches listenerSwitches){
        int discardedSoFar = discarded;
        discarded = 0;
        for (int i = discardedSoFar; i > 0; i--) {
            String cardName1 = drawCard(handListener);
            if (!(cardName1.equals("null"))) {
                reactToNewCardInHand(cardName1, listenerSwitches);
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
        return discardedSoFar;
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

    public ArrayList<CardData> playAllTreasures(View.OnTouchListener inPlayListener, View.OnTouchListener handListener,
                                 ListenerSwitches listenerSwitches){
        int listLength = player.hand.size()-1;
        ArrayList<CardData> treasureList = new ArrayList<>();
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
                treasureList.add(player.inPlay.get(player.inPlay.size()-1));
                reactToNewCardInPlay(cardName, handListener, listenerSwitches);
            }
        }
        if (!treasureFlag){
            Toast.makeText(context, "no treasures left.", Toast.LENGTH_SHORT).show();
            startOpenBankPhase(listenerSwitches);
        }
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

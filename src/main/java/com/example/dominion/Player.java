package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static com.example.dominion.MyConstants.*;

public class Player implements Serializable{

    String Name;
    int number;
    boolean human;
    int score;
    ArrayList<CardData> deck;
    ArrayList<CardData> hand;
    ArrayList<CardData> discard;
    ArrayList<CardData> inPlay;
    int handTally = 0;
    int deckTally = 0;
    int discardTally = 0;
    int inPlayTally = 0;
    CardData deckPile;
    CardData discardPile;
    BasicCards basicCardSet;
    private static final long serialVersionUID = 1L;

    Player(PlayerInfo playerInfo, BasicCards basicCardSet) {
        this.Name = playerInfo.getName();
        this.number = playerInfo.getNumber();
        this.human = playerInfo.getHuman();
        this.basicCardSet = basicCardSet;
        this.deck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deck.add(new CardData("estate", "deck", deckTally, deckTally, basicCardSet)); // add 3 estates to the deck
            deckTally +=1;
        }
        for (int i = 0; i < 7; i++) {
            deck.add(new CardData("copper", "deck", deckTally, deckTally, basicCardSet)); // add 7 coppers to the deck
            deckTally += 1;
        }
        this.hand = new ArrayList<>();
        this.discard = new ArrayList<>();
        this.inPlay = new ArrayList<>();
    }



     public void displayHand(ConstraintLayout layout, Context context, Activity activity,
                             View.OnTouchListener handListener) {
        //calculate placement to center group of overlapped cards within hand zone
        int numberOfCards = this.hand.size();
        int handWidth = ((cardWidth-minOverlap)*(numberOfCards-1)+cardWidth);
        int overlap;
        int shift;
        if (handZoneWidth<handWidth){
            overlap = minOverlap + (handWidth-handZoneWidth)/(numberOfCards-1);
            shift = 0;
        }
        else {
            overlap = minOverlap;
            shift = (handZoneWidth-handWidth)/2;
        }
        //create and place hand card images
        for (int i = 0; i < this.hand.size(); i++){
            String cardName = this.hand.get(i).getCardName();
            ImageView imageView = (new ImageView(context));
            final int FINALI = this.hand.get(i).getPosition();
            this.hand.get(i).setImageViewId(PLAYER_HAND_VIEW_ID + FINALI);
            imageView.setId(PLAYER_HAND_VIEW_ID + FINALI);
            imageView.setImageDrawable(getImageDps(activity, cardName, (cardWidth/2)));
            imageView.setTag(hand.get(i).getCardMultiTag());
            int leftMargin = handMargin+shift+(cardWidth-overlap)*i;
            int bottomMargin = buffer*3;
            this.hand.get(i).setImageViewLeftMargin(leftMargin);
            this.hand.get(i).setImageViewBottomMargin(bottomMargin);
            ConstraintLayout.LayoutParams params = new ConstraintLayout
                    .LayoutParams(cardWidth, cardHeight);
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMargins(leftMargin, 0, 0,bottomMargin);
            imageView.setLayoutParams(params);
            layout.addView(imageView);
            imageView.setOnTouchListener(handListener);
            imageView.setOnDragListener(hand.get(i).getDragListener());
        }
    }//display hand


//initialize deck
    public void initializeDeck(ConstraintLayout layout, Context context, Activity activity){
        deckPile = new CardData("back", "deck", 0,0, basicCardSet);
        deckPile.setImageViewId(DECK_PILE_ID);
        ImageView imageView = new ImageView(context);
        imageView.setId(DECK_PILE_ID);
        imageView.setImageDrawable(getImageDps(activity, "back", (cardWidth/2)));
        imageView.setTag(deckPile.getCardMultiTag());
        int leftMargin = buffer;
        int bottomMargin = 3*buffer;
        deckPile.setImageViewLeftMargin(leftMargin);
        deckPile.setImageViewBottomMargin(bottomMargin);
        ConstraintLayout.LayoutParams params = new ConstraintLayout
                .LayoutParams(cardWidth, cardHeight);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(leftMargin, 0, 0,bottomMargin);
        imageView.setLayoutParams(params);
        layout.addView(imageView);
//deck counter
        deckPile.setTextView(new TextView(context));
        deckPile.getTextView().setId(DECK_PILE_ID + 1);
        deckPile.getTextView().setTextSize(counterTextSize);
        deckPile.getTextView().setTextColor(BLACK_COLOR);
        deckPile.getTextView().setIncludeFontPadding(false);
        deckPile.getTextView().setText(String.valueOf(this.deck.size()));
        deckPile.getTextView().setTag(new CardMultiTag(
                0,0,"deckSize", "deck"));
        deckPile.getTextView().setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
        params = new ConstraintLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftToLeft = DECK_PILE_ID;
        params.rightToRight = DECK_PILE_ID;
        params.topToTop = DECK_PILE_ID;
        params.bottomToBottom = DECK_PILE_ID;
        params.setMargins(0, 0, 0,0);
        deckPile.getTextView().setLayoutParams(params);
        layout.addView(deckPile.getTextView());
//deck label
        TextView deckLabel = new TextView(context);
        deckLabel.setId(DECK_PILE_ID+2);
        deckLabel.setTextSize(textSize);
        deckLabel.setTextColor(ACCENT_COLOR);
        deckLabel.setIncludeFontPadding(false);
        deckLabel.setText("Deck");
        deckLabel.setTag("deck");
        deckLabel.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
        params = new ConstraintLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = DECK_PILE_ID;
        params.rightToRight = DECK_PILE_ID;
        params.setMargins(0, 0, 0,0);
        deckLabel.setLayoutParams(params);
        layout.addView(deckLabel);
    }

//initialize discard
    public void initializeDiscard(ConstraintLayout layout, Context context, Activity activity){
        discardPile = new CardData("back", "discard", 0,0,
                basicCardSet);
        ImageView imageView = new ImageView(context);
        discardPile.setImageViewId(DISCARD_PILE_ID);
        imageView.setId(DISCARD_PILE_ID);
        imageView.setImageDrawable(getImageDps(activity, "back", (cardWidth/2)));
        imageView.setTag(discardPile.getCardMultiTag());
        int rightMargin = buffer;
        int bottomMargin = 3*buffer;
        discardPile.setImageViewRightMargin(rightMargin);
        discardPile.setImageViewBottomMargin(bottomMargin);
        ConstraintLayout.LayoutParams params = new ConstraintLayout
                .LayoutParams(cardWidth, cardHeight);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.rightToRight= ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(0, 0, rightMargin, bottomMargin);
        imageView.setLayoutParams(params);
        layout.addView(imageView);
//deck label
        TextView discardLabel = new TextView(context);
        discardLabel.setId(DISCARD_PILE_ID+2);
        discardLabel.setTextSize(textSize);
        discardLabel.setTextColor(ACCENT_COLOR);
        discardLabel.setIncludeFontPadding(false);
        discardLabel.setText("Discard");
        discardLabel.setTag("discard");
        discardLabel.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
        params = new ConstraintLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = DISCARD_PILE_ID;
        params.rightToRight = DISCARD_PILE_ID;
        params.setMargins(0, 0, 0,0);
        discardLabel.setLayoutParams(params);
        layout.addView(discardLabel);
    }


    public void addCardToHand(String cardName, ConstraintLayout layout, Context context,
    Activity activity, View.OnTouchListener handListener) {
        int numberOfCards = hand.size()+1;
        int handWidth = ((cardWidth - minOverlap) * (numberOfCards - 1) + cardWidth);
        int overlap;
        int shift;
        if (handZoneWidth < handWidth) {
            overlap = minOverlap + (handWidth - handZoneWidth) / (numberOfCards - 1);
            shift = 0;
        } else {
            overlap = minOverlap;
            shift = (handZoneWidth - handWidth) / 2;
        }
        //shift existing hand cards
        for (int i = 0; i < hand.size(); i++) {
            int leftMargin = handMargin + shift + (cardWidth - overlap) * i;
            int bottomMargin = buffer * 3;
            hand.get(i).setImageViewLeftMargin(leftMargin);
            hand.get(i).setImageViewBottomMargin(bottomMargin);
            ConstraintLayout.LayoutParams params = new ConstraintLayout
                    .LayoutParams(cardWidth, cardHeight);
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMargins(leftMargin, 0, 0, bottomMargin);
            ImageView imageView = ((Activity) activity).findViewById(hand.get(i).getImageViewId());
            imageView.setLayoutParams(params);
        }
        //add new card to hand
        int i = hand.size();
        CardData cardData = new CardData(cardName, "hand", i, handTally, basicCardSet);
        ImageView imageView = new ImageView(context);
        final int FINAL_ID = handTally;
        handTally += 1;
        cardData.setImageViewId(PLAYER_HAND_VIEW_ID + FINAL_ID);
        imageView.setId(PLAYER_HAND_VIEW_ID + FINAL_ID);
        imageView.setImageDrawable(getImageDps(activity, cardName, (cardWidth/2)));
        imageView.setTag(cardData.getCardMultiTag());
        int leftMargin = handMargin + shift + (cardWidth - overlap) * i;
        cardData.setImageViewBottomMargin(handBottomMargin);
        cardData.setImageViewLeftMargin(leftMargin);
        ConstraintLayout.LayoutParams params = new ConstraintLayout
                .LayoutParams(cardWidth, cardHeight);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(leftMargin, 0, 0, handBottomMargin);
        imageView.setLayoutParams(params);
        imageView.bringToFront();
        layout.addView(imageView);
        hand.add(cardData);
        imageView.setOnTouchListener(handListener);
        imageView.setOnDragListener(hand.get(i).getDragListener());
    }//add card to hand


    public void removeCardFromHand(int viewId, Activity activity, ConstraintLayout layout) {
        int index = this.findHandImageView(viewId);
        ImageView imageView = ((Activity) activity).findViewById(hand.get(index).getImageViewId());
        layout.removeView(imageView);
        hand.remove(index);
        for (int i = index; i < hand.size(); i++) {
            hand.get(i).decreasePosition(1);
            ImageView imageView1 = ((Activity) activity).findViewById(hand.get(i).getImageViewId());
            imageView1.setTag(hand.get(i).getCardMultiTag());
        }
        if (hand.size() == 0) handTally = 0;
        int numberOfCards = hand.size();
        int handWidth = ((cardWidth - minOverlap) * (numberOfCards - 1) + cardWidth);
        int overlap;
        int shift;
        if (handZoneWidth < handWidth) {
            overlap = minOverlap + (handWidth - handZoneWidth) / (numberOfCards - 1);
            shift = 0;
        } else {
            overlap = minOverlap;
            shift = (handZoneWidth - handWidth) / 2;
        }
        //shift remaining hand cards
        for (int i = 0; i < hand.size(); i++) {
            int leftMargin = handMargin + shift + (cardWidth - overlap) * i;
            hand.get(i).setImageViewLeftMargin(leftMargin);
            hand.get(i).setImageViewBottomMargin(handBottomMargin);
            ConstraintLayout.LayoutParams params = new ConstraintLayout
                    .LayoutParams(cardWidth, cardHeight);
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMargins(leftMargin, 0, 0, handBottomMargin);
            imageView = ((Activity) activity).findViewById(hand.get(i).getImageViewId());
            imageView.setLayoutParams(params);
        }
    }// RemoveCardFromHand


    public void addCardToPlayArea(String cardName, ConstraintLayout layout, Context context,
                                  Activity activity, View.OnTouchListener inPlayListener) {
        int numberOfCards = inPlay.size() + 1;
        int pileWidth = ((cardWidth - minOverlap) * (numberOfCards - 1) + cardWidth);
        int overlap;
        int shift;
        if (inPlayZoneWidth < pileWidth) {
            overlap = minOverlap + (pileWidth - inPlayZoneWidth) / (numberOfCards - 1);
            shift = 0;
        } else {
            overlap = minOverlap;
            shift = (inPlayZoneWidth - pileWidth) / 2;
        }
        //shift existing area cards
        for (int i = 0; i < inPlay.size(); i++) {
            int leftMargin = inPlayMargin + shift + (cardWidth - overlap) * i;
            inPlay.get(i).setImageViewLeftMargin(leftMargin);
            inPlay.get(i).setImageViewBottomMargin(inPlayBottomMargin);
            ConstraintLayout.LayoutParams params = new ConstraintLayout
                    .LayoutParams(cardWidth, cardHeight);
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMargins(leftMargin, 0, 0, inPlayBottomMargin);
            ImageView imageView = ((Activity) activity).findViewById(inPlay.get(i).getImageViewId());
            imageView.setLayoutParams(params);
        }
        //add new card to area
        int i = inPlay.size();
        final int FINAL_ID = inPlayTally;
        CardData cardData = new CardData(cardName, "inPlay", i, inPlayTally, basicCardSet);
        ImageView imageView = new ImageView(context);
        inPlayTally += 1;
        cardData.setImageViewId(PLAY_AREA_VIEWS_ID + FINAL_ID);
        imageView.setId(PLAY_AREA_VIEWS_ID + FINAL_ID);
        imageView.setImageDrawable(getImageDps(activity, cardName, (cardWidth/2)));
        imageView.setTag(cardData.getCardMultiTag());
        int leftMargin = inPlayMargin + shift + (cardWidth - overlap) * i;
        cardData.setImageViewLeftMargin(leftMargin);
        cardData.setImageViewBottomMargin(inPlayBottomMargin);
        ConstraintLayout.LayoutParams params = new ConstraintLayout
                .LayoutParams(cardWidth, cardHeight);
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(leftMargin, 0, 0, inPlayBottomMargin);
        imageView.setLayoutParams(params);
        imageView.bringToFront();
        layout.addView(imageView);
        inPlay.add(cardData);
        imageView.setOnTouchListener(inPlayListener);
        imageView.setOnDragListener(inPlay.get(i).getDragListener());
    }


    public void removeCardFromInPlay(int viewId, Activity activity, ConstraintLayout layout) {
        int index = this.findInPlayImageView(viewId);
        ImageView imageView = ((Activity) activity).findViewById(inPlay.get(index).getImageViewId());
        layout.removeView(imageView);
        inPlay.remove(index);
        for (int i = index; i < inPlay.size(); i++) {
            inPlay.get(i).decreasePosition(1);
            ImageView imageView1 = ((Activity) activity).findViewById(inPlay.get(i).getImageViewId());
            imageView1.setTag(inPlay.get(i).getCardMultiTag());
        }
        if (inPlay.size() == 0) inPlayTally = 0;
        int numberOfCards = inPlay.size();
        int inPlayWidth = ((cardWidth - minOverlap) * (numberOfCards - 1) + cardWidth);
        int overlap;
        int shift;
        if (inPlayZoneWidth < inPlayWidth) {
            overlap = minOverlap + (inPlayWidth - inPlayZoneWidth) / (numberOfCards - 1);
            shift = 0;
        } else {
            overlap = minOverlap;
            shift = (inPlayZoneWidth - inPlayWidth) / 2;
        }
        //shift remaining inPlay cards
        for (int i = 0; i < inPlay.size(); i++) {
            int leftMargin = inPlayMargin + shift + (cardWidth - overlap) * i;
            inPlay.get(i).setImageViewLeftMargin(leftMargin);
            inPlay.get(i).setImageViewBottomMargin(inPlayBottomMargin);
            ConstraintLayout.LayoutParams params = new ConstraintLayout
                    .LayoutParams(cardWidth, cardHeight);
            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            params.setMargins(leftMargin, 0, 0, inPlayBottomMargin);
            imageView = ((Activity) activity).findViewById(inPlay.get(i).getImageViewId());
            imageView.setLayoutParams(params);
        }
    }// RemoveCardFromInPlay


    public void addCardToDeck(String cardName, Activity activity, Context context) {
        deck.add(new CardData(cardName, "deck", this.deck.size(), this.deckTally, basicCardSet));
        ImageView imageView = new ImageView(context);
        deckTally +=1;
        final int NUMBER = deck.get(deck.size()-1).getNumber();
        deck.get(deck.size()-1).setImageViewId(DECK_CARD_ID+NUMBER);
        imageView.setId(DECK_CARD_ID+NUMBER);
        imageView.setImageDrawable(getImageDps(activity, cardName, (cardWidth / 2)));
        this.deckPile.getTextView().setText(String.valueOf(this.deck.size()));
    }

    public void addCardToOffHandDeck(String cardName){
        deck.add(new CardData(cardName, "deck", this.deck.size(), this.deckTally, basicCardSet));
        deckTally += 1;
    }

    public void removeCardFromDeck(int index, Activity activity) {
        this.deck.remove(index);
        if (this.deckPile != null)
            this.deckPile.getTextView().setText(String.valueOf(this.deck.size()));
        for (int i = index; i < this.deck.size(); i++) {
            this.deck.get(i).decreasePosition(1);
            //ImageView imageView1 = ((Activity) activity).findViewById(deck.get(i).getImageViewId());
            //imageView1.setTag(deck.get(i).getCardMultiTag());
        }
        if (deck.size() <= 0) deckTally = 0;
    }


    public void addCardToDiscard(String cardName, Activity  activity, Context context) {
        discard.add(new CardData(cardName, "discard", this.discard.size(), this.discardTally,
                basicCardSet));
        ImageView imageView = new ImageView(context);
        discardTally +=1;
        final int NUMBER = discard.get(discard.size()-1).getNumber();
        discard.get(discard.size()-1).setImageViewId(DISCARD_CARD_ID + NUMBER);
        imageView.setId(DISCARD_CARD_ID + NUMBER);
        Drawable drawable = getImageDps(activity, cardName, (cardWidth / 2));
        ImageView imageView1 = ((Activity) activity).findViewById(discardPile.getImageViewId());
        imageView1.setImageDrawable(drawable);
    }

    public void removeCardFromDiscard(int index, Context context, Activity activity) {
        discard.remove(index);
        Drawable drawable;
        for (int i = index; i < this.discard.size(); i++) {
            this.discard.get(i).decreasePosition(1);
        }
        if (discard.size() <= 0) {
            drawable = getImageDps(activity, "back", cardWidth / 2);
            discardTally = 0;
        } else {
            String topCardName = discard.get(discard.size() - 1).getCardName();
            drawable = getImageDps(activity, topCardName, cardWidth / 2);
        }
        ImageView imageView = ((Activity) activity).findViewById(discardPile.getImageViewId());
        imageView.setImageDrawable(drawable);
    }

    public Drawable getImageDps(Activity activity, String imageName, int size){
        Drawable drawable;
        String drawableString = "";
        String[] parsedName = imageName.split("(?=\\p{Upper})");
        if (parsedName.length == 2){
            parsedName[1] = Character.toLowerCase(parsedName[1].charAt(0)) + parsedName[1].substring(1);
            drawableString = parsedName[0] + "_" + parsedName[1] + Integer.toString(size);
        } else if (parsedName.length == 1) {
            drawableString = parsedName[0] + Integer.toString(size);
        }
        int drawableResourceId = activity.getResources().getIdentifier(drawableString,
                "drawable", activity.getPackageName() );
        drawable = ContextCompat.getDrawable(activity, drawableResourceId);
        return drawable;
    }


    public void shufflePile(String pile) {
        CardData temp;
        switch (pile) {
            case "deck":
                int length = deck.size(); //number of cards in the deck
                if (length > 1) {
                    Random rand = new Random();
                    if (length > 2) {
                        for (int j = length-1; j > 0; j--) {
                            int n = rand.nextInt(j); //a random index of the deck
                            temp = deck.get(n);
                            deck.remove(n);
                            for (int i = n; i < deck.size(); i++) deck.get(i).decreasePosition(1);
                            deck.add(temp);
                            deck.get(deck.size()-1).setPosition(deck.size()-1);
                        }
                    } else {
                        int n = rand.nextInt(length);
                        if (n == 1) {
                            temp = deck.get(1);
                            deck.remove(1);
                            deck.add(0, temp);
                        }
                    }
                }
                break;
            case "discard":
                length = discard.size(); //number of cards in the discard
                if (length > 1) {
                    Random rand = new Random();
                    if (length > 2) {
                        for (int j = length - 1; j > 0; j--) {
                            int n = rand.nextInt(j); //a random index of the discard
                            temp = discard.get(n);
                            discard.remove(n);
                            for (int i = n; i < discard.size(); i++)
                                discard.get(i).decreasePosition(1);
                            discard.add(temp);
                            discard.get(discard.size() - 1).setPosition(discard.size() - 1);
                        }
                    } else {
                        int n = rand.nextInt(length);
                        if (n == 1) {
                            temp = discard.get(1);
                            discard.remove(1);
                            discard.add(0, temp);
                        }
                    }
                }
                break;
        }
    }

    public void drawHand(ConstraintLayout layout, Context context, Activity activity,
                         View.OnTouchListener handListener) {
        int deckSize = deck.size();
        int discardSize = discard.size();
        int cardsStillNeeded;
        int newDeckSize;
        int index = hand.size();
        if (deckSize >= 5) {  //5 or more cards in deck
            for (int i = deckSize - 1; i > deckSize - 6; i--) { // add last 5 cards in deck to hand
                String cardName = deck.get(i).getCardName();
                removeCardFromDeck(i, activity);
                addCardToHand(cardName, layout, context, activity, handListener);
            }
        } else if (deckSize > 0) { //less than 5 cards in deck, some in discard
            cardsStillNeeded = 5-deckSize;
            for (int i = deckSize - 1; i >= 0; i--) {
                String cardName = deck.get(i).getCardName();
                removeCardFromDeck(i, activity);
                addCardToHand(cardName, layout, context, activity, handListener);
            }
            putDiscardInDeck(activity);
            if (discardSize > cardsStillNeeded){
                for (int i = discardSize - 1; i > discardSize - cardsStillNeeded-1; i--) { // add last needed cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeCardFromDeck(i, activity);
                    addCardToHand(cardName, layout, context, activity, handListener);
                }
            } else{
                for (int i = discardSize - 1; i >= 0; i--) { // add remaining cards in shuffled deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeCardFromDeck(i, activity);
                    addCardToHand(cardName, layout, context, activity, handListener);
                }
            }
        } else{                 //no cards in deck, some in discard
            putDiscardInDeck(activity);
            newDeckSize = deck.size();
            if (newDeckSize >= 5){ // 5  or more cards in shuffled deck
                for (int i = newDeckSize - 1; i > newDeckSize - 6; i--) { // add last 5 cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeCardFromDeck(i, activity);
                    addCardToHand(cardName, layout, context, activity, handListener);
                }
            } else { // less than 5 cards in shuffled deck
                for (int i = newDeckSize - 1; i >=0; i--) { // add remaining cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeCardFromDeck(i, activity);
                    addCardToHand(cardName, layout, context, activity, handListener);
                }
            }
        }
        //return this.deck.size();
    }

    public void drawOffTurnHand() {
        int deckSize = deck.size();
        int discardSize = discard.size();
        int cardsStillNeeded;
        int newDeckSize;
        int index = hand.size();
        if (deckSize >= 5) {  //5 or more cards in deck
            for (int i = deckSize - 1; i > deckSize - 6; i--) { // add last 5 cards in deck to hand
                String cardName = deck.get(i).getCardName();
                removeOffTurnCard(i,"deck");
                addOffTurnCard(cardName, "hand");
            }
        } else if (deckSize > 0) { //less than 5 cards in deck, some in discard
            cardsStillNeeded = 5-deckSize;
            for (int i = deckSize - 1; i >= 0; i--) {
                String cardName = deck.get(i).getCardName();
                removeOffTurnCard(i,"deck");
                addOffTurnCard(cardName, "hand");
            }
            putOffTurnDiscardInDeck();
            if (discardSize > cardsStillNeeded){
                for (int i = discardSize - 1; i > discardSize - cardsStillNeeded-1; i--) { // add last needed cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeOffTurnCard(i,"deck");
                    addOffTurnCard(cardName, "hand");
                }
            } else{
                for (int i = discardSize - 1; i >= 0; i--) { // add remaining cards in shuffled deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeOffTurnCard(i,"deck");
                    addOffTurnCard(cardName, "hand");
                }
            }
        } else {                 //no cards in deck, some in discard
            putOffTurnDiscardInDeck();
            newDeckSize = deck.size();
            if (newDeckSize >= 5){ // 5  or more cards in shuffled deck
                for (int i = newDeckSize - 1; i > newDeckSize - 6; i--) { // add last 5 cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeOffTurnCard(i,"deck");
                    addOffTurnCard(cardName, "hand");
                }
            } else { // less than 5 cards in shuffled deck
                for (int i = newDeckSize - 1; i >=0; i--) { // add remaining cards in deck to hand
                    String cardName = deck.get(i).getCardName();
                    removeOffTurnCard(i,"deck");
                    addOffTurnCard(cardName, "hand");
                }
            }
        }
        //return this.deck.size();
    }

    public void putDiscardInDeck(Activity activity) {
        if (deck.size() == 0) deckTally = 0;
        for (int i = 0; i < discard.size(); i++) {
            deck.add(discard.get(i));
            deck.get(deck.size() - 1).setCardMultiTagOnMoveToNewPile(deck.get(deck.size() - 1),
                    (deck.size() - 1), deckTally, "deck");
            deckTally += 1;
        }
        discard.clear();
        discardTally = 0;
        ImageView imageView = ((Activity) activity).findViewById(discardPile.getImageViewId());
        Drawable drawable = getImageDps(activity, "back", cardWidth / 2);
        imageView.setImageDrawable(drawable);
        deckPile.getTextView().setText(String.valueOf(deck.size()));
        shufflePile("deck");
    }

    public void putDeckInDiscard(Activity activity) {
        if (discard.size() == 0) discardTally = 0;
        for (int i = 0; i < deck.size(); i++) {
            discard.add(deck.get(i));
            discard.get(discard.size() - 1).setCardMultiTagOnMoveToNewPile(discard.get(discard.size() - 1),
                    (discard.size() - 1), discardTally, "discard");
            discardTally += 1;
        }
        deck.clear();
        deckTally = 0;
        String topCard = discard.get(discard.size()-1).getCardName();
        ImageView imageView = ((Activity) activity).findViewById(discardPile.getImageViewId());
        Drawable drawable = getImageDps(activity, topCard, cardWidth / 2);
        imageView.setImageDrawable(drawable);
        deckPile.getTextView().setText(String.valueOf(0));
    }

    public void putOffTurnDiscardInDeck() {
        if (deck.size() == 0) deckTally = 0;
        for (int i = 0; i < discard.size(); i++) {
            deck.add(discard.get(i));
            deck.get(deck.size() - 1).setCardMultiTagOnMoveToNewPile(deck.get(deck.size() - 1),
                    (deck.size() - 1), deckTally, "deck");
            deckTally += 1;
        }
        discard.clear();
        discardTally = 0;
        shufflePile("deck");
    }

    public void setDiscardToDeckView(Activity activity, Context context){
        ImageView imageView = ((Activity) activity).findViewById(discardPile.getImageViewId());
        imageView.setImageDrawable(getImageDps(activity, "back", cardWidth/2));
        deckPile.getTextView().setText(String.valueOf(this.deck.size()));
    }


    public int findHandImageView(int viewId){
        int index = -1;
        for(int i = 0; i < this.hand.size(); i++){
            if (this.hand.get(i).getImageViewId() == viewId){
                index = i;
            }
        }
        return index;
    }


    public int findInPlayImageView(int viewId){
        int index = -1;
        for(int i = 0; i < this.inPlay.size(); i++){
            if (this.inPlay.get(i).getImageViewId() == viewId){
                index = i;
            }
        }
        return index;
    }

    public String checkForReaction(String attackName){
        String reactionName = "null";
        for (int i = 0; i < hand.size(); i++){
            if (hand.get(i).getCard().getType().equals("action - reaction")){
               /* switch (attackName){
                    case "bandit":
                        break;
                    case "bureaucrat":
                        break;
                    case "militia":
                        break;
                    case "spy":
                        break;
                    case "theif":
                        break;
                    case "witch":
                        break;
                }*/
               reactionName = hand.get(i).getCardName();
            }
        }
        return reactionName;
    }

    public void addOffTurnCard(String cardName, String pileName){
        int position;
        int itemNumber;
        switch (pileName){
            case "hand":
                itemNumber = handTally;
                handTally +=1;
                position = hand.size();
                CardData cardData = new CardData(cardName, "hand", position, itemNumber, basicCardSet);
                hand.add(cardData);
                break;
            case "deck":
                itemNumber = deckTally;
                deckTally += 1;
                position = deck.size();
                cardData = new CardData(cardName, "hand", position, itemNumber, basicCardSet);
                deck.add(cardData);
                break;
            case "discard":
                itemNumber = discardTally;
                discardTally += 1;
                position = discard.size();
                cardData = new CardData(cardName, "hand", position, itemNumber, basicCardSet);
                discard.add(cardData);
                break;
        }
    }

    public void removeOffTurnCard(int index, String pileName) {
        switch (pileName){
            case "hand":
                hand.remove(index);
                for (int i = index; i < hand.size(); i++) {
                    hand.get(i).decreasePosition(1);
                }
                if (hand.size() <= 0) handTally = 0;
                break;
            case "deck":
                deck.remove(index);
                for (int i = index; i < deck.size(); i++) {
                    deck.get(i).decreasePosition(1);
                }
                if (deck.size() <= 0) deckTally = 0;
                break;
            case "discard":
                discard.remove(index);
                for (int i = index; i < discard.size(); i++) {
                    discard.get(i).decreasePosition(1);
                }
                if (discard.size() <= 0) discardTally = 0;
                break;
        }
    }

    public String getName() {
        return Name;
    }

    public boolean isHuman() {
        return human;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
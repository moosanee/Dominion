package com.example.dominion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;


public class RevealDialogActivity extends AppCompatActivity {

    ArrayList<String> cardList = new ArrayList<>();
    ArrayList<String> discardList = new ArrayList<>();
    ArrayList<String> drawnCards = new ArrayList<>();
    ArrayList<String> discardedCards = new ArrayList<>();
    ConstraintLayout cLayout;
    LinearLayout lLayout;
    LinearLayout mLayout;
    ConstraintSet constraintSet;
    Activity activity;
    Context context;
    BasicCards basicCardSet;
    Button drawButton;
    Button exitButton;
    Button discardButton;
    int numberOfCards;
    int treasures = 0;
    int phase;
    int cardsToDraw;
    int cardsDrawn = 0;
    ImageView firstView;
    TextView instructionView;
    int drawTally = 0;
    boolean rejected = false;
    boolean outOfCards = false;
    Card card;

    private View.OnClickListener exitListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            exitButtonClicked();
        }
    };

    private View.OnClickListener discardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            discardButtonClicked();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_dialog);

        cardList = getIntent().getStringArrayListExtra("revealedCardsKey");
        discardList = getIntent().getStringArrayListExtra("discardCardsKey");
        cardsToDraw = getIntent().getIntExtra("cardsToDrawKey", 0);
        phase = getIntent().getIntExtra("phaseKey", 0);
        numberOfCards = cardList.size() + discardList.size();

        mLayout = findViewById(R.id.activity_reveal_dialog);
        cLayout = findViewById(R.id.card_list);
        cLayout.setId(REVEAL_LAYOUT_ID);
        lLayout = findViewById(R.id.button_layout);
        lLayout.setId(BUTTON_LAYOUT_ID);
        activity = this;
        context = getApplicationContext();
        constraintSet = new ConstraintSet();
        constraintSet.clone(cLayout);
        basicCardSet = new BasicCards();

        instructionView = findViewById(R.id.instructions);

        firstView = findViewById(R.id.revealed_card);
        firstView.setId(REVEALED_CARD_ID);

        drawButton = findViewById(R.id.draw_card);
        drawButton.setId(DRAW_BUTTON_ID);

        // deck and discard are empty
        if (numberOfCards == 0) abortDraw(drawButton);


        switch (phase) {
            case ADVENTURER:
                card = basicCardSet.getCard("adventurer");
                instructionView.setText(card.getInstructions());
                operateAdventurerButton(drawButton);
                break;
            case LIBRARY:
                card = basicCardSet.getCard("library");
                instructionView.setText(card.getInstructions());
                operateLibraryButton(drawButton);
                break;
        }
    }

    private void abortDraw(Button drawButton){
        mLayout.removeView(instructionView);
        cLayout.removeView(firstView);
        TextView textView = new TextView(context);
        textView.setText("there are no cards to draw");
        textView.setTextAppearance(getApplicationContext(), R.style.appText);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.alegreya_sc);
        textView.setTypeface(typeface);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(buffer, buffer, buffer, buffer);
        textView.setLayoutParams(params);
        cLayout.addView(textView);
        drawButton.setText("exit");
    }

    private void operateAdventurerButton(Button button) {
        final Button drawButton = button;
        drawButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if (drawButton.getText().equals("draw card")) {
                    int cardsDisplayed = numberOfCards - cardList.size();
                    int availableWidth = screenWidth - 4 * buffer;
                    int neededWidth = cardWidth * (cardsDisplayed + 1) + 2 * buffer;
                    int overlap;
                    int shift;
                    int windowWidth;
                    int leftSet;
                    if (availableWidth < neededWidth) {
                        overlap = (neededWidth - availableWidth) / cardsDisplayed;
                        shift = (cardWidth - overlap) * cardsDisplayed;
                        windowWidth = availableWidth;
                    } else {
                        shift = cardWidth * cardsDisplayed;
                        windowWidth = cardWidth * (cardsDisplayed + 1) + 2 * buffer;
                    }
                    String imageName = cardList.get(0);
                    Card card = basicCardSet.getCard(imageName);
                    if (card.getType().equals("treasure")) treasures += 1;
                    Drawable drawable = getDrawable(imageName);
                    if (cardsDisplayed == 0) {
                        firstView.setImageDrawable(drawable);
                        cardList.remove(0);
                    } else if (cardsDisplayed < numberOfCards) {
                        //expand dialog
                        Window window = getWindow();
                        window.setLayout(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                        window.setGravity(Gravity.CENTER);
                        //move existing views
                        for (int i = 0; i < cardsDisplayed; i++) {
                            if (availableWidth < neededWidth) {
                                overlap = (neededWidth - availableWidth) / cardsDisplayed;
                                leftSet = (cardWidth - overlap) * i;
                            } else {
                                overlap = 0;
                                leftSet = (cardWidth - overlap) * i;
                            }
                            ImageView imageView = findViewById(REVEALED_CARD_ID + i);
                            ConstraintLayout.LayoutParams params =
                                    new ConstraintLayout.LayoutParams(cardWidth, cardHeight);
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.setMargins(leftSet, 0, 0, 0);
                            imageView.setLayoutParams(params);
                        }
                        //add new view
                        final int FINALID = REVEALED_CARD_ID + numberOfCards - cardList.size();
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setId(FINALID);
                        imageView.setImageDrawable(drawable);
                        ConstraintLayout.LayoutParams params =
                                new ConstraintLayout.LayoutParams(cardWidth, cardHeight);
                        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.setMargins(shift, 0, 0, 0);
                        imageView.setLayoutParams(params);
                        cLayout.addView(imageView);
                        if (numberOfCards < 3) {
                            cLayout.setPadding(0, 0, 0, 0);
                        }
                        cardList.remove(0);
                    }
                    if (cardList.size() == 0) {
                        if (treasures < 2) {
                            Window window = getWindow();
                            window.setLayout(windowWidth, cardHeight + 350);
                            window.setGravity(Gravity.CENTER);
                            TextView textView = new TextView(context);
                            textView.setText("there are no more cards to draw");
                            textView.setTextAppearance(getApplicationContext(), R.style.appText);
                            Typeface typeface = ResourcesCompat.getFont(context, R.font.alegreya_sc);
                            textView.setTypeface(typeface);
                            textView.setGravity(Gravity.CENTER);
                            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.setMargins(0, 0, 0, cardHeight + buffer);
                            textView.setLayoutParams(params);
                            cLayout.addView(textView);
                            cLayout.setPadding(buffer, 0, 0, 0);
                        }
                        drawButton.setText("exit");
                    }
                } else if (drawButton.getText().equals("exit")) finish();
            }
        });
    }

    private void operateLibraryButton(Button button) {
        final Button drawButton = button;
        drawButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if (drawButton.getText().equals("draw card")) {
                    //expand dialog
                    Window window = getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                    int deckSize = cardList.size();
                    int discardSize = discardList.size();
                    if (drawTally < (deckSize + discardSize)) rejected = false;
                    //first view
                    if (drawTally == 0){
                        // add exit button beside draw button
                        exitButton = new Button(context);
                        exitButton.setText("exit");
                        exitButton.setTextSize(textSize);
                        exitButton.setTextColor(BACKGROUND_COLOR_DARK);
                        exitButton.setBackgroundResource(R.drawable.text80);
                        exitButton.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = buffer;
                        params.rightMargin = buffer;
                        params.gravity = Gravity.CENTER;
                        exitButton.setLayoutParams(params);
                        lLayout.addView(exitButton);
                        //set exit listener
                        exitButton.setOnClickListener(exitListener);
                        //disable exit button until required cards are chosen
                        exitButton.setClickable(false);
                        exitButton.setAlpha(0.5f);
                        //get first card
                        if (deckSize > 0){
                            card = basicCardSet.getCard(cardList.get(cardList.size()-1));
                        } else {
                            card = basicCardSet.getCard(discardList.get(discardList.size()-1));
                        }
                        //add discard button to first view (greyed out if not action)
                        discardButton = new Button(context);
                        discardButton.setText("discard");
                        discardButton.setId(DISCARD_BUTTON);
                        discardButton.setTextSize(textSize);
                        discardButton.setTextColor(BACKGROUND_COLOR_DARK);
                        discardButton.setBackgroundResource(R.drawable.text80);
                        discardButton.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
                        cLayout.addView(discardButton);
                        cLayout.setMinHeight(450);
                        ConstraintLayout.LayoutParams cParams = new ConstraintLayout.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.bottomMargin = buffer;
                        discardButton.setLayoutParams(cParams);
                        //adjust placement of first view and set image to first card
                        cParams = new ConstraintLayout.LayoutParams(cardWidth, cardHeight);
                        cParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.topMargin = buffer;
                        firstView.setLayoutParams(cParams);
                        Drawable drawable = getDrawable(card.getName());
                        firstView.setImageDrawable(drawable);
                        //grey out discard button if not an action card
                        if (card.getType().equals("action") || card.getType().equals("action - reaction")
                                || card.getType().equals("action - attack")){
                            discardButton.setClickable(true);
                            discardButton.setAlpha(1);
                        } else {
                            discardButton.setClickable(false);
                            discardButton.setAlpha(0.5f);
                        }
                        //set discard listener
                        discardButton.setOnClickListener(discardListener);
                        //add card to list
                        drawnCards.add(card.getName());
                        drawTally += 1;
                    //for remaining views
                    } else if ((drawnCards.size() < cardsToDraw) && (drawTally < deckSize + discardSize)){
                        //grey out previous discard button
                        Button oldDiscardButton = discardButton;
                        oldDiscardButton.setVisibility(View.INVISIBLE);
                        //get new draw
                        if (drawTally < deckSize){
                            int index = deckSize - drawTally - 1;
                            card = basicCardSet.getCard(cardList.get(index));
                        } else {
                            int index = deckSize + discardSize - drawTally - 1;
                            card = basicCardSet.getCard(discardList.get(index));
                        }
                        Drawable drawable = getDrawable(card.getName());
                        //calculate layout parameters
                        int cardsDisplayed = drawTally;
                        int availableWidth = screenWidth - 4 * buffer;
                        int neededWidth = cardWidth * (cardsDisplayed + 1) + 2 * buffer;
                        int overlap;
                        int shift;
                        int windowWidth;
                        int leftSet;
                        if (availableWidth < neededWidth) {
                            overlap = (neededWidth - availableWidth) / cardsDisplayed;
                            shift = (cardWidth - overlap) * cardsDisplayed;
                            windowWidth = availableWidth;
                        } else {
                            shift = cardWidth * cardsDisplayed;
                            windowWidth = cardWidth * (cardsDisplayed + 1) + 2 * buffer;
                        }

                        //move existing views
                        for (int i = 0; i < cardsDisplayed; i++) {
                            if (availableWidth < neededWidth) {
                                overlap = (neededWidth - availableWidth) / cardsDisplayed;
                                leftSet = (cardWidth - overlap) * i;
                            } else {
                                overlap = 0;
                                leftSet = (cardWidth - overlap) * i;
                            }
                            ImageView imageView = findViewById(REVEALED_CARD_ID + i);
                            ConstraintLayout.LayoutParams params =
                                    new ConstraintLayout.LayoutParams(cardWidth, cardHeight);
                            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.setMargins(leftSet, buffer, 0, 0);
                            imageView.setLayoutParams(params);
                        }
                        //create Imageview and Button for new draw
                        final int FINALID = REVEALED_CARD_ID + drawTally;
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setId(FINALID);
                        imageView.setImageDrawable(drawable);
                        ConstraintLayout.LayoutParams params =
                                new ConstraintLayout.LayoutParams(cardWidth, cardHeight);
                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.setMargins(shift, buffer, 0, 0);
                        imageView.setLayoutParams(params);
                        cLayout.addView(imageView);
                        discardButton = new Button(context);
                        discardButton.setText("discard");
                        discardButton.setId(DISCARD_BUTTON);
                        discardButton.setTextSize(textSize);
                        discardButton.setTextColor(BACKGROUND_COLOR_DARK);
                        discardButton.setBackgroundResource(R.drawable.text80);
                        discardButton.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
                        cLayout.addView(discardButton);
                        ConstraintLayout.LayoutParams cParams = new ConstraintLayout.LayoutParams
                                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        cParams.setMargins(shift+10, 0, 0, buffer);
                        discardButton.setLayoutParams(cParams);
                        discardButton.setOnClickListener(discardListener);
                        if (card.getType().equals("action") || card.getType().equals("action - reaction")
                                || card.getType().equals("action - attack")){
                            discardButton.setClickable(true);
                            discardButton.setAlpha(1);
                        } else {
                            discardButton.setClickable(false);
                            discardButton.setAlpha(0.5f);
                        }
                        drawnCards.add(card.getName());
                        drawTally += 1;
                        if (drawnCards.size() == cardsToDraw){
                            instructionView.setText("you have 7 cards in your hand");
                            drawButton.setClickable(false);
                            drawButton.setAlpha(0.5f);
                            exitButton.setClickable(true);
                            exitButton.setAlpha(1f);
                        }
                    } else if (drawTally >= (deckSize + discardSize)){ //run out of cards to draw
                        instructionView.setText("there are no more cards to draw");
                        drawButton.setClickable(false);
                        drawButton.setAlpha(0.5f);
                        outOfCards = true;
                        exitButton.setClickable(true);
                        exitButton.setAlpha(1f);
                    } else { //drawn enough cards
                        instructionView.setText("you have 7 cards in your hand");
                        drawButton.setClickable(false);
                        drawButton.setAlpha(0.5f);
                        exitButton.setClickable(true);
                        exitButton.setAlpha(1f);
                    }
                } else if (drawButton.getText().equals("exit")) finish();
            }
        });
    }

    public void exitButtonClicked(){
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("drawnCardsKey", drawnCards);
        returnIntent.putStringArrayListExtra("discardedCardsKey", discardedCards);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void discardButtonClicked(){
        ImageView imageView = findViewById(REVEALED_CARD_ID + drawTally-1);
        if (rejected) {
            drawnCards.add(card.getName());
            discardedCards.remove(discardedCards.size()-1);
            rejected = false;
            discardButton.setText("discard");
            imageView.setAlpha(1f);
            if ((drawTally < cardsToDraw) && !outOfCards){
                instructionView.setText(basicCardSet.getCard("library").getInstructions());
                exitButton.setClickable(false);
                exitButton.setAlpha(0.5f);
                drawButton.setClickable(true);
                drawButton.setAlpha(1f);
            } else {
                instructionView.setText("you have 7 cards in your hand");
                exitButton.setClickable(true);
                exitButton.setAlpha(1f);
                drawButton.setClickable(false);
                drawButton.setAlpha(0.5f);
            }
        } else {
            drawnCards.remove(drawnCards.size()-1);
            discardedCards.add(card.getName());
            rejected = true;
            discardButton.setText("un-discard");
            imageView.setAlpha(0.5f);
            if(!outOfCards) {
                instructionView.setText(basicCardSet.getCard("library").getInstructions());
                exitButton.setClickable(false);
                exitButton.setAlpha(0.5f);
                drawButton.setClickable(true);
                drawButton.setAlpha(1f);
            } else instructionView.setText("there are no more cards to draw");
        }
    }

    private Drawable getDrawable(String imageName){
        Drawable drawable;
        String drawableString = "";
        String[] parsedName = imageName.split("(?=\\p{Upper})");
        if (parsedName.length == 2){
            parsedName[1] = Character.toLowerCase(parsedName[1].charAt(0)) + parsedName[1].substring(1);
            drawableString = parsedName[0] + "_" + parsedName[1] + Integer.toString(100);
        } else if (parsedName.length == 1) {
            drawableString = parsedName[0] + Integer.toString(100);
        }
        int drawableId = getResources().getIdentifier(drawableString,"drawable",
                    activity.getPackageName());
        drawable = ContextCompat.getDrawable(activity, drawableId);
        return drawable;
    }
}

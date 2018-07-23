package com.example.dominion;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;


public class RevealDialogActivity extends AppCompatActivity {

    ArrayList<String> cardList = new ArrayList<>();
    ConstraintLayout layout;
    ConstraintSet constraintSet;
    Activity activity;
    Context context;
    BasicCards basicCardSet;
    int numberOfCards;
    int treasures = 0;
    int phase;
    ImageView firstView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_dialog);

        cardList = getIntent().getStringArrayListExtra("revealedCardsKey");
        phase = getIntent().getIntExtra("phaseKey", 0);
        numberOfCards = cardList.size();

        layout = findViewById(R.id.card_list);
        layout.setId(REVEAL_LAYOUT_ID);
        activity = this;
        context = getApplicationContext();
        constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        basicCardSet = new BasicCards();

        firstView = findViewById(R.id.revealed_card);
        firstView.setId(REVEALED_CARD_ID);

        final Button button = findViewById(R.id.draw_card);
        button.setId(DRAW_BUTTON_ID);

        if (numberOfCards == 0){
            layout.removeView(firstView);
            TextView textView = new TextView(context);
            textView.setText("there are no cards to draw");
            textView.setTextAppearance(getApplicationContext(), R.style.appText);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.alegreya_sc);
            textView.setTypeface(typeface);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(buffer, buffer, buffer, buffer);
            textView.setLayoutParams(params);
            layout.addView(textView);
            button.setText("exit");
        }

        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                if (button.getText().equals("draw card")) {
                    int cardsDisplayed = numberOfCards - cardList.size();
                    int availableWidth = screenWidth - 4 * buffer;
                    int neededWidth = cardWidth * (cardsDisplayed+1) + 2*buffer;
                    int overlap;
                    int shift;
                    int windowWidth;
                    int leftSet;
                    if (availableWidth < neededWidth) {
                        overlap = (neededWidth - availableWidth) / cardsDisplayed;
                        shift = (cardWidth - overlap)*cardsDisplayed;
                        windowWidth = availableWidth;
                    } else {
                        shift = cardWidth * cardsDisplayed;
                        windowWidth = cardWidth*(cardsDisplayed+1) + 2*buffer;
                    }
                    String imageName = cardList.get(0);
                    Card card = basicCardSet.getCard(imageName);
                    if (card.getType().equals("treasure")) treasures +=1;
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
                        layout.addView(imageView);
                        if (numberOfCards < 3) {
                            layout.setPadding(0,0,0,0);
                        }
                        cardList.remove(0);
                    }
                    if (cardList.size() == 0){
                        if (phase == ADVENTURER && treasures < 2){
                            Window window = getWindow();
                            window.setLayout(windowWidth,cardHeight + 350);
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
                            params.setMargins(0, 0, 0, cardHeight+buffer);
                            textView.setLayoutParams(params);
                            layout.addView(textView);
                            layout.setPadding(buffer, 0,0,0);
                        }
                        button.setText("exit");
                    }
                } else if (button.getText().equals("exit")) finish();
            }
        });

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

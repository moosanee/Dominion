package com.example.dominion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class DiscardBrowseActivity extends AppCompatActivity {

    private ArrayList<CardData> pileCardData;
    ArrayList<Button> takeButtons = new ArrayList<>();
    boolean cardChosen = false;
    int chosenIndex = -1;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pileCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("discardPileListKey");
        setContentView(R.layout.activity_discard_browse);

        LinearLayout discardPileLayout = (LinearLayout) findViewById(R.id.discard_pile_layout);
        LinearLayout discardCardLayout = (LinearLayout) findViewById(R.id.discard_card_layout);

        if (pileCardData.size() == 0){
            TextView noCardsText = new TextView(this);
            noCardsText.setText("Empty Pile");
            noCardsText.setTextSize(emptyPileTextSize);
            noCardsText.setTextColor(ACCENT_COLOR);
            noCardsText.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
            noCardsText.bringToFront();
            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            noCardsText.setLayoutParams(params);
            discardCardLayout.addView(noCardsText);
            cardChosen = false;
        }
        else {
            String drawableString = "";
            for (int i = 0; i < pileCardData.size(); i++){
                String cardName = pileCardData.get(i).getCardName();
                //get drawable from card name string
                String[] parsedName = cardName.split("(?=\\p{Upper})");
                if (parsedName.length == 2){
                    parsedName[1] = Character.toLowerCase(parsedName[1].charAt(0)) + parsedName[1].substring(1);
                    drawableString = parsedName[0] + "_" + parsedName[1] + "450";
                } else if (parsedName.length == 1) {
                    drawableString = parsedName[0] + "450";
                }
                int drawableResourceId = this.getResources().getIdentifier(drawableString,
                        "drawable", this.getPackageName() );
                //create vertical Card layout
                LinearLayout newCardLayout = new LinearLayout(this);
                LinearLayout.LayoutParams params = new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCardLayout.setLayoutParams(params);
                newCardLayout.setOrientation(LinearLayout.VERTICAL);
                discardPileLayout.addView(newCardLayout);
                // create card image
                ImageView imageView = new ImageView(this);
                imageView.setId(i);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1232);
                params.leftMargin = browseSideMargin;
                params.rightMargin = browseSideMargin;
                params.bottomMargin = browseBottomMargin;
                imageView.setLayoutParams(params);
                imageView.setImageResource(drawableResourceId);
                newCardLayout.addView(imageView);
                //create take button
                Button takeButton = new Button(this);
                takeButton.setText("Take");
                takeButton.setTextSize(20f);
                takeButton.setTextColor(BACKGROUND_COLOR_DARK);
                takeButton.setBackgroundResource(R.drawable.text80);
                ButtonInfo biTag = new ButtonInfo(i, pileCardData.get(i).getCardName());
                takeButton.setTag(biTag);
                Typeface typeface = ResourcesCompat.getFont(context, R.font.alegreya_sc);
                takeButton.setTypeface(typeface);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                takeButton.setLayoutParams(params);
                newCardLayout.addView(takeButton);
                //add button to listener list
                takeButtons.add(takeButton);

            }
        }
        setButtonListeners();
    }

    void setButtonListeners(){

        for (int i = 0; i < takeButtons.size(); i++) {
            final int FINALI = i;
            final Button button = takeButtons.get(i);
            button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    ButtonInfo biTag = (ButtonInfo) takeButtons.get(FINALI).getTag();
                    int thisCardIndex = biTag.getListLocation();
                    if (!cardChosen){
                        cardChosen = true;
                        chosenIndex = thisCardIndex;
                        button.setAlpha(0.5f);
                        findViewById(thisCardIndex).setAlpha(0.5f);
                    } else if (cardChosen && (thisCardIndex == chosenIndex)){
                        cardChosen = false;
                        chosenIndex = -1;
                        button.setAlpha(1f);
                        findViewById(thisCardIndex).setAlpha(1f);
                    } else {
                        Toast toast = Toast.makeText
                                (getApplicationContext(), "choose only one", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }

        Button exitButton = findViewById(R.id.exit_discard_browse);
        exitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("chosenCardIndexKey", chosenIndex);
                returnIntent.putExtra("cardChosenKey", cardChosen);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}

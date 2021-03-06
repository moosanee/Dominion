package com.example.dominion;

import android.content.Context;
import android.content.Intent;
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

public class OpponentPileBrowseActivity extends AppCompatActivity {

    private ArrayList<CardData> pileCardData;
    String playerName;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pileCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("pileListKey");
        playerName = getIntent().getStringExtra("playerNameKey");
        String pileName = getIntent().getStringExtra("pileNameKey");
        setContentView(R.layout.activity_opponent_pile_browse);

        LinearLayout pileLayout = (LinearLayout) findViewById(R.id.pile_layout);
        LinearLayout cardLayout = (LinearLayout) findViewById(R.id.card_layout);
        TextView textView = findViewById(R.id.playername_pilename);
        textView.setText(playerName + "'s " + pileName );

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
            cardLayout.addView(noCardsText);
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
                pileLayout.addView(newCardLayout);
                // create card image
                ImageView imageView = new ImageView(this);
                //imageView.setId(i);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, xlCardHeight);
                params.leftMargin = browseSideMargin;
                params.rightMargin = browseSideMargin;
                params.bottomMargin = browseBottomMargin;
                imageView.setLayoutParams(params);
                imageView.setImageResource(drawableResourceId);
                newCardLayout.addView(imageView);

            }
        }
        setButtonListeners();
    }

    void setButtonListeners(){

        Button exitButton = findViewById(R.id.exit_pile_browse);
        exitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

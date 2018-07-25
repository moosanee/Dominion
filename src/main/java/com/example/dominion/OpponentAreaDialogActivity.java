package com.example.dominion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class OpponentAreaDialogActivity extends AppCompatActivity {

    private ArrayList<CardData> deckCardData;
    private ArrayList<CardData> discardCardData;
    private ArrayList<CardData> handCardData;
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponent_area_dialog);

        deckCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("deckPileListKey");
        handCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("handPileListKey");
        discardCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("discardPileListKey");
        playerName = getIntent().getStringExtra("opponentKey");

        ImageView deckView = findViewById(R.id.opponent_deck);
        deckView.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OpponentPileBrowseActivity.class);
                intent.putExtra("pileListKey", deckCardData);
                intent.putExtra("playerNameKey", playerName);
                intent.putExtra("pileNameKey", "deck");
                startActivity(intent);
            }
        });
        ImageView handView = findViewById(R.id.opponent_hand);
        handView.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OpponentPileBrowseActivity.class);
                intent.putExtra("pileListKey", handCardData);
                intent.putExtra("playerNameKey", playerName);
                intent.putExtra("pileNameKey", "hand");
                startActivity(intent);
            }
        });
        ImageView discardView = findViewById(R.id.opponent_discard);
        discardView.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OpponentPileBrowseActivity.class);
                intent.putExtra("pileListKey", discardCardData);
                intent.putExtra("playerNameKey", playerName);
                intent.putExtra("pileNameKey", "discard");
                startActivity(intent);
            }
        });

        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}

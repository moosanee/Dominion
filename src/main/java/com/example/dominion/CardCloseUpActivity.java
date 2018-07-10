package com.example.dominion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class CardCloseUpActivity extends AppCompatActivity {

    private String cardName;
    private int viewId;
    private String[] actions;
    private String drawableString;
    private boolean[] buttonVisibility = new boolean[4];
    int chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.cardName = getIntent().getExtras().getString("cardNameKey");
        this.viewId = getIntent().getExtras().getInt("viewIdKey");
        this.actions = getIntent().getExtras().getStringArray("actionKey");
        this.buttonVisibility = getIntent().getExtras().getBooleanArray("buttonVisibilityKey");
        setContentView(R.layout.activity_card_close_up);

        String[] parsedName = cardName.split("(?=\\p{Upper})");
        if (parsedName.length == 2){
           parsedName[1] = Character.toLowerCase(parsedName[1].charAt(0)) + parsedName[1].substring(1);
           drawableString = parsedName[0] + "_" + parsedName[1] + "450";
        } else if (parsedName.length == 1) {
            drawableString = parsedName[0] + "450";
        }
        int drawableResourceId = this.getResources().getIdentifier(drawableString,
                "drawable", this.getPackageName() );
        ImageView imageView = findViewById(R.id.close_up_view);
        imageView.setImageResource(drawableResourceId);



        Button discardButton = findViewById(R.id.discard_button);
        Button chooseButton = findViewById(R.id.choose_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button trashButton = findViewById(R.id.trash_button);
        if (buttonVisibility[0]){
            discardButton.setVisibility(View.VISIBLE);
            discardButton.setClickable(true);
            discardButton.setText(actions[0]);
            discardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosen = 0;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chooseKey", chosen);
                    returnIntent.putExtra("viewIdKey", viewId);
                    returnIntent.putExtra("cardNameKey", cardName);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
        else {
            discardButton.setVisibility(View.GONE);
            discardButton.setClickable(false);
        }

        if (buttonVisibility[1]){
            chooseButton.setVisibility(View.VISIBLE);
            chooseButton.setClickable(true);
            chooseButton.setText(actions[1]);
            chooseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosen = 1;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chooseKey", chosen);
                    returnIntent.putExtra("viewIdKey", viewId);
                    returnIntent.putExtra("cardNameKey", cardName);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
        else {
            chooseButton.setVisibility(View.GONE);
            chooseButton.setClickable(false);
        }

        if (buttonVisibility[2]){
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setClickable(true);
            cancelButton.setText(actions[2]);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosen = 2;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chooseKey", chosen);
                    returnIntent.putExtra("viewIdKey", viewId);
                    returnIntent.putExtra("cardNameKey", cardName);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
        else {
            cancelButton.setVisibility(View.GONE);
            cancelButton.setClickable(false);
        }

        if (buttonVisibility[3]){
            trashButton.setVisibility(View.VISIBLE);
            trashButton.setClickable(true);
            trashButton.setText(actions[3]);
            trashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chosen = 3;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chooseKey", chosen);
                    returnIntent.putExtra("viewIdKey", viewId);
                    returnIntent.putExtra("cardNameKey", cardName);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
        else {
            trashButton.setVisibility(View.GONE);
            trashButton.setClickable(false);
        }

    }

}

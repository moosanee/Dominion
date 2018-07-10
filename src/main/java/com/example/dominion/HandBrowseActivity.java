package com.example.dominion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class HandBrowseActivity extends AppCompatActivity {

    private ArrayList<CardData> pileCardData = new ArrayList<>();
    ArrayList<Button> takeButtons = new ArrayList<>();
    ArrayList<ButtonInfo> chooseList = new ArrayList<>();
    String returnCardName = "";
    boolean takeCard = true;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pileCardData = (ArrayList<CardData>) getIntent().getSerializableExtra
                ("handPileListKey");
        setContentView(R.layout.activity_hand_browse);

        LinearLayout handPileLayout = (LinearLayout) findViewById(R.id.hand_pile_layout);

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
            handPileLayout.addView(newCardLayout);
            // create card image
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, xlCardHeight);
            params.leftMargin = browseSideMargin;
            params.rightMargin = browseSideMargin;
            params.bottomMargin = browseBottomMargin;
            imageView.setLayoutParams(params);
            imageView.setImageResource(drawableResourceId);
            newCardLayout.addView(imageView);
            //create take button
            Button takeButton = new Button(this);
            takeButton.setText("Choose");
            takeButton.setTextSize(textSize);
            takeButton.setTextColor(BACKGROUND_COLOR_DARK);
            takeButton.setBackgroundResource(R.drawable.text80);
            ButtonInfo biTag = new ButtonInfo(i,pileCardData.get(i).getCardName());
            takeButton.setTag(biTag);
            takeButton.setId(i);
            takeButton.setTypeface(ResourcesCompat.getFont(context, R.font.alegreya_sc));
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            takeButton.setLayoutParams(params);
            newCardLayout.addView(takeButton);
            //add button to listener list
            takeButtons.add(takeButton);
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
                    if (chooseList.contains(biTag)) {
                        chooseList.remove(biTag);
                        button.setAlpha(1f);
                        int index = biTag.getListLocation();
                        findViewById(index).setAlpha(1f);
                    }
                    else{
                        chooseList.add(biTag);
                        button.setAlpha(0.5f);
                        int index = biTag.getListLocation();
                        findViewById(index).setAlpha(0.5f);
                    }
                }
            });
        }

        Button exitButton = findViewById(R.id.exit_hand_browse);
        exitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] indexList = new int[chooseList.size()];
                for (int j = 0; j < chooseList.size(); j++){
                    indexList[j] = chooseList.get(j).getListLocation();
                    for (int i = 0; i < j; i ++){
                        if (indexList[i] < indexList[j]){
                            int temp = indexList[j];
                            indexList[j] = indexList[i];
                            indexList[i] = temp;
                        }
                    }
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("listSizeKey", chooseList.size());
                returnIntent.putExtra("indexListKey", indexList);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}

class ButtonInfo implements Serializable{
    int listLocation;
    String cardName;

    ButtonInfo(int listLocation, String cardName){
        this.listLocation = listLocation;
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }

    public int getListLocation() {
        return listLocation;
    }
}
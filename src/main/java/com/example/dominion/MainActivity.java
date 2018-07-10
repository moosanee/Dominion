package com.example.dominion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int CHOOSE_CARDS_ACTIVITY_CODE = 0;
    private static final int START_GAME_ACTIVITY_CODE = 0;

    ArrayList<String> gameCardList = new ArrayList<>();
    ArrayList<PlayerInfo> playerList = new ArrayList<>();
    private boolean[] players = {true, false, false, false};
    private boolean[] human = {true, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButtonListeners();
    }

    private void addButtonListeners() {
        Button chooseCards = findViewById(R.id.choose_cards_button);
        chooseCards.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                boolean complete = setPlayers();
                if(complete) {
                    Intent intent = new Intent(view.getContext(), ChooseGameCardsActivity.class);
                    intent.putExtra("playerListKey", playerList);
                    startActivityForResult(intent, CHOOSE_CARDS_ACTIVITY_CODE);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "more players", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button startGame = findViewById(R.id.start_game_button);
        startGame.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                boolean complete = setPlayers();
                if(complete) {
                    Intent intent = new Intent(view.getContext(), GameBoardActivity.class);
                    intent.putStringArrayListExtra("gameCardListKey", gameCardList);
                    intent.putExtra("playerListKey", playerList);
                    startActivityForResult(intent, START_GAME_ACTIVITY_CODE);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "more players", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean setPlayers() {
        for (int i = 0; i < 4; i++){
            if (players[i]){
                String idName = "player"+(i+1)+"_name";
                int viewId = getResources().getIdentifier(idName, "id", this.getPackageName());
                EditText editText = findViewById(viewId);
                String name = editText.getText().toString();
                if (name.equals("")) name = "Player"+(i+1);
                PlayerInfo playerInfo = new PlayerInfo(i+1, name, human[i]);
                playerList.add(playerInfo);
            }
        }
        if (playerList.size() > 0)
            return true;
        else
            return false;
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHOOSE_CARDS_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                if (data==null)
                {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    gameCardList = data.getStringArrayListExtra("cardListKey");
                }
            }
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        String playing = (String) view.getTag();
        String[] parsedName = playing.split("(?=\\p{Punct})|(?=\\d)");
        int playerNumber = Integer.parseInt(parsedName[1]);
        switch (playerNumber) {
            case 1:
                EditText editText = findViewById(R.id.player1_name);
                RadioButton humanButton = findViewById(R.id.human1);
                RadioButton robotButton = findViewById(R.id.robot1);
                if (checked){
                    editText.setFocusableInTouchMode(true);
                    humanButton.setClickable(true);
                    robotButton.setClickable(true);
                    players[0] = true;
                } else {
                    editText.setFocusableInTouchMode(false);
                    humanButton.setClickable(false);
                    robotButton.setClickable(false);
                    players[0] = false;
                }

                break;
            case 2:
                editText = findViewById(R.id.player2_name);
                humanButton = findViewById(R.id.human2);
                robotButton = findViewById(R.id.robot2);
                if (checked){
                    editText.setFocusableInTouchMode(true);
                    humanButton.setClickable(true);
                    robotButton.setClickable(true);
                    players[1] = true;
                } else {
                    editText.setFocusableInTouchMode(false);
                    humanButton.setClickable(false);
                    robotButton.setClickable(false);
                    players[1] = false;
                }
                break;

            case 3:
                editText = findViewById(R.id.player3_name);
                humanButton = findViewById(R.id.human3);
                robotButton = findViewById(R.id.robot3);
                if (checked){
                    editText.setFocusableInTouchMode(true);
                    humanButton.setClickable(true);
                    robotButton.setClickable(true);
                    players[2] = true;
                } else {
                    editText.setFocusableInTouchMode(false);
                    humanButton.setClickable(false);
                    robotButton.setClickable(false);
                    players[2] = false;
                }
                break;

            case 4:
                editText = findViewById(R.id.player4_name);
                humanButton = findViewById(R.id.human4);
                robotButton = findViewById(R.id.robot4);
                if (checked){
                    editText.setFocusableInTouchMode(true);
                    humanButton.setClickable(true);
                    robotButton.setClickable(true);
                    players[3] = true;
                } else {
                    editText.setFocusableInTouchMode(false);
                    humanButton.setClickable(false);
                    robotButton.setClickable(false);
                    players[3] = false;
                }
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        String playerType = (String) view.getTag();
        String[] parsedName = playerType.split("(?=\\p{Punct})|(?=\\d)");
        int playerNumber = Integer.parseInt(parsedName[1]);
        if (parsedName[0].equals("human")) human[playerNumber-1] = true;
        else human[playerNumber-1] = false;
    }

}


class PlayerInfo implements Serializable{
    private int number;
    private String name;
    private boolean human;

    public PlayerInfo(int number, String name, boolean human) {
        this.number = number;
        this.name = name;
        this.human = human;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
    public boolean getHuman() {
        return human;
    }
}
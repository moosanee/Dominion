package com.example.dominion;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;

public class BasicCardsActivity extends AppCompatActivity {

    ArrayList<ImageView> viewList;
    ArrayList<String> cardList = new ArrayList<>();
    ArrayList<Card> basicCardSet = new ArrayList<>();
    ArrayList<Integer> costList = new ArrayList<>();
    boolean[] buttonVisibility = {false, true, true, false};
    int extraCards = 0;
    int extraActions = 0;
    int extraBuys = 0;
    int extraCoins = 0;
    int attacks = 0;
    int trashers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.cardList = getIntent().getStringArrayListExtra("gameCardListKey");
        this.basicCardSet = (ArrayList<Card>) getIntent().getSerializableExtra("basicCardSetKey");
        Bundle statBundle = this.getIntent().getBundleExtra("statBundleKey");
        GameCardStats gameCardStats = (GameCardStats) statBundle.getSerializable("cardStatKey");

        setContentView(R.layout.activity_basic_cards);

        nameImageViews();

        costList.clear();
        for (int i = 0; i < 15; i++){
            costList.add(gameCardStats.getCardCostTally()[i]);
        }
        for (int i = 0; i < 5; i++)extraCards += gameCardStats.getCardDrawTally()[i];
        for (int i = 0; i < 5; i++)extraActions += gameCardStats.getExtraActionTally()[i];
        for (int i = 0; i < 5; i++)extraBuys += gameCardStats.getExtraBuysTally()[i];
        for (int i = 0; i < 5; i++)extraCoins += gameCardStats.getExtraCoinTally()[i];
        for (int i = 0; i < gameCardStats.getcardTypeTally().size(); i++){
            if (gameCardStats.getcardTypeTally().get(i).getTypeName().equals("action - attack"))
                attacks +=gameCardStats.getcardTypeTally().get(i).getTypeTally();
        }
        trashers = gameCardStats.getTrashTally();
        setCountViews(costList, extraCards, extraActions, extraBuys, extraCoins, attacks, trashers);

        //for (int i = 0; i < 6; i++) costList.add(0);
        addImageListeners();

    }

    private void setCountViews(ArrayList<Integer> costList, int extraCards, int extraActions,
                               int extraBuys, int extraCoins, int attacks, int trashers) {
        TextView view = findViewById(R.id.costs2_count);
        view.setText(String.valueOf(costList.get(0)+costList.get(1)));
        view = findViewById(R.id.costs3_count);
        view.setText(String.valueOf(costList.get(2)));
        view = findViewById(R.id.costs4_count);
        view.setText(String.valueOf(costList.get(3)));
        view = findViewById(R.id.costs5_count);
        view.setText(String.valueOf(costList.get(4)));
        view = findViewById(R.id.costs6_count);
        int cost = 0;
        for (int i = 5; i < 15; i++) cost += costList.get(i);
        view.setText(String.valueOf(cost));
        view = findViewById(R.id.extra_card_count);
        view.setText(String.valueOf(extraCards));
        view = findViewById(R.id.extra_action_count);
        view.setText(String.valueOf(extraActions));
        view = findViewById(R.id.extra_buy_count);
        view.setText(String.valueOf(extraBuys));
        view = findViewById(R.id.extra_coin_count);
        view.setText(String.valueOf(extraCoins));
        view = findViewById(R.id.attacks_count);
        view.setText(String.valueOf(attacks));
        view = findViewById(R.id.trashes_count);
        view.setText(String.valueOf(trashers));
        view = findViewById(R.id.card_count);
        view.setText(String.valueOf(cardList.size()));
    }


    private void addImageListeners() {
        for (final ImageView listItem : viewList){
            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View view) {
                    String cardName = String.valueOf(listItem.getTag());
                    String[] actions = {"", "Choose", "Cancel", ""};
                    Intent closeUpIntent = new Intent(view.getContext(),CardCloseUpActivity.class);
                    closeUpIntent.putExtra("cardNameKey", cardName);
                    closeUpIntent.putExtra("viewIdKey", view.getId());
                    closeUpIntent.putExtra("actionKey", actions);
                    closeUpIntent.putExtra("buttonVisibilityKey", buttonVisibility);
                    startActivityForResult(closeUpIntent, CARD_CLOSEUP_ACTIVITY_CODE);

                }
            });
        }
        final Button finishButton = findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("cardListKey", cardList);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        int chosen = 4;
        int viewId = 0;
        String cardName;
        if (requestCode == CARD_CLOSEUP_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                if (data==null)
                {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                }
                else {
                    // check if clicked card is in the game list, and where
                    chosen = data.getIntExtra("chooseKey", chosen);
                    viewId = data.getIntExtra("viewIdKey", viewId);
                    cardName = data.getStringExtra("cardNameKey");
                    ImageView examinedImageView = findViewById(viewId);
                    boolean alreadyListed = false;
                    int atIndex = 0;
                    for (int i = 0; i < cardList.size(); i++){
                        if (cardList.get(i).equals(cardName)) {
                            alreadyListed = true;
                            atIndex = i;
                        }
                    }
                    // if chosen for the first time
                    Card card = basicCardSet.get(atIndex);
                    if (chosen == 1) {
                        if (!alreadyListed) {
                            cardList.add(cardName);
                            if (card.getDrawCards() > 0) extraCards ++;
                            if (card.getActions() > 0) extraActions ++;
                            if (card.getExtraBuys() > 0) extraBuys ++;
                            if (card.getExtraCoins() > 0) extraCoins ++;
                            if (card.getType().equals("action - attack")) attacks ++;
                            if (card.isTrasher()) trashers ++;
                            int cost = card.getCost();
                            costList.set(cost-1, costList.get(cost-1)+1);
                        }
                        examinedImageView.setColorFilter
                                (Color.argb(80, 255, 255, 255));
                    }
                    //if removed from list
                    else if (chosen == 2) {
                        if (alreadyListed){
                            cardList.remove(atIndex);
                            if (card.getDrawCards() > 0) extraCards --;
                            if (card.getActions() > 0) extraActions --;
                            if (card.getExtraBuys() > 0) extraBuys --;
                            if (card.getExtraCoins() > 0) extraCoins --;
                            if (card.getType().equals("action - attack")) attacks --;
                            if (card.isTrasher()) trashers --;
                            int cost = card.getCost();
                            costList.set(cost-1, costList.get(cost-1)-1);
                        }
                        examinedImageView.setColorFilter
                                (Color.argb(0, 255, 255, 255));
                    }
                    else {}
                }
                setCountViews(costList, extraCards, extraActions, extraBuys, extraCoins, attacks, trashers);
            }
        }
    }

    //name imageviews and put them in a list
    private void nameImageViews() {
        ImageView adventurerImage = findViewById(R.id.adventurer);
        ImageView artisanImage = findViewById(R.id.artisan);
        ImageView banditImage = findViewById(R.id.bandit);
        ImageView bureaucratImage = findViewById(R.id.bureaucrat);
        ImageView cellarImage = findViewById(R.id.cellar);
        ImageView chancellorImage = findViewById(R.id.chancellor);
        ImageView chapelImage = findViewById(R.id.chapel);
        ImageView councilRoomImage = findViewById(R.id.council_room);
        ImageView feastImage = findViewById(R.id.feast);
        ImageView festivalImage = findViewById(R.id.festival);
        ImageView gardensImage = findViewById(R.id.gardens);
        ImageView harbingerImage = findViewById(R.id.harbinger);
        ImageView laboratoryImage = findViewById(R.id.laboratory);
        ImageView libraryImage = findViewById(R.id.library);
        ImageView marketImage = findViewById(R.id.market);
        ImageView merchantImage = findViewById(R.id.merchant);
        ImageView militiaImage = findViewById(R.id.militia);
        ImageView mineImage = findViewById(R.id.mine);
        ImageView moatImage = findViewById(R.id.moat);
        ImageView moneyLenderImage = findViewById(R.id.money_lender);
        ImageView poacherImage = findViewById(R.id.poacher);
        ImageView remodelImage = findViewById(R.id.remodel);
        ImageView sentryImage = findViewById(R.id.sentry);
        ImageView smithyImage = findViewById(R.id.smithy);
        ImageView spyImage = findViewById(R.id.spy);
        ImageView thiefImage = findViewById(R.id.thief);
        ImageView throneRoomImage = findViewById(R.id.throne_room);
        ImageView vassalImage = findViewById(R.id.vassal);
        ImageView villageImage = findViewById(R.id.village);
        ImageView witchImage = findViewById(R.id.witch);
        ImageView woodcutterImage = findViewById(R.id.woodcutter);
        ImageView workshopImage = findViewById(R.id.workshop);

        viewList = new ArrayList<>();
        viewList.add(adventurerImage);
        viewList.add(artisanImage);
        viewList.add(banditImage);
        viewList.add(bureaucratImage);
        viewList.add(cellarImage);
        viewList.add(chancellorImage);
        viewList.add(chapelImage);
        viewList.add(councilRoomImage);
        viewList.add(feastImage);
        viewList.add(festivalImage);
        viewList.add(gardensImage);
        viewList.add(harbingerImage);
        viewList.add(laboratoryImage);
        viewList.add(libraryImage);
        viewList.add(marketImage);
        viewList.add(merchantImage);
        viewList.add(militiaImage);
        viewList.add(mineImage);
        viewList.add(moatImage);
        viewList.add(moneyLenderImage);
        viewList.add(poacherImage);
        viewList.add(remodelImage);
        viewList.add(sentryImage);
        viewList.add(smithyImage);
        viewList.add(spyImage);
        viewList.add(thiefImage);
        viewList.add(throneRoomImage);
        viewList.add(vassalImage);
        viewList.add(villageImage);
        viewList.add(witchImage);
        viewList.add(woodcutterImage);
        viewList.add(workshopImage);
    }
}

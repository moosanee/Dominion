package com.example.dominion;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.dominion.MyConstants.*;


public class GameBoardActivity extends AppCompatActivity {

    ArrayList<CardData> trash = new ArrayList<>();
    CardData trashPile;
    int trashTally = 0;
    ArrayList<CardData> bankPiles = new ArrayList<>();
    int bankPileTally = 0;
    ArrayList<ImageView> opponentImageViews = new ArrayList<>();
    ArrayList<Card> gameCards = new ArrayList<>();
    ArrayList<String> gameCardList = new ArrayList<>();
    BasicCards basicCardSet = new BasicCards();
    ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
    ArrayList<Player> playerList = new ArrayList<>();
    Undo undo;
    Button undoButton;
    Turn turn;
    int turnMarker = 0;
    int roundNumber = 0;
    int emptyBankPiles = 0;
    int emptyPilesToEnd = 3;
    ArrayList<TurnArchive> turnList = new ArrayList<>();
    boolean doubleTap = false;
    ListenerSwitches listenerSwitches = new ListenerSwitches();
    private GestureDetectorCompat detector;
    ConstraintSet constraintSet;
    ConstraintLayout layout;
    Context context;
    GameBoardActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        detector = new GestureDetectorCompat(this, new GestureListener());

        gameCardList = getIntent().getStringArrayListExtra("gameCardListKey");
        this.playerInfoList = (ArrayList<PlayerInfo>) getIntent()
                .getSerializableExtra("playerListKey");
        emptyPilesToEnd = getIntent().getIntExtra("emptyPilesKey", 3);

        layout = findViewById(R.id.activity_game_board);
        constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        context = getApplicationContext();
        activity = this;
        undoButton = findViewById(R.id.undo_button);

        layoutHandAndInPlayAreas();
        for (int i = 0; i < playerInfoList.size(); i++){
            playerList.add( new Player(playerInfoList.get(i)));
            playerList.get(i).shufflePile("deck");
            if (i == 0) {
                playerList.get(0).drawHand(layout, context, activity, handListener);
            } else {
                playerList.get(i).drawOffTurnHand();
            }
        }

        TextView textView = findViewById(R.id.hand);
        textView.setId(FOCUS_HAND_ID);
        textView.setText(playerList.get(0).getName() + " hand");
        ImageView imageViewB = findViewById(R.id.playerB_hand);
        imageViewB.setId(POSITION_B_IMAGE_ID);
        ImageView imageViewC = findViewById(R.id.playerC_hand);
        imageViewC.setId(POSITION_C_IMAGE_ID);
        ImageView imageViewD = findViewById(R.id.playerD_hand);
        imageViewD.setId(POSITION_D_IMAGE_ID);
        TextView textViewB = findViewById(R.id.playerB);
        textViewB.setId(POSITION_B_NAME_ID);
        TextView textViewC = findViewById(R.id.playerC);
        textViewC.setId(POSITION_C_NAME_ID);
        TextView textViewD = findViewById(R.id.playerD);
        textViewD.setId(POSITION_D_NAME_ID);
        switch (playerInfoList.size()){
            case 1:
                layout.removeView(imageViewB);
                layout.removeView(imageViewC);
                layout.removeView(imageViewD);
                layout.removeView(textViewB);
                layout.removeView(textViewC);
                layout.removeView(textViewD);
                break;
            case 2:
                layout.removeView(imageViewB);
                layout.removeView(imageViewD);
                layout.removeView(textViewB);
                layout.removeView(textViewD);
                textViewC.setText(playerList.get(1).getName());
                imageViewC.setTag(playerList.get(1));
                opponentImageViews.add(imageViewC);
                break;
            case 3:
                layout.removeView(imageViewB);
                layout.removeView(textViewB);
                textViewC.setText(playerList.get(1).getName());
                textViewD.setText(playerList.get(2).getName());
                imageViewC.setTag(playerList.get(1));
                imageViewD.setTag(playerList.get(2));
                opponentImageViews.add(imageViewC);
                opponentImageViews.add(imageViewD);
                break;
            case 4:
                textViewB.setText(playerList.get(1).getName());
                textViewC.setText(playerList.get(2).getName());
                textViewD.setText(playerList.get(3).getName());
                imageViewB.setTag(playerList.get(1));
                imageViewC.setTag(playerList.get(2));
                imageViewD.setTag(playerList.get(3));
                opponentImageViews.add(imageViewB);
                opponentImageViews.add(imageViewC);
                opponentImageViews.add(imageViewD);
                break;
        }
        playerList.get(0).initializeDeck(layout, context, activity);
        playerList.get(0).initializeDiscard(layout, context, activity);
        for (int i = 1; i < playerList.size(); i++){
            playerList.get(i).deckPile = playerList.get(0).deckPile;
            playerList.get(i).discardPile = playerList.get(0).discardPile;
        }
        pullOutCards(gameCardList);
        reorderByCost(gameCards);
        boolean curse = checkCurse(gameCardList);
        if (curse) {
            int viewId = getResources().getIdentifier("curse", "id", getPackageName());
            ImageView curseView = (ImageView) layout.getViewById(viewId);
            final int BANK_TALLY = bankPileTally;
            bankPileTally +=1;
            curseView.setId(BANK_VIEW_ID + BANK_TALLY);
            bankPiles.add(new CardData("curse", "bank", 0, 0));
            bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + BANK_TALLY);
            curseView.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
            int drawableId = getResources().getIdentifier("curse60",
                    "drawable", getPackageName());
            curseView.setImageResource(drawableId);
            bankPiles.get(bankPiles.size()-1).setTextView(
                    makePileSizeCounter(192, 96, "curse", "30"));
            bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
            layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        }
        layoutActionCards(layout, gameCards);
        completeImageViewList(layout);
        turn = new Turn(playerList.get(turnMarker), emptyBankPiles, bankPiles, activity, context, layout);
        setImageListeners();
        turn.startTurn(listenerSwitches);
    }

    private void setImageListeners() {
//bank card views
        for (int i = 0; i < bankPiles.size(); i++) {
            ImageView view = findViewById(bankPiles.get(i).getImageViewId());
            view.setOnTouchListener(bankListener);
        }
//hand card views
        for (int i = 0; i < playerList.get(turnMarker).hand.size(); i++) {
            ImageView view = findViewById(playerList.get(turnMarker).hand.get(i).getImageViewId());
            view.setOnTouchListener(handListener);
            playerList.get(turnMarker).hand.get(i).setDragListener(new MyDragListener());
            view.setOnDragListener(playerList.get(turnMarker).hand.get(i).getDragListener());
        }
//deck view
        View view = findViewById(playerList.get(turnMarker).deckPile.getImageViewId());
        view.setOnTouchListener(deckListener);
        playerList.get(turnMarker).deckPile.setDragListener(new MyDragListener());
        view.setOnDragListener(playerList.get(turnMarker).deckPile.getDragListener());
//discard view
        view = findViewById(playerList.get(turnMarker).discardPile.getImageViewId());
        view.setOnTouchListener(discardListener);
        playerList.get(turnMarker).discardPile.setDragListener(new MyDragListener());
        view.setOnDragListener( playerList.get(turnMarker).discardPile.getDragListener());
//trash view
        ImageView imageView = findViewById(TRASH_VIEW_ID);
        imageView.setOnTouchListener(trashListener);
        trashPile.setDragListener(new MyDragListener());
        imageView.setOnDragListener(trashPile.getDragListener());
//opponents views
        for (int i = 0; i < opponentImageViews.size(); i++) {
            final int FINALI = i;
            view = opponentImageViews.get(i);
            view.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    Player player = (Player) opponentImageViews.get(FINALI).getTag();
                    String opponentName = player.getName();
                    ArrayList<CardData> deckCardList = new ArrayList<>();
                    for (int j = 0; j < player.deck.size(); j++) {
                        deckCardList.add(player.deck.get(j));
                    }
                    ArrayList<CardData> handCardList = new ArrayList<>();
                    for (int j = 0; j < player.hand.size(); j++) {
                        handCardList.add(player.hand.get(j));
                    }
                    ArrayList<CardData> discardCardList = new ArrayList<>();
                    for (int j = 0; j < player.discard.size(); j++) {
                        discardCardList.add(player.discard.get(j));
                    }
                    Intent intent = new Intent(view.getContext(), OpponentAreaDialogActivity.class);
                    intent.putExtra("deckPileListKey", deckCardList);
                    intent.putExtra("handPileListKey", handCardList);
                    intent.putExtra("discardPileListKey", discardCardList);
                    intent.putExtra("opponentKey", opponentName);
                    startActivity(intent);
                }
            });
        }

        Button button = findViewById(R.id.end_game_button);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

//hand area view
        ImageView handView = findViewById(HAND_AREA_VIEW_ID);
        handView.setOnDragListener(new MyDragListener());

//in play area view
        ImageView inPlayView = findViewById(INPLAY_VIEW_ID);
        inPlayView.setOnDragListener(new MyDragListener());

// turn progression button
        view = findViewById(PHASE_BUTTON_ID);
        view.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                int mode = (int) view.getTag();
                switch (mode){
                    case ACTION_PHASE: //"finished actions"
                        turn.startBuyingPhase(listenerSwitches);
                        undo = new Undo("start buying phase", turn, listenerSwitches);
                        undoButton.setClickable(true);
                        undoButton.setAlpha(1f);
                        break;
                    case BUYING_PHASE: //"play all treasures"
                        ArrayList<CardData> treasureList =
                                turn.playAllTreasures(inPlayListener, handListener, listenerSwitches);
                        undo = new Undo("play all treasures", turn, treasureList,
                                handListener, listenerSwitches);
                        undoButton.setClickable(true);
                        undoButton.setAlpha(1f);
                        break;
                    case OPEN_BANK://"finished buying"
                        turn.startCleanUpPhase(listenerSwitches);
                        undo = new Undo("start clean up phase", turn, listenerSwitches);
                        undoButton.setClickable(true);
                        undoButton.setAlpha(1f);
                        break;
                    case CLEAN_UP_PHASE: // "clean up"
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        turn.cleanUp();
                        break;
                    case ADVENTURER: //"apply adventurer"
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        Toast.makeText(context, "putting revealed treasures in hand." +
                                "\ndiscarding other revealed cards", Toast.LENGTH_SHORT).show();
                        turn.finishAdventurer(listenerSwitches, handListener);
                        break;
                    case ARTISAN1: // "gain card"
                        Toast.makeText(context,"gain a card costing up to 5 to your hand",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ARTISAN2: // "card to deck"
                        Toast.makeText(context,"put a card from your hand onto your deck",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case CELLAR: // "finished discarding"
                        int discarded = turn.finishCellar(handListener, listenerSwitches);
                        undo = new Undo("finish cellar", turn, discarded, listenerSwitches);
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        break;
                    case CHAPEL: // "finished trashing"
                        int trashed = turn.finishChapel(listenerSwitches);
                        undo = new Undo("finish chapel", turn, trashed, listenerSwitches);
                        undoButton.setClickable(true);
                        undoButton.setAlpha(1f);
                        break;
                    case FEAST: // "gain card"
                        Toast.makeText(context,"gain a card costing up to 5 coins",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case HARBINGER:
                        String toast = basicCardSet.getCard("harbinger").getInstructions();
                        Toast.makeText(context, toast,Toast.LENGTH_SHORT).show();
                        break;
                    case POACHER: // "discard card"
                        if (playerList.get(turnMarker).hand.size() == 0) {
                            Toast.makeText(context, "no cards in hand to discard",
                                    Toast.LENGTH_SHORT).show();
                            turn.startOpenBankPhase(listenerSwitches);
                        }
                        else Toast.makeText(context,"discard another card from your hand",
                                Toast.LENGTH_SHORT).show();
                        break;


                }
            }
        });

        undoButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                undo.undoAction();
                undoButton.setClickable(false);
                undoButton.setAlpha(0.5f);
            }
        });

    }//setImageListeners




    //bank onTouch listener
    View.OnTouchListener bankListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isBankListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    CardMultiTag cmt = (CardMultiTag) view.getTag();
                    String cardName = cmt.getCardName();
                    int viewId = view.getId();
                    String[] actions = {"","","exit",""};
                    boolean[] buttonVisibility = {false, false, true, false};
                    Intent intent = new Intent(view.getContext(), CardCloseUpActivity.class);
                    intent.putExtra("cardNameKey", cardName);
                    intent.putExtra("viewIdKey", viewId);
                    intent.putExtra("actionKey", actions);
                    intent.putExtra("buttonVisibilityKey", buttonVisibility);
                    startActivity(intent);
                }
            }
            return detected;
        }
    }; // bank onTouch listener

    //hand onTouch listener
    View.OnTouchListener handListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isHandListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    Intent intent = new Intent(view.getContext(), HandBrowseActivity.class);
                    intent.putExtra("handPileListKey", playerList.get(turnMarker).hand);
                    startActivityForResult(intent, HAND_BROWSE_LISTENER);
                }
            }
            return detected;
        }
    }; // hand onTouch listener

    //in play onTouch listener
    View.OnTouchListener inPlayListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isInPlayListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                }
            }
           /* if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    Intent intent = new Intent(view.getContext(), HandBrowseActivity.class);
                    intent.putExtra("inPlayPileListKey", playerList.get(turnMarker).inPlay);
                    startActivityForResult(intent, INPLAY_BROWSE_LISTENER);
                }
            }*/
            return detected;
        }
    }; // in play onTouch listener

    //deck onTouch listener
    View.OnTouchListener deckListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isDeckListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    Intent intent = new Intent(view.getContext(), DeckBrowseActivity.class);
                    intent.putExtra("deckPileListKey", playerList.get(turnMarker).deck);
                    startActivityForResult(intent, DECK_BROWSE_LISTENER);
                }
            }
            return detected;
        }
    }; // deck onTouch listener

    //discard onTouch listener
    View.OnTouchListener discardListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isDiscardListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    Intent intent = new Intent(view.getContext(), DiscardBrowseActivity.class);
                    intent.putExtra("discardPileListKey", playerList.get(turnMarker).discard);
                    startActivityForResult(intent, DISCARD_BROWSE_LISTENER);
                }
            }
            return detected;
        }
    }; // discard onTouch listener

    //trash onTouch listener
    View.OnTouchListener trashListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean detected = detector.onTouchEvent(motionEvent);

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                if (!doubleTap && listenerSwitches.isTrashListenerSwitch()) {
                    ClipData data = ClipData.newPlainText("", "");
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            view);
                    view.startDrag(data, shadowBuilder, view, 0);
                }
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (doubleTap) {
                    doubleTap = false;
                    Intent intent = new Intent(view.getContext(), TrashPileActivity.class);
                    intent.putExtra("trashPileListKey", trash);
                    startActivityForResult(intent, TRASH_LISTENER);
                }
            }
            return detected;
        }
    }; // trash onTouch listener



    class MyDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int targetId = 0;
            final ImageView movingView = (ImageView) event.getLocalState();
            int viewId = movingView.getId();
            CardMultiTag cmt = (CardMultiTag) movingView.getTag();
            String movingViewName = cmt.getCardName();
            String movingViewType = cmt.getCardType();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    cmt = (CardMultiTag) v.getTag();
                    String targetType = cmt.getCardType();
                    if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).hand.size(); i++) {
                            findViewById(playerList.get(turnMarker).hand.get(i).getImageViewId()).setAlpha(0.5f);
                        }
                    }
                    if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).inPlay.size(); i++) {
                            findViewById(playerList.get(turnMarker).inPlay.get(i).getImageViewId()).setAlpha(0.5f);
                        }
                        v.setBackgroundColor(BACKGROUND_COLOR);
                    }
                    if (targetType.equals("deck") && listenerSwitches.isDeckDragSwitch()) {
                        v.setAlpha(0.5f);
                    }
                    if (targetType.equals("discard") && listenerSwitches.isDiscardDragSwitch()) {
                        v.setAlpha(0.5f);
                    }
                    if (targetType.equals("trash") && listenerSwitches.isTrashDragSwitch()) {
                        v.setAlpha(0.5f);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    cmt = (CardMultiTag) v.getTag();
                    targetType = cmt.getCardType();
                    if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).hand.size(); i++) {
                            findViewById(playerList.get(turnMarker).hand.get(i).getImageViewId()).setAlpha(1f);
                        }
                    }
                    if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).inPlay.size(); i++) {
                            findViewById(playerList.get(turnMarker).inPlay.get(i).getImageViewId()).setAlpha(1f);
                        }
                        v.setBackgroundColor(BACKGROUND_COLOR_DARK);
                    }
                    if (targetType.equals("deck") && listenerSwitches.isDeckDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    if (targetType.equals("discard") && listenerSwitches.isDiscardDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    if (targetType.equals("trash") && listenerSwitches.isTrashDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    boolean cardsLeft = true;
                    targetId = v.getId();
                    cmt = (CardMultiTag) v.getTag();
                    targetType = cmt.getCardType();
                    if (movingViewType.equals("bank")) {
                        int pileSize = getBankPileSize(movingViewName);
                        if (pileSize > 0) {
                            int trashId = trashPile.getImageViewId();
                            int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                            int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                            if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                                cardsLeft = removeCardFromBankPile(movingViewName);
                                if (cardsLeft) addCardToTrash(movingViewName);
                            } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                                cardsLeft = removeCardFromBankPile(movingViewName);
                                if (cardsLeft) playerList.get(turnMarker)
                                        .addCardToDiscard(movingViewName, activity, context);
                                String fromTo = "moved " + movingViewName + " to discard";
                                undo = new Undo(fromTo, turn, turn.phase, bankPiles, listenerSwitches);
                                undoButton.setClickable(true);
                                undoButton.setAlpha(1f);
                                turn.reactToNewCardInDiscard(movingViewName, bankPiles, listenerSwitches);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                cardsLeft = removeCardFromBankPile(movingViewName);
                                if (cardsLeft) playerList.get(turnMarker).addCardToDeck(movingViewName,
                                        activity, context);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                cardsLeft = removeCardFromBankPile(movingViewName);
                                if (cardsLeft) playerList.get(turnMarker).addCardToHand(movingViewName,
                                        layout, context,
                                        activity, handListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                                turn.reactToNewCardInHand(movingViewName, listenerSwitches);
                            } else if (targetType.equals("inPlay") &&
                                    listenerSwitches.isInPlayDragSwitch()) {
                                cardsLeft = removeCardFromBankPile(movingViewName);
                                if (cardsLeft) playerList.get(turnMarker).addCardToPlayArea
                                        (movingViewName, layout, context,
                                        activity, inPlayListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                                turn.reactToNewCardInPlay(movingViewName, handListener, listenerSwitches);
                            } else {
                                movingView.setAlpha(1f);
                                return false;
                            }
                        } else {
                            movingView.setAlpha(1f);
                            return false;
                        }
                    }
                    if (movingViewType.equals("hand")) {
                        int trashId = trashPile.getImageViewId();
                        int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                        int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromHand(viewId, activity, layout);
                            addCardToTrash(movingViewName);
                            String description = "moved " + movingViewName + " to trash";
                            undo = new Undo( description, turn, turn.phase, handListener, listenerSwitches);
                            undoButton.setClickable(true);
                            undoButton.setAlpha(1f);
                            turn.reactToNewCardInTrash(movingViewName, handListener, listenerSwitches);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromHand(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToDiscard(movingViewName, activity, context);
                            String fromTo = "moved " + movingViewName + " to discard";
                            undo = new Undo(fromTo, turn, turn.phase, handListener, listenerSwitches);
                            undoButton.setClickable(true);
                            undoButton.setAlpha(1f);
                            turn.reactToNewCardInDiscard(movingViewName, bankPiles, listenerSwitches);
                        } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromHand(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToDeck(movingViewName, activity, context);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                            turn.reactToNewCardOnDeck(movingViewName, handListener, listenerSwitches);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromHand(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToPlayArea(movingViewName, layout, context,
                                    activity, inPlayListener);
                            String fromTo = "moved " + movingViewName + " to inPlay";
                            undo = new Undo( fromTo, turn, turn.phase, handListener, listenerSwitches);
                            undoButton.setClickable(true);
                            undoButton.setAlpha(1f);
                            turn.reactToNewCardInPlay(movingViewName, handListener, listenerSwitches);
                        } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                            movingView.setAlpha(1f);
                            return false;
                        } else {
                            movingView.setAlpha(1f);
                            return false;
                        }
                    }
                    if (movingViewType.equals("inPlay")) {
                        int trashId = trashPile.getImageViewId();
                        int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                        int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromInPlay(viewId, activity, layout);
                            addCardToTrash(movingViewName);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToDiscard(movingViewName, activity, context);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToDeck(movingViewName, activity, context);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(turnMarker).addCardToHand(movingViewName, layout, context,
                                    activity, handListener);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            movingView.setAlpha(1f);
                            return false;
                        } else {
                            movingView.setAlpha(1f);
                            return false;
                        }
                    }
                    if (movingViewType.equals("deck")) {
                        int top = playerList.get(turnMarker).deck.size() - 1;
                        if (top == -1) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "shuffling discard", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, buffer,
                                    cardHeight / 2 + 2 * buffer);
                            toast.show();
                            playerList.get(turnMarker).putDiscardInDeck(activity);
                            playerList.get(turnMarker).setDiscardToDeckView(activity, context);
                            top = playerList.get(turnMarker).deck.size() - 1;
                            if (top <= 0) {
                                toast = Toast.makeText(getApplicationContext(),
                                        "deck and\n discard empty", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, buffer,
                                        cardHeight / 2 + 2 * buffer);
                                toast.show();
                                return false;
                            }
                        }
                        movingViewName = playerList.get(turnMarker).deck.get(top).getCardName();
                        int trashId = trashPile.getImageViewId();
                        int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                        int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromDeck(top, activity);
                            addCardToTrash(movingViewName);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromDeck(top, activity);
                            playerList.get(turnMarker).addCardToDiscard(movingViewName, activity, context);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromDeck(top, activity);
                            playerList.get(turnMarker).addCardToHand(movingViewName, layout, context,
                                    activity, handListener);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            playerList.get(turnMarker).removeCardFromDeck(top, activity);
                            playerList.get(turnMarker).addCardToPlayArea(movingViewName, layout, context,
                                    activity, inPlayListener);
                            undoButton.setClickable(false);
                            undoButton.setAlpha(0.5f);
                            turn.reactToNewCardInPlay(movingViewName, handListener, listenerSwitches);
                        } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                            movingView.setAlpha(1f);
                            return false;
                        } else {
                            movingView.setAlpha(1f);
                            return false;
                        }
                    }
                    if (movingViewType.equals("discard")) {
                        int top = playerList.get(turnMarker).discard.size() - 1;
                        if (top == -1) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "discard is empty", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, buffer,
                                    cardHeight / 2 + 2 * buffer);
                            toast.show();
                            return true;
                        } else {
                            movingViewName = playerList.get(turnMarker).discard.get(top).getCardName();
                            int trashId = trashPile.getImageViewId();
                            int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                            int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                            if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                                playerList.get(turnMarker).removeCardFromDiscard(top, context, activity);
                                addCardToTrash(movingViewName);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                playerList.get(turnMarker).removeCardFromDiscard(top, context, activity);
                                playerList.get(turnMarker).addCardToDeck(movingViewName, activity, context);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                playerList.get(turnMarker).removeCardFromDiscard(top, context, activity);
                                playerList.get(turnMarker).addCardToHand(movingViewName, layout, context,
                                        activity, handListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                                playerList.get(turnMarker).removeCardFromDiscard(top, context, activity);
                                playerList.get(turnMarker).addCardToPlayArea(movingViewName, layout, context,
                                        activity, inPlayListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                                turn.reactToNewCardInPlay(movingViewName, handListener, listenerSwitches);
                            } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                                movingView.setAlpha(1f);
                                return false;
                            } else {
                                movingView.setAlpha(1f);
                                return false;
                            }
                        }
                    }
                    if (movingViewType.equals("trash")) {
                        int top = trash.size() - 1;
                        if (top == -1) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "trash is empty", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP | Gravity.LEFT, buffer,
                                    cardHeight / 2 + 2 * buffer);
                            toast.show();
                            return true;
                        } else {
                            movingViewName = trash.get(top).getCardName();
                            int trashId = trashPile.getImageViewId();
                            int discardId = playerList.get(turnMarker).discardPile.getImageViewId();
                            int deckId = playerList.get(turnMarker).deckPile.getImageViewId();
                            if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                                removeCardFromTrashByIndex(top);
                                playerList.get(turnMarker).addCardToDiscard(movingViewName, activity, context);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                removeCardFromTrashByIndex(top);
                                playerList.get(turnMarker).addCardToDeck(movingViewName, activity, context);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                removeCardFromTrashByIndex(top);
                                playerList.get(turnMarker).addCardToHand(movingViewName, layout, context,
                                        activity, handListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                            } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                                removeCardFromTrashByIndex(top);
                                playerList.get(turnMarker).addCardToPlayArea(movingViewName, layout, context,
                                        activity, inPlayListener);
                                undoButton.setClickable(false);
                                undoButton.setAlpha(0.5f);
                                turn.reactToNewCardInPlay(movingViewName, handListener, listenerSwitches);
                            } else if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                                movingView.setAlpha(1f);
                                return false;
                            } else {
                                movingView.setAlpha(1f);
                                return false;
                            }
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    cmt = (CardMultiTag) v.getTag();
                    targetType = cmt.getCardType();
                    if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).inPlay.size(); i++) {
                            findViewById(playerList.get(turnMarker).inPlay.get(i).getImageViewId()).setAlpha(1f);
                        }
                        findViewById(INPLAY_VIEW_ID).setBackgroundColor(BACKGROUND_COLOR_DARK);
                    }
                    if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                        for (int i = 0; i < playerList.get(turnMarker).hand.size(); i++) {
                            findViewById(playerList.get(turnMarker).hand.get(i).getImageViewId()).setAlpha(1f);
                        }
                    }
                    if (targetType.equals("deck") && listenerSwitches.isDeckDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    if (targetType.equals("discard") && listenerSwitches.isDiscardDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    if (targetType.equals("trash") && listenerSwitches.isTrashDragSwitch()) {
                        v.setAlpha(1f);
                    }
                    if (!event.getResult()) {
                        movingView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (movingView.getVisibility() != View.VISIBLE) {
                                    movingView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                default:
                    break;
            }
            return true;
        }
    }//MyDragListener


    public void refreshInPlay(){
        for (int i = 0; i < playerList.get(turnMarker).inPlay.size(); i++) {
            findViewById(playerList.get(turnMarker).inPlay.get(i).getImageViewId()).setAlpha(1f);
        }
        findViewById(INPLAY_VIEW_ID).setBackgroundColor(BACKGROUND_COLOR_DARK);
    }

    public void refreshDiscard(){
        findViewById(playerList.get(turnMarker).discardPile.getImageViewId()).setAlpha(1f);
    }

    public void refreshDeck(){
        findViewById(playerList.get(turnMarker).deckPile.getImageViewId()).setAlpha(1f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case HAND_BROWSE_LISTENER:
                int listSize = 0;
                int[] chosenIndexList;
                if (data == null) {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                } else {
                    listSize = data.getIntExtra("listSizeKey", listSize);
                    chosenIndexList = data.getIntArrayExtra("indexListKey");
                    for (int i = 0; i < listSize; i++) {
                        int index = chosenIndexList[i];
                        int viewId = playerList.get(turnMarker).hand.get(index).getImageViewId();
                        String cardName = playerList.get(turnMarker).hand.get(index).getCardName();
                        playerList.get(turnMarker).removeCardFromHand(viewId, activity, layout);
                        playerList.get(turnMarker).addCardToPlayArea(cardName, layout, context, activity,
                                inPlayListener);
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                    }
                }
                break;
            case DISCARD_BROWSE_LISTENER:
                boolean cardChosen = false;
                int chosenCardIndex;
                if (data == null) {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                } else {
                    cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                    chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                    if (turn.phase == HARBINGER) {
                        if (cardChosen) {
                            String cardName = playerList.get(turnMarker).discard.get(chosenCardIndex)
                                    .getCardName();
                            playerList.get(turnMarker).removeCardFromDiscard(chosenCardIndex, context,
                                    activity);
                            playerList.get(turnMarker).addCardToDeck(cardName, activity, context);
                            undo = new Undo("moved " + cardName + " to deck", turn,
                                    HARBINGER, handListener, listenerSwitches);
                            undoButton.setClickable(true);
                            undoButton.setAlpha(1f);
                            turn.continueToTurnPhase(listenerSwitches);
                        } else {
                            undo = new Undo("browsed discard", turn, HARBINGER, handListener,
                                    listenerSwitches);
                            undoButton.setClickable(true);
                            undoButton.setAlpha(1f);
                            turn.continueToTurnPhase(listenerSwitches);
                        }
                    }
                }
                break;

            case DECK_BROWSE_LISTENER:
                cardChosen = false;
                if (data == null) {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                } else {
                    cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                    chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                    if (cardChosen) {
                        String cardName = playerList.get(turnMarker).deck.get(chosenCardIndex).getCardName();
                        playerList.get(turnMarker).removeCardFromDeck(chosenCardIndex, activity);
                        playerList.get(turnMarker).addCardToPlayArea(cardName, layout, context, activity,
                                inPlayListener);
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                    }
                }
                break;
            case TRASH_LISTENER:
                cardChosen = false;
                if (data == null) {
                    Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
                } else {
                    cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                    chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                    if (cardChosen) {
                        String cardName = trash.get(chosenCardIndex).getCardName();
                        removeCardFromTrashByIndex(chosenCardIndex);
                        playerList.get(turnMarker).addCardToPlayArea(cardName, layout, context, activity,
                                inPlayListener);
                        undoButton.setClickable(false);
                        undoButton.setAlpha(0.5f);
                        turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                    }
                }
                break;
            case CHANCELLOR_ANSWER_CODE:
                if (resultCode == RESULT_OK) {
                   boolean chancellorChoice = data.getBooleanExtra("chancellorKey", false);
                   if (chancellorChoice){
                       int deckSize = playerList.get(turnMarker).deck.size();
                       undo = new Undo("put deck in discard", turn, deckSize, listenerSwitches);
                       playerList.get(turnMarker).putDeckInDiscard(activity);
                   }
                   turn.continueToTurnPhase(listenerSwitches);
                   undoButton.setClickable(true);
                   undoButton.setAlpha(1f);
                }
                break;
            case LIBRARY_REVEAL_CODE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> drawnCards = data.getStringArrayListExtra("drawnCardsKey");
                    ArrayList<String> discardedCards =data.getStringArrayListExtra("discardedCardsKey");
                    turn.finishLibrary(drawnCards, discardedCards, handListener, listenerSwitches);
                }
                break;
        }


    }// onActivityResult

    public void addCardToBankPile(String cardName){
        int index = 0;
        for (int i = 0; i < bankPiles.size(); i++) {
            String viewName = bankPiles.get(i).getCardName();
            if (viewName.equals(cardName)) index = i;
        }
        ImageView imageView = activity.findViewById(bankPiles.get(index).getImageViewId());
        String text = (String) bankPiles.get(index).getTextView().getText();
        int counter = Integer.parseInt(text);
        if (counter == 0) {
            counter = 1;
            imageView.setVisibility(View.VISIBLE);
            imageView.setClickable(true);
            emptyBankPiles -= 1;
        } else {
            counter += 1;
        }
        text = String.valueOf(counter);
        bankPiles.get(index).getTextView().setText(text);
    }


    public void addCardToTrash(String cardName) {
        trash.add(new CardData(cardName, "trash", trashTally, trash.size()));
        trash.get(trash.size()-1).setImageViewId(trashTally);
        trashTally +=1;
        trashPile.getTextView().setText(String.valueOf(trash.size()));
    }


    public boolean removeCardFromBankPile(String cardName) {
        boolean empty = false;
        int index = 0;
        for (int i = 0; i < bankPiles.size(); i++) {
            String viewName = bankPiles.get(i).getCardName();
            if (viewName.equals(cardName)) index = i;
        }
        ImageView imageView = findViewById(bankPiles.get(index).getImageViewId());
        String text = (String) bankPiles.get(index).getTextView().getText();
        int counter = Integer.parseInt(text);
        if (counter == 0) {
            empty = true;
        } else {
            counter -= 1;

            if (counter == 0) {
                if (cardName.equals("province")) endGame();
                else {
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setClickable(false);
                    emptyBankPiles += 1;
                    if (emptyBankPiles == emptyPilesToEnd) endGame();
                }
            }
        }
        text = String.valueOf(counter);
        bankPiles.get(index).getTextView().setText(text);
        return !empty;
    }//removeCardFromBankPile



    void removeCardFromTrashByIndex(int index){
        trash.remove(index);
        for(int i = index; i < trash.size(); i ++){
            trash.get(i).decreasePosition(1);
        }
        if (trash.size() <= 0) trashTally = 0;
        trashPile.getTextView().setText(String.valueOf(trash.size()));
    }

    public boolean removeCardFromTrashByName(String cardName){
        boolean found = false;
        int index = -1;
        for (int i = trash.size()-1; i >= 0; i--){
            if (trash.get(i).getCardName().equals(cardName)) {
            index = i;
            found = true;
            break;
            }
        }
        if (index >= 0) {
            trash.remove(index);
            for (int i = index; i < trash.size(); i++) {
                trash.get(i).decreasePosition(1);
            }
            if (trash.size() <= 0) trashTally = 0;
            trashPile.getTextView().setText(String.valueOf(trash.size()));
        }
        return found;
    }

    private int getBankPileSize(String cardName){
        int index = 0;
        for (int i = 0; i < bankPiles.size(); i++) {
            String viewName = bankPiles.get(i).getCardName();
            if (viewName.equals(cardName)) index = i;
        }
        String text = (String) bankPiles.get(index).getTextView().getText();
        int size = Integer.parseInt(text);
        return size;
    }

    private void completeImageViewList(ConstraintLayout layout) {
        int viewId = getResources().getIdentifier("copper", "id", getPackageName());
        ImageView view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("copper", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(344, 288, "copper", "60"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("silver", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("silver", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(344, 480, "silver", "40"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("gold", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("gold", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(344, 672, "gold", "30"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("estate", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("estate", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(192, 288, "estate", "12"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("duchy", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("duchy", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(192, 480, "duchy", "12"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("province", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        bankPiles.add(new CardData("province", "bank", 0,0));
        view.setId(BANK_VIEW_ID + bankPileTally);
        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
        bankPileTally +=1;
        view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
        bankPiles.get(bankPiles.size()-1).setTextView(
                makePileSizeCounter(192, 672, "province", "12"));
        bankPiles.get(bankPiles.size()-1).setTextViewId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
        layout.addView(bankPiles.get(bankPiles.size()-1).getTextView());

        viewId = getResources().getIdentifier("trash", "id", getPackageName());
        ImageView imageView = findViewById(viewId);
        imageView.setId(TRASH_VIEW_ID);
        trashPile= new CardData("trash", "trash", 0, 0);
        trashPile.setImageViewId(TRASH_VIEW_ID);
        imageView.setTag(trashPile.getCardMultiTag());
        viewId = getResources().getIdentifier("trash_size", "id", getPackageName());
        trashPile.setTextView((TextView) findViewById(viewId));

    }


    private void layoutHandAndInPlayAreas() {

        //create camouflage hand view
        ImageView handView = new ImageView(this);
        handView.setId(HAND_AREA_VIEW_ID);
        CardMultiTag cmt =new CardMultiTag(0,0,"handZone","hand");
        handView.setTag(cmt);
        handView.setBackgroundColor(BACKGROUND_COLOR_DARK);
        int margin = cardWidth + 2 * buffer + handBuffer;
        int bottomMargin = 3 * buffer;
        int width = screenWidth - 2 * margin;
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, cardHeight);
        params.leftToLeft =ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom =ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(margin,0,0,bottomMargin);
        handView.setLayoutParams(params);
        layout.addView(handView);

        //create camouflage In Play view
        ImageView inPlayView = new ImageView(this);
        inPlayView.setId(INPLAY_VIEW_ID);
        cmt = new CardMultiTag(0, 0, "inPlayZone", "inPlay");
        inPlayView.setTag(cmt);
        inPlayView.setBackgroundColor(BACKGROUND_COLOR_DARK);
        bottomMargin = cardHeight + 4 * buffer;
        params = new ConstraintLayout.LayoutParams(inPlayZoneWidth, cardHeight);
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(inPlayMargin, 0, 0, bottomMargin);
        inPlayView.setLayoutParams(params);
        layout.addView(inPlayView);

        //create in play info zone
    //button
        Button button = findViewById(R.id.phase_button);
        button.setId(PHASE_BUTTON_ID);
    //action stats
        TextView actionView = findViewById(R.id.action_text);
        actionView.setId(ACTIONS_LEFT_ID);
    //coins collected
        TextView coinView = findViewById(R.id.coin_text);
        coinView.setId(COINS_COLLECTED_ID);
    //coin image
        ImageView coinImage = findViewById(R.id.coin_view);
    //buys left
        TextView buyView = findViewById(R.id.buy_text);
        buyView.setId(BUYS_LEFT_ID);
}


    private void layoutActionCards(ConstraintLayout layout, ArrayList<Card> gameCards) {
        GameCardStats gameCardStats = new GameCardStats();
        gameCardStats.calculateGameCardStats(gameCards);
        int viewId;
        ImageView view;
        String cardName = "";
        CardMultiTag viewTag;
        TextView counterView;
        String pileName = "";
        int displacement;
        int gameCardIndex = 0;
        int cost12 = gameCardStats.getCardCostTally()[0] + gameCardStats.getCardCostTally()[1]; //number of action cards that cost 1 or 2
        if (cost12 > 0) {
            viewId = getResources().getIdentifier("cost2", "id", getPackageName());
            view = (ImageView) layout.getViewById(viewId);
            bankPiles.add(new CardData(gameCards.get
                    (gameCardIndex).getName(), "bank", 0,0));
            view.setId(BANK_VIEW_ID + bankPileTally);
            bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
            bankPileTally += 1;
            view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
            pileName = bankPiles.get(bankPiles.size()-1).getCardName();
            int cost2CardLocStart = 248*2; //pixels
            int cost2CardLocTop = 144*2; //pixels
            counterView = makePileSizeCounter(cost2CardLocStart, cost2CardLocTop, pileName, "10");
            counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
            bankPiles.get(bankPiles.size()-1).setTextView(counterView);
            layout.addView(counterView);
            if (cost12 <= 3) displacement = smallCardWidth + 2 * buffer;
            else {
                int availSpace = 3 * smallCardWidth + 120;
                int neededSpace = cost12 * smallCardWidth + 2 * buffer * (cost12 - 1);
                int overlap = (neededSpace - availSpace) / (cost12 - 1);
                displacement = smallCardWidth + 2 * buffer - overlap;
            }

            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < gameCardStats.getCardCostTally()[j]; i++) { //for cards costing 1 or 2
                    gameCardIndex += 1;
                    if (gameCardIndex == 1) {
                        Drawable drawable = getImageDps(this,
                                gameCards.get(0).getName(), smallCardWidth/2 );
                        view.setImageDrawable(drawable);
                    } else {
                        int totalDispX = cost2CardLocStart + (displacement * (i));
                        cardName = gameCards.get(gameCardIndex - 1).getName();
                        bankPiles.add(new CardData(cardName, "bank", 0,0));
                        viewTag = bankPiles.get(bankPiles.size()-1).getCardMultiTag();
                        Drawable drawable = getImageDps(this, cardName, smallCardWidth/2);
                        ImageView newView = makeNewImageView(totalDispX, cost2CardLocTop, viewTag, drawable);
                        newView.setId(BANK_VIEW_ID + bankPileTally);
                        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
                        bankPileTally += 1;
                        pileName = String.valueOf(bankPiles.size()-1);
                        layout.addView(newView);
                        counterView = makePileSizeCounter(totalDispX, cost2CardLocTop, pileName, "10");
                        counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
                        bankPiles.get(bankPiles.size()-1).setTextView(counterView);
                        layout.addView(counterView);
                    }
                }
            }
        }
        int cost3 = gameCardStats.getCardCostTally()[2]; //number of action cards that cost 3
        if (cost3 > 0) {
            viewId = getResources().getIdentifier("cost3", "id", getPackageName());
            view = (ImageView) layout.getViewById(viewId);
            bankPiles.add(new CardData(gameCards.get
                    (gameCardIndex).getName(), "bank", 0,0));
            view.setId(BANK_VIEW_ID + bankPileTally);
            bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
            bankPileTally += 1;
            view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
            pileName = bankPiles.get(bankPiles.size()-1).getCardName();                                                               //first of cost 3
            int cost3CardLocStart = 248*2; //pixels
            int cost3CardLocTop = 240*2; //pixels
            counterView = makePileSizeCounter(cost3CardLocStart, cost3CardLocTop, pileName, "10");
            counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
            bankPiles.get(bankPiles.size()-1).setTextView(counterView);
            layout.addView(counterView);
            if (cost3 <= 3) displacement = smallCardWidth + 2 * buffer;
            else {
                int availSpace = 3 * smallCardWidth + 120;
                int neededSpace = cost3 * smallCardWidth + 2 * buffer * (cost3 - 1);
                int overlap = (neededSpace - availSpace) / (cost3 - 1);
                displacement = smallCardWidth + 2 * buffer - overlap;
            }
            int cost3Index = 0;
            for (int i = 0; i < gameCardStats.getCardCostTally()[2]; i++) { //for cards costing 3
                gameCardIndex += 1;
                cost3Index += 1;
                if (cost3Index == 1) {
                    Drawable drawable = getImageDps(this,
                            gameCards.get(gameCardIndex - 1).getName(), smallCardWidth/2);
                    view.setImageDrawable(drawable);
                } else {
                    int totalDispX = cost3CardLocStart + (displacement * (i));
                    cardName = gameCards.get(gameCardIndex - 1).getName();
                    bankPiles.add(new CardData(cardName, "bank", 0,0));
                    viewTag = bankPiles.get(bankPiles.size()-1).getCardMultiTag();
                    Drawable drawable = getImageDps(this, cardName, smallCardWidth/2);
                    ImageView newView = makeNewImageView(totalDispX, cost3CardLocTop, viewTag, drawable);
                    newView.setId(BANK_VIEW_ID + bankPileTally);
                    bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
                    bankPileTally += 1;
                    pileName = String.valueOf(bankPiles.size()-1);
                    layout.addView(newView);
                    counterView = makePileSizeCounter(totalDispX, cost3CardLocTop, pileName, "10");
                    counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
                    bankPiles.get(bankPiles.size()-1).setTextView(counterView);
                    layout.addView(counterView);

                }
            }
        }
        int cost4 = gameCardStats.getCardCostTally()[3]; //number of action cards that cost 3
        if (cost4 > 0) {
            viewId = getResources().getIdentifier("cost4", "id", getPackageName());
            view = (ImageView) layout.getViewById(viewId);
            bankPiles.add(new CardData(gameCards.get
                    (gameCardIndex).getName(), "bank", 0,0));
            view.setId(BANK_VIEW_ID + bankPileTally);
            bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
            bankPileTally += 1;
            view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
            pileName = bankPiles.get(bankPiles.size()-1).getCardName();                                                                // first of cost 4
            int cost4CardLocStart = 248*2; //pixels
            int cost4CardLocTop = 336*2; //pixels
            counterView = makePileSizeCounter(cost4CardLocStart, cost4CardLocTop, pileName, "10");
            counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
            bankPiles.get(bankPiles.size()-1).setTextView(counterView);
            layout.addView(counterView);
            if (cost4 <= 3) displacement = smallCardWidth + 2 * buffer;
            else {
                int availSpace = 3 * smallCardWidth + 120;
                int neededSpace = cost4 * smallCardWidth + 2 * buffer * (cost4 - 1);
                int overlap = (neededSpace - availSpace) / (cost4 - 1);
                displacement = smallCardWidth + 2 * buffer - overlap;
            }
            int cost4Index = 0;
            for (int i = 0; i < gameCardStats.getCardCostTally()[3]; i++) { //for cards costing 3
                gameCardIndex += 1;
                cost4Index += 1;
                if (cost4Index == 1) {
                    Drawable drawable = getImageDps(this,
                            gameCards.get(gameCardIndex - 1).getName(), smallCardWidth/2);
                    view.setImageDrawable(drawable);
                } else {
                    int totalDispX = cost4CardLocStart + (displacement * (i));
                    cardName = gameCards.get(gameCardIndex - 1).getName();
                    bankPiles.add(new CardData(cardName, "bank", 0,0));
                    viewTag = bankPiles.get(bankPiles.size()-1).getCardMultiTag();
                    Drawable drawable = getImageDps(this, cardName, smallCardWidth/2);
                    ImageView newView = makeNewImageView(totalDispX, cost4CardLocTop, viewTag, drawable);
                    newView.setId(BANK_VIEW_ID + bankPileTally);
                    bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
                    bankPileTally += 1;
                    pileName = String.valueOf(bankPiles.size()-1);
                    layout.addView(newView);
                    counterView = makePileSizeCounter(totalDispX, cost4CardLocTop, pileName, "10");
                    counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
                    bankPiles.get(bankPiles.size()-1).setTextView(counterView);
                    layout.addView(counterView);
                }
            }
        }
        int cost567 = gameCardStats.getCardCostTally()[4] + gameCardStats.getCardCostTally()[5]
                + gameCardStats.getCardCostTally()[6]; //number of action cards that cost 3
        if (cost567 > 0) {
            viewId = getResources().getIdentifier("cost5", "id", getPackageName());
            view = (ImageView) layout.getViewById(viewId);
            bankPiles.add(new CardData(gameCards.get
                    (gameCardIndex).getName(), "bank", 0,0));
            view.setId(BANK_VIEW_ID + bankPileTally);
            bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
            bankPileTally += 1;
            view.setTag(bankPiles.get(bankPiles.size()-1).getCardMultiTag());
            pileName = bankPiles.get(bankPiles.size()-1).getCardName();                                                       // first of cost 5, 6 or 7
            int cost5CardLocStart = 248*2; // pixels
            int cost5CardLocTop = 432*2; //pixels
            counterView = makePileSizeCounter(cost5CardLocStart, cost5CardLocTop, pileName, "10");
            counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
            bankPiles.get(bankPiles.size()-1).setTextView(counterView);
            layout.addView(counterView);
            if (cost567 <= 3) displacement = smallCardWidth + 2 * buffer;
            else {
                int availSpace = 3 * smallCardWidth + 120;
                int neededSpace = cost567 * smallCardWidth + 2 * buffer * (cost567 - 1);
                int overlap = (neededSpace - availSpace) / (cost567 - 1);
                displacement = smallCardWidth + 2 * buffer - overlap;
            }
            int cost567Index = 0;
            for (int j = 4; j < 7; j++) {
                for (int i = 0; i < gameCardStats.getCardCostTally()[j]; i++) { //for cards costing 5,6, or 7
                    gameCardIndex += 1;
                    cost567Index += 1;
                    if (cost567Index == 1) {
                        Drawable drawable = getImageDps(this,
                                gameCards.get(gameCardIndex - 1).getName(), smallCardWidth/2);
                        view.setImageDrawable(drawable);
                    } else {
                        int totalDispX = cost5CardLocStart + (displacement * (cost567Index - 1));
                        cardName = gameCards.get(gameCardIndex - 1).getName();
                        bankPiles.add(new CardData(cardName, "bank", 0,0));
                        viewTag = bankPiles.get(bankPiles.size()-1).getCardMultiTag();
                        Drawable drawable = getImageDps(this, cardName, smallCardWidth/2);
                        ImageView newView = makeNewImageView(totalDispX, cost5CardLocTop, viewTag, drawable);
                        newView.setId(BANK_VIEW_ID + bankPileTally);
                        bankPiles.get(bankPiles.size()-1).setImageViewId(BANK_VIEW_ID + bankPileTally);
                        bankPileTally += 1;
                        pileName = String.valueOf(bankPiles.size()-1);
                        layout.addView(newView);
                        counterView = makePileSizeCounter(totalDispX, cost5CardLocTop, pileName, "10");
                        counterView.setId(BANK_COUNTER_VIEW_ID+bankPileTally-1);
                        bankPiles.get(bankPiles.size()-1).setTextView(counterView);
                        layout.addView(counterView);
                    }
                }
            }
        }
    }


    private void pullOutCards(ArrayList<String> actionCardList) {
        for (int i=0; i<actionCardList.size(); i++){
            String string = actionCardList.get(i);
            Card card = basicCardSet.getCard(string);
            gameCards.add(card);
        }

    }

    private void reorderByCost(ArrayList<Card> gameCard) {
        for (int i = 0; i < (gameCard.size()-1); i++){
            Card temp;
            for (int j = i+1; j < gameCard.size(); j++){
                if (gameCard.get(i).getCost() > gameCard.get(j).getCost()){
                    temp = gameCard.get(i);
                    gameCard.set(i, gameCard.get(j));
                    gameCard.set(j, temp);
                }
            }
        }
    }


    private boolean checkCurse(ArrayList<String> actionCardList) {
        boolean witchChosen = false;
        for (int i = 0; i < actionCardList.size(); i++){
            if (actionCardList.get(i).equals("witch")) {
                witchChosen = true;
                gameCardList.add("curse");
            }
        }
        return witchChosen;
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


    ImageView makeNewImageView(int left, int top, CardMultiTag viewTag, Drawable drawable){
        ImageView view = new ImageView(this);
        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(smallCardWidth, smallCardHeight);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(left, top, 0, 0);
        view.setTag(viewTag);
        view.setImageDrawable(drawable);
        view.setLayoutParams(params);
        return view;
    }


    TextView makePileSizeCounter(int left, int top, String nameTag, String text){
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextAppearance(getApplicationContext(), R.style.appPileCounterText);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.alegreya_sc);
        view.setTypeface(typeface);
        left += 20;
        top += 20;
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(left, top, 0, 0);
        view.setTag(nameTag);
        view.setLayoutParams(params);
        return view;
    }


    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent motionEvent){
            doubleTap = true;
            return true;
        }
        @Override
        public boolean onDown(MotionEvent motionEvent){
            return true;
        }
    }// GestureListener


    public void startNextTurn(){
        turnList.add(new TurnArchive(playerList.get(turnMarker), turn, roundNumber));
        turnMarker +=1;
        if (turnMarker >= playerList.size()) {
            turnMarker = 0;
            roundNumber +=1;
        }
        turn = new Turn(playerList.get(turnMarker), emptyBankPiles, bankPiles, activity, context, layout);
        turnTable(playerList.get(turnMarker));
        turn.startTurn(listenerSwitches);

    }

    private void turnTable(Player newPlayer){
        Drawable drawable;
        newPlayer.displayHand(layout, context, activity, handListener);
        newPlayer.deckPile.getTextView().setText(String.valueOf(newPlayer.deck.size()));
        int discardSize = newPlayer.discard.size();
        if (discardSize == 0) {
            drawable = getImageDps(activity, "back", cardWidth/2);
        } else {
            String topCard = newPlayer.discard.get(discardSize - 1).getCardName();
            drawable = getImageDps(activity, topCard, cardWidth/2);
        }
        ImageView discardView = findViewById(newPlayer.discardPile.getImageViewId());
        discardView.setImageDrawable(drawable);
        switch (playerList.size()){
            case 1:
                break;
            case 2:
                TextView textViewC = findViewById(POSITION_C_NAME_ID);
                ImageView imageViewC = findViewById(POSITION_C_IMAGE_ID);
                TextView textViewFocus = findViewById(FOCUS_HAND_ID);
                int next = turnMarker;
                textViewFocus.setText(playerList.get(next).getName() + " hand");
                next = (turnMarker+1)%2;
                textViewC.setText(playerList.get(next).getName());
                imageViewC.setTag(playerList.get(next));
                break;
            case 3:
                textViewC = findViewById(POSITION_C_NAME_ID);
                TextView textViewD = findViewById(POSITION_D_NAME_ID);
                imageViewC = findViewById(POSITION_C_IMAGE_ID);
                ImageView imageViewD = findViewById(POSITION_D_IMAGE_ID);
                textViewFocus = findViewById(FOCUS_HAND_ID);
                next = turnMarker;
                textViewFocus.setText(playerList.get(next).getName() + " hand");
                next = (turnMarker+1)%3;
                textViewC.setText(playerList.get(next).getName());
                imageViewC.setTag(playerList.get(next));
                next = (turnMarker+2)%3;
                textViewD.setText(playerList.get(next).getName());
                imageViewD.setTag(playerList.get(next));
                break;
            case 4:
                TextView textViewB = findViewById(POSITION_B_NAME_ID);
                textViewC = findViewById(POSITION_C_NAME_ID);
                textViewD = findViewById(POSITION_D_NAME_ID);
                ImageView imageViewB = findViewById(POSITION_B_IMAGE_ID);
                imageViewC = findViewById(POSITION_C_IMAGE_ID);
                imageViewD = findViewById(POSITION_D_IMAGE_ID);
                textViewFocus = findViewById(FOCUS_HAND_ID);
                next = turnMarker;
                textViewFocus.setText(playerList.get(next).getName() + " hand");
                next = (turnMarker+1)%4;
                textViewB.setText(playerList.get(next).getName());
                imageViewB.setTag(playerList.get(next));
                next = (turnMarker+2)%4;
                textViewC.setText(playerList.get(next).getName());
                imageViewC.setTag(playerList.get(next));
                next = (turnMarker+3)%4;
                textViewD.setText(playerList.get(next).getName());
                imageViewD.setTag(playerList.get(next));
                break;
        }
    }


    public void reactToWitch(String playerName){
        boolean cardsLeft = true;
        ArrayList<String> postList = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++){
            if (!playerList.get(i).getName().equals(playerName)){
                String reaction = playerList.get(i).checkForReaction("witch");
                if (!reaction.equals("moat")) {
                    postList.add(playerList.get(i).getName() + " gained a curse.");
                    cardsLeft = removeCardFromBankPile("curse");
                    if (cardsLeft) playerList.get(i).addOffTurnCard("curse", "discard");
                } else {
                    postList.add(playerList.get(i).getName() + " has a moat.");
                }
            }
        }
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putStringArrayListExtra("postListKey", postList);
        startActivity(intent);
    }

    public ArrayList<BanditAttack> reactToBanditAttack(String playerName){
        ArrayList<BanditAttack> banditAttackResults = new ArrayList<>();
        Card[] topCard = new Card[2];
        for (int i = 0; i < playerList.size(); i++){
            boolean[] treasures = {true, true};
            if (!playerList.get(i).getName().equals(playerName)) {
                BanditAttack banditAttack = new BanditAttack(i, playerList.get(i).getName());
                String reaction = playerList.get(i).checkForReaction("bandit");
                if (reaction.equals("moat")) {
                    banditAttack.setBlocked(true);
                } else {
                    int index;
                    if (playerList.get(i).deck.size() > 1)
                        index = playerList.get(i).deck.size() - 1;
                    else if (playerList.get(i).deck.size() == 1) {
                        index = 0;
                        playerList.get(i).putDiscardInDeck(activity);
                    } else {
                        playerList.get(i).putDiscardInDeck(activity);
                        index = playerList.get(i).deck.size() - 1;
                    }
                    if (index >= 0) {
                        topCard[0] = playerList.get(i).deck.get(index).getCard();
                        banditAttack.setCard1(topCard[0].getName());
                        playerList.get(i).removeOffTurnCard(index, "deck");
                        if (!topCard[0].getType().equals("treasure") || topCard[0].getName().equals("copper")) {
                            playerList.get(i).addOffTurnCard(topCard[0].getName(), "discard");
                            treasures[0] = false;
                        }
                        if (playerList.get(i).deck.size() > 0) {
                            index = playerList.get(i).deck.size() - 1;
                            topCard[1] = playerList.get(i).deck.get(index).getCard();
                            banditAttack.setCard2(topCard[1].getName());
                            playerList.get(i).removeOffTurnCard(index, "deck");
                            if (!topCard[1].getType().equals("treasure") || topCard[1].getName().equals("copper")) {
                                playerList.get(i).addOffTurnCard(topCard[1].getName(), "discard");
                                treasures[1] = false;
                            }
                        } else {
                            treasures[1] = false;
                            String toast = "no more cards to reveal";
                            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                            banditAttack.setCard2("");
                        }
                    } else {
                        String toast = "no more cards to reveal";
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                        banditAttack.setCard1("");
                        banditAttack.setCard2("");
                        treasures[1] = false;
                        treasures[0] = false;
                    }
                    if (treasures[0] && treasures[1]) {
                        int value1 = topCard[0].getValue();
                        int value2 = topCard[1].getValue();
                        if (value1 < value2) {
                            banditAttack.setTrashed(0);
                            addCardToTrash(topCard[0].getName());
                            playerList.get(i).addOffTurnCard(topCard[1].getName(), "discard");
                        } else {
                            banditAttack.setTrashed(1);
                            addCardToTrash(topCard[1].getName());
                            playerList.get(i).addOffTurnCard(topCard[0].getName(), "discard");
                        }
                    } else if (treasures[0]) {
                        banditAttack.setTrashed(0);
                        addCardToTrash(topCard[0].getName());
                    } else if (treasures[1]) {
                        banditAttack.setTrashed(1);
                        addCardToTrash(topCard[1].getName());
                    }
                }
                banditAttackResults.add(banditAttack);
            }
        }
        return banditAttackResults;
    }

    public void reactToCouncilRoom(String playerName){
        int size = playerList.size();
        boolean[] cardDrawn = new boolean[size];
        for (int i = 0; i < playerList.size(); i ++){
            if (playerName.equals(playerList.get(i).getName())) {
                cardDrawn[i] = false;
            } else {
                Player player = playerList.get(i);
                if (player.deck.size() == 0){
                    player.putOffTurnDiscardInDeck();
                }
                if (player.deck.size()>0) {
                    String cardName = player.deck.get(player.deck.size() - 1).getCardName();
                    player.addOffTurnCard(cardName, "hand");
                    player.removeOffTurnCard(player.deck.size()-1, "deck");
                    cardDrawn[i] = true;
                } else {
                    String toast = player.getName() + " has no cards to draw";
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                    cardDrawn[i] = false;
                }

            }
        }
        undo = new Undo("other players drew a card", turn, cardDrawn, handListener,
                listenerSwitches);
        undoButton.setClickable(true);
        undoButton.setAlpha(1f);
    }

    public void undoCouncilRoom(boolean[] playerToggles){
        for (int i = 0; i < playerList.size(); i ++){
            Player player = playerList.get(i);
            if (playerToggles[i]) {
                String cardName = player.hand.get(player.hand.size() - 1).getCardName();
                player.addOffTurnCard(cardName, "deck");
                player.removeOffTurnCard(player.hand.size()-1, "hand");
            }
        }
        undoButton.setClickable(false);
        undoButton.setAlpha(0.5f);
    }

    public ArrayList<BureaucratAttack> reactToBureaucratAttack(String playerName){
        ArrayList<BureaucratAttack> bureaucratAttackResults = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++){
            boolean vpCard = false;
            if (!playerList.get(i).getName().equals(playerName)) {
                BureaucratAttack bureaucratAttack = new BureaucratAttack(i, playerList.get(i).getName());
                String reaction = playerList.get(i).checkForReaction("bureaucrat");
                if (reaction.equals("moat")) {
                    bureaucratAttack.setBlocked(true);
                } else {
                    int vpValue = 100;
                    String replacedCard = "";
                    int index = -1;
                    for (int j = 0; j < playerList.get(i).hand.size(); j++) {
                        Card card = playerList.get(i).hand.get(j).getCard();
                        if (card.getType().equals("victory")) {
                            vpCard = true;
                            if (card.getVictoryPoints() < vpValue) {
                                vpValue = card.getVictoryPoints();
                                replacedCard = card.getName();
                                index = j;
                            }
                        }
                    }
                    bureaucratAttack.setCardOnDeck(replacedCard);
                    bureaucratAttack.setVictoryInHand(vpCard);
                    if (index >= 0) {
                        playerList.get(i).removeOffTurnCard(index, "hand");
                        playerList.get(i).addOffTurnCard(replacedCard, "deck");
                    }
                }
                bureaucratAttackResults.add(bureaucratAttack);
            }
        }
        return bureaucratAttackResults;
    }


    public void endGame(){
        ArrayList<String> postList = new ArrayList<>();

        for (int i = 0; i < playerList.size(); i ++){
            Player player = playerList.get(i);
            int totalDeckSize = player.deck.size() + player.hand.size() + player.discard.size()
                    + player.inPlay.size();
            for (int j = 0; j < player.deck.size(); j++){
                Card card = player.deck.get(j).getCard();
                if (card.getName().equals("gardens")){
                    player.setScore(player.getScore() + totalDeckSize/4 );
                } else if (card.getType().equals("victory")){
                    player.setScore(player.getScore() + card.getVictoryPoints() );
                } else if (card.getName().equals("curse")){
                    player.setScore(player.getScore() - 1 );
                }
            }
            for (int j = 0; j < player.hand.size(); j++){
                Card card = player.hand.get(j).getCard();
                if (card.getName().equals("gardens")){
                    player.setScore(player.getScore() + totalDeckSize/4 );
                } else if (card.getType().equals("victory")){
                    player.setScore(player.getScore() + card.getVictoryPoints() );
                } else if (card.getName().equals("curse")){
                    player.setScore(player.getScore() - 1 );
                }
            }
            for (int j = 0; j < player.discard.size(); j++){
                Card card = player.discard.get(j).getCard();
                if (card.getName().equals("gardens")){
                    player.setScore(player.getScore() + totalDeckSize/4 );
                } else if (card.getType().equals("victory")){
                    player.setScore(player.getScore() + card.getVictoryPoints() );
                } else if (card.getName().equals("curse")){
                    player.setScore(player.getScore() - 1 );
                }
            }
            for (int j = 0; j < player.inPlay.size(); j++){
                Card card = player.inPlay.get(j).getCard();
                if (card.getName().equals("gardens")){
                    player.setScore(player.getScore() + totalDeckSize/4 );
                } else if (card.getType().equals("victory")){
                    player.setScore(player.getScore() + card.getVictoryPoints() );
                } else if (card.getName().equals("curse")){
                    player.setScore(player.getScore() - 1 );
                }
            }
        }
        int[] turns = new int[4];
        for (int i = 0; i < turnMarker+1; i++) turns[i] = roundNumber+1;
        for (int i = turnMarker+1; i < 4; i++) turns[i] = roundNumber;
        int n;
        Player tempi;
        Player tempj;
        for (int i = 0; i < playerList.size(); i ++){
            for (int j = i+1; j < playerList.size(); j++){
                if (playerList.get(i).getScore() < playerList.get(j).getScore()) {
                    tempi = playerList.get(i);
                    tempj = playerList.get(j);
                    n = turns[i];
                    playerList.remove(i);
                    playerList.add(i, tempj);
                    playerList.remove(j);
                    turns[i] = turns[j];
                    playerList.add(j, tempi);
                    turns[j] = n;
                }
            }
        }
        String post;
        if (playerList.get(0).getScore() > playerList.get(1).getScore()) {
            post = playerList.get(0).getName() + " won!";
        } else {
            if (turns[0] > turns[1]) {
                tempi = playerList.get(0);
                playerList.remove(0);
                playerList.add(1, tempi);
                post = playerList.get(0).getName() + " won!";
            } else{
                post = playerList.get(0) + " and " + playerList.get(1) + " tied";
            }
        }
        postList.add(post);
        postList.add("");
        for (int i = 0; i < playerList.size(); i++){
            post = playerList.get(i).getName() + " scored " + playerList.get(i).getScore() + " victory points.";
            postList.add(post);
        }

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("postListKey", postList);
        startActivity(intent);
    }
}

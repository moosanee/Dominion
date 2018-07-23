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
    Turn turn;
    int turnMarker = 0;
    int roundNumber = 0;
    int emptyBankPiles = 0;
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

        layout = findViewById(R.id.activity_game_board);
        constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        context = getApplicationContext();
        activity = this;

        layoutHandAndInPlayAreas();
        for (int i = 0; i < playerInfoList.size(); i++){
            playerList.add( new Player(playerInfoList.get(i)));
            playerList.get(i).shufflePile("deck");
            playerList.get(i).drawHand(layout, context, activity, handListener);
        }
        playerList.get(0).layoutDeck(layout, context, activity);
        playerList.get(0).layoutDiscard(layout, context, activity);

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
        for (int i = 0; i < playerList.get(0).hand.size(); i++) {
            ImageView view = findViewById(playerList.get(0).hand.get(i).getImageViewId());
            view.setOnTouchListener(handListener);
            playerList.get(0).hand.get(i).setDragListener(new MyDragListener());
            view.setOnDragListener(playerList.get(0).hand.get(i).getDragListener());
        }
//deck view
        View view = findViewById(playerList.get(0).deckPile.getImageViewId());
        view.setOnTouchListener(deckListener);
        playerList.get(0).deckPile.setDragListener(new MyDragListener());
        view.setOnDragListener(playerList.get(0).deckPile.getDragListener());
//discard view
        view = findViewById(playerList.get(0).discardPile.getImageViewId());
        view.setOnTouchListener(discardListener);
        playerList.get(0).discardPile.setDragListener(new MyDragListener());
        view.setOnDragListener( playerList.get(0).discardPile.getDragListener());
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
                    String opponent = String.valueOf(opponentImageViews.get(FINALI).getTag());
                    Intent intent = new Intent(view.getContext(), OpponentAreaDialogActivity.class);
                    intent.putExtra("opponentKey", opponent);
                    intent.putExtra("viewIdKey", view.getId());
                    startActivityForResult(intent, OPPONENT_LISTENERS + FINALI);
                }
            });
        }
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
                    case BEGIN_TURN: //"finished actions"
                        turn.startBuyingPhase(listenerSwitches);
                        break;
                    case ACTION_PHASE: //"finished actions"
                        turn.startBuyingPhase(listenerSwitches);
                        break;
                    case BUYING_PHASE: //"play all treasures"
                        turn.playAllTreasures(inPlayListener, handListener, listenerSwitches);
                        break;
                    case OPEN_BANK://"finished buying"
                        turn.startCleanUpPhase(listenerSwitches);
                        break;
                    case CLEAN_UP_PHASE: // "clean up"
                        turn.cleanUp(listenerSwitches, handListener);
                        break;
                    case CHAPEL: // "finished trashing"
                        turn.finishChapel(listenerSwitches);
                        break;
                    case POACHER: // "discard card"
                        if (playerList.get(0).hand.size() == 0) {
                            Toast.makeText(context, "no cards in hand to discard",
                                    Toast.LENGTH_SHORT).show();
                            turn.startOpenBankPhase(listenerSwitches);
                        }
                        else Toast.makeText(context,"discard another card from your hand",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ARTISAN1: // "gain card"
                        Toast.makeText(context,"gain a card costing less than 5 to your hand",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ARTISAN2: // "card to deck"
                        Toast.makeText(context,"put a card from your hand onto your deck",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case ADVENTURER: //"apply adventurer"
                        Toast.makeText(context, "putting revealed treasures in hand." +
                                "\ndiscarding other revealed cards", Toast.LENGTH_SHORT).show();
                        turn.finishAdventurer(listenerSwitches, handListener);
                        break;
                }
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
                    intent.putExtra("handPileListKey", playerList.get(0).hand);
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
                    intent.putExtra("inPlayPileListKey", playerList.get(0).inPlay);
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
                    intent.putExtra("deckPileListKey", playerList.get(0).deck);
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
                    intent.putExtra("discardPileListKey", playerList.get(0).discard);
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
                        for (int i = 0; i < playerList.get(0).hand.size(); i++) {
                            findViewById(playerList.get(0).hand.get(i).getImageViewId()).setAlpha(0.5f);
                        }
                    }
                    if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                        for (int i = 0; i < playerList.get(0).inPlay.size(); i++) {
                            findViewById(playerList.get(0).inPlay.get(i).getImageViewId()).setAlpha(0.5f);
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
                        for (int i = 0; i < playerList.get(0).hand.size(); i++) {
                            findViewById(playerList.get(0).hand.get(i).getImageViewId()).setAlpha(1f);
                        }
                    }
                    if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                        for (int i = 0; i < playerList.get(0).inPlay.size(); i++) {
                            findViewById(playerList.get(0).inPlay.get(i).getImageViewId()).setAlpha(1f);
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
                    targetId = v.getId();
                    cmt = (CardMultiTag) v.getTag();
                    targetType = cmt.getCardType();
                    if (movingViewType.equals("bank")) {
                        int pileSize = getBankPileSize(movingViewName);
                        if (pileSize > 0) {
                            int trashId = trashPile.getImageViewId();
                            int discardId = playerList.get(0).discardPile.getImageViewId();
                            int deckId = playerList.get(0).deckPile.getImageViewId();
                            if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                                removeCardFromBankPile(movingViewName);
                                addCardToTrash(movingViewName);
                            } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                                removeCardFromBankPile(movingViewName);
                                playerList.get(0).addCardToDiscard(movingViewName, activity, context);
                                turn.reactToNewCardInDiscard(movingViewName, bankPiles, listenerSwitches);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                removeCardFromBankPile(movingViewName);
                                playerList.get(0).addCardToDeck(movingViewName, activity, context);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                removeCardFromBankPile(movingViewName);
                                playerList.get(0).addCardToHand(movingViewName, layout, context,
                                        activity, handListener);
                                turn.reactToNewCardInHand(movingViewName, listenerSwitches);
                            } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                                removeCardFromBankPile(movingViewName);
                                playerList.get(0).addCardToPlayArea(movingViewName, layout, context,
                                        activity, inPlayListener);
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
                        int discardId = playerList.get(0).discardPile.getImageViewId();
                        int deckId = playerList.get(0).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(0).removeCardFromHand(viewId, activity, layout);
                            addCardToTrash(movingViewName);
                            turn.reactToNewCardInTrash(movingViewName, handListener, listenerSwitches);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(0).removeCardFromHand(viewId, activity, layout);
                            playerList.get(0).addCardToDiscard(movingViewName, activity, context);
                            turn.reactToNewCardInDiscard(movingViewName, bankPiles, listenerSwitches);
                        } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                            playerList.get(0).removeCardFromHand(viewId, activity, layout);
                            playerList.get(0).addCardToDeck(movingViewName, activity, context);
                            turn.reactToNewCardOnDeck(movingViewName, listenerSwitches);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            playerList.get(0).removeCardFromHand(viewId, activity, layout);
                            playerList.get(0).addCardToPlayArea(movingViewName, layout, context,
                                    activity, inPlayListener);
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
                        int discardId = playerList.get(0).discardPile.getImageViewId();
                        int deckId = playerList.get(0).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(0).removeCardFromInPlay(viewId, activity, layout);
                            addCardToTrash(movingViewName);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(0).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(0).addCardToDiscard(movingViewName, activity, context);
                        } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                            playerList.get(0).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(0).addCardToDeck(movingViewName, activity, context);
                        } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                            playerList.get(0).removeCardFromInPlay(viewId, activity, layout);
                            playerList.get(0).addCardToHand(movingViewName, layout, context,
                                    activity, handListener);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            movingView.setAlpha(1f);
                            return false;
                        } else {
                            movingView.setAlpha(1f);
                            return false;
                        }
                    }
                    if (movingViewType.equals("deck")) {
                        int top = playerList.get(0).deck.size() - 1;
                        if (top == -1) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "shuffling discard", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, buffer,
                                    cardHeight / 2 + 2 * buffer);
                            toast.show();
                            playerList.get(0).putDiscardInDeck(activity);
                            playerList.get(0).setDiscardToDeckView(activity, context);
                            top = playerList.get(0).deck.size() - 1;
                            if (top <= 0) {
                                toast = Toast.makeText(getApplicationContext(),
                                        "deck and\n discard empty", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, buffer,
                                        cardHeight / 2 + 2 * buffer);
                                toast.show();
                                return false;
                            }
                        }
                        movingViewName = playerList.get(0).deck.get(top).getCardName();
                        int trashId = trashPile.getImageViewId();
                        int discardId = playerList.get(0).discardPile.getImageViewId();
                        int deckId = playerList.get(0).deckPile.getImageViewId();
                        if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                            playerList.get(0).removeCardFromDeck(top, activity);
                            addCardToTrash(movingViewName);
                        } else if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                            playerList.get(0).removeCardFromDeck(top, activity);
                            playerList.get(0).addCardToDiscard(movingViewName, activity, context);
                        } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                            playerList.get(0).removeCardFromDeck(top, activity);
                            playerList.get(0).addCardToHand(movingViewName, layout, context,
                                    activity, handListener);
                        } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                            playerList.get(0).removeCardFromDeck(top, activity);
                            playerList.get(0).addCardToPlayArea(movingViewName, layout, context,
                                    activity, inPlayListener);
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
                        int top = playerList.get(0).discard.size() - 1;
                        if (top == -1) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "discard is empty", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.RIGHT, buffer,
                                    cardHeight / 2 + 2 * buffer);
                            toast.show();
                            return true;
                        } else {
                            movingViewName = playerList.get(0).discard.get(top).getCardName();
                            int trashId = trashPile.getImageViewId();
                            int discardId = playerList.get(0).discardPile.getImageViewId();
                            int deckId = playerList.get(0).deckPile.getImageViewId();
                            if (targetId == trashId && listenerSwitches.isTrashDragSwitch()) {
                                playerList.get(0).removeCardFromDiscard(top, context, activity);
                                addCardToTrash(movingViewName);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                playerList.get(0).removeCardFromDiscard(top, context, activity);
                                playerList.get(0).addCardToDeck(movingViewName, activity, context);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                playerList.get(0).removeCardFromDiscard(top, context, activity);
                                playerList.get(0).addCardToHand(movingViewName, layout, context,
                                        activity, handListener);
                            } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                                playerList.get(0).removeCardFromDiscard(top, context, activity);
                                playerList.get(0).addCardToPlayArea(movingViewName, layout, context,
                                        activity, inPlayListener);
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
                            int discardId = playerList.get(0).discardPile.getImageViewId();
                            int deckId = playerList.get(0).deckPile.getImageViewId();
                            if (targetId == discardId && listenerSwitches.isDiscardDragSwitch()) {
                                removeCardFromTrash(top);
                                playerList.get(0).addCardToDiscard(movingViewName, activity, context);
                            } else if (targetId == deckId && listenerSwitches.isDeckDragSwitch()) {
                                removeCardFromTrash(top);
                                playerList.get(0).addCardToDeck(movingViewName, activity, context);
                            } else if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                                removeCardFromTrash(top);
                                playerList.get(0).addCardToHand(movingViewName, layout, context,
                                        activity, handListener);
                            } else if (targetType.equals("inPlay") && listenerSwitches.isInPlayDragSwitch()) {
                                removeCardFromTrash(top);
                                playerList.get(0).addCardToPlayArea(movingViewName, layout, context,
                                        activity, inPlayListener);
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
                        for (int i = 0; i < playerList.get(0).inPlay.size(); i++) {
                            findViewById(playerList.get(0).inPlay.get(i).getImageViewId()).setAlpha(1f);
                        }
                        findViewById(INPLAY_VIEW_ID).setBackgroundColor(BACKGROUND_COLOR_DARK);
                    }
                    if (targetType.equals("hand") && listenerSwitches.isHandDragSwitch()) {
                        for (int i = 0; i < playerList.get(0).hand.size(); i++) {
                            findViewById(playerList.get(0).hand.get(i).getImageViewId()).setAlpha(1f);
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
        for (int i = 0; i < playerList.get(0).inPlay.size(); i++) {
            findViewById(playerList.get(0).inPlay.get(i).getImageViewId()).setAlpha(1f);
        }
        findViewById(INPLAY_VIEW_ID).setBackgroundColor(BACKGROUND_COLOR_DARK);
    }

    public void refreshDiscard(){
        findViewById(playerList.get(0).discardPile.getImageViewId()).setAlpha(1f);
    }

    public void refreshDeck(){
        findViewById(playerList.get(0).discardPile.getImageViewId()).setAlpha(1f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (HAND_BROWSE_LISTENER == requestCode) {
            int listSize = 0;
            int[] chosenIndexList;
            if (data == null) {
                Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
            } else {
                listSize = data.getIntExtra("listSizeKey", listSize);
                chosenIndexList = data.getIntArrayExtra("indexListKey");
                for (int i = 0; i < listSize; i++) {
                    int index = chosenIndexList[i];
                    int viewId = playerList.get(0).hand.get(index).getImageViewId();
                    String cardName = playerList.get(0).hand.get(index).getCardName();
                    playerList.get(0).removeCardFromHand(viewId, activity, layout);
                    playerList.get(0).addCardToPlayArea(cardName, layout, context, activity,
                            inPlayListener);
                    turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                }
            }
        }
        if (requestCode == DISCARD_BROWSE_LISTENER) {
            boolean cardChosen = false;
            int chosenCardIndex;
            if (data == null) {
                Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
            } else {
                cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                if (cardChosen) {
                    String cardName = playerList.get(0).discard.get(chosenCardIndex).getCardName();
                    playerList.get(0).removeCardFromDiscard(chosenCardIndex, context, activity);
                    playerList.get(0).addCardToPlayArea(cardName, layout, context, activity,
                            inPlayListener);
                    turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                }
            }
        }

        if (requestCode == DECK_BROWSE_LISTENER) {
            boolean cardChosen = false;
            int chosenCardIndex;
            if (data == null) {
                Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
            } else {
                cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                if (cardChosen) {
                    String cardName = playerList.get(0).deck.get(chosenCardIndex).getCardName();
                    playerList.get(0).removeCardFromDeck(chosenCardIndex, activity);
                    playerList.get(0).addCardToPlayArea(cardName, layout, context, activity,
                            inPlayListener);
                    turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                }
            }
        }
        if (requestCode == TRASH_LISTENER) {
            boolean cardChosen = false;
            int chosenCardIndex;
            if (data == null) {
                Toast.makeText(this, "Intent is Null", Toast.LENGTH_SHORT).show();
            } else {
                cardChosen = data.getBooleanExtra("cardChosenKey", cardChosen);
                chosenCardIndex = data.getIntExtra("chosenCardIndexKey", -1);
                if (cardChosen) {
                    String cardName = trash.get(chosenCardIndex).getCardName();
                    removeCardFromTrash(chosenCardIndex);
                    playerList.get(0).addCardToPlayArea(cardName, layout, context, activity,
                            inPlayListener);
                    turn.reactToNewCardInPlay(cardName, handListener, listenerSwitches);
                }
            }
        }
    }// onActivityResult


    private void addCardToTrash(String cardName) {
        trash.add(new CardData(cardName, "trash", trashTally, trash.size()));
        trash.get(trash.size()-1).setImageViewId(trashTally);
        trashTally +=1;
        trashPile.getTextView().setText(String.valueOf(trash.size()));
    }


    private void removeCardFromBankPile(String cardName) {
        int index = 0;
        for (int i = 0; i < bankPiles.size(); i++) {
            String viewName = bankPiles.get(i).getCardName();
            if (viewName.equals(cardName)) index = i;
        }
        ImageView imageView = findViewById(bankPiles.get(index).getImageViewId());
        String text = (String) bankPiles.get(index).getTextView().getText();
        int counter = Integer.parseInt(text) - 1;
        if (counter == 0) {
            imageView.setVisibility(View.INVISIBLE);
            imageView.setClickable(false);
            emptyBankPiles +=1;
        }
        text = String.valueOf(counter);
        bankPiles.get(index).getTextView().setText(text);
    }//removeCardFromBankPile



    void removeCardFromTrash(int index){
        trash.remove(index);
        for(int i = index; i < trash.size(); i ++){
            trash.get(i).decreasePosition(1);
        }
        if (trash.size() <= 0) trashTally = 0;
        trashPile.getTextView().setText(String.valueOf(trash.size()));
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

        viewId = getResources().getIdentifier("playerB_hand", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        opponentImageViews.add(view);
        viewId = getResources().getIdentifier("playerC_hand", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        opponentImageViews.add(view);
        viewId = getResources().getIdentifier("playerD_hand", "id", getPackageName());
        view = (ImageView) layout.getViewById(viewId);
        opponentImageViews.add(view);

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
        turn.startTurn(listenerSwitches);

    }

    public void reactToWitch(String playerName){
        for (int i = 0; i < playerList.size(); i++){
            if (!playerList.get(i).getName().equals(playerName)){
                String reaction = playerList.get(i).checkForReaction("witch");
                if (!reaction.equals("moat")) {
                    if (i == 0) {
                        playerList.get(i).addCardToHand("curse", layout, context, activity, handListener);
                        Toast.makeText(context, "you gained a curse card", Toast.LENGTH_SHORT).show();
                    } else {
                        String toast = playerList.get(i).getName() + "gained a curse";
                        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                        playerList.get(i).addCard("hand", "curse");
                    }
                }
            }
        }
    }
}

package com.example.dominion;

import android.graphics.Color;

import java.io.Serializable;

public final class MyConstants implements Serializable{
    public final static int screenWidth = 1200; //pixels (nexus 7) = 600 dps
    public final static int screenHeight = 1920; //pixels (nexus 7) == 960 dps
    public final static int cardWidth = 200; // width of a card in pixels
    public final static int cardHeight = 308; // (cardWidth*1.542);
    public final static int xlCardWidth = 800; // width of a card in pixels
    public final static int xlCardHeight = 1232; // (cardWidth*1.542);
    public final static int smallCardWidth = 120; //width of a small card
    public final static int smallCardHeight = 185; // (smallCardWidth*1.542f);
    public final static int minOverlap = 32; // minimum overlap of cards
    public final static int buffer = 16; // minimum generic spacer
    public final static int handBuffer = 32; // extra space to separate the hand from the deck and discard
    public final static int handMargin = cardWidth + 2 * buffer + handBuffer; //space to left of hand area
    public final static int handBottomMargin = buffer * 3;
    public final static int handZoneWidth = screenWidth - 2 * handMargin - 2 * buffer; // adjustment to screen width for unknown reason
    public final static int inPlayMargin = buffer + handBuffer;
    public final static int inPlayBottomMargin = cardHeight + buffer * 4;
    public final static int inPlayZoneWidth = screenWidth - 2 * inPlayMargin - 2 * buffer - cardWidth; // adjustment to screen width for unknown reason
    public final static int browseSideMargin = 20; // pixels
    public final static int browseBottomMargin = 40; // pixels
    public final static int textSize = 20; //sp units
    public final static int emptyPileTextSize = 40; //sp units
    public final static int counterTextSize = 50; //sp units

    public static final int FOCUS_HAND_ID = 585858;
    public static final int POSITION_B_NAME_ID = 585859;
    public static final int POSITION_C_NAME_ID = 585860;
    public static final int POSITION_D_NAME_ID = 585861;
    public static final int POSITION_B_IMAGE_ID = 585862;
    public static final int POSITION_C_IMAGE_ID = 585863;
    public static final int POSITION_D_IMAGE_ID = 585864;
    public static final int OPPONENT_LISTENERS = 470;
    public static final int TRASH_LISTENER = 480;
    public static final int TRASH_VIEW_ID = 989898;
    public static final int PLAYER_HAND_VIEW_ID = 232323;
    public static final int PLAY_AREA_VIEWS_ID = 454545;
    public static final int DECK_PILE_ID = 343434;
    public static final int DECK_CARD_ID = 343435;
    public static final int DISCARD_PILE_ID = 363636;
    public static final int DISCARD_CARD_ID = 363637;
    public static final int BANK_VIEW_ID = 727272;
    public static final int BANK_COUNTER_VIEW_ID = 737373;
    public static final int DISCARD_BROWSE_LISTENER = 492;
    public static final int DECK_BROWSE_LISTENER = 496;
    public static final int HAND_BROWSE_LISTENER = 498;
    public static final int INPLAY_BROWSE_LISTENER = 500;
    public static final int REVEAL_DIALOG_KEY = 300;
    public static final int REVEAL_LAYOUT_ID = 305;
    public static final int REVEALED_CARD_ID = 310;
    public static final int DRAW_BUTTON_ID = 330;
    public static final int PHASE_BUTTON_ID = 604;
    public static final int ACTIONS_LEFT_ID = 600;
    public static final int COINS_COLLECTED_ID = 601;
    public static final int BUYS_LEFT_ID = 602;
    public static final int INPLAY_VIEW_ID = 502;
    public static final int HAND_AREA_VIEW_ID = 504;
    public static final int CARD_CLOSEUP_ACTIVITY_CODE = 1003;
    public static final int BASIC_CARDS_ACTIVITY_CODE = 6;
    public static final int INTRIGUE_CARDS_ACTIVITY_CODE = 7;
    public static final int GAME_BOARD_ACTIVITY_CODE = 8;
    public static final int TESTER_BUTTON_ID = 474747;
    public static final int NOTIFICATION_ACTIVITY_CODE = 9;
    public static final int CHOOSE_CARDS_ACTIVITY_CODE = 0;
    public static final int START_GAME_ACTIVITY_CODE = 1;

    public static final int BEGIN_TURN = 0;
    public static final int ACTION_PHASE = 1;
    public static final int BUYING_PHASE = 2;
    public static final int CLEAN_UP_PHASE = 3;
    public static final int OPEN_BANK = 4;
    public static final int CHAPEL = 10;
    public static final int POACHER = 11;
    public static final int ARTISAN1 = 12;
    public static final int ARTISAN2 = 13;
    public static final int ADVENTURER = 14;
    //public static final int

    public final static int BACKGROUND_COLOR_DARK = Color.parseColor("#363c61");
    public final static int BACKGROUND_COLOR = Color.parseColor("#45508b");
    public final static int ACCENT_COLOR = Color.parseColor("#ffe9be");
    public final static int BLACK_COLOR = Color.parseColor("#000000");

    private static final long serialVersionUID = 100L;
}

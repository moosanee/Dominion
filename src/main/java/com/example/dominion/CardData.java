package com.example.dominion;


import android.widget.TextView;

import java.io.Serializable;

public class CardData implements Serializable{

    private Card card;
    private BasicCards basicCardSet;
    private CardMultiTag cardMultiTag;
    private int imageViewId;
    private GameBoardActivity.MyDragListener dragListener;
    private int position;
    private int number;
    private TextView textView;
    private int textViewId;
    private String group;
    private String cardName;
    private int imageViewLeftMargin;
    private int imageViewRightMargin;
    private int imageViewBottomMargin;
    private int textViewTopMargin;
    private int textViewBottomMargin;
    private int textViewLeftMargin;
    private static final long serialVersionUID = 100L;

    public CardData(String imageName, String group, int position, int number, BasicCards basicCardSet){
        this.position = position;
        this.number = number;
        this.cardName = imageName;
        this.group = group;
        this.cardMultiTag = new CardMultiTag(number, position, imageName, group);
        this.card = basicCardSet.getCard(cardName);
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
        this.textViewId = textView.getId();
    }

    public void setTextViewId(int textViewId) {
        this.textViewId = textViewId;
        this.getTextView().setId(textViewId);
    }

    public void setImageViewId(int id){
        this.imageViewId = id;
    }

    public void setImageViewLeftMargin(int imageViewLeftMargin) {
        this.imageViewLeftMargin = imageViewLeftMargin;
    }

    public void setImageViewRightMargin(int imageViewRightMargin) {
        this.imageViewRightMargin = imageViewRightMargin;
    }

    public void setImageViewBottomMargin(int imageViewBottomMargin) {
        this.imageViewBottomMargin = imageViewBottomMargin;
    }

    public void setPosition(int position) {
        this.position = position;
        this.cardMultiTag.setCardPosition(position);
    }

    public void decreasePosition(int dec){
        this.position -= dec;
        this.cardMultiTag.setCardPosition(this.position);
    }
    public void increasePosition(int inc){
        this.position += inc;
        this.cardMultiTag.setCardPosition(this.position);
    }

    public void setNumber(int number) {
        this.number = number;
        this.cardMultiTag.setCardPosition(number);
    }

    public void setCardMultiTagOnMoveToNewPile(CardData cardData, int index, int number, String newGroup) {
        CardMultiTag cardMultiTag = cardData.getCardMultiTag();
        cardMultiTag.setCardPosition(index);
        cardMultiTag.setCardNumber(number);
        cardMultiTag.setCardType(newGroup);
        this.cardMultiTag = cardMultiTag;
        this.position = index;
        this.number = number;
        this.group = newGroup;
    }

    public void setDragListener(GameBoardActivity.MyDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public GameBoardActivity.MyDragListener getDragListener() {
        return dragListener;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setTextViewBottomMargin(int textViewBottomMargin) {
        this.textViewBottomMargin = textViewBottomMargin;
    }

    public void setTextViewLeftMargin(int textViewLeftMargin) {
        this.textViewLeftMargin = textViewLeftMargin;
    }

    public void setTextViewTopMargin(int textViewTopMargin) {
        this.textViewTopMargin = textViewTopMargin;
    }

    public Card getCard(){
        return card;
    }

    public CardMultiTag getCardMultiTag() {
        return cardMultiTag;
    }

    public int getImageViewId() {
        return imageViewId;
    }

    public int getPosition() {
        return position;
    }

    public int getNumber() {
        return number;
    }

    public TextView getTextView() {
        return textView;
    }

    public int getTextViewId() {
        return textViewId;
    }

    public String getGroup() {
        return group;
    }

    public String getCardName() {
        return cardName;
    }
}

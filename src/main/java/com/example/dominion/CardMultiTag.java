package com.example.dominion;

import java.io.Serializable;

public class CardMultiTag implements Serializable{
    private int cardNumber;
    private int cardPosition;
    private String cardName;
    private String cardType;
    private static final long serialVersionUID = 100L;

    CardMultiTag(int cardNumber, int cardPosition, String cardName, String cardType){
        this.cardName = cardName;
        this.cardPosition = cardPosition;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public int getCardPosition() {
        return cardPosition;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardPosition(int cardPosition) {
        this.cardPosition = cardPosition;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}

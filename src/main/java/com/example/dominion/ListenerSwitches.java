package com.example.dominion;

public class ListenerSwitches {
    private boolean bankListenerSwitch;
    private boolean handListenerSwitch;
    private boolean inPlayListenerSwitch;
    private boolean deckListenerSwitch;
    private boolean discardListenerSwitch;
    private boolean trashListenerSwitch;
    private boolean handDragSwitch;
    private boolean inPlayDragSwitch;
    private boolean deckDragSwitch;
    private boolean discardDragSwitch;
    private boolean trashDragSwitch;

    ListenerSwitches(){
        this.bankListenerSwitch = false;
        this.handListenerSwitch = true;
        this.inPlayListenerSwitch = false;
        this.deckListenerSwitch = false;
        this.discardListenerSwitch = false;
        this.trashListenerSwitch = false;
        this.handDragSwitch = true;
        this.inPlayDragSwitch = true;
        this.deckDragSwitch = false;
        this.discardDragSwitch = false;
        this.trashDragSwitch = false;
    }

    public boolean isBankListenerSwitch() {
        return bankListenerSwitch;
    }

    public boolean isHandListenerSwitch() {
        return handListenerSwitch;
    }

    public boolean isInPlayListenerSwitch() {
        return inPlayListenerSwitch;
    }

    public boolean isDeckListenerSwitch() {
        return deckListenerSwitch;
    }

    public boolean isDiscardListenerSwitch() {
        return discardListenerSwitch;
    }

    public boolean isTrashListenerSwitch() {
        return trashListenerSwitch;
    }

    public boolean isHandDragSwitch() {
        return handDragSwitch;
    }

    public boolean isInPlayDragSwitch() {
        return inPlayDragSwitch;
    }

    public boolean isDeckDragSwitch() {
        return deckDragSwitch;
    }

    public boolean isDiscardDragSwitch() {
        return discardDragSwitch;
    }

    public boolean isTrashDragSwitch() {
        return trashDragSwitch;
    }

    public void setBankListenerSwitch(boolean bankListenerSwitch) {
        this.bankListenerSwitch = bankListenerSwitch;
    }

    public void setHandListenerSwitch(boolean handListenerSwitch) {
        this.handListenerSwitch = handListenerSwitch;
    }

    public void setInPlayListenerSwitch(boolean inPlayListenerSwitch) {
        this.inPlayListenerSwitch = inPlayListenerSwitch;
    }

    public void setDeckListenerSwitch(boolean deckListenerSwitch) {
        this.deckListenerSwitch = deckListenerSwitch;
    }

    public void setDiscardListenerSwitch(boolean discardListenerSwitch) {
        this.discardListenerSwitch = discardListenerSwitch;
    }

    public void setTrashListenerSwitch(boolean trashListenerSwitch) {
        this.trashListenerSwitch = trashListenerSwitch;
    }

    public void setHandDragSwitch(boolean handDragSwitch) {
        this.handDragSwitch = handDragSwitch;
    }

    public void setInPlayDragSwitch(boolean inPlayDragSwitch) {
        this.inPlayDragSwitch = inPlayDragSwitch;
    }

    public void setDeckDragSwitch(boolean deckDragSwitch) {
        this.deckDragSwitch = deckDragSwitch;
    }

    public void setDiscardDragSwitch(boolean discardDragSwitch) {
        this.discardDragSwitch = discardDragSwitch;
    }

    public void setTrashDragSwitch(boolean trashDragSwitch) {
        this.trashDragSwitch = trashDragSwitch;
    }

    public void setAllTrue(){
        this.bankListenerSwitch = true;
        this.handListenerSwitch = true;
        this.inPlayListenerSwitch = true;
        this.deckListenerSwitch = true;
        this.discardListenerSwitch = true;
        this.trashListenerSwitch = true;
        this.handDragSwitch = true;
        this.inPlayDragSwitch = true;
        this.deckDragSwitch = true;
        this.discardDragSwitch = true;
        this.trashDragSwitch = true;
    }

    public void setAllFalse(){
        this.bankListenerSwitch = false;
        this.handListenerSwitch = false;
        this.inPlayListenerSwitch = false;
        this.deckListenerSwitch = false;
        this.discardListenerSwitch = false;
        this.trashListenerSwitch = false;
        this.handDragSwitch = false;
        this.inPlayDragSwitch = false;
        this.deckDragSwitch = false;
        this.discardDragSwitch = false;
        this.trashDragSwitch = false;
    }
}

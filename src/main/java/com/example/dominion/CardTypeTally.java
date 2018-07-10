package com.example.dominion;

import java.io.Serializable;

public class CardTypeTally implements Serializable{
    private String typeName;
    private int typeTally;

    CardTypeTally(String typeName, int typeTally){
        this.typeName = typeName;
        this.typeTally = typeTally;
    }

    public int getTypeTally() {
        return typeTally;
    }
    public void setTypeTally(int tally) {
        this.typeTally = tally;
    }
    public void add1TypeTally() {
        typeTally +=1;
    }

    public String getTypeName() {
        return typeName;
    }


}

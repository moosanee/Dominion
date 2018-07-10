package com.example.dominion;

import java.io.Serializable;

class ExpansionTally implements Serializable{

    private String expansionName;
    private int expansionTally;

    ExpansionTally(String expansionName, int expansionTally){
        this.expansionName = expansionName;
        this.expansionTally = expansionTally;
    }

    public int getExpansionTally() {
        return expansionTally;
    }
    public void setExpansionTally(int tally) {
        this.expansionTally = tally;
    }
    public void add1ExpansionTally() {
        expansionTally +=1;
    }

    public String getExpansionName() {
        return expansionName;
    }


}

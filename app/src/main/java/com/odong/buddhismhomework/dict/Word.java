package com.odong.buddhismhomework.dict;

/**
 * This class is used to store word and its index.
 */
public class Word {

    private String word = "";

    private int index = -1;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
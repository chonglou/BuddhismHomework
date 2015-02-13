package com.odong.buddhismhomework.dict;

/**
 * This class is used to store entries in .idx file.
 */
public class WordEntry {


    private String lowerWord;


    private String word;

    /**
     * position of meaning of this word in ".dict" file.
     */
    private long offset;

    /**
     * length of the meaning of this word in ".dict" file.
     */
    private long size;


    public void setSize(long size) {
        this.size = size;
    }


    public long getSize() {
        return size;
    }


    public void setOffset(long offset) {
        this.offset = offset;
    }


    public long getOffset() {
        return offset;
    }


    public void setWord(String word) {
        this.word = word;
    }


    public String getWord() {
        return word;
    }


    public void setLowerWord(String lowerWord) {
        this.lowerWord = lowerWord;
    }


    public String getLowerWord() {
        return lowerWord;
    }
}

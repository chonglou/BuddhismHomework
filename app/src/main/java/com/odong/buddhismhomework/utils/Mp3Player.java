package com.odong.buddhismhomework.utils;

/**
 * Created by flamen on 15-2-8.
 */
public class Mp3Player {
    public static Mp3Player get(){
        return instance;
    }
    private final static Mp3Player instance = new Mp3Player();

    private Mp3Player(){

    }
    private String filename;
    private boolean play;
}

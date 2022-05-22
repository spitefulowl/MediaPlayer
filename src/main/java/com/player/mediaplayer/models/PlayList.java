package com.player.mediaplayer.models;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayList<T> implements Serializable {
    private String name;
    private ArrayList<T> playList;
    public PlayList(String name, ArrayList<T> playList) {
        this.name = name;
        this.playList = playList;
        if (this.playList == null) {
            this.playList = new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<T> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<T> playList) {
        this.playList = playList;
    }
}

package com.player.mediaplayer.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayList {
    private SimpleStringProperty name = new SimpleStringProperty("");
    private ArrayList<Track> playList;
    public PlayList(String name, ArrayList<Track> playList) {
        this.name.set(name);
        this.playList = playList;
        if (this.playList == null) {
            this.playList = new ArrayList<>();
        }
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ArrayList<Track> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<Track> playList) {
        this.playList = playList;
    }
}

package com.player.mediaplayer.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayList<T> implements Serializable {
    private SimpleStringProperty name = new SimpleStringProperty("");
    private ArrayList<T> playList;
    public PlayList(String name, ArrayList<T> playList) {
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

    public ArrayList<T> getPlayList() {
        return playList;
    }

    public void setPlayList(ArrayList<T> playList) {
        this.playList = playList;
    }
}

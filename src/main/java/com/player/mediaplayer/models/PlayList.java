package com.player.mediaplayer.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayList {
    public static class JsonPlayList {
        public JsonPlayList(PlayList playlist) {
            this.name = playlist.getName().get();
            this.playList = playlist.getPlayList().stream().map(item -> item.getFilePath()).toList();
        }
        public String name;
        public List<String> playList;
    }
    private SimpleStringProperty name = new SimpleStringProperty("");
    private int currentTrackID;
    private ArrayList<Track> playList;
    public PlayList(String name, ArrayList<Track> playList) {
        this.name.set(name);
        this.playList = playList;
        if (this.playList == null) {
            this.playList = new ArrayList<>();
        }
        this.currentTrackID = -1;
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


    public int getCurrentTrackID() {
        return currentTrackID;
    }

    public void setCurrentTrackID(int currentTrackID) {
        this.currentTrackID = currentTrackID;
    }
}

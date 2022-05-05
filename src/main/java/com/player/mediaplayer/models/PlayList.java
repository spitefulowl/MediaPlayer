package com.player.mediaplayer.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayList {
    private ObservableList<MP3Track> playList;

    public ObservableList<MP3Track> getPlayList() {
        return playList;
    }

    public void addMP3Track(MP3Track mp3Track) {
        playList.add(mp3Track);
    }

    public void addMP3Tracks(ObservableList<MP3Track> mp3Tracks) {
        playList.addAll(mp3Tracks);
    }

    public void clearPlayList() {
        playList.clear();
    }

    public PlayList() {
        this.playList = FXCollections.observableArrayList();
    }
}

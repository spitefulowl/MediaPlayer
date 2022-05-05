package com.player.mediaplayer.models;

import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayList {
    private ObservableList<MP3Track> playList;

    private SimpleIntegerProperty selectedSong;

    private SimpleIntegerProperty songToPlay;

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
        this.selectedSong = new SimpleIntegerProperty(0);
        this.songToPlay = new SimpleIntegerProperty(-1);
    }

    public SimpleIntegerProperty getSelectedSong() {
        return selectedSong;
    }

    public void setSelectedSong(int songIndex) {
        selectedSong.set(songIndex);
    }

    public SimpleIntegerProperty getSongToPlay() {
        return songToPlay;
    }

    public void setSongToPlay(int songIndex) {
        songToPlay.set(songIndex);
    }
}

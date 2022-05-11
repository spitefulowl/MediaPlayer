package com.player.mediaplayer.models;

import javafx.scene.image.Image;

public class Track {
    private String songName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;
    private Boolean songLiked = false;
    private String filePath;
    private Image songArtwork;

    public Track(String songName, String songArtist, String songAlbum, String songDuration, String filePath, Image songArtwork) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
        this.filePath = filePath;
        this.songArtwork = songArtwork;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public Boolean getSongLiked() {
        return songLiked;
    }

    public String getFilePath() {
        return filePath;
    }

    public Image getSongArtwork() {
        return songArtwork;
    }

    public void setSongLiked(Boolean newState) {
        songLiked = newState;
    }
}

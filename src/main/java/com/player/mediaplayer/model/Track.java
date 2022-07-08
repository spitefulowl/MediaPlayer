package com.player.mediaplayer.model;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

public class Track implements Serializable {
    private String songName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;
    private Boolean songLiked = false;
    private String filePath;
    private byte[] songArtworkImageArray;

    public Track(String songName, String songArtist, String songAlbum, String songDuration, String filePath, byte[] songArtworkImageArray) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
        this.filePath = filePath;
        this.songArtworkImageArray = songArtworkImageArray;
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
        return songArtworkImageArray != null ? new Image(new ByteArrayInputStream(songArtworkImageArray)) : null;
    }

    public void setSongLiked(Boolean newState) {
        songLiked = newState;
    }
}

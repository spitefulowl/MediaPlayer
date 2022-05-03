package com.player.mediaplayer.models;

public class SongModel {
    private String songName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;
    private String songLiked;

    public SongModel(String songName, String songArtist, String songAlbum, String songDuration, String songLiked) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
        this.songLiked = songLiked;
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

    public String getSongLiked() {
        return songLiked;
    }
}

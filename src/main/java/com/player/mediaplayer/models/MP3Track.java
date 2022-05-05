package com.player.mediaplayer.models;

public class MP3Track {
    private String songName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;

    private String filePath;


    public MP3Track(String songName, String songArtist, String songAlbum, String songDuration, String filePath) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }
}

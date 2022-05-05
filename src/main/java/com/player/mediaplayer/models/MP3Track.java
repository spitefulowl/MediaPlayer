package com.player.mediaplayer.models;

public class MP3Track {
    private String songName;
    private String songArtist;
    private String songAlbum;
    private String songDuration;


    public MP3Track(String songName, String songArtist, String songAlbum, String songDuration) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songAlbum = songAlbum;
        this.songDuration = songDuration;
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
}

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

    public SongModel(MP3Track mp3Track) {
        this.songName = mp3Track.getSongName();
        this.songArtist = mp3Track.getSongArtist();
        this.songAlbum = mp3Track.getSongAlbum();
        this.songDuration = mp3Track.getSongDuration();
        this.songLiked = "n";
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

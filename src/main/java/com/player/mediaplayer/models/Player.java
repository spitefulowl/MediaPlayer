package com.player.mediaplayer.models;

import com.player.mediaplayer.PlayerContext;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Player {
    private final int PLAY_PREVIOUS_THRESHOLD = 3;
    private ObservableList<Track> playList;

    private SimpleIntegerProperty currentTrackID;
    private SimpleDoubleProperty currentVolume;

    private MediaPlayer mediaPlayer = null;

    public ObservableList<Track> getPlayList() {
        return playList;
    }

    public void addTrack(Track mp3Track) {
        playList.add(mp3Track);
    }

    public void addTracks(ObservableList<Track> mp3Tracks) {
        playList.addAll(mp3Tracks);
    }

    public void clearPlayList() {
        playList.clear();
    }

    public Player() {
        this.playList = FXCollections.observableArrayList();
        this.currentTrackID = new SimpleIntegerProperty(-1);
        this.currentVolume = new SimpleDoubleProperty(0.5);
    }

    public SimpleIntegerProperty getCurrentTrackID() {
        return currentTrackID;
    }

    public void setCurrentTrackID(int id) {
        currentTrackID.set(id);
    }

    public SimpleDoubleProperty getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(double volume) {
        currentVolume.set(volume);
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (currentTrackID.get() == -1) {
            throw new IllegalStateException("Nothing to play");
        }
        Track track = playList.get(currentTrackID.get());
        Media media = new Media(track.getFilePath());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(currentVolume.get());
        mediaPlayer.setOnEndOfMedia(() -> {
            PlayerContext.globalTimer.cancel();
            PlayerContext.globalTimer = null;
            next();
        });
        mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        mediaPlayer.pause();
    }

    public void resume() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        mediaPlayer.play();
    }

    public void next() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        if (currentTrackID.get() == playList.size() - 1) {
            currentTrackID.set(0);
        }
        else {
            currentTrackID.set(currentTrackID.get() + 1);
        }
    }

    public Boolean previous() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        if (mediaPlayer.getCurrentTime().toSeconds() < PLAY_PREVIOUS_THRESHOLD && currentTrackID.get() > 0) {
            currentTrackID.set(currentTrackID.get() - 1);
        }
        else {
            currentTrackID.set(currentTrackID.get());
            return false;
        }
        return true;
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        return mediaPlayer;
    }

    public Track getCurrentTrack() {
        if (currentTrackID.get() == -1) {
            throw new IllegalStateException("No track");
        }
        return playList.get(currentTrackID.get());
    }
}

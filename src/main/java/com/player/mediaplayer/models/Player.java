package com.player.mediaplayer.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;

public class Player {
    private final int PLAY_PREVIOUS_THRESHOLD = 3;
    private ObservableList<Track> allTracks;
    private ObservableList<Track> playList;
    private Runnable onEndOfMediaRunnable = null;
    private SimpleIntegerProperty currentTrackID;
    private SimpleDoubleProperty currentVolume;
    private SimpleBooleanProperty isShuffling;
    private SimpleBooleanProperty isRepeating;

    private SimpleBooleanProperty onlyFavorites;
    private SimpleObjectProperty<Predicate<Track>> currentTrackFilter;
    private MediaPlayer mediaPlayer = null;

    public Player() {
        this.allTracks = FXCollections.observableArrayList();
        this.playList = FXCollections.observableArrayList();
        this.currentTrackID = new SimpleIntegerProperty(-1);
        this.currentVolume = new SimpleDoubleProperty(0.5);
        this.isShuffling = new SimpleBooleanProperty(false);
        this.isRepeating = new SimpleBooleanProperty(false);
        this.onlyFavorites = new SimpleBooleanProperty(false);
        this.currentTrackFilter = new SimpleObjectProperty<>(track -> true);
        this.onlyFavorites.addListener((observableValue, aBoolean, t1) -> filterPlayList());
        this.currentTrackFilter.addListener((observableValue, trackPredicate, t1) -> filterPlayList());
    }

    private void filterPlayList() {
        FilteredList<Track> searchedTracks = new FilteredList(allTracks);
        searchedTracks.setPredicate(track -> onlyFavorites.get() ? track.getSongLiked() && currentTrackFilter.get().test(track) : currentTrackFilter.get().test(track));
        setPlayList(searchedTracks);
    }

    public ObservableList<Track> getPlayList() {
        return playList;
    }

    public void addTrack(Track mp3Track) {
        allTracks.add(mp3Track);
    }

    public void setPlayList(List<Track> tracks) {
        playList.clear();
        playList.setAll(tracks);
    }

    public ObservableList<Track> getAllTracks() {
        return allTracks;
    }

    public void setIsShuffling(Boolean isShuffling) {
        this.isShuffling.set(isShuffling);
    }

    public void setIsRepeating(Boolean isRepeating) {
        this.isRepeating.set(isRepeating);
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

    public SimpleObjectProperty<Predicate<Track>> getCurrentTrackFilter() {
        return currentTrackFilter;
    }

    public void setCurrentTrackFilter(Predicate<Track> callable) {
        currentTrackFilter.set(callable);
    }

    public SimpleBooleanProperty getOnlyFavorites() {
        return this.onlyFavorites;
    }

    public void setOnlyFavorites(Boolean onlyFavorites) {
        this.onlyFavorites.set(onlyFavorites);
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
        if (onEndOfMediaRunnable == null) {
            throw new IllegalStateException("On end of media action is not set up");
        }
        mediaPlayer.setOnEndOfMedia(onEndOfMediaRunnable);
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

    public Boolean previous() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        if (mediaPlayer.getCurrentTime().toSeconds() < PLAY_PREVIOUS_THRESHOLD && currentTrackID.get() > 0) {
            currentTrackID.set(currentTrackID.get() - 1);
        } else {
            return false;
        }
        return true;
    }

    private int findNextTrackID() {
        if (isRepeating.get()) {
            return currentTrackID.get();
        }

        int nextTrackID = currentTrackID.get();
        if (isShuffling.get()) {
            while (true) {
                nextTrackID = ThreadLocalRandom.current().nextInt(0, playList.size());
                if (nextTrackID != currentTrackID.get()) {
                    break;
                }
            }
        } else {
            if (currentTrackID.get() >= playList.size() - 1) {
                nextTrackID = 0;
            } else {
                ++nextTrackID;
            }
        }

        return nextTrackID;
    }

    public Boolean next() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        int currentTrackID = getCurrentTrackID().get();
        int nextTrackID = findNextTrackID();
        this.currentTrackID.set(nextTrackID);
        return currentTrackID != nextTrackID;
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

    public void setOnEndOfMedia(Runnable runnable) {
        onEndOfMediaRunnable = runnable;
    }
}

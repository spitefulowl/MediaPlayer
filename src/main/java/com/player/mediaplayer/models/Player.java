package com.player.mediaplayer.models;

import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.controllers.ContentPaneController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Player {
    private final int PLAY_PREVIOUS_THRESHOLD = 3;
    private final String APP_DATA_NAME = "data";
    private ObservableList<Track> allTracks;
    private ObservableList<Track> currentPlayList;
    private ObservableList<PlayList> playLists;
    private Queue<Track> queue = new LinkedList<>();
    private Runnable onEndOfMediaRunnable = null;
    private Runnable onPause = null;
    private Runnable onPlay = null;
    private int currentTrackID;
    private SimpleObjectProperty<Track> currentTrack;
    private SimpleDoubleProperty currentVolume;
    private SimpleBooleanProperty isShuffling;
    private SimpleBooleanProperty isRepeating;

    private SimpleBooleanProperty onlyFavorites;
    private SimpleObjectProperty<Predicate<Track>> currentTrackFilter;
    private PlayerState state = null;
    private MediaPlayer mediaPlayer = null;

    public Player() {
        this.allTracks = FXCollections.observableArrayList();
        this.currentPlayList = FXCollections.observableArrayList();
        this.currentTrackID = -1;
        this.currentVolume = new SimpleDoubleProperty(0.5);
        this.isShuffling = new SimpleBooleanProperty(false);
        this.isRepeating = new SimpleBooleanProperty(false);
        this.onlyFavorites = new SimpleBooleanProperty(false);
        this.currentTrackFilter = new SimpleObjectProperty<>(track -> true);
        this.currentTrack = new SimpleObjectProperty<>();
        this.onlyFavorites.addListener((observableValue, aBoolean, t1) -> filterPlayList());
        this.currentTrackFilter.addListener((observableValue, trackPredicate, t1) -> filterPlayList());
        this.playLists = FXCollections.observableArrayList();
        loadState();
    }

    public PlayerState getLoadedState() {
        return state;
    }

    private void loadState() {
        try
        {
            FileInputStream file = new FileInputStream(APP_DATA_NAME);
            ObjectInputStream in = new ObjectInputStream(file);
            PlayerState state = (PlayerState) in.readObject();
            in.close();
            file.close();
            this.state = state;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    public void applyState() {
        if (state != null) {
            state.initPlayer(this);
            pause();
        }
    }

    public void saveState() {
        try {
            FileOutputStream file = new FileOutputStream(APP_DATA_NAME);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(new PlayerState(this));
            out.close();
            file.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    private void filterPlayList() {
        FilteredList<Track> searchedTracks = new FilteredList(allTracks);
        searchedTracks.setPredicate(track -> onlyFavorites.get() ? track.getSongLiked() && currentTrackFilter.get().test(track) : currentTrackFilter.get().test(track));
        PlayerContext.selectedPlaylist.clear();
        PlayerContext.selectedPlaylist.setAll(searchedTracks);
    }

    public ObservableList<Track> getCurrentPlayList() {
        return currentPlayList;
    }

    public void addTrack(Track mp3Track) {
        allTracks.add(mp3Track);
    }

    public void setCurrentPlayList(List<Track> tracks) {
        currentPlayList.clear();
        currentPlayList.setAll(tracks);
        currentTrackID = 0;
    }

    public ObservableList<Track> getAllTracks() {
        return allTracks;
    }

    public SimpleBooleanProperty getIsShuffling() {
        return isShuffling;
    }

    public void setIsShuffling(Boolean isShuffling) {
        this.isShuffling.set(isShuffling);
    }

    public SimpleBooleanProperty getIsRepeating() {
        return isRepeating;
    }

    public void setIsRepeating(Boolean isRepeating) {
        this.isRepeating.set(isRepeating);
    }

    public SimpleDoubleProperty getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(double volume) {
        currentVolume.set(volume);
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
        if (currentTrackID == -1) {
            throw new IllegalStateException("Nothing to play");
        }
        Track track = currentTrack.get();
        Media media = new Media(track.getFilePath());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(currentVolume.get());
        if (onEndOfMediaRunnable == null) {
            throw new IllegalStateException("On end of media action is not set up");
        }
        mediaPlayer.setOnEndOfMedia(onEndOfMediaRunnable);
        mediaPlayer.setOnPlaying(onPlay);
        mediaPlayer.setOnPaused(onPause);
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
        if (!queue.isEmpty()) {
            return false;
        }
        if (mediaPlayer.getCurrentTime().toSeconds() < PLAY_PREVIOUS_THRESHOLD && currentTrackID > 0) {
            --currentTrackID;
            currentTrack.set(currentPlayList.get(currentTrackID));
            return true;
        }
        return false;
    }

    private void findNextTrack() {
        if (isRepeating.get()) {
            return;
        }

        int nextTrackID = currentTrackID;
        if (isShuffling.get() && !queue.isEmpty()) {
            if (currentPlayList.size() < 2) {
                return;
            }
            do {
                nextTrackID = ThreadLocalRandom.current().nextInt(0, currentPlayList.size());
            } while (nextTrackID == currentTrackID);
        } else {
            if (!queue.isEmpty()) {
                currentTrack.set(queue.poll());
                return;
            }
            if (currentTrackID >= currentPlayList.size() - 1) {
                nextTrackID = 0;
            } else {
                ++nextTrackID;
            }
        }
        currentTrackID = nextTrackID;
        currentTrack.set(currentPlayList.get(currentTrackID));
    }

    public void next() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        findNextTrack();
        play();
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            throw new IllegalStateException("MediaPlayer does not exist");
        }
        return mediaPlayer;
    }

    public SimpleObjectProperty<Track> getCurrentTrack() {
        if (currentTrack == null) {
            throw new IllegalStateException("No track");
        }
        return currentTrack;
    }

    public void setCurrentTrack(Track track) {
        currentTrack.set(track);
    }

    public void setOnEndOfMedia(Runnable runnable) {
        onEndOfMediaRunnable = runnable;
    }

    public Runnable getOnPlay() {
        return onPlay;
    }

    public void setOnPlay(Runnable onPlay) {
        this.onPlay = onPlay;
    }

    public Runnable getOnPause() {
        return onPause;
    }

    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
    }

    public ObservableList<PlayList> getPlayLists() {
        return playLists;
    }

    public void setPlayLists(ArrayList<PlayList> playLists) {
        this.playLists.setAll(playLists);
    }

    public void addToQueue(Track track) {
        queue.add(track);
    }

    public int getCurrentTrackID() {
        return currentTrackID;
    }
    public void setCurrentTrackID(int currentTrackID) {
        this.currentTrackID = currentTrackID;
        this.currentTrack.set(currentPlayList.get(currentTrackID));
    }
}

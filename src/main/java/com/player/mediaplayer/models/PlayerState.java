package com.player.mediaplayer.models;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.utils.MP3Parser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.function.Function;

public class PlayerState implements Serializable {
    private class SerializableTrack implements Serializable {
        private String path;
        private Boolean songLiked;
        public SerializableTrack(String path, Boolean songLiked) {
            this.path = path;
            this.songLiked = songLiked;
        }

        public String getPath() {
            return path;
        }

        public Boolean getSongLiked() {
            return songLiked;
        }
    }

    public ArrayList<SerializableTrack> allTracks;
    public Integer currentTrackID;
    public Double currentVolume;
    public Boolean isShuffling;
    public Boolean isRepeating;
    public PlayerState(Player player) {
        allTracks = new ArrayList<>(player.getAllTracks().stream().map(o -> new SerializableTrack(o.getFilePath(), o.getSongLiked())).toList());
        currentTrackID = player.getCurrentTrackID().get();
        currentVolume = player.getCurrentVolume().get();
        isShuffling = player.getIsShuffling().get();
        isRepeating = player.getIsRepeating().get();
    }
    public void initPlayer(Player player) {
        Function<SerializableTrack, Track> func = item -> {
            try {
                Track track = MP3Parser.parse(new File(URI.create(item.getPath())));
                track.setSongLiked(item.getSongLiked());
                return track;
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        };
        player.getAllTracks().setAll(allTracks.stream().map(func).toList());
        player.getCurrentPlayList().setAll(allTracks.stream().map(func).toList());
        player.setCurrentTrackID(currentTrackID);
        player.setIsShuffling(isShuffling);
        player.setIsRepeating(isRepeating);
    }
}

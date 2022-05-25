package com.player.mediaplayer.models;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public HashMap<String, ArrayList<Integer>> playlistMapping;
    public Integer currentTrackID;
    public Double currentVolume;
    public Boolean isShuffling;
    public Boolean isRepeating;
    public PlayerState(Player player) {
        allTracks = new ArrayList<>(player.getAllTracks().stream().map(o -> new SerializableTrack(o.getFilePath(), o.getSongLiked())).toList());
        currentTrackID = player.getCurrentTrackID();
        currentVolume = player.getCurrentVolume().get();
        isShuffling = player.getIsShuffling().get();
        isRepeating = player.getIsRepeating().get();
        playlistMapping = new HashMap<>();
        for (PlayList playlist : player.getPlayLists()) {
            if (playlistMapping.get(playlist.getName().get()) == null) {
                playlistMapping.put(playlist.getName().get(), new ArrayList<>());
            }
            ArrayList<Integer> currentPlaylist = playlistMapping.get(playlist.getName().get());
            for (Track track : playlist.getPlayList()) {
                currentPlaylist.add(player.getAllTracks().indexOf(track));
            }
        }
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
        List<Track> trackList = allTracks.stream().map(func).toList();
        PlayerContext.selectedPlaylist.setAll(trackList);
        for (var entry : playlistMapping.entrySet()) {
            ArrayList<Track> currentPlaylist = new ArrayList<>();
            for (Integer trackID : entry.getValue()) {
                currentPlaylist.add(trackList.get(trackID));
            }
            player.getPlayLists().add(new PlayList(entry.getKey(), currentPlaylist));
        }
        player.getAllTracks().setAll(trackList);
        player.getCurrentPlayList().setAll(trackList);
        player.setCurrentTrackID(currentTrackID);
        player.setIsShuffling(isShuffling);
        player.setIsRepeating(isRepeating);
    }
}

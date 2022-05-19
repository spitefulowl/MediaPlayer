package com.player.mediaplayer.models;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.utils.MP3Parser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

public class PlayerState implements Serializable {
    public ArrayList<String> allTracks;
    public ArrayList<String> currentPlayList;
    public Integer currentTrackID;
    public Double currentVolume;
    public Boolean isShuffling;
    public Boolean isRepeating;
    public PlayerState(Player player) {
        allTracks = new ArrayList<>(player.getAllTracks().stream().map(Track::getFilePath).toList());
        currentPlayList = new ArrayList<>(player.getCurrentPlayList().stream().map(Track::getFilePath).toList());
        currentTrackID = player.getCurrentTrackID().get();
        currentVolume = player.getCurrentVolume().get();
        isShuffling = player.getIsShuffling().get();
        isRepeating = player.getIsRepeating().get();
    }
    public void initPlayer(Player player) {
        player.getAllTracks().setAll(allTracks.stream().map(item -> {
            try {
                return MP3Parser.parse(new File(URI.create(item)));
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        }).toList());
        player.getCurrentPlayList().setAll(currentPlayList.stream().map(item -> {
            try {
                return MP3Parser.parse(new File(URI.create(item)));
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        }).toList());
        player.setCurrentTrackID(currentTrackID);
        player.setIsShuffling(isShuffling);
        player.setIsRepeating(isRepeating);
    }
}

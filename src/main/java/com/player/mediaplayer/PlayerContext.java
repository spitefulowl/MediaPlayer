package com.player.mediaplayer;

import com.player.mediaplayer.model.Player;
import com.player.mediaplayer.model.Track;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Timer;

public class PlayerContext {
    public static Timer globalTimer = null;
    public static ObservableList<Track> selectedPlaylist;
    public static List<Track> selectedPlaylistRef = null;
    public static SimpleStringProperty selectedPlaylistName = new SimpleStringProperty("All tracks");
    public final static Player player = new Player();
    private PlayerContext() {}
}

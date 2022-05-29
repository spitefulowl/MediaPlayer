package com.player.mediaplayer;

import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Timer;

public class PlayerContext {
    public static Timer globalTimer = null;
    public static ObservableList<Track> selectedPlaylist;
    public static SimpleStringProperty selectedPlaylistName = new SimpleStringProperty("All tracks");
    public final static Player player = new Player();
    private PlayerContext() {}
}

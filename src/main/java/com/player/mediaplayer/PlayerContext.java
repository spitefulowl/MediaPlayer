package com.player.mediaplayer;

import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.util.Timer;

public class PlayerContext {
    private final static PlayerContext instance = new PlayerContext();
    public static Timer globalTimer = null;
    public static ObservableList<Track> selectedPlaylist;
    private PlayerContext() {}
    public static PlayerContext getInstance() {
        return instance;
    }
    private Player player = new Player();
    public Player getPlayer() {
        return player;
    }
}

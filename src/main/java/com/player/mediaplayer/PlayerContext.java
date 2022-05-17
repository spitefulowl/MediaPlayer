package com.player.mediaplayer;

import com.player.mediaplayer.models.Player;

import java.util.Timer;

public class PlayerContext {
    private final static PlayerContext instance = new PlayerContext();
    public static Timer globalTimer = null;
    private PlayerContext() {}
    public static PlayerContext getInstance() {
        return instance;
    }
    private Player player = new Player();
    public Player getPlayer() {
        return player;
    }
}

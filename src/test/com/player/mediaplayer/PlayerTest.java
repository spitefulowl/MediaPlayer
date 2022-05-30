package com.player.mediaplayer;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.PlayerState;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerTest {
    @InjectMocks
    private Player player;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetOfCurrentPlayList() {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(new Track("Yesterday", "Beatles", "", "200", "yesterday.mp3", null));
        player.setCurrentPlayList(tracks);

        //
        // When
        //
        ObservableList<Track> currentTracks = player.getCurrentPlayList();

        //
        // Then
        //
        assert(
                currentTracks.size() == 1 &&
                        Objects.equals(currentTracks.get(0).getSongName(), "Yesterday") &&
                        Objects.equals(currentTracks.get(0).getSongArtist(), "Beatles") &&
                        Objects.equals(currentTracks.get(0).getSongDuration(), "200") &&
                        Objects.equals(currentTracks.get(0).getFilePath(), "yesterday.mp3") &&
                        currentTracks.get(0).getSongArtwork() == null
        );
    }

    @Test
    public void testPlayerState() throws InvalidDataException, UnsupportedTagException, IOException {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track track = MP3Parser.parse(new File("src/test/com/player/mediaplayer/resources/test.mp3"));
        tracks.add(track);
        PlayerContext.selectedPlaylist = FXCollections.observableArrayList(player.getCurrentPlayList());
        player.addTrack(track);
        player.setCurrentPlayList(tracks);
        player.setCurrentTrackID(0);
        player.setIsRepeating(true);
        player.setIsShuffling(true);
        player.setCurrentVolume(15);

        //
        // When
        //
        player.saveState();
        Player newPlayer = new Player();
        try {
            newPlayer.applyState();
        } catch (IllegalStateException e) { }
        PlayerState newState = newPlayer.getLoadedState();

        //
        // Then
        //
        assert(
                newState.allTracks.size() == tracks.size() &&
                        newState.isRepeating &&
                        newState.isShuffling &&
                        Objects.equals(newState.currentVolume, 15.0)
        );
    }
}

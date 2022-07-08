package com.player.mediaplayer;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.model.Player;
import com.player.mediaplayer.model.PlayerState;
import com.player.mediaplayer.model.Track;
import com.player.mediaplayer.utils.MP3Parser;
import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Queue;

@RunWith(JfxRunner.class)
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

    @Test
    public void testPlayListFilter() throws InvalidDataException, UnsupportedTagException, IOException {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track firstTrack = MP3Parser.parse(new File("src/test/com/player/mediaplayer/resources/test.mp3"));
        Track secondTrack = new Track("Yesterday", "Beatles", "", "200", "yesterday.mp3", null);
        PlayerContext.selectedPlaylist = FXCollections.observableArrayList(player.getCurrentPlayList());
        tracks.add(firstTrack);
        tracks.add(secondTrack);
        player.addTrack(firstTrack);
        player.addTrack(secondTrack);
        player.setCurrentPlayList(tracks);

        //
        // When
        //
        player.setCurrentTrackFilter(track -> (track.getSongName().toLowerCase().contains("yesterday")));

        //
        // Then
        //
        Track filteredTrack = PlayerContext.selectedPlaylist.get(0);
        assert(
                PlayerContext.selectedPlaylist.size() == 1 &&
                        Objects.equals(filteredTrack.getSongName(), "Yesterday")
        );
    }

    @Test
    public void testFavoritesSong() throws InvalidDataException, UnsupportedTagException, IOException {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track firstTrack = MP3Parser.parse(new File("src/test/com/player/mediaplayer/resources/test.mp3"));
        Track secondTrack = new Track("Yesterday", "Beatles", "", "200", "yesterday.mp3", null);
        PlayerContext.selectedPlaylist = FXCollections.observableArrayList(player.getCurrentPlayList());
        tracks.add(firstTrack);
        tracks.add(secondTrack);
        player.addTrack(firstTrack);
        player.addTrack(secondTrack);
        player.setCurrentPlayList(tracks);

        //
        // When
        //
        PlayerContext.selectedPlaylist.get(1).setSongLiked(true);
        player.setOnlyFavorites(true);

        //
        // Then
        //
        Track favoriteSong = PlayerContext.selectedPlaylist.get(0);
        assert(
                PlayerContext.selectedPlaylist.size() == 1 &&
                        Objects.equals(favoriteSong.getSongName(), "Yesterday") &&
                        player.getOnlyFavorites().get()
        );
    }

    @Test
    public void testPlayMedia() throws InvalidDataException, UnsupportedTagException, IOException {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track firstTrack = MP3Parser.parse(new File("src/test/com/player/mediaplayer/resources/test.mp3"));
        PlayerContext.selectedPlaylist = FXCollections.observableArrayList(player.getCurrentPlayList());
        tracks.add(firstTrack);
        player.addTrack(firstTrack);
        player.setCurrentPlayList(tracks);
        player.setCurrentTrack(firstTrack);
        player.setCurrentTrackID(0);
        player.setOnEndOfMedia(() -> {});

        //
        // When
        //
        player.play();
        player.pause();
        player.resume();
        player.next();
        player.previous();

        //
        // Then
        //
        assert(
                player.getMediaPlayer().getVolume() == 0.5 &&
                        player.getCurrentTrack().get() == firstTrack
        );
    }

    @Test
    public void testQueue() {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track firstTrack = new Track("Twist And Shout", "Beatles", "", "250", "yesterday.mp3", null);
        Track secondTrack = new Track("Yesterday", "Beatles", "", "200", "yesterday.mp3", null);
        tracks.add(firstTrack);
        tracks.add(secondTrack);
        player.setCurrentPlayList(tracks);

        //
        // When
        //
        player.addToQueue(secondTrack);
        Queue<Track> queue = player.getQueue();

        //
        // Then
        //
        Track trackFromQueue = queue.peek();
        assert(trackFromQueue == secondTrack);
    }
}
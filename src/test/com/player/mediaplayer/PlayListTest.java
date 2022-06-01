package com.player.mediaplayer;

import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Track;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

public class PlayListTest {

    @Test
    public void testPlayList() {

        //
        // Given
        //
        ArrayList<Track> tracks = new ArrayList<>();
        Track firstTrack = new Track("Twist And Shout", "Beatles", "", "250", "yesterday.mp3", null);
        Track secondTrack = new Track("Yesterday", "Beatles", "", "200", "yesterday.mp3", null);
        tracks.add(firstTrack);
        tracks.add(secondTrack);
        PlayList playList = new PlayList("testPlayList", tracks);

        //
        // When
        //
        playList.setName("testList");
        Track thirdTrack = new Track("Strawberry fields", "Beatles", "", "280", "yesterday.mp3", null);
        tracks.add(thirdTrack);
        playList.setPlayList(tracks);
        playList.setCurrentTrackID(0);

        //
        // Then
        //
        assert(
                Objects.equals(playList.getName().get(), "testList") &&
                        playList.getPlayList() == tracks &&
                        playList.getPlayList().size() == 3 &&
                        playList.getCurrentTrackID() == 0
        );
    }
}

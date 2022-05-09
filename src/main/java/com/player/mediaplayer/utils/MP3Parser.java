package com.player.mediaplayer.utils;

import com.mpatric.mp3agic.*;
import com.player.mediaplayer.models.Track;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;


public class MP3Parser {
    private final static int MINUTES_IN_HOUR = 60;
    private final static int SECONDS_IN_MINUTE = 60;

    public static Track parse(File file) throws InvalidDataException, UnsupportedTagException, IOException {

        Mp3File mp3File = new Mp3File(file);

        ID3v1 tag;
        if (mp3File.hasId3v2Tag()) {
            tag = mp3File.getId3v2Tag();
        } else {
            tag = mp3File.getId3v1Tag();
        }

        Image artwork = null;
        if (mp3File.hasId3v2Tag()) {
            byte[] array = mp3File.getId3v2Tag().getAlbumImage();
            if (array != null) {
                artwork = new Image(new ByteArrayInputStream(mp3File.getId3v2Tag().getAlbumImage()));
            }
        }

        int length = (int) Math.ceil(mp3File.getLengthInSeconds());

        Track mp3Track = new Track(
                parseTitle(file, tag.getTitle()),
                tag.getArtist(),
                tag.getAlbum(),
                parseSongLength(length),
                file.toURI().toString(),
                artwork
        );

        return mp3Track;
    }

    public static String parseSongLength(int length) {
        int seconds = length % SECONDS_IN_MINUTE;
        int totalMinutes = length / SECONDS_IN_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_HOUR;
        int hours = totalMinutes / MINUTES_IN_HOUR;

        String time;
        if (hours == 0) {
            time = String.format("%d:%2d", minutes, seconds);
        } else {
            time = String.format("%d:%2d:%2d", hours, minutes, seconds);
        }
        return time.replaceAll(" ", "0");
    }

    private static String parseTitle(File file, String title) {
        if (title == null || title.isEmpty()) {
            return file.getName().split("\\.mp3")[0];
        } else return title;
    }

}

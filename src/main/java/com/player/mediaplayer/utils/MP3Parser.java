package com.player.mediaplayer.utils;

import com.mpatric.mp3agic.*;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.SongModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class MP3Parser {
    private final static int MINUTES_IN_HOUR = 60;
    private final static int SECONDS_IN_MINUTE = 60;

    public static MP3Track parse(File file) throws InvalidDataException, UnsupportedTagException, IOException {

        Mp3File mp3File = new Mp3File(file);

        ID3v1 tag;
        if (mp3File.hasId3v1Tag()) {
            tag = mp3File.getId3v1Tag();
        } else {
            tag = (ID3v2) mp3File.getId3v2Tag();
        }

        int length = (int) Math.ceil(mp3File.getLengthInSeconds());

        MP3Track mp3Track = new MP3Track(
                parseTitle(file, tag.getTitle()),
                tag.getArtist(),
                tag.getAlbum(),
                parseSongLength(length)
        );

        return mp3Track;
    }

    private static String parseSongLength(int length) {
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
            return file.getName();
        } else return title;
    }

}

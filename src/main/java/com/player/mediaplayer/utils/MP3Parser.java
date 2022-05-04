package com.player.mediaplayer.utils;

import com.mpatric.mp3agic.*;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.SongModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class MP3Parser {
    final static String EXTENSION = ".mp3";

    public MP3Track parse(File file) throws InvalidDataException, UnsupportedTagException, IOException {

        Mp3File mp3File = new Mp3File(file);

        ID3v1 tag;
        if (mp3File.hasId3v1Tag()) {
            tag = mp3File.getId3v1Tag();
        } else {
            tag = (ID3v2) mp3File.getId3v2Tag();
        }

        int length = (int) Math.ceil(mp3File.getLengthInSeconds());

        MP3Track mp3Track = new MP3Track(
                tag.getTitle(),
                tag.getArtist(),
                tag.getAlbum(),
                length
        );

        return mp3Track;
    }

}

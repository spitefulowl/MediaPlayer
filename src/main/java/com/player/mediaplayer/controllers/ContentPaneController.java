package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.SongModel;

import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ContentPaneController implements Initializable {
    private final PlayList playList;
    public TextField songSearchField;
    public TableView songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;

    public ContentPaneController(PlayList playList) {
        this.playList = playList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songName.setCellValueFactory(new PropertyValueFactory<>("SongName"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("SongArtist"));
        songAlbum.setCellValueFactory(new PropertyValueFactory<>("SongAlbum"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("SongDuration"));
        songLiked.setCellValueFactory(new PropertyValueFactory<>("SongLiked"));

        observePlayList();
    }

    private void observePlayList() {

        playList.getPlayList().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                songsListTable.getItems().clear();
                for (MP3Track mp3Track : playList.getPlayList()) {
                    try {
                        addToSongListTable(mp3Track);
                    } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void onDragExited(DragEvent dragEvent) throws InvalidDataException, UnsupportedTagException, IOException {

        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            for(File file : dragboard.getFiles()) {
                playList.addMP3Track(MP3Parser.parse(file));
            }
        }
    }

    public void addToSongListTable(MP3Track mp3Track) throws InvalidDataException, UnsupportedTagException, IOException {


        songsListTable.getItems().add(new SongModel(mp3Track));
    }
}

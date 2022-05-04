package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.SongModel;

import com.player.mediaplayer.utils.MP3Parser;
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

    public TextField songSearchField;
    public TableView songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;

    public void onEnterPressed(KeyEvent keyEvent) throws URISyntaxException, InvalidDataException, UnsupportedTagException, IOException {

        File file = new File(Objects.requireNonNull(getClass().getResource("/com/player/mediaplayer/images/Deep_Purple_-_Smoke_On_The_Water_(musmore.com).mp3")).toURI());

        addToSongListTable(file);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songName.setCellValueFactory(new PropertyValueFactory<>("SongName"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("SongArtist"));
        songAlbum.setCellValueFactory(new PropertyValueFactory<>("SongAlbum"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("SongDuration"));
        songLiked.setCellValueFactory(new PropertyValueFactory<>("SongLiked"));
    }

    public void onDragExited(DragEvent dragEvent) throws InvalidDataException, UnsupportedTagException, IOException {

        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            for(File file : dragboard.getFiles()) {
                addToSongListTable(file);
            }
        }
    }

    private void addToSongListTable(File file) throws InvalidDataException, UnsupportedTagException, IOException {

        MP3Track mp3Track = MP3Parser.parse(file);

        songsListTable.getItems().add(new SongModel(mp3Track));
    }
}

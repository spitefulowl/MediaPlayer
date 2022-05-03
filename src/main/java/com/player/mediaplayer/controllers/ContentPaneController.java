package com.player.mediaplayer.controllers;

import com.player.mediaplayer.models.SongModel;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class ContentPaneController implements Initializable {

    public TextField songSearchField;
    public TableView songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;

    public void onEnterPressed(KeyEvent keyEvent) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songName.setCellValueFactory(new PropertyValueFactory<>("SongName"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("SongArtist"));
        songAlbum.setCellValueFactory(new PropertyValueFactory<>("SongAlbum"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("SongDuration"));
        songLiked.setCellValueFactory(new PropertyValueFactory<>("SongLiked"));
    }
}

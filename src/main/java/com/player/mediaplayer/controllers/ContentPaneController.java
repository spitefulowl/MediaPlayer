package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.SongModel;

import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

import java.io.Console;
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

    // TODO: 05.05.2022 remove call technologies
    private int previousIndex = -1;

    public ContentPaneController(PlayList playList) {
        this.playList = playList;
    }

    public void onEnterPressed(KeyEvent keyEvent) throws URISyntaxException, InvalidDataException, UnsupportedTagException, IOException {

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songName.setCellValueFactory(new PropertyValueFactory<>("SongName"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("SongArtist"));
        songAlbum.setCellValueFactory(new PropertyValueFactory<>("SongAlbum"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("SongDuration"));
        songLiked.setCellValueFactory(new PropertyValueFactory<>("SongLiked"));

        observePlayList();
        //elementClickHandler();
        selectedElementIndex();
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

    public void selectedElementIndex() {
        songsListTable.setOnMouseClicked(mouseEvent -> {
            int index = songsListTable.getSelectionModel().getSelectedIndex();
            playList.setSelectedSong(index);
            // TODO: 05.05.2022 remove call technologies
            if (index == previousIndex) {
                playList.setSongToPlay(index);
                previousIndex = -1;
            }
            previousIndex = index;
        });
    }

    // TODO: 05.05.2022 make it work
    public void elementClickHandler() {
        TableRow<SongModel> row = new TableRow<>();
        songsListTable.setRowFactory(tableView -> {
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getButton()== MouseButton.PRIMARY) {
                    if (mouseEvent.getClickCount() == 2) {
                        playList.setSongToPlay(row.getIndex());
                    }
                    playList.setSelectedSong(row.getIndex());
                }
            });
            return row;
        });
    }
}

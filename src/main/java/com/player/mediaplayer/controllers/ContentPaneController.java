package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;

import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContentPaneController implements Initializable {
    private final Player player = PlayerContext.getInstance().getPlayer();
    public TextField songSearchField;
    public TableView songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;

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
        elementClickHandler();
    }

    private void observePlayList() {

        player.getPlayList().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                songsListTable.getItems().clear();
                for (Track track : player.getPlayList()) {
                    try {
                        addToSongListTable(track);
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
                player.addTrack(MP3Parser.parse(file));
            }
        }
    }

    public void addToSongListTable(Track track) throws InvalidDataException, UnsupportedTagException, IOException {
        songsListTable.getItems().add(track);
    }

    public void elementClickHandler() {
        songsListTable.setRowFactory(tableView -> {
            TableRow<Track> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if (mouseEvent.getClickCount() == 2) {
                        player.setCurrentTrackID(row.getIndex());
                    }
                }
            });
            return row;
        });
    }
}

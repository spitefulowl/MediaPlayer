package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContentPaneController implements Initializable {
    private final Player player = PlayerContext.getInstance().getPlayer();
    public CustomTextField songSearchField;
    public TableView songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;
    public Label tableLabel;
    public TableColumn songNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songName.setCellValueFactory(new PropertyValueFactory<>("SongName"));
        songArtist.setCellValueFactory(new PropertyValueFactory<>("SongArtist"));
        songAlbum.setCellValueFactory(new PropertyValueFactory<>("SongAlbum"));
        songDuration.setCellValueFactory(new PropertyValueFactory<>("SongDuration"));

        observePlayList();
        elementClickHandler();
        setupRowUpdater();
        setupDragAndDrop();
        searchBarAction();
        tableButtonsAction();
        showFavoritesAction();
        initializeColumns();
    }

    private void initializeColumns() {
        songNumber.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Track, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Track, String> item) {
                return new ReadOnlyObjectWrapper(songsListTable.getItems().indexOf(item.getValue()) + 1 + "");
            }
        });
        songNumber.setSortable(false);
        songDuration.setSortable(false);
        songNumber.setReorderable(false);
        songName.setReorderable(false);
        songArtist.setReorderable(false);
        songAlbum.setReorderable(false);
        songDuration.setReorderable(false);
        songLiked.setReorderable(false);
    }

    private void showFavoritesAction() {
        player.getOnlyFavorites().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (player.getOnlyFavorites().get()) {
                    tableLabel.setText("Liked tracks");
                } else {
                    tableLabel.setText("All tracks");
                }
            }
        });
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

    private void setupRowUpdater() {
        player.getCurrentTrackID().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                songsListTable.getSelectionModel().select(player.getCurrentTrackID().get());
            }
        });
    }

    private void setupDragAndDrop() {
        songsListTable.setOnDragOver(dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
        });
        songsListTable.setOnDragDropped(dragEvent -> {
            Dragboard dragboard = dragEvent.getDragboard();
            if (dragboard.hasFiles()) {
                for (File file : dragboard.getFiles()) {
                    try {
                        player.addTrack(MP3Parser.parse(file));
                    } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            player.setPlayList(player.getAllTracks());
        });
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

    private void searchBarAction() {
        songSearchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            player.setCurrentTrackFilter(track ->
                    (track.getSongName().toLowerCase().contains(newValue.toLowerCase().trim()) ||
                            ((track.getSongArtist() != null) ? track.getSongArtist().toLowerCase() : "").contains(newValue.toLowerCase().trim()))
            );
        });
    }

    private void tableButtonsAction() {

        Callback<TableColumn<Track, Void>, TableCell<Track, Void>> cellFactory = new Callback<TableColumn<Track, Void>, TableCell<Track, Void>>() {
            @Override
            public TableCell<Track, Void> call(final TableColumn<Track, Void> param) {
                final TableCell<Track, Void> cell = new TableCell<Track, Void>() {
                    private final ToggleButton favoriteButton = new ToggleButton();
                    {
                        favoriteButton.setId("favoriteButton");
                        favoriteButton.setGraphic(new FontIcon());
                        favoriteButton.setOnMouseClicked(mouseEvent -> {
                            player.getPlayList().get(getIndex()).setSongLiked(favoriteButton.isSelected());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            favoriteButton.setSelected(player.getPlayList().get(getIndex()).getSongLiked());
                            setGraphic(favoriteButton);
                        }
                    }
                };
                cell.getStyleClass().add("cell-style");
                cell.getStyleClass().add("favorite-button-padding");
                return cell;
            }
        };

        songLiked.setCellFactory(cellFactory);
    }
}

package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ContentPaneController implements Initializable {
    private final Player player = PlayerContext.player;
    public CustomTextField songSearchField;
    public TableView<Track> songsListTable;
    public TableColumn songName;
    public TableColumn songArtist;
    public TableColumn songAlbum;
    public TableColumn songDuration;
    public TableColumn songLiked;
    public Label tableLabel;
    public TableColumn songNumber;
    public TableColumn songSettings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        PlayerContext.selectedPlaylist = FXCollections.observableArrayList(player.getCurrentPlayList());
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
        songNumber.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Track, String>, ObservableValue<String>>) item -> new ReadOnlyObjectWrapper(songsListTable.getItems().indexOf(item.getValue()) + 1 + ""));
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
        PlayerContext.selectedPlaylistName.addListener((observableValue, s, t1) -> {
            tableLabel.setText(observableValue.getValue());
        });
        player.getOnlyFavorites().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (player.getOnlyFavorites().get()) {
                    PlayerContext.selectedPlaylistName.set("Liked tracks");
                } else {
                    PlayerContext.selectedPlaylistName.set("All tracks");
                }
            }
        });
    }

    private void observePlayList() {
        PlayerContext.selectedPlaylist.addListener((InvalidationListener) observable -> {
            songsListTable.getItems().clear();
            for (Track track : PlayerContext.selectedPlaylist) {
                try {
                    addToSongListTable(track);
                } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            songsListTable.getSelectionModel().select(player.getCurrentTrack().get());
        });
    }

    private void setupRowUpdater() {
        player.getCurrentTrack().addListener((observableValue, number, t1) -> {
            songsListTable.getSelectionModel().select(player.getCurrentTrack().get());
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
                        player.setCurrentPlayList(PlayerContext.selectedPlaylist);
                        player.setCurrentTrack(row.getItem());
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
            if (observableValue.getValue().isEmpty()) {
                PlayerContext.selectedPlaylistName.setValue("All tracks");
            }
            if (!observableValue.getValue().isEmpty()) {
                PlayerContext.selectedPlaylistName.setValue("Search results");
            }
        });
    }

    private void tableButtonsAction() {
        Callback<TableColumn<Track, Track>, TableCell<Track, Track>> cellFactoryLiked = new Callback<>() {
            @Override
            public TableCell<Track, Track> call(TableColumn<Track, Track> param) {
                final TableCell<Track, Track> cell = new TableCell<>() {
                    private final ToggleButton favoriteButton = new ToggleButton();

                    {
                        favoriteButton.setId("favoriteButton");
                        favoriteButton.setGraphic(new FontIcon());
                        favoriteButton.setOnMouseClicked(mouseEvent -> {
                            PlayerContext.selectedPlaylist.get(getIndex()).setSongLiked(favoriteButton.isSelected());
                        });
                    }

                    @Override
                    public void updateItem(Track item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            favoriteButton.setSelected(PlayerContext.selectedPlaylist.get(getIndex()).getSongLiked());
                            setGraphic(favoriteButton);
                        }
                    }
                };
                cell.getStyleClass().add("cell-style");
                cell.getStyleClass().add("favorite-table-button-alignment");
                return cell;
            }
        };

        Callback<TableColumn<Track, Track>, TableCell<Track, Track>> cellFactorySettings = new Callback<>() {
            @Override
            public TableCell<Track, Track> call(final TableColumn<Track, Track> param) {
                ContextMenu songSettingsContextMenu = new ContextMenu();
                Menu playlistMenu = new Menu("Add to playlist");
                MenuItem queueMenuItem = new MenuItem("Add to queue");
                MenuItem removeMenuItem = new MenuItem("Remove from the current playlist");

                songSettingsContextMenu.getItems().add(playlistMenu);
                songSettingsContextMenu.getItems().add(queueMenuItem);
                songSettingsContextMenu.getItems().add(removeMenuItem);
                songSettingsContextMenu.setId("settingsContextMenu");
                final TableCell<Track, Track> cell = new TableCell<>() {
                    private final Button settingsButton = new Button();
                    {
                        settingsButton.setId("settingsButton");
                        settingsButton.setGraphic(new FontIcon());
                        settingsButton.setOnMouseClicked(mouseEvent -> {
                            songSettingsContextMenu.show(settingsButton.getScene().getWindow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
                            removeMenuItem.setOnAction(actionEvent -> {
                                Track trackToRemove = PlayerContext.selectedPlaylist.get(getIndex());
                                player.getCurrentPlayList().remove(trackToRemove);
                                PlayerContext.selectedPlaylist.remove(trackToRemove);
                                if (getItem() == player.getCurrentTrack().get()) {
                                    player.pause();
                                    player.play();
                                }
                            });
                            queueMenuItem.setOnAction(actionEvent -> {
                                player.addToQueue(PlayerContext.selectedPlaylist.get(getIndex()));
                            });
                        });
                    }

                    @Override
                    public void updateItem(Track item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(settingsButton);
                        }
                    }
                };
                songSettingsContextMenu.setOnShown(event -> {
                    playlistMenu.getItems().clear();
                    for (PlayList playlist : player.getPlayLists()) {
                        MenuItem item = new MenuItem();
                        item.textProperty().bind(playlist.getName());
                        playlistMenu.getItems().add(item);
                        item.setOnAction(actionEvent -> playlist.getPlayList().add(PlayerContext.selectedPlaylist.get(cell.getIndex())));
                    }
                });
                cell.getStyleClass().add("cell-style");
                cell.getStyleClass().add("settings-table-button-alignment");
                return cell;
            }
        };

        songLiked.setCellFactory(cellFactoryLiked);
        songSettings.setCellFactory(cellFactorySettings);
    }
}

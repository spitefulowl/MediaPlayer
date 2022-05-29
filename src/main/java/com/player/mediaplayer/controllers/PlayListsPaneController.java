package com.player.mediaplayer.controllers;

import com.google.gson.Gson;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlayListsPaneController implements Initializable {
    public class JsonPlayList {
        JsonPlayList(PlayList playlist) {
            this.name = playlist.getName().get();
            this.playList = playlist.getPlayList().stream().map(item -> item.getFilePath()).toList();
        }
        public String name;
        public List<String> playList;
    }

    private final Player player = PlayerContext.player;
    public ListView playListsListView;
    public Button addPlayListButton;
    public Button importPlayListButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playListsListView.setEditable(false);
        initializePlayLists();
        initializeButtonsIcons();
        addPlayListButtonAction();
        importPlayListButtonAction();
    }

    private void addPlayListButtonAction() {
        addPlayListButton.setOnMouseClicked(mouseEvent -> {
            player.getPlayLists().add(new PlayList("New playlist", null));
        });
    }

    private void importPlayListButtonAction() {
        importPlayListButton.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                Gson gson = new Gson();
                try ( final FileReader fileReader = new FileReader(file) ) {
                    JsonPlayList jsonPlayList = gson.fromJson(fileReader, JsonPlayList.class);
                    List<Track> playList = jsonPlayList.playList.stream().map(item -> {
                        try {
                            return MP3Parser.parse(new File(URI.create(item)));
                        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
                    player.getPlayLists().add(new PlayList(jsonPlayList.name, new ArrayList<>(playList)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void initializeButtonsIcons() {
        addPlayListButton.setGraphic(new FontIcon());
        importPlayListButton.setGraphic(new FontIcon());
    }

    private void initializePlayLists() {
        playListsListView.setCellFactory((Callback<ListView<PlayList>, ListCell<PlayList>>) lv -> {
            TextFieldListCell<PlayList> cell = new TextFieldListCell<>();
            cell.setConverter(new StringConverter<>() {
                @Override
                public String toString(PlayList object) {
                    return object.getName().get();
                }

                @Override
                public PlayList fromString(String string) {
                    cell.getItem().setName(string);
                    return cell.getItem();
                }
            });
            ContextMenu playListItemContextMenu = new ContextMenu();
            MenuItem queueMenuItem = new MenuItem("Add to queue");
            MenuItem renameMenuItem = new MenuItem("Rename");
            MenuItem deleteMenuItem = new MenuItem("Delete");
            MenuItem exportMenuItem = new MenuItem("Export playlist");
            renameMenuItem.setOnAction(actionEvent -> {
                playListsListView.setEditable(true);
                playListsListView.edit(cell.getIndex());
            });
            playListsListView.setOnEditCommit((EventHandler<ListView.EditEvent>) editEvent -> playListsListView.setEditable(false));
            playListsListView.setOnEditCancel((EventHandler<ListView.EditEvent>) editEvent -> playListsListView.setEditable(false));
            deleteMenuItem.setOnAction(actionEvent -> {
                PlayList playlist = cell.getItem();
                player.getPlayLists().remove(playlist);
            });
            playListItemContextMenu.getItems().addAll(queueMenuItem, renameMenuItem, deleteMenuItem, exportMenuItem);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(playListItemContextMenu);
                }
            });
            cell.setOnMouseClicked(mouseEvent -> {
                if (!cell.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    PlayerContext.selectedPlaylist.clear();
                    PlayerContext.selectedPlaylist.addAll(cell.getItem().getPlayList());
                    PlayerContext.selectedPlaylistRef = cell.getItem().getPlayList();
                    PlayerContext.selectedPlaylistName.set(cell.getItem().getName().get());
                }
            });
            exportMenuItem.setOnAction(actionEvent -> {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON (*.json)", "*.json");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {
                    try (final FileWriter fileWriter = new FileWriter(file)) {
                        PlayList playlist = cell.getItem();
                        Gson gson = new Gson();
                        gson.toJson(new JsonPlayList(playlist), fileWriter);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return cell;
        });
        player.getPlayLists().addListener((InvalidationListener) observable -> {
            playListsListView.getItems().clear();
            for (PlayList playList : player.getPlayLists()) {
                playListsListView.getItems().add(playList);
            }
        });
    }
}

package com.player.mediaplayer.controllers;

import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayListsPaneController implements Initializable {
    private final Player player = PlayerContext.getInstance().getPlayer();
    public ListView playListsListView;
    public Button addPlayListButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playListsListView.setEditable(false);
        initializePlayLists();
        initializeButtonsIcons();
        addPlayListButtonAction();
    }

    private void addPlayListButtonAction() {
        addPlayListButton.setOnMouseClicked(mouseEvent -> {
            player.getPlayLists().add(new PlayList("New playlist", null));
        });
    }

    private void initializeButtonsIcons() {
        addPlayListButton.setGraphic(new FontIcon());
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
            playListItemContextMenu.getItems().addAll(queueMenuItem, renameMenuItem, deleteMenuItem);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(playListItemContextMenu);
                }
            });
            cell.setOnMouseClicked(mouseEvent -> {
                if (!cell.isEmpty() && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    player.setCurrentPlayList(cell.getItem().getPlayList());
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

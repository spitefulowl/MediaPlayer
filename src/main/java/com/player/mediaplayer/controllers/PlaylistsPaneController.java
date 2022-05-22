package com.player.mediaplayer.controllers;

import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.models.Track;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistsPaneController implements Initializable {
    private final Player player = PlayerContext.getInstance().getPlayer();
    public ListView playListsListView;
    public Button addPlayListButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playListsListView.setEditable(true);
        initializePlayLists();
        initializeButtonsIcons();
        addPlayListButtonAction();
    }

    private void addPlayListButtonAction() {
        addPlayListButton.setOnMouseClicked(mouseEvent -> {
            player.getPlayLists().add(new PlayList<Track>("New playlist", null));
        });
        playListsListView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                playListsListView.getItems().set(t.getIndex(), t.getNewValue());
            }
        });
    }

    private void initializeButtonsIcons() {
        addPlayListButton.setGraphic(new FontIcon());
    }
    private void initializePlayLists() {
        playListsListView.setCellFactory(new Callback<ListView<PlayList<Track>>, ListCell<PlayList<Track>>>() {
            @Override
            public ListCell<PlayList<Track>> call(ListView<PlayList<Track>> lv) {
                TextFieldListCell<PlayList<Track>> cell = new TextFieldListCell<PlayList<Track>>() {
                    @Override
                    public void updateItem(PlayList<Track> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText(null);
                        }
                    }
                };
                cell.setConverter(new StringConverter<PlayList<Track>>() {
                    @Override
                    public String toString(PlayList<Track> object) {
                        return object.getName();
                    }

                    @Override
                    public PlayList<Track> fromString(String string) {
                        return new PlayList<Track>(string, null);
                    }
                });
                return cell;
            }
        });
        player.getPlayLists().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                playListsListView.getItems().clear();
                for (PlayList playList : player.getPlayLists()) {
                    playListsListView.getItems().add(playList);
                }
            }
        });
    }
}

package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ControlPaneController implements Initializable {
    private final Player player = PlayerContext.getInstance().getPlayer();
    public ImageView albumImage;
    public Text currentDuration;
    public Slider durationSlider;
    public Text totalDuration;
    public ToggleButton shuffleButton;
    public Button previousSongButton;
    public ToggleButton playSongButton;
    public Button nextSongButton;
    public ToggleButton repeatSongButton;
    public Slider volumeSlider;
    public Button folderButton;
    public Label songNameText;
    public Label authorNameText;
    public ToggleButton favoriteSongsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonsIcons();
        playButtonAction();
        openFolderButtonAction();
        shuffleButtonAction();
        repeatButtonAction();
        initializeVolumeSlider();
        initializeDurationSlider();
        initializeOnEndOfMediaAction();
        currentTrackChangedHandler();
        updateControlsDisable(true);
        favoriteSongsButtonAction();
    }

    private void setSongImage() {
        Image image = player.getCurrentTrack().getSongArtwork();
        if (image == null) {
            URL url = getClass().getResource("/com/player/mediaplayer/images/default_artwork.png");
            image = new Image(url.toString());
        }
        albumImage.setImage(image);
    }

    private void initializeButtonsIcons() {
        shuffleButton.setGraphic(new FontIcon());
        previousSongButton.setGraphic(new FontIcon());
        nextSongButton.setGraphic(new FontIcon());
        repeatSongButton.setGraphic(new FontIcon());
        folderButton.setGraphic(new FontIcon());
        playSongButton.setGraphic(new FontIcon());
        favoriteSongsButton.setGraphic(new FontIcon());
    }

    private void updateControlsDisable(Boolean disabled) {
        playSongButton.setDisable(disabled);
        previousSongButton.setDisable(disabled);
        nextSongButton.setDisable(disabled);
        shuffleButton.setDisable(disabled);
        repeatSongButton.setDisable(disabled);
        durationSlider.setDisable(disabled);
        volumeSlider.setDisable(disabled);
    }

    private void initializeOnEndOfMediaAction() {
        player.setOnEndOfMedia(() -> playNextSong());
    }

    private void shuffleButtonAction() {
        shuffleButton.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                player.setIsShuffling(shuffleButton.isSelected());
            }
        });
    }

    private void repeatButtonAction() {
        repeatSongButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                player.setIsRepeating(repeatSongButton.isSelected());
            }
        });
    }

    private void playButtonAction() {
        playSongButton.setOnMouseClicked(mouseEvent -> {
            if (playSongButton.isSelected()) {
                resumeMedia();
            } else {
                pauseMedia();
            }
        });
    }

    private void openFolderButtonAction() {
        folderButton.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                DirectoryChooser directoryChoose = new DirectoryChooser();
                File directory = directoryChoose.showDialog(new Stage());
                try {
                    if (directory != null) {
                        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                        if (files != null) {
                            for (File file : files) {
                                player.addTrack(MP3Parser.parse(file));
                            }
                        }
                        player.setPlayList(player.getAllTracks());
                    }
                } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void currentTrackChangedHandler() {
        player.getCurrentTrackID().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                updateTrackInfo();
                playMedia();
            }
        });
    }

    private void updateTrackInfo() {
        Track trackToPlay = player.getCurrentTrack();
        songNameText.setText(trackToPlay.getSongName());
        authorNameText.setText(trackToPlay.getSongArtist());
        totalDuration.setText(trackToPlay.getSongDuration());
        setSongImage();
    }

    private void playMedia() {
        if (PlayerContext.globalTimer != null) {
            stopTimer();
        }
        player.play();
        startTimer();
        if (!playSongButton.isSelected()) {
            playSongButton.fire();
        }
    }

    private void resumeMedia() {
        player.resume();
        startTimer();
    }

    private void pauseMedia() {
        player.pause();
        stopTimer();
    }

    private void playPreviousSong() {
        if (PlayerContext.globalTimer != null) {
            stopTimer();
        }
        if (!player.previous()) {
            playMedia();
        }
    }

    private void playNextSong() {
        if (PlayerContext.globalTimer != null) {
            stopTimer();
        }
        if (!player.next()) {
            playMedia();
        }
    }

    private void startTimer() {
        updateControlsDisable(false);
        if (PlayerContext.globalTimer != null) {
            throw new IllegalStateException("Timer not stopped");
        }
        PlayerContext.globalTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                double currentTime = player.getMediaPlayer().getCurrentTime().toSeconds();
                double totalDuration = player.getMediaPlayer().getTotalDuration().toSeconds();
                if (!durationSlider.isValueChanging()) {
                    double newSliderValue = currentTime / totalDuration;
                    if (Double.isNaN(newSliderValue)) {
                        newSliderValue = 0;
                    }
                    durationSlider.setValue(newSliderValue);
                }
            }
        };
        PlayerContext.globalTimer.schedule(timerTask, 0, 1000);
    }

    private void stopTimer() {
        PlayerContext.globalTimer.cancel();
        PlayerContext.globalTimer = null;
    }

    private void initializeVolumeSlider() {
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double newVolume = observableValue.getValue().doubleValue();
                player.setCurrentVolume(newVolume);
                String style = String.format("-fx-background-color: linear-gradient(to right, -fx-track-color %d%%, #a9a9a9 %d%%) !important;",
                        (int) (newVolume * 100), (int) (newVolume * 100));
                volumeSlider.lookup(".track").setStyle(style);
            }
        });
        player.getCurrentVolume().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                player.getMediaPlayer().setVolume(player.getCurrentVolume().get());
            }
        });
    }

    private void sliderHoverActions() {
        durationSlider.hoverProperty().addListener((event) -> {
            StackPane thumb = (StackPane) durationSlider.lookup(".thumb");
            if (durationSlider.isHover()) {
                durationSlider.getStyleClass().add("slider-active-color");
                thumb.getStyleClass().add("thumb-active-color");
            } else {
                durationSlider.getStyleClass().remove("slider-active-color");
                thumb.getStyleClass().remove("thumb-active-color");
            }
        });
        volumeSlider.hoverProperty().addListener((event) -> {
            StackPane thumb = (StackPane) volumeSlider.lookup(".thumb");
            if (volumeSlider.isHover()) {
                volumeSlider.getStyleClass().add("slider-active-color");
                thumb.getStyleClass().add("thumb-active-color");
            } else {
                volumeSlider.getStyleClass().remove("slider-active-color");
                thumb.getStyleClass().remove("thumb-active-color");
            }
        });
        durationSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            durationSlider.getStyleClass().add("slider-active-color");
            (durationSlider.lookup(".thumb")).getStyleClass().add("thumb-active-color");
        });
        durationSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            durationSlider.getStyleClass().remove("slider-active-color");
            (durationSlider.lookup(".thumb")).getStyleClass().remove("thumb-active-color");
        });
        volumeSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            volumeSlider.getStyleClass().add("slider-active-color");
            (volumeSlider.lookup(".thumb")).getStyleClass().add("thumb-active-color");
        });
        volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            volumeSlider.getStyleClass().remove("slider-active-color");
            (volumeSlider.lookup(".thumb")).getStyleClass().remove("thumb-active-color");
        });
    }

    private void initializeDurationSlider() {
        durationSlider.setMin(0);
        durationSlider.setMax(1);
        durationSlider.setValue(0);
        durationSlider.valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double newCurrentDuration = observableValue.getValue().doubleValue();
                currentDuration.setText(MP3Parser.parseSongLength((int) player.getMediaPlayer().getTotalDuration().multiply(newCurrentDuration).toSeconds()));
                String style = String.format("-fx-background-color: linear-gradient(to right, -fx-track-color %d%%, #a9a9a9 %d%%) !important;",
                        (int) (newCurrentDuration * 100), (int) (newCurrentDuration * 100));
                durationSlider.lookup(".track").setStyle(style);
            }
        });
        durationSlider.setOnMouseReleased((MouseEvent event) -> player.getMediaPlayer().seek(player.getMediaPlayer().getTotalDuration().multiply(durationSlider.getValue())));
        durationSlider.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> durationSlider.setValueChanging(true));
        durationSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> durationSlider.setValueChanging(false));
        sliderHoverActions();
    }

    private void favoriteSongsButtonAction() {
        favoriteSongsButton.setOnMouseClicked(mouseEvent -> player.setOnlyFavorites(favoriteSongsButton.isSelected()));
    }

    public void previousButtonAction(ActionEvent actionEvent) {
        playPreviousSong();
    }

    public void nextButtonAction(ActionEvent actionEvent) {
        playNextSong();
    }
}

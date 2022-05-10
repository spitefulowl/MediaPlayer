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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
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
    public ImageView volumeImage;
    public Button folderButton;
    public Label songNameText;
    public Label authorNameText;

    private void setSongImage() {
        Image image = player.getCurrentTrack().getSongArtwork();
        if (image == null) {
            URL url = getClass().getResource("/com/player/mediaplayer/images/default_artwork.png");
            image = new Image(url.toString());
        }
        albumImage.setImage(image);
    }

    private void setDefaultImages() {
        URL url = getClass().getResource("/com/player/mediaplayer/images/icon_volume.png");
        Image image = new Image(url.toString());
        volumeImage.setImage(image);

        url = getClass().getResource("/com/player/mediaplayer/images/icon_play.png");
        image = new Image(url.toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        playSongButton.setGraphic(imageView);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDefaultImages();
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
    }

    private void initializeButtonsIcons() {
        shuffleButton.setGraphic(new FontIcon());
        previousSongButton.setGraphic(new FontIcon());
        nextSongButton.setGraphic(new FontIcon());
        repeatSongButton.setGraphic(new FontIcon());
        folderButton.setGraphic(new FontIcon());
        playSongButton.setGraphic(new FontIcon());
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
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
                File file = fileChooser.showOpenDialog(new Stage());
                try {
                    if (file != null) {
                        player.addTrack(MP3Parser.parse(file));
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
                String style = String.format("-fx-background-color: linear-gradient(to right, #3a3937 %d%%, #a9a9a9 %d%%) !important;",
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

    private void initializeDurationSlider() {
        durationSlider.setMin(0);
        durationSlider.setMax(1);
        durationSlider.setValue(0);
        durationSlider.valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double newCurrentDuration = observableValue.getValue().doubleValue();
                currentDuration.setText(MP3Parser.parseSongLength((int) player.getMediaPlayer().getTotalDuration().multiply(newCurrentDuration).toSeconds()));
                String style = String.format("-fx-background-color: linear-gradient(to right, #3a3937 %d%%, #a9a9a9 %d%%) !important;",
                        (int) (newCurrentDuration * 100), (int) (newCurrentDuration * 100));
                durationSlider.lookup(".track").setStyle(style);
            }
        });
        durationSlider.setOnMouseReleased((MouseEvent event) -> player.getMediaPlayer().seek(player.getMediaPlayer().getTotalDuration().multiply(durationSlider.getValue())));
    }

    public void previousButtonAction(ActionEvent actionEvent) {
        playPreviousSong();
    }

    public void nextButtonAction(ActionEvent actionEvent) {
        playNextSong();
    }
}

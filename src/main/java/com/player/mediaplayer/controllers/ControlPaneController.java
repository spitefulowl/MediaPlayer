package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.HelloApplication;
import com.player.mediaplayer.PlayerContext;
import com.player.mediaplayer.models.Track;
import com.player.mediaplayer.models.Player;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    public Button shuffleButton;
    public Button previousSongButton;
    public ToggleButton playSongButton;
    public Button nextSongButton;
    public Button repeatSongButton;
    public Slider volumeSlider;
    public ImageView volumeImage;
    public Button folderButton;
    public Text songNameText;
    public Text authorNameText;

    private void setSongImage() {
        URL url = getClass().getResource("/com/player/mediaplayer/images/beatles.png");
        Image image = new Image(url.toString());
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

    private void playButtonAction() {
        playSongButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                URL url;
                if (playSongButton.isSelected()) {
                    url = getClass().getResource("/com/player/mediaplayer/images/icon_pause.png");
                } else {
                    url = getClass().getResource("/com/player/mediaplayer/images/icon_play.png");
                }
                Image image = new Image(url.toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(25);
                imageView.setFitWidth(25);
                playSongButton.setGraphic(imageView);
            }
        });
        playSongButton.setOnMouseClicked(mouseEvent -> {
            URL url;
            if (playSongButton.isSelected()) {
                resumeMedia();
            } else {
                pauseMedia();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSongImage();
        setDefaultImages();
        playButtonAction();
        initializeVolumeSlider();
        currentTrackChangedHandler();
    }

    public void onFolderPressed(MouseEvent mouseEvent) throws InvalidDataException, UnsupportedTagException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3","*mp3"));
        File file = fileChooser.showOpenDialog(new Stage());
        player.addTrack(MP3Parser.parse(file));
    }

    public void currentTrackChangedHandler() {

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
    }

    private void playMedia() {
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

    private void startTimer() {
        if (PlayerContext.globalTimer != null) {
            throw new IllegalStateException("Timer not stopped");
        }
        PlayerContext.globalTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                currentDuration.setText(MP3Parser.parseSongLength((int)player.getMediaPlayer().getCurrentTime().toSeconds()));
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
                player.getMediaPlayer().setVolume(volumeSlider.getValue());
            }
        });
    }

    private void playNextSong() {
        stopTimer();
        player.next();
        selectRow(player.getCurrentTrackID().get());
    }

    private void playPreviousSong() {
        stopTimer();
        if(!player.previous()) {
            updateTrackInfo();
            playMedia();
        }
        selectRow(player.getCurrentTrackID().get());
    }

    private void selectRow(int row) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ContentPane.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fxmlLoader.<ContentPaneController>getController().songsListTable.getSelectionModel().select(row);
    }
    private void setupShutdownAction() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ControlPane.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void previousButtonAction(ActionEvent actionEvent) {
        playPreviousSong();
    }

    public void nextButtonAction(ActionEvent actionEvent) {
        playNextSong();
    }
}

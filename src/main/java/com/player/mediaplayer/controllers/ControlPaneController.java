package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.MP3Track;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private final PlayList playList;

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

    private Media media;
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private TimerTask timerTask;

    public ControlPaneController (PlayList playList) {
        this.playList = playList;
    }

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
                    MP3Track trackToPlay = playList.getPlayList().get(playList.getSelectedSong().get());
                    playList.setSongToPlay(playList.getSelectedSong().get());
                    playMedia(trackToPlay.getFilePath(), playList.getSongToPlay().get() == playList.getSelectedSong().get());
                } else {
                    url = getClass().getResource("/com/player/mediaplayer/images/icon_play.png");
                    pauseMedia();
                }
                Image image = new Image(url.toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(25);
                imageView.setFitWidth(25);
                playSongButton.setGraphic(imageView);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSongImage();
        setDefaultImages();
        playButtonAction();
        songToPlayChangesHandler();
        initializeVolumeSlider();
    }

    public void onFolderPressed(MouseEvent mouseEvent) throws InvalidDataException, UnsupportedTagException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3","*mp3"));
        File file = fileChooser.showOpenDialog(new Stage());
        playList.addMP3Track(MP3Parser.parse(file));
    }

    public void songToPlayChangesHandler() {
        playList.getSelectedSong().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

            }
        });

        playList.getSongToPlay().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                MP3Track trackToPlay = playList.getPlayList().get(playList.getSongToPlay().get());
                songNameText.setText(trackToPlay.getSongName());
                authorNameText.setText(trackToPlay.getSongArtist());
                totalDuration.setText(trackToPlay.getSongDuration());
                playMedia(trackToPlay.getFilePath(), false);
            }
        });
    }

    private void playMedia(String filePath, Boolean toResume) {
        if (mediaPlayer == null || !toResume) {
            media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
        }
        if (mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds() == 1) {
            media = new Media(filePath);
            mediaPlayer = new MediaPlayer(media);
        }
        mediaPlayer.play();
        if (!playSongButton.isSelected()) {
            playSongButton.fire();
        }
        startTimer();
    }

    private void pauseMedia() {
        mediaPlayer.pause();
        stopTimer();
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                double totalTime = mediaPlayer.getTotalDuration().toSeconds();
                currentDuration.setText(MP3Parser.parseSongLength(((int) currentTime)));
                if (currentTime / totalTime == 1) {
                    stopTimer();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void stopTimer() {
        timer.cancel();
    }

    private void initializeVolumeSlider() {
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.5);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue());
            }
        });
    }

    private void playNextSong() {
        pauseMedia();
        int nextSongIndex = 0;
        if (playList.getSongToPlay().get() != playList.getPlayList().size() - 1) {
            nextSongIndex = playList.getSongToPlay().get() + 1;
        }
        playList.setSongToPlay(nextSongIndex);
        playList.setSelectedSong(nextSongIndex);
        MP3Track trackToPlay = playList.getPlayList().get(playList.getSongToPlay().get());
        playMedia(trackToPlay.getFilePath(), false);
    }

    private void playPreviousSong() {
        int previousSongIndex = playList.getPlayList().size() - 1;
        if (playList.getSongToPlay().get() != 0) {
            previousSongIndex = playList.getSongToPlay().get() - 1;
        }
        playList.setSongToPlay(previousSongIndex);
        playList.setSelectedSong(previousSongIndex);
        MP3Track trackToPlay = playList.getPlayList().get(playList.getSongToPlay().get());
        playMedia(trackToPlay.getFilePath(), false);
    }


    public void previousButtonAction(ActionEvent actionEvent) {
        playPreviousSong();
    }

    public void nextButtonAction(ActionEvent actionEvent) {
        playNextSong();
    }
}

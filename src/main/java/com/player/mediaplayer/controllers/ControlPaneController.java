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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSongImage();
        setDefaultImages();
        playButtonAction();
        songToPlayChangesHandler();
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
                System.out.println("getSelectedSong = " + playList.getSelectedSong().get());
            }
        });

        playList.getSongToPlay().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {

                System.out.println("getSongToPlay = " + playList.getSongToPlay().get());

                MP3Track trackToPlay = playList.getPlayList().get(playList.getSongToPlay().get());
                songNameText.setText(trackToPlay.getSongName());
                authorNameText.setText(trackToPlay.getSongArtist());
                totalDuration.setText(trackToPlay.getSongDuration());
                playMedia(trackToPlay.getFilePath());
            }
        });
    }

    private void playMedia(String filePath) {
        media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();
        if (!playSongButton.isSelected()) {
            playSongButton.fire();
        }
    }
}

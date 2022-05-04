package com.player.mediaplayer.controllers;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.player.mediaplayer.models.PlayList;
import com.player.mediaplayer.utils.MP3Parser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

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
        File file = null;
        try {
            file = new File(Objects.requireNonNull(getClass().getResource("/com/player/mediaplayer/images/Deep_Purple_-_Smoke_On_The_Water_(musmore.com).mp3")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            playList.addMP3Track(MP3Parser.parse(file));
        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

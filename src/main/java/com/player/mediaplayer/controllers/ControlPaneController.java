package com.player.mediaplayer.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ControlPaneController implements Initializable {
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


    private void setSongImage() {
        //Image image = new Image("C:\\Users\\kugem\\IdeaProjects\\MediaPlayer\\src\\main\\resources\\com\\player\\mediaplayer\\images\\beatles.png");
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
    }
}

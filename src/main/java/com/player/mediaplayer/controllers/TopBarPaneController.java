package com.player.mediaplayer.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class TopBarPaneController implements Initializable {
    public Button minimizeButton;
    public Button maximizeButton;
    public Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtonsIcons();
        initializeButtonActions();
        initializeHoverActions();
    }

    private void initializeButtonsIcons() {
        minimizeButton.setGraphic(new FontIcon());
        maximizeButton.setGraphic(new FontIcon());
        closeButton.setGraphic(new FontIcon());
    }

    private void initializeButtonActions() {
        minimizeButton.setOnMouseClicked(mouseEvent -> {
            ((Stage)((Node) mouseEvent.getSource()).getScene().getWindow()).setIconified(true);
        });
        maximizeButton.setOnMouseClicked(mouseEvent -> {
            ((Stage)((Node) mouseEvent.getSource()).getScene().getWindow()).setMaximized(true);
        });
        closeButton.setOnMouseClicked(mouseEvent -> {
            Platform.exit();
        });
    }

    private void initializeHoverActions() {
        minimizeButton.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (minimizeButton.isHover()) {
                    minimizeButton.getStyleClass().add("top-bar-button-active-color");
                } else {
                    minimizeButton.getStyleClass().remove("top-bar-button-active-color");
                }
            }
        });
        maximizeButton.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (maximizeButton.isHover()) {
                    maximizeButton.getStyleClass().add("top-bar-button-active-color");
                } else {
                    maximizeButton.getStyleClass().remove("top-bar-button-active-color");
                }
            }
        });
        closeButton.hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (closeButton.isHover()) {
                    closeButton.getStyleClass().add("close-button-active-color");
                } else {
                    closeButton.getStyleClass().remove("close-button-active-color");
                }
            }
        });
    }
}

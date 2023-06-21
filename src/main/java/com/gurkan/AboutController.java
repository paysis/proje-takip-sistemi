package com.gurkan;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutController implements Initializable {

    @FXML
    private Label mainLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void init() {
        Stage aboutStage = (Stage) mainLabel.getScene().getWindow();
        aboutStage.setResizable(false);
    }

}

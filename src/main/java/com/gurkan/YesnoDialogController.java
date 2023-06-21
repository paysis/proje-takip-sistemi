package com.gurkan;

import java.net.URL;
import java.util.ResourceBundle;

import com.gurkan.interfaces.IYesnoDialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class YesnoDialogController implements Initializable {

    @FXML
    private TextFlow textFlowContainer;

    @FXML
    Button yesButton;
    @FXML
    Button noButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void init(Text headline, Text content, IYesnoDialog callback) {
        yesButton.setOnAction(arg0 -> {
            callback.call(true);
            Stage stage = (Stage) yesButton.getScene().getWindow();
            stage.close();
        });

        noButton.setOnAction(arg0 -> {
            callback.call(false);
            Stage stage = (Stage) noButton.getScene().getWindow();
            stage.close();
        });

        textFlowContainer.getChildren().addAll(headline, content);
    }
}

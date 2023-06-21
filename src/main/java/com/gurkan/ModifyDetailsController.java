package com.gurkan;

import java.net.URL;
import java.util.ResourceBundle;

import com.gurkan.data.Note;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.HTMLEditor;

public class ModifyDetailsController implements Initializable {

    @FXML
    HTMLEditor editor;

    Note currentNote;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void init(Note passedNote) {
        currentNote = passedNote;

        editor.setHtmlText(currentNote.getDetails());
    }

    @FXML
    public void handleFinishBtn() {
        currentNote.setDetails(editor.getHtmlText());
    }
}

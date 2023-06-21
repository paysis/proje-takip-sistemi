package com.gurkan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.gurkan.data.Note;
import com.gurkan.data.Project;
import com.gurkan.interfaces.IProjectControllerCallback;
import com.gurkan.ui.EditableTreeCell;
import com.gurkan.utils.CustomStringConverter;
import com.gurkan.utils.FileOperations;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CreateProjectController implements Initializable {
    @FXML
    TextField projectName;

    @FXML
    Label projectImgPath;

    @FXML
    TreeView<Note> notesTreeView;

    @FXML
    TextField noteTextField;

    TreeItem<Note> rootTreeItem;

    TreeItem<Note> selectedTreeItem;

    Project currentProject;

    IProjectControllerCallback callbackfn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void init(IProjectControllerCallback callbackfn) {
        currentProject = new Project("", "");
        this.callbackfn = callbackfn;

        selectedTreeItem = null;

        notesTreeView.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
            selectedTreeItem = notesTreeView.getSelectionModel().getSelectedItem();

            // todo: selected note operations
        });

        notesTreeView.setCellFactory(arg0 -> {
            EditableTreeCell cell = new EditableTreeCell();
            cell.setConverter(CustomStringConverter.createCustomNoteConverter(cell));
            return cell;
        });

        rootTreeItem = new TreeItem<>(new Note("Root Item"));
        rootTreeItem.setExpanded(true);

        notesTreeView.setRoot(rootTreeItem);
        notesTreeView.setEditable(true);
    }

    @FXML
    private void addNoteHandler() {
        String noteText = "New Note";

        if (noteTextField.getText().length() > 0) {
            noteText = noteTextField.getText();
            noteTextField.setText("");
        }

        TreeItem<Note> newTreeItem = new TreeItem<>(new Note(noteText));

        if (selectedTreeItem != null) {
            selectedTreeItem.getChildren().add(newTreeItem);
        }

    }

    @FXML
    private void removeNoteHandler() {
        if (selectedTreeItem != null && selectedTreeItem.getParent() != null) {
            selectedTreeItem.getParent().getChildren().remove(selectedTreeItem);
        }
    }

    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select a project icon.");
        fileChooser
                .setInitialDirectory(
                        FileOperations.getFileFromResource("data/images/", getClass()));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            projectImgPath.setText(selectedFile.toURI().toString());
        }
    }

    @FXML
    private void finishHandler() throws IOException {
        if (currentProject == null) {
            return;
        }

        App.showYesnoDialog("Are you sure?",
                "Are you really sure that you want to finish creating this project? If you answer yes, then you will go back to main workspace window.",
                isYes -> {
                    if (isYes) {
                        currentProject.setImgPath(projectImgPath.getText());
                        currentProject.setName(projectName.getText() == "" ? "Empty Project" : projectName.getText());
                        currentProject.setRootTreeItem(rootTreeItem);
                        callbackfn.call(currentProject);

                        currentProject = null;

                        Stage stage = (Stage) projectName.getScene().getWindow();
                        stage.close();
                    }
                });

        // App.setRoot("mainui");
    }

    @FXML
    private void switchToModifyDetails() throws IOException {
        if (selectedTreeItem != null)
            App.showModifyDetails(selectedTreeItem.getValue());
    }
}

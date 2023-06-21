package com.gurkan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import com.gurkan.data.StorableObject;
import com.gurkan.database.DatabaseManager;
import com.gurkan.database.RecentDbManager;
import com.gurkan.enums.ERenderKeys;
import com.gurkan.ui.EditableListCell;
import com.gurkan.ui.EditableTreeCell;
import com.gurkan.utils.CustomStringConverter;
import com.gurkan.data.Note;
import com.gurkan.data.Project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.fxml.Initializable;

public class MainController implements Initializable {

    @FXML
    private WebView webView1;

    @FXML
    private TreeView<Note> notesTreeView;

    @FXML
    private ListView<Project> projectListView;
    private final ObservableList<Project> projectListViewData = FXCollections.observableArrayList();

    @FXML
    private Project currentProject;

    @FXML
    private Menu recentMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectListViewData.clear();

        projectListViewData.addListener(new ListChangeListener<Project>() {
            @Override
            public void onChanged(Change<? extends Project> arg0) {
                System.out.println("projectListViewData.onChanged invoked");

            }
        });

        // projectListViewData.add(new Project("Project 1", "dummy_img.png"));
        // projectListViewData.add(new Project("Project 1", "dummy_img.png"));

        projectListView.setCellFactory(arg0 -> {
            EditableListCell cell = new EditableListCell();
            cell.setConverter(CustomStringConverter.createCustomProjectConverter(cell));

            Project.modifyProjectCellFactory(cell, projectListViewData, notesTreeView, webView1);

            return cell;
        });

        // projectListView.setCellFactory(Project.createCellFactoryCallback(projectListViewData,
        // notesTreeView, webView1));
        // get items from the database
        // projectListView.getItems().addAll(null);
        projectListView.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) ->

        {

            currentProject = (Project) projectListView.getSelectionModel().getSelectedItem();
            if (currentProject != null)
                notesTreeView.setRoot(currentProject.getRootTreeItem());
        });

        projectListView.setEditable(true);

        projectListView.setItems(projectListViewData);

        // treeview
        notesTreeView.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
            if (notesTreeView.getSelectionModel().getSelectedItem() != null) {
                String details = notesTreeView.getSelectionModel().getSelectedItem().getValue().getDetails();

                WebEngine engine = webView1.getEngine();
                engine.loadContent(details);

            }

            // System.out.println(notesTreeView.getSelectionModel().getSelectedItem().getValue().getText());
        });

        notesTreeView.setCellFactory(arg0 -> {
            EditableTreeCell cell = new EditableTreeCell();
            cell.setConverter(CustomStringConverter.createCustomNoteConverter(cell));
            return cell;
        });
        notesTreeView.setEditable(true);
        /*
         * TreeItem<String> root = new TreeItem<>("Root Note");
         * 
         * TreeItem<String> child = new TreeItem<>("Child Note");
         * 
         * child.setExpanded(false);
         * 
         * root.getChildren().add(child);
         * 
         * notesTreeView.setRoot(root);
         */

        // recently open menu code
        String[] recentlyOpenedArr = RecentDbManager.getRecentlyOpened();

        for (String recentlyOpenedStr : recentlyOpenedArr) {
            if (!(new File(recentlyOpenedStr)).exists()) {
                continue;
            }

            MenuItem mItem = new MenuItem(recentlyOpenedStr);

            mItem.setOnAction(arg0 -> {
                File targetDb = new File(mItem.getText());

                if (targetDb != null && targetDb.exists()) {
                    openDatabase(targetDb);
                }

            });

            recentMenu.getItems().add(mItem);
        }

        // webview

        WebEngine webEngine = webView1.getEngine();

        webEngine.loadContent(Project.DEFAULT_WELCOME);
    }

    private void openDatabase(File targetDatabase) {
        // todo: maybe check if its a valid database file.
        try {

            App.showYesnoDialog("Save the current database?",
                    "The current database file may not be saved. If you would like to, I will save the current workspace so that you will not lose your precious work!",
                    isYes -> {
                        if (isYes) {
                            saveCurrentWorkplace();
                        }

                        if (targetDatabase == null)
                            throw new RuntimeException(
                                    "[MainController] openDatabase: targetDatabase file instance is null!");

                        DatabaseManager.setDatabaseFilePath(targetDatabase.getAbsolutePath());
                        try {

                            projectListViewData.clear();

                            notesTreeView.setRoot(null);

                            WebEngine engine = webView1.getEngine();
                            engine.loadContent(Note.DEFAULT_DETAILS);

                            ArrayList<String> targetObjectNameList = new ArrayList<>();

                            ArrayList<Map<String, String>> allData = DatabaseManager.extractAllFeatures();

                            for (Map<String, String> dataMap : allData) {

                                String targetObjectName = dataMap.get(ERenderKeys.FILENAME.toString());

                                targetObjectNameList.add(targetObjectName);
                            }

                            ArrayList<Project> retObjects = DatabaseManager
                                    .retrieveProjects(targetObjectNameList);

                            for (Project retInstance : retObjects) {
                                projectListViewData.add(retInstance);
                            }

                            if (projectListViewData.size() == 0)
                                notesTreeView.setRoot(null);
                            else
                                notesTreeView.setRoot(projectListViewData.get(0).getRootTreeItem());

                        } catch (IOException ioe) {
                            throw new RuntimeException(
                                    "[openDatabase] extractAllFeatures() error: " + ioe.getMessage());
                        }
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("[openDatabase] App.showYesnoDialog() error: " + ioe.getMessage());
        }

    }

    @FXML
    private void openDatabaseAndRetrieve() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select a database file.");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        fileChooser
                .setInitialDirectory(new File(System.getProperty("user.dir")));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            openDatabase(selectedFile);
        }
    }

    @FXML
    private void saveCurrentWorkplace() {
        if (DatabaseManager.getDatabaseFilePath() == null || DatabaseManager.getDatabaseFilePath() == "") {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Please select a location to save database file.");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
            fileChooser
                    .setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedFile = fileChooser.showSaveDialog(null);
            if (selectedFile != null) {
                DatabaseManager.setDatabaseFilePath(selectedFile.getAbsolutePath());
            }
        }

        if (DatabaseManager.getDatabaseFilePath() != null && DatabaseManager.getDatabaseFilePath() != "") {
            DatabaseManager.saveProjects(projectListViewData.toArray(new Project[0]));
            RecentDbManager.insertRecentlyOpened(DatabaseManager.database_filepath);

        }
    }

    @FXML
    private void saveAsCurrentWorkplace() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Please select a location to save database file.");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        fileChooser
                .setInitialDirectory(new File(System.getProperty("user.dir")));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            DatabaseManager.setDatabaseFilePath(selectedFile.getAbsolutePath());

            DatabaseManager.saveProjects(projectListViewData.toArray(new Project[0]));
            RecentDbManager.insertRecentlyOpened(DatabaseManager.database_filepath);
        }
    }

    @FXML
    private void createNewWorkspace() {
        try {
            App.showYesnoDialog("Are you sure?",
                    "If you answer yes, a brand new workspace will welcome you! You will also be asked to save your current workspace as to protect your precious work!",
                    isYes -> {
                        if (isYes) {

                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Please select a location to save workspace database file.");
                            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
                            fileChooser
                                    .setInitialDirectory(new File(System.getProperty("user.dir")));
                            File selectedFile = fileChooser.showSaveDialog(null);
                            if (selectedFile != null) {
                                openDatabase(selectedFile);
                                // DatabaseManager.setDatabaseFilePath(selectedFile.getAbsolutePath());
                            }

                        }
                    });
        } catch (IOException ioe) {
            throw new RuntimeException("[MainController] createNewWorkspace error: " + ioe.getMessage());
        }
    }

    @FXML
    private void QuitApp() {
        Platform.exit();
    }

    @FXML
    private void switchToAbout() throws IOException {
        App.showAbout();
    }

    @FXML
    private void switchToCreateProjectForm() throws IOException {

        App.showProjectCreate((project -> {
            projectListViewData.add(project);

            for (StorableObject p : projectListViewData) {
                System.out.println(p.render());
            }
        }));
    }
}

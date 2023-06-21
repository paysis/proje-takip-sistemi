package com.gurkan.data;

import java.io.IOException;
import java.util.Date;
import com.gurkan.App;
import com.gurkan.database.DatabaseManager;
import com.gurkan.enums.EHashTypes;
import com.gurkan.ui.EditableListCell;
import com.gurkan.utils.FileOperations;
import com.gurkan.utils.HashCrypto;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Project extends StorableObject {
    public static final String DEFAULT_WELCOME = "<html><head></head><body><h1>Hoş geldiniz!</h1><p>Yeni bir proje ekleyerek ya da listeden varsa bir proje seçerek başlayabilirsiniz!</p></body></html>";

    private String name;
    private String imgPath;
    private boolean imgSmooth;

    private transient TreeItem<Note> rootTreeItem;

    public boolean isImgSmooth() {
        return imgSmooth;
    }

    public void setImgSmooth(boolean imgSmooth) {
        this.imgSmooth = imgSmooth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public TreeItem<Note> getRootTreeItem() {
        return rootTreeItem;
    }

    public void setRootTreeItem(TreeItem<Note> rootTreeItem) {
        this.rootTreeItem = rootTreeItem;
    }

    public Project(String projectName, String projectImgPath) {
        super(HashCrypto.encrypt(
                String.format("%s-%s-%s-%d",
                        DatabaseManager.getDatabaseFilePath() == null ? "" : DatabaseManager.getDatabaseFilePath(),
                        projectName, projectImgPath,
                        (new Date().getTime())),
                EHashTypes.SHA512));
        this.setName(projectName);
        this.setImgPath(projectImgPath);
        this.setImgSmooth(true);

        rootTreeItem = new TreeItem<>(new Note(""));

    }

    public Project(String projectName, String projectImgPath, boolean projectImgSmooth) {
        super(HashCrypto.encrypt(
                String.format("%s-%s-%s-%s-%d",
                        DatabaseManager.getDatabaseFilePath() == null ? "" : DatabaseManager.getDatabaseFilePath(),
                        projectName, projectImgPath, projectImgSmooth ? "smooth" : "notsmooth",
                        (new Date().getTime())),
                EHashTypes.SHA512));
        this.setName(projectName);
        this.setImgPath(projectImgPath);
        this.setImgSmooth(projectImgSmooth);

        rootTreeItem = new TreeItem<>(new Note(""));

    }

    public Project(Project other) {
        super(other.getFileName());

        this.setName(other.getName());
        this.setImgPath(other.getImgPath());
        this.setImgSmooth(other.isImgSmooth());
        this.setRootTreeItem(other.getRootTreeItem());
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /*
     * @Override
     * public void importData(Map<String, String> map) {
     * this.setName(map.get(ERenderKeys.NAME.toString()));
     * this.setImgPath(map.get(ERenderKeys.IMG_PATH.toString()));
     * this.setImgSmooth(map.get(ERenderKeys.IMG_SMOOTH.toString()).equals("true") ?
     * true : false);
     * 
     * super.importData(map);
     * }
     * 
     * @Override
     * public String render() {
     * 
     * Map<String, String> map = new HashMap<>();
     * 
     * map.put(ERenderKeys.NAME.toString(), this.getName());
     * map.put(ERenderKeys.IMG_PATH.toString(), this.getImgPath());
     * map.put(ERenderKeys.IMG_SMOOTH.toString(), this.isImgSmooth() ? "true" :
     * "false");
     * 
     * return super.render(map);
     * }
     * 
     * @Override
     * public String render(Map<String, String> map) {
     * 
     * map.put(ERenderKeys.NAME.toString(), this.getName());
     * map.put(ERenderKeys.IMG_PATH.toString(), this.getImgPath());
     * map.put(ERenderKeys.IMG_SMOOTH.toString(), this.isImgSmooth() ? "true" :
     * "false");
     * 
     * return super.render(map);
     * }
     */

    public static void modifyProjectCellFactory(EditableListCell cell, ObservableList<Project> projectListViewData,
            TreeView<Note> notesTreeView, WebView webView1) {

        cell.setCallback((Project projectInstance, boolean empty) -> {
            if (empty) {

                cell.setText(null);
                cell.setGraphic(null);

            } else if (projectInstance != null) {
                /*
                 * Image img = new Image(
                 * getClass().getResource(
                 * printableItem.getImgPath().startsWith("file:") ? printableItem.getImgPath()
                 * : "images/" + printableItem.getImgPath())
                 * .toExternalForm(),
                 * 0,
                 * 48, true, printableItem.isImgSmooth());
                 */
                String imgPath = "";
                if (projectInstance.getImgPath().startsWith("file:")) {
                    imgPath = projectInstance.getImgPath();
                } else {
                    imgPath = FileOperations.getFileFromResource("images/dummy_img.png", projectInstance.getClass())
                            .toURI().toString();

                }

                Image img = new Image(imgPath, 0, 48, true, projectInstance.isImgSmooth());
                ImageView imgView = new ImageView(img);

                cell.setGraphic(imgView);
                cell.setText(projectInstance.getName());
            }
        });

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> {
            try {

                App.showYesnoDialog("Are you sure you want to delete this?",
                        "If you answer yes, the selected project will be removed from the workspace and eventually also from the database, eternally gone.",
                        isYes -> {
                            if (isYes) {
                                projectListViewData.remove(cell.getItem());

                                if (projectListViewData.size() == 0)
                                    notesTreeView.setRoot(null);
                                else
                                    notesTreeView.setRoot(projectListViewData.get(0).getRootTreeItem());

                                WebEngine engine = webView1.getEngine();
                                engine.loadContent(Note.DEFAULT_DETAILS);
                            }
                        });
            } catch (IOException ioe) {
                throw new RuntimeException(
                        "[ModifyDetailsController] createCellFactoryCallback: " + ioe.getMessage());
            }
        });

        cell.addContextMenuItem(deleteItem);

    }

}

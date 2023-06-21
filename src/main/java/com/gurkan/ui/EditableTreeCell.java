/**
 * Source: https://stackoverflow.com/questions/25623802/edit-item-in-javafx-treeview
 * Source: https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm#BABEJCHA
 */

package com.gurkan.ui;

import java.io.IOException;
import com.gurkan.App;
import com.gurkan.data.Note;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class EditableTreeCell extends TextFieldTreeCell<Note> {
    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String DROP_HINT_STYLE = "-fx-border-color: gray; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3;";
    private static TreeItem<Note> draggedItem;
    private static TreeCell<Note> dropZone;

    private ContextMenu menu = new ContextMenu();

    public EditableTreeCell() {
        super();

        MenuItem addNoteItem = new MenuItem("Add Branch Note");
        menu.getItems().add(addNoteItem);

        addNoteItem.setOnAction(arg0 -> {
            this.getTreeItem().getChildren().add(new TreeItem<Note>(new Note("New Note")));
        });

        MenuItem renameItem = new MenuItem("Rename");
        menu.getItems().add(renameItem);

        renameItem.setOnAction(arg0 -> {
            startEdit();
        });

        MenuItem deleteItem = new MenuItem("Delete");
        menu.getItems().add(deleteItem);

        deleteItem.setOnAction(arg0 -> {
            try {

                App.showYesnoDialog("Are you sure you want to delete this?",
                        "If you answer yes, the selected note and the connected branch notes will all be removed from the workspace and eventually also from the database, eternally gone.",
                        isYes -> {
                            if (isYes) {

                                if (getTreeItem().getParent() != null) {

                                    getTreeItem().getParent().getChildren().remove(getTreeItem());

                                }
                            }
                        });
            } catch (IOException ioe) {
                throw new RuntimeException(
                        "[ModifyDetailsController] createCellFactoryCallback: " + ioe.getMessage());
            }
        });

        this.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
            if (isEmpty) {
                setContextMenu(null);
            } else {
                setContextMenu(menu);
            }
        });

        // drag and drop is inspired from
        // https://github.com/cerebrosoft/treeview-dnd-example/blob/master/treedrag/TaskCellFactory.java

        this.setOnDragDetected((MouseEvent event) -> {
            draggedItem = this.getTreeItem();

            if (draggedItem.getParent() == null) {
                return;
            }

            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.put(JAVA_FORMAT, draggedItem.getValue());
            db.setContent(content);
            db.setDragView(this.snapshot(null, null));
            event.consume();
        });

        this.setOnDragOver((DragEvent event) -> {

            if (!event.getDragboard().hasContent(JAVA_FORMAT))
                return;

            if (draggedItem == null || this.getTreeItem() == null || this.getTreeItem() == draggedItem)
                return;

            if (draggedItem.getParent() == null) {
                clearDropLoc();
                return;
            }

            event.acceptTransferModes(TransferMode.MOVE);
            if (dropZone != this) {
                clearDropLoc();
                dropZone = this;

                dropZone.setStyle(DROP_HINT_STYLE);
            }
        });

        this.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();

            if (!db.hasContent(JAVA_FORMAT))
                return;

            TreeItem<Note> droppedParent = draggedItem.getParent();

            droppedParent.getChildren().remove(draggedItem);

            if (droppedParent == this.getTreeItem()) {
                this.getTreeItem().getChildren().add(0, draggedItem);
                this.getTreeView().getSelectionModel().select(draggedItem);
            } else {
                if (this.getTreeItem().getParent() == null) {
                    this.getTreeView().getRoot().getChildren().add(draggedItem);

                } else {
                    int indexInParent = this.getTreeItem().getParent().getChildren().indexOf(this.getTreeItem());
                    this.getTreeItem().getParent().getChildren().add(indexInParent + 1, draggedItem);

                }
            }
            // isSuccess = true;

            this.getTreeView().getSelectionModel().select(draggedItem);
            event.setDropCompleted(true);
        });

        this.setOnDragDone((DragEvent event) -> clearDropLoc());
    }

    private void clearDropLoc() {
        if (dropZone != null)
            dropZone.setStyle("");
    }

    @Override
    public void updateItem(Note item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        if (!isEditing()) {
            setContextMenu(menu);
        }
    }
}

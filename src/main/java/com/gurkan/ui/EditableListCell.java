package com.gurkan.ui;

import com.gurkan.data.Project;
import com.gurkan.interfaces.IEditableListCellCallback;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.TextFieldListCell;

public class EditableListCell extends TextFieldListCell<Project> {

    private ContextMenu contextMenu = new ContextMenu();
    private IEditableListCellCallback callback;

    public EditableListCell() {
        super();

        MenuItem renameItem = new MenuItem("Rename");
        contextMenu.getItems().add(renameItem);

        renameItem.setOnAction(arg0 -> {
            startEdit();
        });

        this.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
            if (isEmpty) {
                setContextMenu(null);
            } else {
                setContextMenu(contextMenu);
            }
        });
    }

    public void addContextMenuItem(MenuItem item) {
        contextMenu.getItems().add(item);
    }

    public void setCallback(IEditableListCellCallback callback) {
        this.callback = callback;
    }

    @Override
    public void updateItem(Project item, boolean isEmpty) {
        super.updateItem(item, isEmpty);

        this.callback.call(item, isEmpty);

        if (!isEditing()) {
            setContextMenu(contextMenu);
        }
    }

}
package com.gurkan.utils;

import com.gurkan.data.Note;
import com.gurkan.data.Project;

import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

public class CustomStringConverter {
    public static StringConverter<Note> createCustomNoteConverter(TextFieldTreeCell<Note> cell) {
        return new StringConverter<Note>() {
            public String toString(Note item) {
                return item == null ? null : item.toString();
            }

            public Note fromString(String s) {
                Note ret = cell.getItem();
                ret.setText(s);
                return ret;
            }
        };
    }

    public static StringConverter<Project> createCustomProjectConverter(TextFieldListCell<Project> cell) {
        return new StringConverter<Project>() {
            public String toString(Project item) {
                return item == null ? null : item.toString();
            }

            public Project fromString(String s) {
                Project ret = cell.getItem();
                ret.setName(s);

                return ret;
            }
        };
    }
}

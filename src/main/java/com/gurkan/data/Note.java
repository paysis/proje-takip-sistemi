package com.gurkan.data;

import java.io.Serializable;

public class Note implements Serializable {
    private String text;
    private String details;

    public static final String DEFAULT_DETAILS = "<html><head></head><body>Buraya herhangi bir detay eklenmemiş.</body></html>";

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Note(String text) {
        /*
         * super("note_" + HashCrypto.encrypt(String.format("%s-%s-%d", text,
         * DEFAULT_DETAILS, (new Date().getTime())),
         * EHashTypes.SHA512));
         */
        this.text = text;
        this.details = DEFAULT_DETAILS;
    }

    public Note(Note other) {
        // super(other.getFileName());
        this.text = other.text;
        this.details = other.details;
    }

    @Override
    public String toString() {
        return this.getText();
    }
}

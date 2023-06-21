package com.gurkan.enums;

public enum ERenderKeys {
    FILENAME;

    @Override
    public String toString() {
        switch (this) {
            case FILENAME:
                return "fileName";
            default:
                return "undefined";
        }
    }
}

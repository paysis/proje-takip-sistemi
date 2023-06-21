module com.gurkan {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.gurkan to javafx.fxml;

    exports com.gurkan;
    exports com.gurkan.data;
    exports com.gurkan.database;
    exports com.gurkan.enums;
    exports com.gurkan.interfaces;
    exports com.gurkan.ui;
    exports com.gurkan.utils;

}

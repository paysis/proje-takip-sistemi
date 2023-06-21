package com.gurkan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import com.gurkan.data.Note;
import com.gurkan.interfaces.IProjectControllerCallback;
import com.gurkan.interfaces.IYesnoDialog;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainui"), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(getAppIcon());
        stage.setTitle("MizzNote");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

    }

    private static Image getAppIcon() {
        return new Image(App.class.getResourceAsStream("icons/app.png"));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Stage showModifyDetails(Note note) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("modifydetails.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.getIcons().add(getAppIcon());

        ModifyDetailsController controller = fxmlLoader.getController();
        controller.init(note);

        stage.show();

        return stage;
    }

    public static Stage showProjectCreate(IProjectControllerCallback callbackfn) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("createproject.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.getIcons().add(getAppIcon());

        CreateProjectController controller = fxmlLoader.getController();
        controller.init(callbackfn);

        stage.show();

        return stage;
    }

    public static Stage showYesnoDialog(String headlineStr, String contentStr, IYesnoDialog callbackfn)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("yesnodialog.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.getIcons().add(getAppIcon());

        YesnoDialogController controller = fxmlLoader.getController();

        Text headline = new Text(headlineStr + "\n\n");
        Text content = new Text(contentStr);

        headline.setFont(Font.font("Arial", FontPosture.REGULAR, 20));
        headline.setFill(Color.DARKCYAN);

        content.setFont(Font.font("Arial", FontPosture.ITALIC, 14));

        controller.init(headline, content, callbackfn);

        stage.show();

        return stage;
    }

    public static Stage showAbout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("about.fxml"));

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.getIcons().add(getAppIcon());

        AboutController controller = fxmlLoader.getController();

        controller.init();

        stage.show();

        return stage;
    }

    public static void main(String[] args) {
        launch();
    }

}
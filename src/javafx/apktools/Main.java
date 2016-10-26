package javafx.apktools;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.apktools.MainController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;

public class Main extends Application {

    public static final String TITLE = "打包工具箱";
    public static Stage stage;
    public File file;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
//        FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
        primaryStage.setTitle(TITLE);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setResizable(false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }
        @Override
        public void stop() throws Exception{
            stage = null;
            super.stop();
        }
        public File sendFile(){
            return file;
        }

    public static void main(String[] args) {
        launch(args);
    }

}

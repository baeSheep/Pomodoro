package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import controller.PomodoroController;

public class PomoBun extends Application {
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Load file FXML từ resource của class
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/PomodoroLayout.fxml"));
            Parent root = loader.load();

            // Lấy controller từ FXMLLoader
            PomodoroController controller = loader.getController();
            // Thiết lập mainStage cho controller
            controller.setMainStage(stage);

            // Tạo Scene với root và thiết lập kiểu Stage là TRANSPARENT
            Scene scene = new Scene(root);

            root.setOnMousePressed((MouseEvent event) -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            root.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                stage.setOpacity(.8);
            });

            root.setOnMouseReleased((MouseEvent event) -> {
                stage.setOpacity(1);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package UI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GifDisplay extends Application {

    private Stage ownerStage;
    private Stage primaryStage;

    private static final String FIRST_IMAGE_PATH = "/resource/image/UI_Retro_Pixel/tb1.png";
    private static final String SECOND_IMAGE_PATH = "/resource/image/UI_Retro_Pixel/tb2.png";

    public void setOwnerStage(Stage mainStage) {
        this.ownerStage = mainStage;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Đường dẫn tương đối đến tệp GIF ban đầu trong thư mục resources
        String gifPath = getClass().getResource(FIRST_IMAGE_PATH).toExternalForm();

        // Tạo một ImageView từ tệp GIF ban đầu
        Image gifImage = new Image(gifPath);
        ImageView gifImageView = new ImageView(gifImage);

        // Lấy kích thước của màn hình chính
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        // Tạo một StackPane để chứa ImageView
        StackPane root = new StackPane(gifImageView);
        root.setStyle("-fx-background-color: transparent;");

        // Tạo Scene với nền trong suốt
        Scene scene = new Scene(root, Color.TRANSPARENT);

        // Thiết lập cửa sổ Stage
        primaryStage.initStyle(StageStyle.TRANSPARENT); // Để ẩn tiêu đề cửa sổ
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setOpacity(1); // Đặt độ trong suốt ban đầu
        primaryStage.show();

        // Đặt vị trí của primaryStage ở góc dưới bên phải của màn hình
        primaryStage.setX(primaryScreenBounds.getMaxX() - gifImage.getWidth());
        primaryStage.setY(primaryScreenBounds.getMaxY() - gifImage.getHeight());

        // Tạo một Timeline để thay đổi hình ảnh sau 5 phút
        Timeline changeImageTimeline = new Timeline(new KeyFrame(Duration.minutes(5), event -> {
            // Đường dẫn đến tệp GIF mới
            String newGifPath = getClass().getResource(SECOND_IMAGE_PATH).toExternalForm();
            Image newGifImage = new Image(newGifPath);
            gifImageView.setImage(newGifImage); // Thay đổi hình ảnh trong ImageView
        }));

        // Ngăn chặn người dùng đóng cửa sổ
        primaryStage.setOnCloseRequest(event -> event.consume());

        // Ngăn chặn minimize bằng cách click vào icon trên taskbar
        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                primaryStage.setIconified(false);
            }
        });

        // Tạo một Timeline để tự động đóng ứng dụng sau 10 phút (5 phút hiển thị ảnh đầu tiên + 30 phút hiển thị ảnh mới)
        Timeline closeTimeline = new Timeline(new KeyFrame(Duration.minutes(5.5), event -> {
            primaryStage.close(); // Đóng primaryStage
        }));

        changeImageTimeline.play();
        closeTimeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

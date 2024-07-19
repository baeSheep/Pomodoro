package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import UI.GifDisplay;

public class PomodoroController implements Initializable {

    private int remainingTime;
    private Timeline timeline;
    private boolean isMuted = false;
    private MediaPlayer mediaPlayer;
    private GifDisplay gifDisplay;
    private Stage gifDisplayStage;
    private Stage mainStage;
    private static final Logger logger = Logger.getLogger(PomodoroController.class.getName());
    private int sessionTimeCount;
    private boolean mainUIHidden = false;
    private double mediaCurrentTime;

    @FXML
    private ImageView bgTimer;

    @FXML
    private ImageView btnClose;

    @FXML
    private ImageView btnPause;

    @FXML
    private ImageView btnPlay;

    @FXML
    private ImageView btnReset;

    @FXML
    private ImageView btnUnmute;

    @FXML
    private AnchorPane timerHome;

    @FXML
    private ImageView timerSec;

    @FXML
    private ImageView timer_Minute_Num1;

    @FXML
    private ImageView timer_Minute_Num2;

    @FXML
    private ImageView timer_Second_Num1;

    @FXML
    private ImageView timer_Second_Num2;

    @FXML
    private Label sessionTime;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        btnClose.setOnMouseEntered(event -> btnClose.setCursor(Cursor.HAND));
        btnClose.setOnMouseExited(event -> btnClose.setCursor(Cursor.DEFAULT));

        btnPlay.setOnMouseClicked(this::btnPlayClicked);
        btnPause.setOnMouseClicked(this::btnPauseClicked);
        btnReset.setOnMouseClicked(this::btnResetClicked);
        btnUnmute.setOnMouseClicked(this::btnUnmuteClicked);

        remainingTime = 25 * 60;
        updateDisplay();

        if (mainStage != null) {
            mainStage.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && gifDisplayStage != null && gifDisplayStage.isShowing()) {
                    gifDisplayStage.hide();
                    Platform.runLater(() -> gifDisplayStage.show());
                }
            });
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    void btnClose(MouseEvent event) {
        Platform.exit();
    }

    @FXML
    void btnPauseClicked(MouseEvent event) {
        if (timeline != null) {
            timeline.pause();
            if (mediaPlayer != null) {
                mediaCurrentTime = mediaPlayer.getCurrentTime().toMillis();
                mediaPlayer.pause();
            }
        }
    }

    @FXML
    void btnPlayClicked(MouseEvent event) {
        if (timeline != null && timeline.getStatus() == Timeline.Status.PAUSED) {
            timeline.play();
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
            return;
        }

        if (mediaPlayer == null) {
            initializeMediaPlayer();
        } else {
            mediaPlayer.seek(Duration.millis(mediaCurrentTime));
            mediaPlayer.play();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remainingTime--;
            updateDisplay();
            if (remainingTime <= 0) {
                timeline.stop();
                runGifDisplay();
                hideMainUI();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                    sessionTimeCount++;
                    sessionTime.setText(String.valueOf(sessionTimeCount));
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();
    }

    private void resumeMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.setStartTime(Duration.millis(mediaCurrentTime));
            mediaPlayer.play();
        }
    }

    @FXML
    private void btnResetClicked(MouseEvent event) {
        if (timeline != null) {
            timeline.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        resetDisplay();
    }

    @FXML
    private void btnUnmuteClicked(MouseEvent event) {
        isMuted = !isMuted;
        if (mediaPlayer != null) {
            mediaPlayer.setMute(isMuted);
        }
        updateMuteButton();
    }

    private void updateDisplay() {
        if (remainingTime < 0) {
            remainingTime = 0;
        }

        int minutes = (remainingTime % 3600) / 60;
        int second = (remainingTime % 60);

        timer_Minute_Num1.setImage(loadImage(minutes / 10));
        timer_Minute_Num2.setImage(loadImage(minutes % 10));
        timer_Second_Num1.setImage(loadImage(second / 10));
        timer_Second_Num2.setImage(loadImage(second % 10));
    }

    private Image loadImage(int digit) {
        String imagePath = String.format("/resource/image/UI_Retro_Pixel/number%d.png", digit);
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            throw new IllegalArgumentException("Invalid URL or resource not found: " + imagePath);
        }
        return new Image(imageStream);
    }

    private void resetDisplay() {
        remainingTime = 25 * 60;
        updateDisplay();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initializeMediaPlayer() {
        String musicPath = getClass().getResource("/resource/sound/Guitar.mp3").toExternalForm();
        Media media = new Media(musicPath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setMute(isMuted);
        mediaPlayer.setVolume(0.10); // Giảm âm lượng xuống còn 10%
        mediaPlayer.play();
    }

    private void runGifDisplay() {
        gifDisplay = new GifDisplay();

        gifDisplayStage = new Stage();
        gifDisplay.setOwnerStage(mainStage);

        // Thiết lập kiểu Stage là TRANSPARENT trước khi hiển thị Stage
        gifDisplayStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        Platform.runLater(() -> {
            try {
                // Bao bọc GifDisplay trong một Group
                Group root = new Group();
                Scene gifScene = new Scene(root, javafx.scene.paint.Color.TRANSPARENT);
                gifDisplayStage.setScene(gifScene);

                gifDisplay.start(gifDisplayStage);
                logger.info("Showing GifDisplay...");

                // Đảm bảo rằng khi GifDisplay đóng, nó sẽ chỉ đóng khi cần thiết
                gifDisplayStage.setOnHidden(event -> {
                    logger.info("GifDisplay closed, showing main UI...");
                    showMainUI();
                    resetDisplay();
                    resumeMedia();
                });
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to start GifDisplay", e);
            }
        });
    }


    private void hideMainUI() {
        mainStage.hide();
        mainUIHidden = true;
    }

    private void showMainUI() {
        mainStage.show();
        mainUIHidden = false;
    }


    private void updateMuteButton() {
        String muteImagePath = isMuted
            ? "/resource/image/UI_Retro_Pixel/btnMute_v3.png"
            : "/resource/image/UI_Retro_Pixel/btnUnmute_v3.png";
        btnUnmute.setImage(new Image(getClass().getResource(muteImagePath).toExternalForm()));
    }
}

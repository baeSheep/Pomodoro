package UI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class TimeSettingDialog extends Dialog<Pair<Integer, Integer>> {

    private final TextField hoursField;
    private final TextField minutesField;

    public TimeSettingDialog() {
        setTitle("Set Time");
        setHeaderText("Enter hours and minutes:");

        ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        hoursField = new TextField();
        hoursField.setPromptText("Minutes");
        minutesField = new TextField();
        minutesField.setPromptText("Second");

        grid.add(new Label("Minutes:"), 0, 0);
        grid.add(hoursField, 1, 0);
        grid.add(new Label("Second:"), 0, 1);
        grid.add(minutesField, 1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == setButtonType) {
                try {
                    int hours = Integer.parseInt(hoursField.getText());
                    int minutes = Integer.parseInt(minutesField.getText());
                    return new Pair<>(hours, minutes);
                } catch (NumberFormatException e) {
                    showAlert("Invalid input", "Please enter valid numbers for minutes and second.");
                }
            }
            return null;
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

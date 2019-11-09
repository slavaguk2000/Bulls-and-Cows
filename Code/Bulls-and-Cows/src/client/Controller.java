package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Controller {

    public Controller(Stage primaryStage) {
        Thread serverErrorThread = new Thread(() -> {// server not activate
            Runnable updater = () -> {
                if (Model.beginServerStop && Model.continueServerStop) {
                    Model.beginServerStop = false;
                    Stage exitStage = new Stage();
                    exitStage.initModality(Modality.APPLICATION_MODAL);
                    exitStage.setTitle("Warning");
                    Label exitLabel = new Label("Server error");
                    exitLabel.setFont(Font.font("", FontPosture.ITALIC, 20));
                    StackPane exitLayout = new StackPane();
                    exitStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - 100);
                    exitStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - 50);

                    exitLayout.getChildren().add(exitLabel);
                    Scene exitScene = new Scene(exitLayout, 200, 100);
                    exitStage.setScene(exitScene);
                    exitStage.show();
                }
            };

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(updater);
            }
        });
        serverErrorThread.setDaemon(true);
        serverErrorThread.start();

        Thread turnThread = new Thread(() -> {// window with request to enter number
            Runnable updater = () -> {
                if (Model.reset) {
                    View.myData.clear();
                    View.opponentData.clear();
                    Model.reset();
                    Model.reset = false;
                }
                if (Model.myTurn) {
                    if (Model.opponentGuess != null && Model.opponentGuess.equals(Model.myNumber)) {
                        View.opponentData.add(new Data(Model.opponentGuess, countCowBull(Model.opponentGuess, Model.myNumber)));
                        winWindow(primaryStage, false);
                        Model.myGuess = "null";
                        Model.sendServer = true;
                    } else if (Model.myNumber == null)
                        secondWindow(primaryStage, "Please, make your number", "Your number");
                    else {
                        if (Model.opponentGuess != null)
                            View.opponentData.add(new Data(Model.opponentGuess, countCowBull(Model.opponentGuess, Model.myNumber)));
                        secondWindow(primaryStage, "Please, try to guess opponent number", "Opponent number");
                    }
                    Model.myTurn = false;
                }

            };
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(updater);
            }
        });
        turnThread.setDaemon(true);
        turnThread.start();

    }

    private boolean checkTextField(String line) {
        if (!line.matches("^\\d{4}$")) return false;//check string contains only 4 digits
        for (int i = 0; i < 3; i++)//check digits not repeat
            for (int j = 1 + i; j < 4; j++)
                if (line.charAt(i) == line.charAt(j)) return false;
        return true;
    }

    private String countCowBull(String baseString, String compareString) {
        short cow = 0, bull = 0;
        for (short i = 0; i < 4; i++)
            for (short j = 0; j < 4; j++) {
                if (baseString.charAt(i) == compareString.charAt(j)) {
                    if (i == j) bull++;
                    else cow++;
                }
            }
        return cow + "c" + bull + "b";
    }

    private void handleCloseRequest(Stage primaryStage) {
        primaryStage.close();
        Model.continueServerStop = false;
        Model.end();
    }

    private void handleTextField(Stage secondStage, TextField textField) {
        String num = textField.getText();
        if (checkTextField(num)) {
            if (Model.myNumber == null) Model.myNumber = num;
            else {
                Model.myGuess = num;
                View.myData.add(new Data(Model.myGuess, countCowBull(Model.myGuess, Model.opponentNumber)));
            }
            Model.sendServer = true;
            secondStage.close();
        }
    }

    private void winWindow(Stage primaryStage, boolean isYouWinner) {

        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setResizable(false);
        secondStage.setWidth(210);
        secondStage.setHeight(350);
        secondStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - secondStage.getWidth() / 2);
        secondStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - secondStage.getHeight() / 2);
        Label labelResult = new Label();
        labelResult.setStyle("-fx-font-weight: bold");
        if (isYouWinner) {
            secondStage.setTitle("Victory!!!");
            labelResult.setText("Victory!!!");
        } else {
            secondStage.setTitle("Defeat!!!");
            labelResult.setText("Defeat!!!");
        }

        Label label = new Label("Would you like to repeat?");
        Button buttonYes = new Button("Yes");
        buttonYes.setMinWidth(50);
        Button buttonNo = new Button("No");
        buttonNo.setMinWidth(50);

        secondStage.setOnCloseRequest(e -> {
            e.consume();
            secondStage.close();
            handleCloseRequest(primaryStage);
        });
        buttonYes.setOnAction(e -> {
            View.myData.clear();
            View.opponentData.clear();
            Model.reset();
            Model.myGuess = "yourTurn";
            Model.sendServer = true;
            secondStage.close();
        });

        buttonNo.setOnAction(e -> {
            secondStage.close();
            handleCloseRequest(primaryStage);
        });
        HBox buttons = new HBox(80);
        buttons.setPadding(new Insets(5));
        buttons.getChildren().addAll(buttonYes, buttonNo);
        VBox layout = new VBox(20);
        layout.getChildren().addAll(labelResult, label, buttons);

        layout.setAlignment(Pos.CENTER);
        Scene secondScene = new Scene(layout);
        secondStage.setScene(secondScene);
        secondStage.show();
    }

    private void secondWindow(Stage primaryStage, String massage, String title) {
        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setResizable(false);
        secondStage.setTitle(title);
        secondStage.setWidth(300);
        secondStage.setHeight(200);
        secondStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - secondStage.getWidth() / 2);
        secondStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - secondStage.getHeight() / 2);
        Label label = new Label(massage);
        Button button = new Button("Enter");
        TextField textField = new TextField();
        textField.setOnKeyPressed(e-> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                handleTextField(secondStage, textField);
                if (Model.myGuess != null && Model.myGuess.equals(Model.opponentNumber)) {
                    winWindow(primaryStage, true);
                }
            }
        });
        secondStage.setOnCloseRequest(e -> {
            e.consume();
            secondStage.close();
            handleCloseRequest(primaryStage);
        });
        button.setOnAction(e -> {
            handleTextField(secondStage, textField);
            if (Model.myGuess != null && Model.myGuess.equals(Model.opponentNumber)) {
                winWindow(primaryStage, true);
            }
        });
        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, button, textField);
        layout.setAlignment(Pos.CENTER);
        Scene secondScene = new Scene(layout);
        secondStage.setScene(secondScene);
        secondStage.show();
    }
}

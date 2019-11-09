package client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

public class View extends Application {

    static ObservableList<Data> opponentData = FXCollections.observableArrayList();
    static ObservableList<Data> myData = FXCollections.observableArrayList();

    private TableView<Data> opponentTable = new TableView<>();
    private TableView<Data> myTable = new TableView<>();


    @Override
    public void start(Stage primaryStage) {
        Model.getInstance().startSocket();
        Controller controller = new Controller(primaryStage);

        primaryStage.setTitle("Bulls and Cows");
        primaryStage.setMaxWidth(400);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);


        TableColumn<Data, String> opponentNumberColumn = new TableColumn<>("Opponent number");
        TableColumn<Data, String> opponentCowBullColumn = new TableColumn<>("Cow/bull");
        opponentNumberColumn.setPrefWidth(115);
        opponentCowBullColumn.setPrefWidth(83);
        opponentNumberColumn.setResizable(false);
        opponentCowBullColumn.setResizable(false);

        TableColumn<Data, String> myNumberColumn = new TableColumn<>("My number");
        TableColumn<Data, String> myCowBullColumn = new TableColumn<>("Cow/bull");
        myNumberColumn.setPrefWidth(115);
        myCowBullColumn.setPrefWidth(83);
        myNumberColumn.setResizable(false);
        myCowBullColumn.setResizable(false);

        opponentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        opponentCowBullColumn.setCellValueFactory(new PropertyValueFactory<>("cowBull"));
        myNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        myCowBullColumn.setCellValueFactory(new PropertyValueFactory<>("cowBull"));

        opponentTable.getColumns().add(opponentNumberColumn);
        opponentTable.getColumns().add(opponentCowBullColumn);
        myTable.getColumns().add(myNumberColumn);
        myTable.getColumns().add(myCowBullColumn);

        opponentNumberColumn.setStyle("-fx-alignment: CENTER");
        opponentCowBullColumn.setStyle("-fx-alignment: CENTER");
        myNumberColumn.setStyle("-fx-alignment: CENTER");
        myCowBullColumn.setStyle("-fx-alignment: CENTER");

        opponentTable.setItems(myData);
        myTable.setItems(opponentData);


        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            primaryStage.close();
            Model.getInstance().setContinueServerStop(false);
            Model.end();
        });

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(opponentTable, myTable);
        primaryStage.setScene(new Scene(hBox, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


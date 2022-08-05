package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.Scout;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class SearchScoutsView extends View{
    private Label lastNameLabel;
    private Label firstNameLabel;
    private Label emailLabel;

    private TextField lastNameTF;
    private TextField firstNameTF;
    private TextField emailTF;


    private Button searchButton;
    private Button cancelButton;

    private MessageView statusLog;

    public SearchScoutsView(IModel tlc){
        super(tlc, "SearchScoutsView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 10, 10, 10));
        container.getChildren().add(createTitle());
        container.getChildren().add(createBody());
        statusLog = new MessageView("     ");

        container.getChildren().add(createFooter());
        container.getChildren().add(statusLog);
        getChildren().add(container);
    }

    private Node createFooter(){
        HBox buttonBox = new HBox(10);
        searchButton = new Button("Submit");
        searchButton.setOnAction(e -> {
            search();
        });

        cancelButton = new Button("Done");
        cancelButton.setOnAction(e -> {
            myModel.stateChangeRequest("Done", "");
        });
        buttonBox.getChildren().addAll(searchButton, cancelButton);
        return buttonBox;
    }

    private Node createBody(){
        HBox mainBox = new HBox(10);
        VBox labelsBox = new VBox(10);
        VBox tfBox = new VBox();

        lastNameLabel = new Label("Last Name");
        firstNameLabel = new Label("First Name");
        emailLabel = new Label("Email");
        labelsBox.getChildren().addAll(firstNameLabel, lastNameLabel, emailLabel);
        lastNameTF = new TextField();
        firstNameTF = new TextField();
        emailTF = new TextField();


        tfBox.getChildren().addAll(firstNameTF, lastNameTF, emailTF);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle(){
        Text titleText = new Text("Search Scouts");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);

        return titleText;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    public void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    /**
     * Display info message
     */
    private void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    private void search(){
        Scout s = new Scout();
        s.updateState("FirstName", firstNameTF.getText());

        s.updateState("LastName", lastNameTF.getText());
        s.updateState("Email", emailTF.getText());
        myModel.stateChangeRequest("DoScoutSearch", s);
    }
}

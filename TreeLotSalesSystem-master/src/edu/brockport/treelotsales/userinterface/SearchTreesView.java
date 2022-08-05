package edu.brockport.treelotsales.userinterface;


import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.Tree;
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

public class SearchTreesView extends View{
    private Label barcodeLabel;
    private Label firstNameLabel;
    private Label emailLabel;

    private TextField barcodeTF;
    private TextField firstNameTF;
    private TextField emailTF;


    private Button searchButton;
    private Button cancelButton;

    private MessageView statusLog;

    public SearchTreesView(IModel tlc){
        super(tlc, "SearchTreesView");

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

        barcodeLabel = new Label("Barcode: ");
        labelsBox.getChildren().addAll(barcodeLabel);
        barcodeTF = new TextField();



        tfBox.getChildren().addAll(barcodeTF);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle(){
        Text titleText = new Text("Search Trees");
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
        Tree t = new Tree();
        t.updateState("Barcode", barcodeTF.getText());
        myModel.stateChangeRequest("DoTreeSearch", t);

        //only happens if the search turns up blank
        displayMessage("Tree Not Found");
    }
}

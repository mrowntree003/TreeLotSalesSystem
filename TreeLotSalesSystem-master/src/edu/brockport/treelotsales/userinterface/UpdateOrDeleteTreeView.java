package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.TLC;
import edu.brockport.treelotsales.model.Tree;
import edu.brockport.treelotsales.utilities.Utilities;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Calendar;
import java.util.Properties;

public class UpdateOrDeleteTreeView extends View {
    private MessageView statusLog;

    private Label barcodeLabel;
    private Label treeTypeLabel;
    private Label notesLabel;
    private Label statusLabel;
//    private Label dateStatusUpdatedLabel;

    private TextField barcodeTF;
    private TextField treeTypeTF;
    private TextField notesTF;
    private ComboBox<String> statusCB;
//    private TextField dateStatusUpdatedTF;

    private Button updateButton;
    private Button deleteButton;
    private Button cancelButton;

    public UpdateOrDeleteTreeView(IModel tree){
        super(tree, "UpdateOrDeleteTreeView");
        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 10, 10, 10));
        container.getChildren().addAll(createBody(), createButtons(), createStatusLog(""));
        getChildren().add(container);
    }

    @Override
    public void updateState(String key, Object value) {
        System.out.println("Update State");
        if(key.equals("InputError")){
            clearErrorMessage();
            displayErrorMessage((String)value);
        } else if(key.equals("Success")){
            System.out.println("key is success");
            displayMessage((String)value);
            System.out.println("done");
        }
    }

    private Node createBody(){
        HBox mainBodyBox = new HBox(10);
        mainBodyBox.getChildren().addAll(createLabels(), createUserInputFields());
        return mainBodyBox;
    }

    private Node createLabels(){
        VBox labelsBox = new VBox(10);
        barcodeLabel = new Label("Barcode (6 digits): ");
        treeTypeLabel = new Label("Tree Type (ID):");
        notesLabel = new Label("Notes:");
        statusLabel = new Label("Status");
//        dateStatusUpdatedLabel = new Label("Last Status Update:");
        labelsBox.getChildren().addAll(barcodeLabel, treeTypeLabel, notesLabel, statusLabel);
        return labelsBox;
    }

    private Node createUserInputFields(){
        VBox fieldsBox = new VBox();
        barcodeTF = new TextField((String)myModel.getState("Barcode"));
        treeTypeTF = new TextField((String)myModel.getState("TreeType"));
        notesTF = new TextField((String)myModel.getState("Notes"));
        statusCB = new ComboBox<String>();
        statusCB.getItems().addAll("Available", "Sold", "Damaged");
        statusCB.getSelectionModel().selectFirst();
//        dateStatusUpdatedTF = new TextField();
        fieldsBox.getChildren().addAll(barcodeTF, treeTypeTF, notesTF, statusCB);
        return fieldsBox;
    }

    private Node createButtons(){
        HBox buttonsBox = new HBox(10);
        updateButton = new Button("Update");
        deleteButton = new Button("Delete");

        cancelButton = new Button("Done");
        //fix this later, hacked for now
        cancelButton.setOnAction(e -> {
            TLC l = new TLC();
            l.createAndShowTLCView();
        });

        updateButton.setOnAction(e -> {
            verifyInputs();
        });

        deleteButton.setOnAction(e -> {
            myModel.stateChangeRequest("DeleteTreeView", null);
        });

        buttonsBox.getChildren().addAll(updateButton, deleteButton, cancelButton);
        return buttonsBox;
    }

    private void verifyInputs(){
        String barcodeText = barcodeTF.getText().trim();
        String treeTypeText = treeTypeTF.getText().trim();
        String notesText = notesTF.getText().trim();
        String statusText = statusCB.getValue();

        if(barcodeText.isEmpty() || treeTypeText.isEmpty()){
            System.out.println("barcode text or tree type text is empty");
            updateState("InputError", "Barcode and Tree Type fields must not be empty.");
        } else {
            System.out.println("creating properties");
            Properties props = new Properties();
            props.setProperty("Barcode", barcodeText);
            props.setProperty("TreeType", treeTypeText);

            if(notesText.isEmpty()){
                props.setProperty("Notes", "None");
            } else {
                props.setProperty("Notes", notesText);
            }

            props.setProperty("Status", statusText);

            Calendar cal = Calendar.getInstance();
            props.setProperty("DateStatusUpdated", Utilities.convertToDefaultDateFormat(cal.getTime()));

            System.out.println("state change request");
            myModel.stateChangeRequest("ProcessTree", props);
            System.out.println("process tree completed");
            updateState("Success", "Tree successfully Updated!");
        }
    }

    private MessageView createStatusLog(String initialMessage){
        statusLog = new MessageView(initialMessage);

        return statusLog;
    }

    private void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    private void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    private void clearErrorMessage()
    {
        statusLog.clearErrorMessage();
    }
}


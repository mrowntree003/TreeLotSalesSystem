package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.TLC;
import edu.brockport.treelotsales.model.TreeType;
import edu.brockport.treelotsales.model.TreeTypeCollection;
import edu.brockport.treelotsales.utilities.Utilities;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Calendar;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class TreeView extends View {
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

    private Button addTreeButton;
    private Button cancelButton;

    public TreeView(IModel tree){
        super(tree, "TreeView");
        System.out.println("In TreeView");
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
        barcodeLabel = new Label("Barcode (6 digit):");
        treeTypeLabel = new Label("Tree Type:");
        notesLabel = new Label("Notes:");
        statusLabel = new Label("Status");
//        dateStatusUpdatedLabel = new Label("Last Status Update:");
        labelsBox.getChildren().addAll(barcodeLabel, treeTypeLabel, notesLabel, statusLabel);
        return labelsBox;
    }

    private Node createUserInputFields(){
        VBox fieldsBox = new VBox();
        barcodeTF = new TextField();
        treeTypeTF = new TextField();
        treeTypeTF.setEditable(false);
        notesTF = new TextField();
        statusCB = new ComboBox<String>();
        statusCB.getItems().addAll("Available", "Damaged");
        statusCB.getSelectionModel().selectFirst();

        TreeTypeCollection treeTypes = new TreeTypeCollection();
        treeTypes.getAll();
        barcodeTF.setOnKeyTyped((e ->{

            if(barcodeTF.getText().length() >= 2) {

                String prefix = barcodeTF.getText().substring(0, 2);
                TreeType t = new TreeType();

                for (int i = 0; i < treeTypes.size(); i++) {
                    if (treeTypes.get(i).getState("BarcodePrefix").equals(prefix)) {
                        t = treeTypes.get(i);
                        break;
                    }
                }

                treeTypeTF.setText((String)t.getState("TypeDescription"));
            }

        }));

//        dateStatusUpdatedTF = new TextField();
        fieldsBox.getChildren().addAll(barcodeTF, treeTypeTF, notesTF, statusCB);
        return fieldsBox;
    }

    private Node createButtons(){
        HBox buttonsBox = new HBox(10);
        addTreeButton = new Button("Add Tree");

        cancelButton = new Button("Done");
        //fix this later, hacked for now
        cancelButton.setOnAction(e -> {
            TLC l = new TLC();
            l.createAndShowTLCView();
        });

        addTreeButton.disableProperty().bind(Bindings.or(
                Bindings.isEmpty(barcodeTF.textProperty()),
                Bindings.isEmpty(treeTypeTF.textProperty()))
        );

        addTreeButton.setOnAction(e -> {
            System.out.println("Verifying inputs");
            verifyInputs();
        });

        buttonsBox.getChildren().addAll(addTreeButton, cancelButton);
        return buttonsBox;
    }

    private void verifyInputs(){
        String barcodeText = barcodeTF.getText().trim();

        //gets treetype ID from barcode prefix
        String barcodePrefix = barcodeText.substring(0,2);
        TreeTypeCollection treeTypes = new TreeTypeCollection();
        treeTypes.findTreeTypesWithInfo("", barcodePrefix);
        TreeType thisTreeType = new TreeType();
        for(int i=0; i<treeTypes.size(); i++){
            if(treeTypes.get(i).getState("BarcodePrefix").equals(barcodePrefix)){
                thisTreeType = treeTypes.get(i);
            }
        }
        String treeTypeIDText = (String)thisTreeType.getState("ID");

        String notesText = notesTF.getText().trim();
        String statusText = statusCB.getValue();

        if(barcodeText.isEmpty() || barcodePrefix.isEmpty()){
            System.out.println("barcode text or tree type text is empty");
            updateState("InputError", "Barcode and Tree Type fields must not be empty.");
        } else if(!barcodeText.matches("\\d{6}")){
            displayErrorMessage("Barcode must be six digits");
        } else {
            System.out.println("creating properties");
            Properties props = new Properties();
            props.setProperty("Barcode", barcodeText);
            props.setProperty("TreeType", treeTypeIDText);

            if(notesText.isEmpty()){
                props.setProperty("Notes", "None");
            } else {
                props.setProperty("Notes", notesText);
            }

            props.setProperty("Status", statusText);

            Calendar cal = Calendar.getInstance();
            props.setProperty("DateStatusUpdated", Utilities.convertToDefaultDateFormat(cal.getTime()));

            System.out.println("state change request");
            myModel.stateChangeRequest("ProcessNewTree", props);
            System.out.println("process tree completed");
            updateState("Success", "Tree successfully added to database!");
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

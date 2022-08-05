package edu.brockport.treelotsales.userinterface;
//import packages
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.TLC;
import edu.brockport.treelotsales.model.TreeType;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Properties;

public class TreeTypeView extends View {
    private MessageView statusLog;
    //declare GUI labels
    private Label typeDescriptionLabel;
    private Label costLabel;
    private Label barcodePrefixLabel;

    //declare text fields
    private TextField typeDescriptionTF;
    private TextField costTF;
    private TextField barcodePrefixTF;

    //declare GUI buttons
    private Button addTreeTypeButton;
    private Button cancelButton;

    public TreeTypeView(IModel treeType) {
        super(treeType, "TreeTypeView");
        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 10, 10, 10));
        container.getChildren().addAll(createBody(), createButtons(), createStatusLog(""));
        getChildren().add(container);
    }
//
    @Override
    public void updateState(String key, Object value) {
        System.out.println("Update State");
        if (key.equals("InputError")) {
            clearErrorMessage();
            displayErrorMessage((String) value);
        } else if (key.equals("Success")) {
            System.out.println("key is success");
            displayMessage((String) value);
            System.out.println("done");
        }
    }

    private Node createBody() {
        HBox mainBodyBox = new HBox(10);
        mainBodyBox.getChildren().addAll(createLabels(), createUserInputFields());
        return mainBodyBox;
    }

    private Node createLabels() {
        VBox labelsBox = new VBox(10);
        typeDescriptionLabel = new Label("Type Description:");
        costLabel = new Label("Cost:");
        barcodePrefixLabel = new Label("Barcode Prefix:");
        labelsBox.getChildren().addAll(typeDescriptionLabel, costLabel, barcodePrefixLabel);
        return labelsBox;
    }

    private Node createUserInputFields() {
        VBox fieldsBox = new VBox();
        typeDescriptionTF = new TextField();
        costTF = new TextField();
        barcodePrefixTF = new TextField();
        fieldsBox.getChildren().addAll(typeDescriptionTF, costTF, barcodePrefixTF);
        return fieldsBox;
    }

    private Node createButtons(){
        HBox buttonsBox = new HBox(10);
        addTreeTypeButton = new Button("Add Tree Type");

        cancelButton = new Button("Done");
        //fix this later, hacked for now
        cancelButton.setOnAction(e -> {
            TLC l = new TLC();
            l.createAndShowTLCView();
        });


        addTreeTypeButton.disableProperty().bind(
                Bindings.isEmpty(typeDescriptionTF.textProperty())
                        .or(Bindings.isEmpty(barcodePrefixTF.textProperty()).or(Bindings.isEmpty(costTF.textProperty())))
        );

        addTreeTypeButton.setOnAction(e -> {
            System.out.println("Verifying inputs");
            verifyInputs();
        });

        buttonsBox.getChildren().addAll(addTreeTypeButton, cancelButton);
        return buttonsBox;
    }

    private void verifyInputs() {
        boolean isError = false;
        String treeTypeDescriptionText = typeDescriptionTF.getText().trim();
        String costText = costTF.getText().trim();
        String barcodePrefixText = barcodePrefixTF.getText().trim();

        if(!costText.matches("\\d+")){
            displayErrorMessage("Cost must be at least one digit in length.");
            isError = true;
        }

        if(!barcodePrefixText.matches("\\d{2}")){
            displayErrorMessage("Barcode Prefix must be exactly two digits.");
            isError = true;
        }

        if (costText.isEmpty() || barcodePrefixText.isEmpty() || treeTypeDescriptionText.isEmpty()) {
            isError = true;
            System.out.println("Tree Type Description, Cost, or Barcode Prefix is Empty.");
            updateState("InputError", "Tree Type Description, Cost, and Barcode Prefix must not be empty.");

        }

        TreeType treeType = TreeType.getTreeTypeWithPrefix(barcodePrefixText);
        if(treeType != null){
            displayErrorMessage("Type already exists with this prefix");
            isError = true;
        }

        if(!isError) {
            System.out.println("creating properties");
            Properties props = new Properties();
            props.setProperty("TypeDescription", treeTypeDescriptionText);
            props.setProperty("Cost", costText);
            props.setProperty("BarcodePrefix", barcodePrefixText);


            System.out.println("state change request");
            myModel.stateChangeRequest("ProcessTreeType", props);
            System.out.println("Adding tree type completed");
            updateState("Success", "Tree type successfully added!");
        }
    }

    private MessageView createStatusLog(String initialMessage) {
        statusLog = new MessageView(initialMessage);

        return statusLog;
    }

    private void displayErrorMessage(String message) {
        statusLog.displayErrorMessage(message);
    }

    private void displayMessage(String message) {
        statusLog.displayMessage(message);
    }

    private void clearErrorMessage() {
        statusLog.clearErrorMessage();
    }
}
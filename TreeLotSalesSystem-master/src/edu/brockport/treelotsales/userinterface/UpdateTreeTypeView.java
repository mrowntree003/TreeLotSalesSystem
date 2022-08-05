package edu.brockport.treelotsales.userinterface;
//import packages
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.TLC;
import edu.brockport.treelotsales.model.TreeType;
import edu.brockport.treelotsales.model.TreeTypeCollection;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Properties;

public class UpdateTreeTypeView extends View {
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
    private Button updateTreeTypeButton;
    private Button cancelButton;

    public UpdateTreeTypeView(IModel treeType) {
        super(treeType, "UpdateTreeTypeView");
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
        typeDescriptionTF = new TextField((String)myModel.getState("TypeDescription"));
        costTF = new TextField((String)myModel.getState("Cost"));
        barcodePrefixTF = new TextField((String)myModel.getState("BarcodePrefix"));
        fieldsBox.getChildren().addAll(typeDescriptionTF, costTF, barcodePrefixTF);
        return fieldsBox;
    }

    private Node createButtons(){
        HBox buttonsBox = new HBox(10);
        updateTreeTypeButton = new Button("Update");

        cancelButton = new Button("Done");
        //fix this later, hacked for now
        cancelButton.setOnAction(e -> {
            TLC l = new TLC();
            l.createAndShowTLCView();
        });


        updateTreeTypeButton.disableProperty().bind(
                Bindings.isEmpty(typeDescriptionTF.textProperty())
                        .or(Bindings.isEmpty(barcodePrefixTF.textProperty()).or(Bindings.isEmpty(costTF.textProperty())))
        );

        updateTreeTypeButton.setOnAction(e -> {
            System.out.println("Verifying inputs");
            verifyInputs();
        });

        buttonsBox.getChildren().addAll(updateTreeTypeButton, cancelButton);
        return buttonsBox;
    }

    private void verifyInputs() {
        String treeTypeDescriptionText = typeDescriptionTF.getText().trim();
        String costText = costTF.getText().trim();
        String barcodePrefixText = barcodePrefixTF.getText().trim();
        boolean isError = false;

        if(!costText.matches("\\d+")){
            isError = true;
            displayErrorMessage("Cost be at least 1 digit in length");
        }

        if(!barcodePrefixText.matches("\\d{2}")){
            isError = true;
            displayErrorMessage("Barcode prefix must be exactly two digits.");
        }

        if (costText.trim().isEmpty() || barcodePrefixText.trim().isEmpty() || treeTypeDescriptionText.trim().isEmpty()) {
            System.out.println("Tree Type Description, Cost, or Barcode Prefix is Empty.");
            updateState("InputError", "Tree Type Description, Cost, and Barcode Prefix must not be empty.");
        }


        TreeTypeCollection types = new TreeTypeCollection();
        types.findTreeTypesWithInfo("", barcodePrefixText);
        if(types.size() == 1){
            if(!((String)types.get(0).getState("ID")).equals((String)myModel.getState("ID"))){
                isError = true;
                displayErrorMessage("Type already exists with that prefix");
            }
        }

        if(!isError) {
            System.out.println("creating properties");
            Properties props = new Properties();
            props.setProperty("TypeDescription", treeTypeDescriptionText);
            props.setProperty("Cost", costText);
            props.setProperty("BarcodePrefix", barcodePrefixText);
            props.setProperty("ID", (String)myModel.getState("ID"));


            System.out.println("state change request");
            myModel.stateChangeRequest("ProcessTreeType", props);
            System.out.println("Adding tree type completed");
            updateState("Success", "Tree type successfully updated!");
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

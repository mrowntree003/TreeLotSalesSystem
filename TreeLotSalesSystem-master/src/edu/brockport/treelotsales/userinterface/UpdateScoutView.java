package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.TLC;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class UpdateScoutView extends View {
    private Label lastNameLabel;
    private Label firstNameLabel;
    private Label middleNameLabel;
    private Label dateOfBirthLabel;
    private Label phoneNumberLabel;
    private Label emailLabel;
    private Label troopIDLabel;
    private Label statusLabel;

    private TextField lastNameTF;
    private TextField firstNameTF;
    private TextField middleNameTF;
    private DatePicker dateOfBirth;
    private TextField phoneNumberTF;
    private TextField emailTF;
    private TextField troopIDTF;
    private ComboBox<String> statusCB;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public UpdateScoutView(IModel scout) {
        super(scout, "UpdateScout");

        VBox container = new VBox(10);
        container.setPadding(new Insets(10, 10, 10, 10));
        container.getChildren().add(createTitle());
        container.getChildren().add(createBody());
        statusLog = new MessageView("     ");

        container.getChildren().add(createFooter());
        container.getChildren().add(statusLog);
        getChildren().add(container);
    }

    private Node createFooter() {
        HBox buttonBox = new HBox(10);
        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            verifyInputs();
        });

        cancelButton = new Button("Done");
        //fix this later, hacked for now
        cancelButton.setOnAction(e -> {
            TLC l = new TLC();
            l.createAndShowTLCView();
        });
        buttonBox.getChildren().addAll(submitButton, cancelButton);
        return buttonBox;
    }

    private Node createBody() {
        HBox mainBox = new HBox(10);
        VBox labelsBox = new VBox(10);
        VBox tfBox = new VBox();


        lastNameLabel = new Label("Last Name");
        middleNameLabel = new Label("Middle Name");
        firstNameLabel = new Label("First Name");
        dateOfBirthLabel = new Label("DOB");
        phoneNumberLabel = new Label("Phone Number (XXX)XXX-XXXX");
        emailLabel = new Label("Email");
        troopIDLabel = new Label("Troop ID (9 digits)");
        statusLabel = new Label("Status");
        labelsBox.getChildren().addAll(lastNameLabel, middleNameLabel, firstNameLabel, dateOfBirthLabel,
                phoneNumberLabel, emailLabel, troopIDLabel, statusLabel);

        lastNameTF = new TextField((String)myModel.getState("LastName"));
        middleNameTF = new TextField((String)myModel.getState("MiddleName"));
        firstNameTF = new TextField((String)myModel.getState("FirstName"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse((String)myModel.getState("DateOfBirth"), formatter);
        dateOfBirth = new DatePicker(localDate);

        phoneNumberTF = new TextField((String)myModel.getState("PhoneNumber"));
        emailTF = new TextField((String)myModel.getState("Email"));
        troopIDTF = new TextField((String)myModel.getState("TroopID"));
        statusCB = new ComboBox<String>();
        statusCB.getItems().addAll("Active", " Inactive");
        statusCB.setValue((String)myModel.getState("Status"));
        tfBox.getChildren().addAll(lastNameTF, middleNameTF, firstNameTF, dateOfBirth, phoneNumberTF,
                emailTF, troopIDTF, statusCB);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle() {
        Text titleText = new Text("Update a Scout");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);

        return titleText;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    private void verifyInputs() {
        String lastName = lastNameTF.getText();
        String firstName = firstNameTF.getText();
        String middleName = middleNameTF.getText();
        String DOB = dateOfBirth.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String phoneNumber = phoneNumberTF.getText();
        String email = emailTF.getText();
        String troopID = troopIDTF.getText();
        String status = statusCB.getValue();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateStatusUpdated = df.format(date);
        boolean isError = false;

        if (status.equals("") || status == null) {
            displayErrorMessage("Please select a status");
            isError = true;
        }
        if (!isError) {
            Properties props = new Properties();
            System.out.println(lastName + " " + middleName + " " + status + " " + firstName);
            props.setProperty("LastName", lastName);
            props.setProperty("MiddleName", middleName);
            props.setProperty("Status", status);
            props.setProperty("FirstName", firstName);
            props.setProperty("DateOfBirth", DOB);
            props.setProperty("PhoneNumber", phoneNumber);
            props.setProperty("Email", email);
            props.setProperty("TroopID", troopID);
            props.setProperty("DateStatusUpdated", dateStatusUpdated);
            props.setProperty("ID", (String)myModel.getState("ID"));

            myModel.stateChangeRequest("ProcessScout", props);
            displayMessage("Scout updated successfully");
        }

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
}
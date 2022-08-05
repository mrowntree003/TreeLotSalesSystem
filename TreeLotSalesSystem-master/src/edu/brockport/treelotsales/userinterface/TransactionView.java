package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.Session;
import edu.brockport.treelotsales.model.SessionCollection;
import edu.brockport.treelotsales.model.TLC;
import edu.brockport.treelotsales.model.Tree;
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
import java.time.LocalTime;
import java.util.Date;
import java.util.Properties;

public class TransactionView extends View{
    private Label paymentTypeLabel;
    private Label customerNameLabel;
    private Label customerPhoneLabel;
    private Label customerEmailLabel;


    private ComboBox paymentTypeCB;
    private TextField customerNameTF;
    private TextField customerPhoneTF;
    private TextField customerEmailTF;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public TransactionView(IModel transaction){
        super(transaction, "TransactionView");

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
        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            verifyInputs();
            new TLC().createAndShowTLCView();
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

    private Node createBody(){
        HBox mainBox = new HBox(10);
        VBox labelsBox = new VBox(10);
        VBox tfBox = new VBox();

        paymentTypeLabel = new Label("Payment Method:");
        customerPhoneLabel = new Label("Customer Phone:");
        customerNameLabel = new Label("Customer Name:");
        customerEmailLabel = new Label("Customer Email:");
        labelsBox.getChildren().addAll(paymentTypeLabel, customerPhoneLabel, customerNameLabel, customerEmailLabel);

        paymentTypeCB = new ComboBox();
        paymentTypeCB.getItems().addAll("Cash", "Check");
        paymentTypeCB.setValue("Cash");
        customerPhoneTF = new TextField();
        customerNameTF = new TextField();
        customerEmailTF = new TextField();
        tfBox.getChildren().addAll(paymentTypeCB, customerPhoneTF, customerNameTF, customerEmailTF);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle(){
        Text titleText = new Text("Transaction");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);

        return titleText;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    private void verifyInputs(){
        String paymentType = (String)paymentTypeCB.getValue();
        String customerName = customerNameTF.getText();
        String customerPhone = customerPhoneTF.getText();
        String customerEmail = customerEmailTF.getText();

        boolean isError = false;

        if(!customerEmail.matches(".+[@].+[.].+")){
            displayErrorMessage("Email must be valid");
            isError = true;
        }

        if(!customerPhone.matches("[(]\\d{3}[)]\\d{3}[-]\\d{4}")){
            displayErrorMessage("Phone Number must be in form\n(XXX)XXX-XXXX");
            isError = true;
        }

        if(!(paymentType.equals("Cash") || paymentType.equals("Check"))){
            displayErrorMessage("Payment Type must be \"Cash\" or \"Check\"");
            isError = true;
        }

        if(paymentType.equals("") || customerName.equals("") || customerPhone.equals("") || customerEmail.equals("")){
            displayErrorMessage("first and last Name fields must not be null");
            isError = true;
        }

//        int yearInt = 0;
//        try {
//            yearInt = Integer.parseInt(DOB);
//        } catch(Exception e){
//            displayErrorMessage("Date must be an integer" + " " + middleName + " " + yearInt);
//            isError = true;
//        }
//
//        if(yearInt < 1800 || yearInt > 2020){
//            displayErrorMessage("Date must be an integer between 1800 and 2020, inclusive" + " " + middleName + " " + yearInt);
//            isError = true;
//        }

        if(!isError) {
            Properties props = new Properties();
            props.setProperty("TransactionAmount", (String)myModel.getState("TransactionAmount"));
            props.setProperty("Barcode", (String)myModel.getState("Barcode"));
            props.setProperty("TransactionType", "Tree Sale");

            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String dateStatusUpdated = df.format(date);

            props.setProperty("DateStatusUpdated", dateStatusUpdated);
            props.setProperty("TransactionDate", dateStatusUpdated);

            String time = getNonMilitary(LocalTime.now().toString().substring(0,5));

            props.setProperty("TransactionTime", time);

            props.setProperty("PaymentMethod", paymentType);
            props.setProperty("CustomerName", customerName);
            props.setProperty("CustomerEmail", customerEmail);
            props.setProperty("CustomerPhone", customerPhone);
            Session session = new SessionCollection().getActiveSession();

            props.setProperty("SessionID", (String)session.getState("ID"));


            myModel.stateChangeRequest("ProcessTransaction", props);


            try{
                Tree tree = new Tree((String)props.getProperty("Barcode"));
                tree.updateState("Status", "Sold");
                tree.stateChangeRequest("UpdateTree", null);
            }catch(InvalidPrimaryKeyException e){
                e.printStackTrace();
            }
            displayMessage("Transaction added Successfully");


        }

    }

    private String getNonMilitary(String time) {
        if(time.substring(0,2).compareTo("12") > 0){
            int newTime =  Integer.parseInt(time.substring(0,2)) - 12;
            time = newTime + time.substring(2) + "pm";

            if(newTime < 10){
                time = "0" + time;
            }
        }else{
            time += "am";
        }
        return time;
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
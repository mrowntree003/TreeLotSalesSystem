package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.ScoutCollection;
import edu.brockport.treelotsales.model.Session;
import edu.brockport.treelotsales.model.Shift;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class SessionView extends View{
    private Label startTimeLabel;
    private Label endTimeLabel;
    private Label startingCashLabel;

    private TextField startTimeTF;
    private TextField endTimeTF;
    private TextField startingCashTF;


    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public SessionView(IModel session){
        super(session, "SessionView");

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

        startTimeLabel = new Label("Start Time:");
        endTimeLabel = new Label("End Time:");
        startingCashLabel = new Label("Starting Cash:");

        labelsBox.getChildren().addAll(startTimeLabel, endTimeLabel, startingCashLabel);

        startTimeTF = new TextField();
        endTimeTF = new TextField();
        startingCashTF = new TextField();

        tfBox.getChildren().addAll(startTimeTF, endTimeTF, startingCashTF);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle(){
        Text titleText = new Text("Open a Shift");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);

        return titleText;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    private void verifyInputs(){
        String startTime = startTimeTF.getText();
        String endTime = endTimeTF.getText();
        String startingCash = startingCashTF.getText();
        if(startTime.length() == 6){
            startTime = "0" + startTime;
        }

        if(endTime.length() == 6){
            endTime = "0" + endTime;
        }

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String startDate =  df.format(date);

        boolean isError = false;

        if(compareTimes(startTime, endTime) >= 0){
            displayErrorMessage("Start time must be before end time");
            isError = true;
        }

        if(!startTime.matches("\\d\\d[:]\\d\\d(am|pm)") || !endTime.matches("\\d\\d[:]\\d\\d(am|pm)")){
            displayErrorMessage("Times must be in the form HH:MMam/pm");
            isError = true;
        }

        if(startTime.equals("") || endTime.equals("") || startingCash.equals("")){
            displayErrorMessage("You must enter all values");
            isError = true;
        }


        if(!isError) {
            Properties props = new Properties();
            props.setProperty("StartTime", startTime);
            props.setProperty("EndTime", endTime);
            props.setProperty("StartingCash", startingCash);
            props.setProperty("StartDate", startDate);

            System.out.print("***********");

            myModel.stateChangeRequest("ProcessSession", props);
            displayMessage("");
            myModel.stateChangeRequest("SelectScouts", null);
        }

    }

    private static int compareTimes(String t1, String t2){

        if(t1.substring(5).equals(t2.substring(5))){
            return t1.substring(0,5).compareTo(t2.substring(0,5));
        }else{
            return t1.substring(5).compareTo(t2.substring(5));
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

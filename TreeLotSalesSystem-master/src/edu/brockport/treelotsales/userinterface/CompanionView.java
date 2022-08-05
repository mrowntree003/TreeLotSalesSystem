package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.Session;
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
import java.util.Date;
import java.util.Properties;

public class CompanionView extends View{
    private Label companionNameLabel;
    private Label endingTimeLabel;

    private TextField companionNameTF;
    private TextField endingTimeTF;

    private Button submitButton;
    private Button cancelButton;

    private MessageView statusLog;

    public CompanionView(IModel session){
        super(session, "CompanionView");

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

        companionNameLabel = new Label("Companion Name:");
        endingTimeLabel = new Label("Ending Time:");

        labelsBox.getChildren().addAll(companionNameLabel, endingTimeLabel);

        companionNameTF = new TextField();
        endingTimeTF = new TextField();

        tfBox.getChildren().addAll(companionNameTF, endingTimeTF);

        mainBox.getChildren().addAll(labelsBox, tfBox);
        return mainBox;
    }

    private Node createTitle(){
        Text titleText = new Text("Shift Information");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);

        return titleText;
    }

    @Override
    public void updateState(String key, Object value) {

    }

    private void verifyInputs(){
        String companion = companionNameTF.getText();
        String endTime = endingTimeTF.getText();
        if(endTime.length() == 6){
            endTime = "0" + endTime;
        }

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String startDate =  df.format(date);

        boolean isError = false;

        if(!endTime.matches("\\d\\d[:]\\d\\d(am|pm)")){
            displayErrorMessage("Times must be in the form HH:MMam/pm");
            isError = true;
        }

        if(companion.equals("") || endTime.equals("")){
            displayErrorMessage("You must enter all values");
            isError = true;
        }


        if(!isError) {
            Properties props = new Properties();
            props.setProperty("SessionID", (String)myModel.getState("SessionID"));
            props.setProperty("ScoutID", (String)myModel.getState("ScoutID"));
            props.setProperty("StartTime", (String)myModel.getState("StartTime"));
            props.setProperty("CompanionName", companion);
            props.setProperty("EndTime", endTime);

            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            String st = (String) myModel.getState("StartTime");
            String startTime = st.substring(0,5) + " " + st.substring(5).toUpperCase();
            endTime = endTime.substring(0,5) + " " + endTime.substring(5). toUpperCase();

            long difference = 0;
            try {
                Date date1 = format.parse(startTime);
                Date date2 = format.parse(endTime);
                difference = (((date2.getTime() - date1.getTime())/1000)/60)/60;

            } catch(Exception e){
                e.printStackTrace();
            }
            System.out.println(difference);
            props.setProperty("CompanionHours", "" + difference);

            myModel.stateChangeRequest("ProcessShift", props);
            displayMessage("");

            try {
                new Session((String)myModel.getState("SessionID")).stateChangeRequest("SelectScouts", null);
            } catch (InvalidPrimaryKeyException ex) {
                ex.printStackTrace();
            }
        }

    }

//    private static int compareTimes(String t1, String t2){
//        if(t1.substring(5).equals(t2.substring(5))){
//            return t1.compare
//        }
//    }

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

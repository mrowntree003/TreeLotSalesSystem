package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.Session;
import edu.brockport.treelotsales.model.TLC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class CloseSessionView extends View {
    private TextArea notesTA;
    private Button doneButton;

    private MessageView statusLog;

    public CloseSessionView(IModel session) {
        super(session, "CloseSession");

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
        doneButton = new Button("Done");
        doneButton.setOnAction(e -> {
            if(!notesTA.getText().trim().isEmpty()){
                ((Session)myModel).updateState("Notes", notesTA.getText());
            }
            myModel.stateChangeRequest("Closed", null);
        });


        buttonBox.getChildren().addAll(doneButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private Node createBody() {
        HBox mainBox = new HBox(10);
        notesTA = new TextArea();

        mainBox.getChildren().add(notesTA);
        return mainBox;
    }

    private Node createTitle() {
        Text titleText = new Text("Notes for Session");
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
}

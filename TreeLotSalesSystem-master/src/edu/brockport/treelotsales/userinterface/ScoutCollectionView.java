package edu.brockport.treelotsales.userinterface;

// system imports
import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.model.Scout;
import edu.brockport.treelotsales.model.ScoutCollection;
import edu.brockport.treelotsales.model.TLC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Vector;
import java.util.Enumeration;

// project imports
import edu.brockport.treelotsales.impresario.IModel;

//==============================================================================
public class ScoutCollectionView extends View
{
    protected TableView<ScoutTableModel> tableOfScouts;
    protected Button cancelButton;
    protected Button updateButton;
    protected Button deleteButton;
//    protected Button submitButton;

    protected MessageView statusLog;


    //--------------------------------------------------------------------------
    public ScoutCollectionView(IModel sc)
    {
        super(sc, "ScoutCollectionView");

        // create a container for showing the contents
        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        // create our GUI components, add them to this panel
        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());

        // Error message area
        container.getChildren().add(createStatusLog("                                            "));

        getChildren().add(container);

        populateFields();
    }

    //--------------------------------------------------------------------------
    protected void populateFields()
    {
        getEntryTableModelValues();
    }

    //--------------------------------------------------------------------------
    protected void getEntryTableModelValues()
    {

        ObservableList<ScoutTableModel> tableData = FXCollections.observableArrayList();
        try
        {
            ScoutCollection scoutCollection = (ScoutCollection)myModel.getState("ScoutList");


            Vector entryList = (Vector)scoutCollection.getState("Scouts");
            Enumeration entries = entryList.elements();

            while (entries.hasMoreElements())
            {
                Scout nextScout = (Scout)(entries.nextElement());
                Vector<String> view = nextScout.getEntryListView();

                // add this list entry to the list
                ScoutTableModel nextTableRowData = new ScoutTableModel(view);
                tableData.add(nextTableRowData);

            }

            tableOfScouts.setItems(tableData);
        }
        catch (Exception e) {//SQLException e) {
            // Need to handle this exception
        }
    }

    // Create the title container
    //-------------------------------------------------------------
    private Node createTitle()
    {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text(" Tree Lot Sales System ");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setWrappingWidth(300);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);
        container.getChildren().add(titleText);

        return container;
    }

    // Create the main form content
    //-------------------------------------------------------------
    private VBox createFormContent()
    {
        VBox vbox = new VBox(10);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text prompt = new Text("LIST OF SCOUTS");
        prompt.setWrappingWidth(350);
        prompt.setTextAlignment(TextAlignment.CENTER);
        prompt.setFill(Color.BLACK);
        grid.add(prompt, 0, 0, 2, 1);

        tableOfScouts = new TableView<ScoutTableModel>();
        tableOfScouts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn IDColumn = new TableColumn("ID") ;
        IDColumn.setMinWidth(100);
        IDColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("id"));

        TableColumn lastNameColumn = new TableColumn("Last Name") ;
        lastNameColumn.setMinWidth(100);
        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("lastName"));

        TableColumn firstNameColumn = new TableColumn("First Name") ;
        firstNameColumn.setMinWidth(100);
        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("firstName"));

        TableColumn middleNameColumn = new TableColumn("Middle Name") ;
        middleNameColumn.setMinWidth(100);
        middleNameColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("middleName"));

        TableColumn DOBColumn = new TableColumn("DOB") ;
        DOBColumn.setMinWidth(100);
        DOBColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("dateOfBirth"));

        TableColumn phoneNumberColumn = new TableColumn("Phone Number") ;
        phoneNumberColumn.setMinWidth(100);
        phoneNumberColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("phoneNumber"));

        TableColumn emailColumn = new TableColumn("Email") ;
        emailColumn.setMinWidth(100);
        emailColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("email"));

        TableColumn troopIDColumn = new TableColumn("Troop ID") ;
        troopIDColumn.setMinWidth(100);
        troopIDColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("troopID"));

        TableColumn statusColumn = new TableColumn("Status") ;
        statusColumn.setMinWidth(100);
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("status"));

        TableColumn dateStatusUpdatedColumn = new TableColumn("Date Status Updated") ;
        dateStatusUpdatedColumn.setMinWidth(100);
        dateStatusUpdatedColumn.setCellValueFactory(
                new PropertyValueFactory<ScoutTableModel, String>("dateStatusUpdated"));

        tableOfScouts.getColumns().addAll(IDColumn,
                lastNameColumn, firstNameColumn, middleNameColumn, DOBColumn,
                phoneNumberColumn, emailColumn, troopIDColumn, statusColumn, dateStatusUpdatedColumn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(115, 150);
        scrollPane.setContent(tableOfScouts);

        cancelButton = new Button("Done");
        updateButton = new Button("Update Scout");
        deleteButton = new Button("Delete Scout");

        //needs to eventually be fixed to go back to scout search view
        //and fixed to be done with low coupling
        cancelButton.setOnAction(e -> {
            TLC tlc = new TLC();
            tlc.stateChangeRequest("Done", "");
        });

        updateButton.setOnAction(e -> {
            try {
                Scout selectedScout = new Scout(tableOfScouts.getSelectionModel().getSelectedItem().getId());
                selectedScout.createAndShowUpdateScoutView();
            } catch (InvalidPrimaryKeyException ex) {
                ex.printStackTrace();
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                Scout selectedScout = new Scout(tableOfScouts.getSelectionModel().getSelectedItem().getId());
                selectedScout.createAndShowDeleteScoutView();
            } catch (InvalidPrimaryKeyException ex) {
                ex.printStackTrace();
            }
        });


        HBox btnContainer = new HBox(100);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(updateButton, deleteButton, cancelButton);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(scrollPane);
        vbox.getChildren().add(btnContainer);

        return vbox;
    }



    //--------------------------------------------------------------------------
    public void updateState(String key, Object value){}

    //--------------------------------------------------------------------------
    protected MessageView createStatusLog(String initialMessage)
    {
        statusLog = new MessageView(initialMessage);

        return statusLog;
    }


    /**
     * Display info message
     */
    //----------------------------------------------------------
    public void displayMessage(String message)
    {
        statusLog.displayMessage(message);
    }

    /**
     * Clear error message
     */
    //----------------------------------------------------------
    public void clearErrorMessage()
    {
        statusLog.clearErrorMessage();
    }

//	//--------------------------------------------------------------------------
//	public void mouseClicked(MouseEvent click)
//	{
//		if(click.getClickCount() >= 2)
//		{
//			processAccountSelected();
//		}
//	}


}

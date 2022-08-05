package edu.brockport.treelotsales.userinterface;

import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Enumeration;
import java.util.Vector;

public class TreeTypeCollectionView extends View{
    private TableView<TreeTypeTableModel> tableOfTreeTypes;
    private Button cancelButton;
    private Button updateButton;
    private MessageView statusLog;

    public TreeTypeCollectionView(IModel treeTypeCollection){
        super(treeTypeCollection, "TreeTypeCollectionView");

        VBox container = new VBox(10);
        container.setPadding(new Insets(15, 5, 5, 5));

        container.getChildren().add(createTitle());
        container.getChildren().add(createFormContent());
        container.getChildren().add(createStatusLog("                          "));
        getChildren().add(container);

        populateFields();
    }

    private void populateFields(){
        getEntryTableModelValues();
    }

    private void getEntryTableModelValues(){
        ObservableList<TreeTypeTableModel> tableData = FXCollections.observableArrayList();
        try
        {
            TreeTypeCollection treeTypeCollection = (TreeTypeCollection) myModel.getState("TreeTypeList");


            Vector entryList = (Vector)treeTypeCollection.getState("TreeTypes");
            Enumeration entries = entryList.elements();

            while (entries.hasMoreElements())
            {
                TreeType treeType = (TreeType)(entries.nextElement());
                Vector<String> view = treeType.getEntryListView();

                // add this list entry to the list
                TreeTypeTableModel nextTableRowData = new TreeTypeTableModel(view);
                tableData.add(nextTableRowData);

            }

            tableOfTreeTypes.setItems(tableData);
        }
        catch (Exception e) {//SQLException e) {
            // Need to handle this exception
        }
    }

    @Override
    public void updateState(String key, Object value) {

    }

    private Node createTitle()
    {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Text titleText = new Text(" Tree Lot Sales System - Tree Types");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setWrappingWidth(300);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.DARKGREEN);
        container.getChildren().add(titleText);

        return container;
    }

    private VBox createFormContent(){
        VBox vbox = new VBox(10);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text prompt = new Text("LIST OF TREE TYPES");
        prompt.setWrappingWidth(350);
        prompt.setTextAlignment(TextAlignment.CENTER);
        prompt.setFill(Color.BLACK);
        grid.add(prompt, 0, 0, 2, 1);

        tableOfTreeTypes = new TableView<TreeTypeTableModel>();
        tableOfTreeTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn IDColumn = new TableColumn("ID") ;
        IDColumn.setMinWidth(100);
        IDColumn.setCellValueFactory(
                new PropertyValueFactory<TreeTypeTableModel, String>("id"));

        TableColumn typeDescriptionColumn = new TableColumn("Type Description") ;
        typeDescriptionColumn.setMinWidth(100);
        typeDescriptionColumn.setCellValueFactory(
                new PropertyValueFactory<TreeTypeTableModel, String>("typeDescription"));

        TableColumn costColumn = new TableColumn("Cost") ;
        costColumn.setMinWidth(100);
        costColumn.setCellValueFactory(
                new PropertyValueFactory<TreeTypeTableModel, String>("cost"));

        TableColumn barcodePrefixColumn = new TableColumn("Barcode Prefix") ;
        barcodePrefixColumn.setMinWidth(100);
        barcodePrefixColumn.setCellValueFactory(
                new PropertyValueFactory<TreeTypeTableModel, String>("barcodePrefix"));

        tableOfTreeTypes.getColumns().addAll(IDColumn, typeDescriptionColumn, costColumn, barcodePrefixColumn);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(115, 150);
        scrollPane.setContent(tableOfTreeTypes);

        cancelButton = new Button("Done");
        updateButton = new Button("Update Tree Type");

        //needs to eventually be fixed to go back to scout search view
        //and fixed to be done with low coupling
        cancelButton.setOnAction(e -> {
            TLC tlc = new TLC();
            tlc.stateChangeRequest("Done", "");
        });

        updateButton.setOnAction(e -> {
            try {
                TreeType selectedTreeType = new TreeType(tableOfTreeTypes.getSelectionModel().getSelectedItem().getId());
                selectedTreeType.createAndShowUpdateTreeTypeView();
            } catch (InvalidPrimaryKeyException ex) {
                ex.printStackTrace();
            }
        });


        HBox btnContainer = new HBox(100);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(updateButton, cancelButton);

        vbox.getChildren().add(grid);
        vbox.getChildren().add(scrollPane);
        vbox.getChildren().add(btnContainer);

        return vbox;
    }

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
}

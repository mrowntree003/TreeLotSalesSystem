package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.exception.InvalidPrimaryKeyException;
import edu.brockport.treelotsales.impresario.IModel;
import edu.brockport.treelotsales.userinterface.View;
import edu.brockport.treelotsales.userinterface.ViewFactory;
import edu.brockport.treelotsales.userinterface.WindowPosition;
import javafx.scene.Scene;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class TreeType extends EntityBase implements IModel {
    private static final String myTableName = "TreeType";
    private String updateStatusMessage = "";

    private Properties dependencies;

    public TreeType(String ID) throws InvalidPrimaryKeyException {
        super(myTableName);

        setDependencies();

        String query = "SELECT * FROM " + myTableName + " WHERE (ID = " + ID + ")";

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if (size == 0) {
                throw new InvalidPrimaryKeyException("No tree types matching ID : "
                        + ID + " found.");

            } else if (size == 1) {
                Properties retrievedBookData = allDataRetrieved.elementAt(0);
                persistentState = new Properties();

                Enumeration allKeys = retrievedBookData.propertyNames();

                while (allKeys.hasMoreElements()) {
                    String nextKey = (String) allKeys.nextElement();
                    String nextValue = retrievedBookData.getProperty(nextKey);

                    if (nextValue != null) {
                        persistentState.setProperty(nextKey, nextValue);
                    }
                }
            } else {
                throw new InvalidPrimaryKeyException("Multiple tree types matching ID : "
                        + ID + " found.");
            }
        } else {
            throw new InvalidPrimaryKeyException("No tree types matching ID : "
                    + ID + " found.");
        }
    }

    public TreeType(Properties props) {
        super(myTableName);

        setDependencies();
        persistentState = new Properties();
        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements()) {
            String nextKey = (String) allKeys.nextElement();
            String nextValue = props.getProperty(nextKey);

            if (nextValue != null) {
                persistentState.setProperty(nextKey, nextValue);
            }
        }
    }

    public TreeType() {
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
    }

    public void setDependencies() {
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }


    @Override
    public Object getState(String key) {
        if (key.equals("UpdateStatusMessage")) {
            return updateStatusMessage;
        } else {
            return persistentState.getProperty(key);
        }
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if (key.equals("ProcessTreeType")) {
            processTreeType(value);
        } else {
            myRegistry.updateSubscribers(key, this);
        }
    }

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }

    private void processTreeType(Object props) {
        this.persistentState = (Properties) (props);
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("ID") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("ID", persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Data for tree type : " + persistentState.getProperty("ID") + " updated successfully in database!";
            } else {
                Integer treeTypeID =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + treeTypeID);
                updateStatusMessage = "Data for new tree type : "
                        + persistentState.getProperty("ID")
                        + "installed successfully in database!";
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
            updateStatusMessage = "Error in installing tree type data in database!";
        }
    }

    public static int compare(TreeType a, TreeType b) {
        String aNum = (String) a.getState("ID");
        String bNum = (String) b.getState("ID");

        return aNum.compareTo(bNum);
    }

    public String toString() {
        return "ID = " + persistentState.getProperty("ID") + "; Type Description = " + persistentState.getProperty("TypeDescription") +
                "; Cost = " + persistentState.getProperty("Cost") + "; Status = " + persistentState.getProperty("Cost") +
                "; Barcode Prefix = " + persistentState.getProperty("BarcodePrefix");
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    public Vector<String> getEntryListView() {
        Vector<String> entryList = new Vector();

        entryList.add(persistentState.getProperty("ID"));
        entryList.add(persistentState.getProperty("TypeDescription"));
        entryList.add(persistentState.getProperty("Cost"));
       // entryList.add(persistentState.getProperty("Status"));
        entryList.add(persistentState.getProperty("BarcodePrefix"));
//
        return entryList;
    }

    public void update() {
        updateStateInDatabase();
    }

    public String getUpdateStatusMessage() {
        return updateStatusMessage;
    }


    public void createAndShowAddTreeTypeView(){
            Scene currentScene = myViews.get("TreeTypeView");

            if(currentScene == null){
                View view = ViewFactory.createView("TreeTypeView", this);
                currentScene = new Scene(view);
                myViews.put("TreeTypeView", currentScene);
            }

            myStage.setScene(currentScene);
            myStage.sizeToScene();

            //Place in center
            WindowPosition.placeCenter(myStage);
        }

    public void createAndShowUpdateTreeTypeView(){
        Scene currentScene = myViews.get("UpdateTreeTypeView");

        if(currentScene == null){
            View view = ViewFactory.createView("UpdateTreeTypeView", this);
            currentScene = new Scene(view);
            myViews.put("UpdateTreeTypeView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public static TreeType getTreeTypeWithPrefix(String prefix){
        TreeTypeCollection t = new TreeTypeCollection();
        t.findTreeTypesWithInfo("",prefix);

        for(int i=0; i<t.size(); i++){
            if(((String)t.get(i).getState("BarcodePrefix")).equals(prefix)){
                try{
                    return new TreeType((String)t.get(i).getState("ID"));
                }catch(InvalidPrimaryKeyException e){
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public void createAndShowDeleteTreeTypeView(){
        //to be written at a later date
    }
}

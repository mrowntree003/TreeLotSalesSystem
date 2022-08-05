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

public class Shift extends EntityBase implements IModel {
    private static final String myTableName = "Shift";
    private String updateStatusMessage = "";

    private Properties dependencies;

    public Shift(String ID) throws InvalidPrimaryKeyException {
        super(myTableName);

        setDependencies();

        String query = "SELECT * FROM " + myTableName + " WHERE (ID = " + ID + ")";

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if(allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if(size == 0) {
                throw new InvalidPrimaryKeyException("No shifts matching id : "
                        + ID + " found.");

            } else if(size == 1){
                Properties retrievedBookData = allDataRetrieved.elementAt(0);
                persistentState = new Properties();

                Enumeration allKeys = retrievedBookData.propertyNames();

                while(allKeys.hasMoreElements()) {
                    String nextKey = (String)allKeys.nextElement();
                    String nextValue = retrievedBookData.getProperty(nextKey);

                    if(nextValue != null) {
                        persistentState.setProperty(nextKey,  nextValue);
                    }
                }
            } else {
                throw new InvalidPrimaryKeyException("Multiple shifts matching id : "
                        + ID + " found.");
            }
        } else {
            throw new InvalidPrimaryKeyException("No shift matching id : "
                    + ID + " found.");
        }
    }

    public Shift(Properties props){
        super(myTableName);

        setDependencies();
        persistentState = new Properties();
        Enumeration allKeys = props.propertyNames();
        while (allKeys.hasMoreElements())
        {
            String nextKey = (String)allKeys.nextElement();
            String nextValue = props.getProperty(nextKey);

            if (nextValue != null)
            {
                persistentState.setProperty(nextKey, nextValue);
            }
        }
    }

    public Shift(){
        super(myTableName);
        setDependencies();
        persistentState = new Properties();
    }

    public void setDependencies(){
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }



    @Override
    public Object getState(String key) {
        if(key.equals("UpdateStatusMessage")){
            return updateStatusMessage;
        } else if(key.equals("StartTime") || key.equals("ID") || key.equals("SessionID")
        || key.equals("ScoutID") || key.equals("CompanionName") || key.equals("EndTime")
        || key.equals("CompanionHours")){
            return persistentState.getProperty(key);
        } else{
            return persistentState.getProperty(key);
        }
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if(key.equals("ProcessShift")){
            processShift(value);
        }else{
            myRegistry.updateSubscribers(key, this);
        }
    }

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null)
        {
            mySchema = getSchemaInfo(tableName);
        }
    }

    private void processShift(Object props){
        this.persistentState = (Properties)(props);
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if(persistentState.getProperty("ID") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("ID", persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Shift data for shift number : " + persistentState.getProperty("ID") + " updated successfully in database!";
            } else {
                Integer shiftID =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + shiftID);
                updateStatusMessage = "Shift data for new shift : "
                        + persistentState.getProperty("ID")
                        + "installed successfully in database!";
            }

        } catch(SQLException ex) {
            System.out.println(ex.toString());
            updateStatusMessage = "Error in installing shift data in database!";
        }
    }

    public void createAndShowCompanionView(){
        Scene currentScene = myViews.get("CompanionView");

        if(currentScene == null){
            View view = ViewFactory.createView("CompanionView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("CompanionView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public static int compare(Shift a, Shift b)
    {
        String aNum = (String)a.getState("ID");
        String bNum = (String)b.getState("ID");

        return aNum.compareTo(bNum);
    }

    public String toString() {
        return "ID = " + persistentState.get("ID") + "; Session ID = " + persistentState.getProperty("SessionID") +
                "; Scout ID = " + persistentState.get("ScoutID") + "; Companion Name = " + persistentState.get("CompanionName") +
                "; " + "Start Time = " + persistentState.get("StartTime") + "; End Time = " + persistentState.get("EndTime") +
                "; Companion Hours = " + persistentState.get("CompanionHours");
    }

    public void updateState(String key, Object value) {
        if((key.equals("StartTime") || key.equals("ID") || key.equals("SessionID")
                || key.equals("ScoutID") || key.equals("CompanionName") || key.equals("EndTime")
                || key.equals("CompanionHours"))){
            persistentState.setProperty(key, (String)value);
        }else {
            stateChangeRequest(key, value);
        }
    }

    public Vector<String> getEntryListView(){
        Vector<String> entryList = new Vector();

        entryList.add(persistentState.getProperty("ID"));
        entryList.add(persistentState.getProperty("SessionID"));
        entryList.add(persistentState.getProperty("ScoutID"));
        entryList.add(persistentState.getProperty("CompanionName"));
        entryList.add(persistentState.getProperty("StartTime"));
        entryList.add(persistentState.getProperty("EndTime"));
        entryList.add(persistentState.getProperty("CompanionHours"));

        return entryList;
    }

    public void update(){
        updateStateInDatabase();
    }

    public String getUpdateStatusMessage(){
        return updateStatusMessage;
    }

}

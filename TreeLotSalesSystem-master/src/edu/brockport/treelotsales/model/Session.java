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

public class Session extends EntityBase implements IModel {
    private static final String myTableName = "Session";
    private String updateStatusMessage = "";

    private Properties dependencies;

    public Session(String ID) throws InvalidPrimaryKeyException {
        super(myTableName);

        setDependencies();

        String query = "SELECT * FROM " + myTableName + " WHERE (ID = " + ID + ")";

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if(allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if(size == 0) {
                throw new InvalidPrimaryKeyException("No session matching id : "
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
                throw new InvalidPrimaryKeyException("Multiple sessions matching id : "
                        + ID + " found.");
            }
        } else {
            throw new InvalidPrimaryKeyException("No session matching id : "
                    + ID + " found.");
        }
    }

    public Session(Properties props){
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

    public Session(){
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
        } else if(key.equals("ID") || key.equals("StartDate") || key.equals("StartTime")
        || key.equals("EndTime") || key.equals("StartingCash") || key.equals("EndingCash")
        || key.equals("TotalCheckTransactionAmount") || key.equals("Notes")) {
            return persistentState.getProperty(key);
        }else{
            return persistentState.getProperty(key);
        }
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if(key.equals("ProcessSession")){
            processSession(value);
        }else if(key.equals("SelectScouts")){
            createAndShowSelectScoutsView();
        }else if(key.equals("Closed")){
            createAndShowSessionClosedView();
        }else if(key.equals("Update")){
            updateStateInDatabase();
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

    private void processSession(Object props){
        this.persistentState = (Properties)(props);
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if(persistentState.getProperty("ID") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("ID", persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Session data for session number : " + persistentState.getProperty("ID") + " updated successfully in database!";
            } else {
                Integer sessionID =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + sessionID);
                updateStatusMessage = "Session data for new session : "
                        + persistentState.getProperty("ID")
                        + "installed successfully in database!";
            }

        } catch(SQLException ex) {
            System.out.println(ex.toString());
            updateStatusMessage = "Error in installing shift data in database!";
        }
    }



    public static int compare(Session a, Session b)
    {
        String aNum = (String)a.getState("ID");
        String bNum = (String)b.getState("ID");

        return aNum.compareTo(bNum);
    }

    public String toString() {
        return "ID = " + persistentState.getProperty("ID") + "; Start Date = " + persistentState.getProperty("StartDate") +
                "; " + "Start Time = " + persistentState.getProperty("StartTime") + "; End Time = " + persistentState.getProperty("EndTime") +
                "; Starting Cash = " + persistentState.getProperty("StartingCash") + "; Ending Cash = " + persistentState.getProperty("EndingCash") +
                "; Total Check Transactions Amount = " + persistentState.getProperty("TotalCheckTransactionsAmount") +
                "; Notes = " + persistentState.getProperty("Notes");
    }

    public void createAndShowSelectScoutsView(){
        Scene currentScene = myViews.get("SelectScoutsView");

        if(currentScene == null){
            View view = ViewFactory.createView("SelectScoutsView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("SelectScoutView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public void createAndShowSessionClosedView(){
        Scene currentScene = myViews.get("SessionClosedView");

        if(currentScene == null){
            View view = ViewFactory.createView("SessionClosedView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("SessionClosedView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public void createAndShowCloseSessionView(){
        Scene currentScene = myViews.get("CloseSessionView");

        if(currentScene == null){
            View view = ViewFactory.createView("CloseSessionView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("CloseSessionView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }


    public void updateState(String key, Object value) {
        if(key.equals("ID") || key.equals("StartDate") || key.equals("StartTime")
                || key.equals("EndTime") || key.equals("StartingCash") || key.equals("EndingCash")
                || key.equals("TotalCheckTransactionsAmount") || key.equals("Notes")){
            persistentState.setProperty(key, (String)value);
        }else{
            stateChangeRequest(key, value);
        }
    }

    public Vector<String> getEntryListView(){
        Vector<String> entryList = new Vector();

        entryList.add(persistentState.getProperty("ID"));
        entryList.add(persistentState.getProperty("StartDate"));
        entryList.add(persistentState.getProperty("StartTime"));
        entryList.add(persistentState.getProperty("EndTime"));
        entryList.add(persistentState.getProperty("StartingCash"));
        entryList.add(persistentState.getProperty("EndingCash"));
        entryList.add(persistentState.getProperty("TotalCheckTransactionsAmount"));
        entryList.add(persistentState.getProperty("Notes"));

        return entryList;
    }

    public void createAndShowShiftView(){
        Scene currentScene = myViews.get("ShiftView");

        if(currentScene == null){
            View view = ViewFactory.createView("ShiftView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("ShiftView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public void update(){
        updateStateInDatabase();
    }

    public String getUpdateStatusMessage(){
        return updateStatusMessage;
    }

}

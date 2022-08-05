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

public class Scout extends EntityBase implements IModel {
    private static final String myTableName = "Scout";
    private String updateStatusMessage = "";

    private Properties dependencies;

    public Scout(String Id) throws InvalidPrimaryKeyException {
        super(myTableName);

        setDependencies();

        String query = "SELECT * FROM " + myTableName + " WHERE (ID = " + Id + ")";

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if(allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if(size == 0) {
                throw new InvalidPrimaryKeyException("No scouts matching id : "
                        + Id + " found.");

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
                throw new InvalidPrimaryKeyException("Multiple scouts matching id : "
                        + Id + " found.");
            }
        } else {
            throw new InvalidPrimaryKeyException("No scout matching id : "
                    + Id + " found.");
        }
    }

    public Scout(Properties props){
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

    public Scout(){
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
        } else {
            return persistentState.getProperty(key);
        }
    }

    @Override
    public void stateChangeRequest(String key, Object value) {
        if(key.equals("ProcessScout")){
            processScout(value);
        }else if(key.equals("DeleteScout")){
            deleteScout();
        }else{
            myRegistry.updateSubscribers(key, this);
        }
    }

    private void deleteScout() {
        updateState("Status", "Inactive");
        updateStateInDatabase();
    }

    @Override
    protected void initializeSchema(String tableName) {
        if (mySchema == null)
        {
            mySchema = getSchemaInfo(tableName);
        }
    }

    private void processScout(Object props){
        this.persistentState = (Properties)(props);
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if(persistentState.getProperty("ID") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("ID", persistentState.getProperty("ID"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Scout data for scout number : " + persistentState.getProperty("ID") + " updated successfully in database!";
            } else {
                Integer scoutID =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("ID", "" + scoutID);
                updateStatusMessage = "Scout data for new scout : "
                        + persistentState.getProperty("ID")
                        + "installed successfully in database!";
            }

        } catch(SQLException ex) {
            System.out.println(ex.toString());
            updateStatusMessage = "Error in installing scout data in database!";
        }
    }

    public static int compare(Scout a, Scout b)
    {
        String aNum = (String)a.getState("ID");
        String bNum = (String)b.getState("ID");

        return aNum.compareTo(bNum);
    }

    public String toString() {
        return "ID = " + persistentState.getProperty("ID") + "; First Name = " + persistentState.getProperty("FirstName") +
                "; Last Name = " + persistentState.getProperty("LastName") + "; MiddleName = " + persistentState.getProperty("MiddleName") +
                "; Date Of Birth = " + persistentState.getProperty("DateOfBirth") + "; Phone Number = " + persistentState.getProperty("PhoneNumber") +
                "; Email = " + persistentState.getProperty("Email") + "Troop ID = " + persistentState.getProperty("TroopID") +
                "; Status = " + persistentState.getProperty("Status") + "; Date Status Updated = " + persistentState.getProperty("DateStatusUpdated");
    }

    public void updateState(String key, Object value) {
        if(key.equals("DateOfBirth") || key.equals("DateStatusUpdated") || key.equals("Email") ||
                key.equals("FirstName") || key.equals("LastName") ||key.equals("MiddleName") ||key.equals("PhoneNumber") ||
                key.equals("Status") ||key.equals("TroopID")){
            persistentState.setProperty(key, (String)value);
        }

        stateChangeRequest(key, value);
    }

    public Vector<String> getEntryListView(){
        Vector<String> entryList = new Vector();

        entryList.add(persistentState.getProperty("ID"));
        entryList.add(persistentState.getProperty("FirstName"));
        entryList.add(persistentState.getProperty("LastName"));
        entryList.add(persistentState.getProperty("MiddleName"));
        entryList.add(persistentState.getProperty("DateOfBirth"));
        entryList.add(persistentState.getProperty("PhoneNumber"));
        entryList.add(persistentState.getProperty("Email"));
        entryList.add(persistentState.getProperty("TroopID"));
        entryList.add(persistentState.getProperty("Status"));
        entryList.add(persistentState.getProperty("DateStatusUpdated"));

        return entryList;
    }

    public void createAndShowScoutView(){
        Scene currentScene = myViews.get("ScoutView");

        if(currentScene == null){
            View view = ViewFactory.createView("ScoutView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("ScoutView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public void createAndShowUpdateScoutView(){
        Scene currentScene = myViews.get("UpdateScoutView");

        if(currentScene == null){
            View view = ViewFactory.createView("UpdateScoutView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("UpdateScoutView", currentScene);
        }

        myStage.setScene(currentScene);
        myStage.sizeToScene();

        //Place in center
        WindowPosition.placeCenter(myStage);
    }

    public void createAndShowDeleteScoutView(){
        Scene currentScene = myViews.get("DeleteScoutView");

        if(currentScene == null){
            View view = ViewFactory.createView("DeleteScoutView", this);
            // if (view == null) System.out.println("Null book view");
            currentScene = new Scene(view);
            myViews.put("DeleteScoutView", currentScene);
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

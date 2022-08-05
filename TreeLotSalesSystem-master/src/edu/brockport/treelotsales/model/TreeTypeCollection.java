package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.impresario.IView;

import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class TreeTypeCollection extends EntityBase implements IView {
    private static final String tableName = "TreeType";

    private Vector<TreeType> treeTypes;

    public TreeTypeCollection() {
        super(tableName);
        treeTypes = new Vector<TreeType>();
    }

    private void addTreeType(TreeType s)
    {
        //accounts.add(a);
        int index = findIndexToAdd(s);
        treeTypes.insertElementAt(s,index); // To build up a collection sorted on some key
    }

    public void findTreeTypesWithInfo(String cost, String barcodePrefix) {
        String query = "SELECT * FROM TreeType";
        Properties values = new Properties();

        if(!(cost.equals("") && barcodePrefix.equals(""))){

            query += " WHERE ";

            if (!cost.equals("")) {
                values.put("Cost", cost);
            }
            if (!barcodePrefix.equals("")) {
                values.put("BarcodePrefix", barcodePrefix);
            }

            Set<String> keySet = values.stringPropertyNames();
            Object[] keys = keySet.toArray();

            for(int i = 0; i < keys.length; i++){
                query += keys[i] + " LIKE '%" + values.get(keys[i]) + "%'";

                if(i < keys.length - 1){
                    query += " OR ";
                }
            }
        }

        System.out.println(query);
        Vector allDataRetrieved = getSelectQueryResult(query);
        System.out.println(allDataRetrieved);

        for(int i = 0; i < allDataRetrieved.size(); i++) {
            TreeType s = new TreeType((Properties)(allDataRetrieved.get(i)));
            addTreeType(s);
        }

    }

    public void getAll(){
        String query = "SELECT * FROM TreeType";

        Vector allDataRetrieved = getSelectQueryResult(query);

        for(int i = 0; i < allDataRetrieved.size(); i++) {
            TreeType t = new TreeType((Properties)(allDataRetrieved.get(i)));
            addTreeType(t);
        }
    }

    private int findIndexToAdd(TreeType a)
    {
        //users.add(u);
        int low=0;
        int high = treeTypes.size()-1;
        int middle;

        while (low <=high)
        {
            middle = (low+high)/2;

            TreeType midSession = treeTypes.elementAt(middle);

            int result = TreeType.compare(a,midSession);

            if (result ==0)
            {
                return middle;
            }
            else if (result<0)
            {
                high=middle-1;
            }
            else
            {
                low=middle+1;
            }


        }
        return low;
    }

    @Override
    public Object getState(String key)
    {
        if (key.equals("TreeTypes"))
            return treeTypes;
        else
        if (key.equals("TreeTypeList"))
            return this;
        return null;
    }

    @Override
    public void stateChangeRequest(String key, Object value)
    {
        myRegistry.updateSubscribers(key, this);
    }

    @Override
    protected void initializeSchema(String tableName)
    {
        if (mySchema == null)
        {
            mySchema = getSchemaInfo(tableName);
        }
    }

    public String toString() {
        String s = "";

        for(TreeType b : treeTypes) {
            s += b.toString() + "\n";
        }

        return s;
    }

    public TreeType get(int i){
        return treeTypes.get(i);
    }

    public int size(){
        return treeTypes.size();
    }


    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}

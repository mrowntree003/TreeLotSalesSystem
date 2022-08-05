package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.impresario.IView;

import java.util.Properties;
import java.util.Vector;

public class SessionCollection extends EntityBase implements IView {
    private static final String tableName = "Session";

    private Vector<Session> sessions;

    public SessionCollection() {
        super(tableName);
        sessions = new Vector<Session>();
    }

    private void addSession(Session s)
    {
        //accounts.add(a);
        int index = findIndexToAdd(s);
        sessions.insertElementAt(s,index); // To build up a collection sorted on some key
    }

    public Session getActiveSession(){
        String query = "SELECT * FROM Session WHERE EndingCash IS NULL";
        Vector allDataRetrieved = getSelectQueryResult(query);

        Session s = (allDataRetrieved.size() == 0) ? null : new Session((Properties)(allDataRetrieved.get(0)));

        return s;
    }

    private int findIndexToAdd(Session a)
    {
        //users.add(u);
        int low=0;
        int high = sessions.size()-1;
        int middle;

        while (low <=high)
        {
            middle = (low+high)/2;

            Session midSession = sessions.elementAt(middle);

            int result = Session.compare(a,midSession);

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
        if (key.equals("Sessions"))
            return sessions;
        else
        if (key.equals("SessionList"))
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

        for(Session b : sessions) {
            s += b.toString() + "\n";
        }

        return s;
    }


    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}

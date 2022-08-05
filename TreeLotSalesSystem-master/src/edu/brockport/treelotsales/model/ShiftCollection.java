package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.impresario.IView;

import java.util.Properties;
import java.util.Vector;

public class ShiftCollection extends EntityBase implements IView{
    private static final String tableName = "Shift";

    private Vector<Shift> shifts;

    public ShiftCollection(){
        super(tableName);
        shifts = new Vector<>();
    }

    private void addShift(Shift s)
    {
        //accounts.add(a);
        int index = findIndexToAdd(s);
        shifts.insertElementAt(s,index); // To build up a collection sorted on some key
    }

    private int findIndexToAdd(Shift a)
    {
        //users.add(u);
        int low=0;
        int high = shifts.size()-1;
        int middle;

        while (low <=high)
        {
            middle = (low+high)/2;

            Shift midSession = shifts.elementAt(middle);

            int result = Shift.compare(a,midSession);

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
        if (key.equals("Shifts"))
            return shifts;
        else
        if (key.equals("ShiftList"))
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

        for(Shift b: shifts) {
            s += b.toString() + "\n";
        }

        return s;
    }


    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}

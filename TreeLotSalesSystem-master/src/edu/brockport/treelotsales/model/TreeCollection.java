package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.impresario.IView;

import java.util.Properties;
import java.util.Vector;

public class TreeCollection extends EntityBase implements IView {
    private static final String tableName = "Tree";

    private Vector<Tree> trees;

    public TreeCollection() {
        super(tableName);
        trees = new Vector<Tree>();
    }

    private void addTree(Tree s)
    {
        //accounts.add(a);
        int index = findIndexToAdd(s);
        trees.insertElementAt(s,index); // To build up a collection sorted on some key
    }

    private int findIndexToAdd(Tree a)
    {
        //users.add(u);
        int low=0;
        int high = trees.size()-1;
        int middle;

        while (low <=high)
        {
            middle = (low+high)/2;

            Tree midSession = trees.elementAt(middle);

            int result = Tree.compare(a,midSession);

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
        if (key.equals("Trees"))
            return trees;
        else
        if (key.equals("TreeList"))
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

        for(Tree b : trees) {
            s += b.toString() + "\n";
        }

        return s;
    }


    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}

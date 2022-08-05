package edu.brockport.treelotsales.model;

import edu.brockport.treelotsales.impresario.IView;

import java.util.Properties;
import java.util.Vector;

public class TransactionCollection extends EntityBase implements IView {
    private static final String tableName = "Transaction";

    private Vector<Transaction> transactions;

    public TransactionCollection() {
        super(tableName);
        transactions = new Vector<Transaction>();
    }

    private void addTransaction(Transaction s)
    {
        //accounts.add(a);
        int index = findIndexToAdd(s);
        transactions.insertElementAt(s,index); // To build up a collection sorted on some key
    }

    private int findIndexToAdd(Transaction a)
    {
        //users.add(u);
        int low=0;
        int high = transactions.size()-1;
        int middle;

        while (low <=high)
        {
            middle = (low+high)/2;

            Transaction midSession = transactions.elementAt(middle);

            int result = Transaction.compare(a,midSession);

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

    public void getTransactionsFromSession(Session session){
        String query = "SELECT * FROM Transaction WHERE SessionID = " + (String)session.getState("ID");

        Vector allDataRetrieved = getSelectQueryResult(query);

        for(int i = 0; i < allDataRetrieved.size(); i++) {
            Transaction t = new Transaction((Properties)(allDataRetrieved.get(i)));
            addTransaction(t);
        }
    }

    @Override
    public Object getState(String key)
    {
        if (key.equals("Transactions"))
            return transactions;
        else
        if (key.equals("TransactionList"))
            return this;
        return null;
    }

    public int size(){
        return transactions.size();
    }

    public Transaction get(int i){
        return transactions.get(i);
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

        for(Transaction b : transactions) {
            s += b.toString() + "\n";
        }

        return s;
    }


    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }
}

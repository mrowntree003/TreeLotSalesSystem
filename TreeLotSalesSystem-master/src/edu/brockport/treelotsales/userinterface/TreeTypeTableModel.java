package edu.brockport.treelotsales.userinterface;

import javafx.beans.property.SimpleStringProperty;

import java.util.Vector;

public class TreeTypeTableModel {

    private final SimpleStringProperty id;
    private final SimpleStringProperty typeDescription;
    private final SimpleStringProperty cost;
    private final SimpleStringProperty barcodePrefix;

    public TreeTypeTableModel(Vector<String> treeTypeData){
        id = new SimpleStringProperty(treeTypeData.elementAt(0));
        typeDescription = new SimpleStringProperty(treeTypeData.elementAt(1));
        cost = new SimpleStringProperty(treeTypeData.elementAt(2));
        barcodePrefix = new SimpleStringProperty(treeTypeData.elementAt(3));
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getTypeDescription() {
        return typeDescription.get();
    }

    public SimpleStringProperty typeDescriptionProperty() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription.set(typeDescription);
    }

    public String getCost() {
        return cost.get();
    }

    public SimpleStringProperty costProperty() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost.set(cost);
    }

    public String getBarcodePrefix() {
        return barcodePrefix.get();
    }

    public SimpleStringProperty barcodePrefixProperty() {
        return barcodePrefix;
    }

    public void setBarcodePrefix(String barcodePrefix) {
        this.barcodePrefix.set(barcodePrefix);
    }
}

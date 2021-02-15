package org.secilrestaurant;

public class Table {

    private int TableId;
    private boolean IsAvailable;
    private Customer customer;

    public Table(int id) {
        this.TableId = id;
        IsAvailable = true;
    }

    public int getTableId() {
        return TableId;
    }

    public boolean isAvailable() {
        return IsAvailable;
    }

    public void setAvailable(boolean available) {
        IsAvailable = available;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Table " + TableId;
    }
}

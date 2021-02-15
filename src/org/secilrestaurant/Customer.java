package org.secilrestaurant;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Customer extends Thread {

    public int getCustomerId() {
        return customerId;
    }

    public enum CustomerStatus {
        InLine,
        AssignedToTable,
        OrderTaken,
        OrderDelivered
    }

    private int customerId;
//    private int ReservationTime;
    private int TableId;

    public int getTableId() {
        return TableId;
    }

    private CountDownLatch latch;
    private CustomerStatus status;

    public Customer(CountDownLatch latch, int customerId) {
        this.latch = latch;
        this.customerId = customerId;
        this.status = CustomerStatus.InLine;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setCustomerAsOrderTaken() {
        this.status = CustomerStatus.OrderTaken;
    }

    public void setCustomerAsOrderDelivered() {
        this.status = CustomerStatus.OrderDelivered;
    }

    public void setCustomerAsAssignedToTable(int tableId) {
        this.status = CustomerStatus.AssignedToTable;
        this.TableId = tableId;
    }

    @Override
    public String toString() {
        return "Customer " + customerId;
    }

    @Override
    public void run() {
        //yeni müşteri geldi, boş masa yoksa boşalana kadar bekle, varsa oturt
        synchronized (Restaurant.tables) {
            while (Arrays.stream(Restaurant.tables).noneMatch(Table::isAvailable)) {
                try {
                    System.out.println(this.toString() + " is waiting for a table");
                    Restaurant.tables.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Arrays.stream(Restaurant.tables).anyMatch(Table::isAvailable)) {
                List<Table> filteredList = Arrays.stream(Restaurant.tables)
                        .filter(Table::isAvailable)
                        .collect(Collectors.toList());
                Table availableTable = filteredList.stream().findFirst().get();
                availableTable.setAvailable(false);
                availableTable.setCustomer(this);
                setCustomerAsAssignedToTable(availableTable.getTableId());
                System.out.println(this.toString() + " is now sitting on " + availableTable.toString());
                Restaurant.tables.notifyAll();
            }
        }

        synchronized (Restaurant.orderList) {
            while (status != CustomerStatus.OrderDelivered) {
                try {
                    Restaurant.orderList.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized (Restaurant.tables)
        {
            Restaurant.tables[TableId - 1].setAvailable(true);
            Restaurant.tables[TableId - 1].setCustomer(null);
            System.out.println("Customer " +this.getCustomerId() + " is leaving");
            Restaurant.tables.notifyAll();
            latch.countDown();
        }
    }
}
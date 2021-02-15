package org.secilrestaurant;

import org.secilrestaurant.Customer.CustomerStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Waiter extends Thread {
    private int WaiterId;

    public Waiter(int id) {
        this.WaiterId = id;
    }

    public int getWaiterId() {
        return this.WaiterId;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (Restaurant.tables) {
                while (Arrays.stream(Restaurant.tables).map(Table::getCustomer)
                        .filter(Objects::nonNull)
                        .filter(x -> x.getStatus() == CustomerStatus.AssignedToTable)
                        .filter(x -> x.getStatus() != CustomerStatus.OrderTaken)
                        .noneMatch(x -> x.getStatus() == CustomerStatus.AssignedToTable)) {
                    try {
                        Restaurant.tables.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                List<Customer> waitingCustomerOrderList = Arrays.stream(Restaurant.tables)
                        .map(Table::getCustomer)
                        .filter(Objects::nonNull)
                        .filter(x -> x.getStatus() == CustomerStatus.AssignedToTable)
                        .filter(x -> x.getStatus() != CustomerStatus.OrderTaken)
                        .collect(Collectors.toList());
                Customer customer = waitingCustomerOrderList.stream().findFirst().get();
                System.out.println(this.toString() + " is taking orders from customer " + customer.getCustomerId());
                customer.setCustomerAsOrderTaken();
                synchronized (Restaurant.orderList) {
                    Restaurant.orderList.add(new Order(customer.getTableId()));
                    System.out.println(this.toString() + " added orders from customer " + customer.getCustomerId() + " to order list.");
                    Restaurant.orderList.notifyAll();
                }
            }

            synchronized (Restaurant.orderList) {
                while (Restaurant.orderList.stream()
                        .noneMatch(x -> x.getOrderStatus() == Order.OrderStatus.Ready)) {
                    try {
                        Restaurant.orderList.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (Restaurant.orderList.stream().anyMatch( x -> x.getOrderStatus() == Order.OrderStatus.Ready)) {
                    List<Order> filteredList = Restaurant.orderList.stream()
                            .filter(x -> x.getOrderStatus() == Order.OrderStatus.Ready)
                            .collect(Collectors.toList());
                    Order order = filteredList.stream().findFirst().get();
                    //synchronized (Restaurant.tables) {
                        System.out.println(this.toString() + " delivering order of table " + order.getTableId() +" for "+Restaurant.tables[order.getTableId()-1].getCustomer());
                        Restaurant.tables[order.getTableId()-1].getCustomer().setCustomerAsOrderDelivered();
                        //Restaurant.tables.notifyAll();
                    //}
                    Restaurant.orderList.remove(order);
                    Restaurant.orderList.notifyAll();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Waiter " + WaiterId;
    }
}

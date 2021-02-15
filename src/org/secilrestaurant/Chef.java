package org.secilrestaurant;

import java.util.List;
import java.util.stream.Collectors;

public class Chef extends Thread {

    private int ChefId;

    public Chef(int chefId) {
        this.ChefId = chefId;
    }

    public int getChefId() {
        return ChefId;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (Restaurant.orderList) {
                while (Restaurant.orderList.stream()
                        .noneMatch(x -> x.getOrderStatus() == Order.OrderStatus.InLine)) {
                    try {
                        Restaurant.orderList.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (Restaurant.orderList.stream().anyMatch(x -> x.getOrderStatus() == Order.OrderStatus.InLine)) {
                    List<Order> filteredList = Restaurant.orderList.stream()
                            .filter(x -> x.getOrderStatus() == Order.OrderStatus.InLine)
                            .collect(Collectors.toList());
                    Order order = filteredList.stream().findFirst().get();
                    order.setOrderReady();
                    System.out.println(this.toString() + " preparing order of table " + order.getTableId());
                }
                Restaurant.orderList.notifyAll();
            }
        }
    }

    @Override
    public String toString() {
        return "Chef " + ChefId;
    }
}

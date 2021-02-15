package org.secilrestaurant;

public class Order {
    private int TableId;
    private OrderStatus OrderStatus;

    public enum OrderStatus {
        InLine,
        Ready
    }

    public Order(int tableId) {
        this.TableId = tableId;
        this.OrderStatus = OrderStatus.InLine;
    }

    public int getTableId() {
        return TableId;
    }

    public OrderStatus getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderReady(){
        this.OrderStatus = OrderStatus.Ready;
    }
}


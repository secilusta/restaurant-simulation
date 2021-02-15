package org.secilrestaurant;

public class Main {

    public static void main(String[] args) {
        Restaurant r = new Restaurant();
        try {
            r.runSimulation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package org.secilrestaurant;

import java.util.concurrent.*;

public class Restaurant {

    final static int CUSTOMER_NUM = 10;
    final static int TABLE_NUM = 5;
    final static int WAITER_NUM = 3;
    final static int CHEF_NUM = 2;

    static final Table[] tables = new Table[TABLE_NUM];
    static final BlockingQueue<Order> orderList = new LinkedBlockingDeque<>();

    public Restaurant() {
        for (int i=0; i<tables.length; i++) {
            tables[i] = new Table(i+1);
        }
    }

    public void runSimulation() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(CUSTOMER_NUM);
        ExecutorService customerExecutor = Executors.newFixedThreadPool(CUSTOMER_NUM);
        for(int i=0; i < CUSTOMER_NUM; i++) {
            customerExecutor.submit(new Customer(latch,i+1));
        }
        ExecutorService waiterExecutor = Executors.newFixedThreadPool(WAITER_NUM);
        for(int i=0; i < WAITER_NUM; i++) {
            waiterExecutor.submit(new Waiter(i+1));
        }
        ExecutorService chefExecutor = Executors.newFixedThreadPool(CHEF_NUM);
        for(int i=0; i < CHEF_NUM; i++) {
            chefExecutor.submit(new Chef(i+1));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        customerExecutor.shutdown();
        if (!customerExecutor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
            System.out.println("Completing customers.");
        }
        System.out.println("Customers completed.");

        waiterExecutor.shutdown();
        if (!waiterExecutor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
            System.out.println("Sending waiters home.");
        }
        System.out.println("Waiters sent to home.");

        chefExecutor.shutdown();
        if (!chefExecutor.awaitTermination(100, TimeUnit.MICROSECONDS)) {
            System.out.println("Sending chefs home.");
        }
        System.out.println("Chefs sent to home.");
        System.exit(0);
    }
}

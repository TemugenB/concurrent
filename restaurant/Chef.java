import java.time.Duration;
import java.util.concurrent.*;

public class Chef implements Runnable {
    private final static long MAX_WORKING_TIME_MS = 1000;
    private static Chef instance;

    // TODO: Orders, chef
    private final static BlockingQueue<Order> orders = new LinkedBlockingQueue<>();
    private final static BlockingQueue<Order> readyOrders = new LinkedBlockingQueue<>();

    public void run() {
        instance = this;
        
        // TODO: Orders, chef
        // If there is an order, work on it for a random time 0-`MAX_WORKING_TIME_MS`
        // Add ready order to `readyOrders`
        try {
            while (Restaurant.isOpen()) {
                Order order = orders.poll(100, TimeUnit.MILLISECONDS);
                if (order != null) {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(0, MAX_WORKING_TIME_MS));
                    readyOrders.put(order);
                }
            }
        } catch (InterruptedException e) {}
    }

    public static void giveOrder(Order order) {
        // TODO: Orders, chef
        // Add order to the list of orders
        try{
            orders.put(order);
        } catch (InterruptedException e) {}
    }

    public static Order getReadyOrder() {
        // TODO: Orders, chef
        // Get oldest ready order or null
        return readyOrders.poll();
    }
}

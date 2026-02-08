
import java.util.concurrent.*;

public class Customer implements Runnable {
    private final static long MIN_PATIENCE_MS = 1000;
    private final static long MAX_PATIENCE_MS = 15000;
    private final static long EATING_TIME_MS = 1000;

    private final long patience_ms;
    private int table = -1;
    private boolean hasOrdered = false;
    private boolean hasReceivedOrder = false;

    public Customer() {
        // TODO: Seating
        // Set `patience_ms`
        patience_ms = ThreadLocalRandom.current().nextLong(MIN_PATIENCE_MS, MAX_PATIENCE_MS);
        // Queue
        Restaurant.queue(this, patience_ms);
    }

    public boolean hasOrdered() {
        return hasOrdered;
    }

    public void run() {
        // TODO: Serving, impatient customers
        // While restaurant is open and after customer has been seated:
        // If `patience_ms` elapses before ordering, leave
        // If `patience_ms` elapses before receiving order, leave
        // If received order, eat for `EATING_TIME_MS`, then leave
        try {
            // PHASE 1: Wait to be Seated
            synchronized (this) {
                while (table == -1 && Restaurant.isOpen()) {
                    wait(100);
                }
            }
            if (table == -1) return; 

            // PHASE 2: Wait to Order
            synchronized (this) {
                long startTime = System.currentTimeMillis();
                
                // Keep waiting while we haven't ordered yet
                while (!hasOrdered && Restaurant.isOpen()) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = patience_ms - elapsed;

                    // Timeout check
                    if (remaining <= 0) {
                        System.out.println(toString() + " had enough of waiting to order!");
                        Restaurant.leave(this);
                        return;
                    }
                    
                    wait(remaining); // Sleep until Waiter calls order() OR time runs out
                }
            }
            if (!Restaurant.isOpen()) return;

            // PHASE 3: Wait for Food
            synchronized (this) {
                long startTime = System.currentTimeMillis();
                
                // Keep waiting while we haven't received food yet
                while (!hasReceivedOrder && Restaurant.isOpen()) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    long remaining = patience_ms - elapsed;

                    if (remaining <= 0) {
                        System.out.println(toString() + " had enough of waiting to receive their order!");
                        Restaurant.leave(this);
                        return;
                    }
                    
                    wait(remaining); // Sleep until Waiter calls giveOrder()
                }
            }

            // PHASE 4: Eat
            if (hasReceivedOrder) {
                Thread.sleep(EATING_TIME_MS);
                System.out.println(toString() + " finished their food");
                Restaurant.leave(this);
            }
        } catch (InterruptedException e) {}
    }

    public void seat(int table) {
        // TODO: Seating
        // Save table number
        // Print message
        this.table = table;
        System.out.println(toString() + " has been seated: table " + table);
    }

    public Order order() {
        // TODO: Orders, chef
        // Create and return new order
        if(this.table != -1 && !this.hasOrdered){
            this.hasOrdered = true;
            Order o = new Order(this);
            System.out.println(toString() + " has ordered:" + o.toString());
            return o;
        }
        return null;
    }

    public void giveOrder(Order order) {
        // TODO: Serving, impatient customers
        // Set `hasReceivedOrder`
        // Print message
        this.hasReceivedOrder = true;
        System.out.println(toString() + " has received order" + order.toString());
        this.notifyAll();
    }

    public String toString() {
        return "Customer " + hashCode();
    }
}


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Waiter implements Runnable {
    // TODO: Seating
    private final BlockingQueue<Customer> lobby;
    private final Customer[] tables;

    public Waiter(BlockingQueue<Customer>lobby, Customer[] tables ) {
        // TODO: Seating
        // Set `lobby` and `tables`
        this.lobby = lobby;
        this.tables = tables;
    }

    public void run() {
        // TODO: Seating
        // Seat customer while the restaurant is open
        try {
            while (Restaurant.isOpen()) {
                seatCustomer();
                takeOrders();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {}
        // TODO: Orders, chef
        // Take orders while the restaurant is open

        // TODO: Serving, impatient customers
        // Serve orders while the restaurant is open
    }

    private void seatCustomer() {
        try {

        // TODO: Seating
        // If lobby has customer waiting, select a random free table and seat customer
        synchronized (tables) {
            
            List<Integer> freeTableIndices = new ArrayList<>();
            for (int i = 0; i < tables.length; i++) {
                if (tables[i] == null) {
                    freeTableIndices.add(i);
                }
            }

            if (!freeTableIndices.isEmpty() && !lobby.isEmpty()) {
                
                int randomIndex = ThreadLocalRandom.current().nextInt(freeTableIndices.size());
                int tableIndex = freeTableIndices.get(randomIndex);

                Customer c = lobby.take();

                if (c != null) {
                    tables[tableIndex] = c; 
                    c.seat(tableIndex);
                }
            }
        }
                } catch (InterruptedException e) {}
    }

    private void takeOrders() {
        // TODO: Orders, chef
        // Take orders of customers who haven't ordered yet and give the orders to the chef
        synchronized (tables){
            for (Customer c : tables) {
                if(c != null){
                    if (!c.hasOrdered()) {
                        Order o = c.order();
                        if (o != null) {
                            Chef.giveOrder(o);
                        }                    
                    }
                }
            }
        }
    }

    private void serveOrder() {
        // TODO: Serving, impatient customers
        // Serve ready order to customer
        Order readyOrder = Chef.getReadyOrder();
        
        if (readyOrder != null) {
            Customer customer = readyOrder.getCustomer();
            
            synchronized (tables) {
                for (Customer c : tables) {
                    if (c != null) {
                        if (c == customer) {
                            c.giveOrder(readyOrder);
                            break;
                        }
                    }
                }
            }
        }
    }

    public String toString() {
        return "Waiter " + hashCode();
    }
}

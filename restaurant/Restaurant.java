import java.util.concurrent.*;

public class Restaurant {
    private final static int LOBBY_CAPACITY = 10;
    private final static int WAITER_COUNT = 5;
    private final static int TABLE_COUNT = 10;

    private static boolean isOpen = false;
    // TODO: Opening, closing, queueing
    private static BlockingQueue<Customer> lobby;
    private static Customer[] tables;
    private static ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        open();

        // TODO: Opening, closing, queueing
        // For 5 iterations, every 2500ms, start 5 new `Customer` threads
        try {
            for(int i = 0; i < 5; ++i) {
                for(int j = 0; j < 5; ++j) {
                    es.submit(() -> {
                        Customer c = new Customer();
                        c.run();
                    });
                }
                Thread.sleep(2500);
            }
        } catch(InterruptedException e) {}

        close();
    }

    public static void open() {
        // TODO: Opening, closing, queueing
        // Print message
        System.out.print("==Opening the restaurant==");
        // Set `isOpen`
        isOpen = true;
        // Initialize `lobby` and `tables`
        lobby = new ArrayBlockingQueue<>(LOBBY_CAPACITY);
        tables = new Customer[TABLE_COUNT];
        // Start `Waiter` threads
        for (int i = 0; i < WAITER_COUNT; i++) {
            es.submit(new Waiter(lobby, tables));
        }
        // TODO: Orders, chef
        // Start `Chef` thread
        es.submit(new Chef());
    }

    public static void close() {
        // TODO: Opening, closing, queueing
        // Print message
        // Set `isOpen`
        System.out.print("==Closing the restaurant==");
        isOpen = false;
        es.shutdown();
        try { es.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch(InterruptedException e){}
        es.shutdownNow();
    }

    public static boolean isOpen() {
        return isOpen;
    }

    public static Customer[] getTables() {
        return tables;
    }

    public static void queue(Customer customer, long patience_ms) {
        // TODO: Opening, closing, queueing
        // Print message
        // Try to queue in lobby (if not possible wait for `patience_ms` max)
        // If waiting time is over, print message
        System.out.println(customer.toString() + " is queueing (patience: " + patience_ms + "ms)");        
        try{
            boolean entered = lobby.offer(customer, patience_ms, TimeUnit.MILLISECONDS);
            if (!entered){
                System.out.println(customer.toString() + " had enough of waiting in line!");            
            }
        } catch(InterruptedException e) {}
    }

    public static void leave(Customer customer) {
        // TODO: Serving, impatient customers
        // Set customer's table to `null`
        // Print message
        synchronized (tables) {
            for (Customer c:tables) {
                if (c != null && c == customer) {
                    c = null;
                    System.out.println(customer.toString() + " left");    
                    tables.notifyAll();
                    break;
                }
            }
        }
    }
}
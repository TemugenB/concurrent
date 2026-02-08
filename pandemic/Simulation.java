import java.util.concurrent.*;

public class Simulation {
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 5;
    private static final int INITIAL_SUSCEPTIBLE_COUNT = 5;
    private static final int INITIAL_INFECTED_COUNT = 5;
    
    private static final int CYCLE_COUNT = 20;
    private static final long SLEEP_MS = 500;

    // Initialization and shutdown:
    // TODO: Declare `grid`
    private static Person[][] grid;
    private static ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        initializeGrid();

        try {
            for(int i = 0; i < CYCLE_COUNT; ++i) {
                printGrid();

                Thread.sleep(SLEEP_MS);
            }
        } catch(InterruptedException e) {}

        shutdown();
    }

    private static void initializeGrid() {
        // Initialization and shutdown:
        // TODO: Initialize `grid` with `GRID_WIDTH * GRID_HEIGHT` cells
        Doctor doctor = new Doctor();
        es.submit(doctor);
        grid = new Person[GRID_WIDTH][GRID_HEIGHT];
        synchronized(grid){
            // TODO: Place `INITIAL_SUSCEPTIBLE_COUNT` susceptible people (`State.SUSCEPTIBLE`) randomly
            for(int i = 0; i < INITIAL_SUSCEPTIBLE_COUNT; i++){
                int x, y;
                do{
                    x = ThreadLocalRandom.current().nextInt(GRID_WIDTH);
                    y = ThreadLocalRandom.current().nextInt(GRID_HEIGHT);
                } while (grid[x][y] != null);
                grid[x][y] = new Person(grid, x, y, State.SUSCEPTIBLE, doctor);
                es.submit(grid[x][y]);
            }
            // TODO: Place `INITIAL_INFECTED_COUNT` susceptible people (`State.INFECTED`) randomly
            for(int i = 0; i < INITIAL_INFECTED_COUNT; i++){
                int x, y;
                do{
                    x = ThreadLocalRandom.current().nextInt(GRID_WIDTH);
                    y = ThreadLocalRandom.current().nextInt(GRID_HEIGHT);
                } while (grid[x][y] != null);
                grid[x][y] = new Person(grid, x, y, State.INFECTED, doctor);
                es.submit(grid[x][y]);
            }
        }
        // Doctor:
        // TODO: Initialize doctor
    }

    private static void printGrid() {
        // Initialization and shutdown:
        // TODO: Print current state of `grid`
        for(int i = 0; i < GRID_WIDTH; i++)
            System.out.print("=");
        System.out.println();
        synchronized(grid){
            for(int j = 0; j < GRID_HEIGHT; j++){
                for(int i = 0; i < GRID_WIDTH; i++){
                    Person p = grid[i][j];
                    System.out.print(p == null ? " " : p.toString());
                }
                System.out.println();
            }
        }
        for(int i = 0; i < GRID_WIDTH; i++)
            System.out.print("=");
        System.out.println();
    }

    private static void shutdown() {
        // Initialization and shutdown:
        // TODO: Shut down all threads
        es.shutdown();
        try{ es.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}
        es.shutdownNow();
    }
}

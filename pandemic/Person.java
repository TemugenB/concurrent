import java.util.concurrent.*;

public class Person implements Runnable {
    private static final long SLEEP_MS = 500;
    private static final float RECOVERY_CHANCE = 0.05f;
    private static final float FATALITY_CHANCE = 0.1f;

    private Person[][] grid;
    private int x;
    private int y;
    private State state;

    // Doctor:
    // TODO: Uncomment line below, set in constructor
    private Doctor doctor;

    public Person(Person[][] grid, int x, int y, State initialState, Doctor doctor) {
        this.x = x;
        this.y = y;
        this.grid = grid;
        this.state = initialState;
        this.doctor = doctor;
    }

    @Override
    public void run() {
        // Movement logic:
        try {
        // TODO: Call `move()` every `SLEEP_MS` milliseconds
            while (true) {
                Thread.sleep(SLEEP_MS);
                move();
        // Infection logic:
        // TODO: Call `getInfected()` every `SLEEP_MS` milliseconds (for susceptible people)
                if (state == State.SUSCEPTIBLE)
                    getInfected();
        // TODO: Possibly recover with `RECOVERY_CHANCE` chance every `SLEEP_MS` milliseconds (for infected people)
                else if (state == State.INFECTED) {
                    if (ThreadLocalRandom.current().nextDouble() <= RECOVERY_CHANCE) {
                        System.out.println("An infected person recovered!");
                        state = State.RECOVERED;
                    }
        // TODO: Otherwise: possibly die with `FATALITY_CHANCE` chance every `SLEEP_MS` milliseconds (for infected people)
                    else if (ThreadLocalRandom.current().nextDouble() <= FATALITY_CHANCE) {
                        System.out.println("An infected person died!");
                        grid[x][y] = null;
                        return;
                    }
        // Doctor:
        // TODO: Try requesting an appointment every cycle (for infected people)
                    else doctor.requestAppointment(this);
                }
            }

        
        } catch (InterruptedException e) {} catch (Exception e) { e.printStackTrace(); }
    }

    public void cure() {
        // Doctor:
        // TODO: Recover
        state = State.RECOVERED;
        System.out.println("An infected person recovered!");
    }

    private void move() {
        // Movement logic:
        // TODO: Pick a random direction to move to
        // TODO: Ensure no collisions with other people
        int newx, newy;
        do {
            newx = x;
            newy = y;
            int dir = ThreadLocalRandom.current().nextInt(4);

            if (dir == 0) newx--;
            else if (dir == 1) newx++;
            else if (dir == 2) newy--;
            else if (dir == 3) newy++;

        } while (newx < 0 || newx >= 20 || newy < 0 || newy >= 5);
        synchronized (grid) {
            if (grid[newx][newy] == null) {
                grid[newx][newy] = this;
                grid[x][y] = null;
            }
        }
        x = newx; y = newy; 
    }

    private void getInfected() {
        // Infection logic:
        // TODO: Search neighboring cells for infected people (`State.INFECTED`), become infected if found
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                
                int neighborX = x + i;
                int neighborY = y + j;
                if (neighborX >= 0 && neighborX < 20 && neighborY >= 0 && neighborY < 5){
                    Person p = grid[neighborX][neighborY];
                    if (p != null && p.state == State.INFECTED) {
                        System.out.println("A susceptible person got infected!");
                        state = State.INFECTED;
                    }
                }
            }
        }
    }

    public State getState() {
        return state;
    }

    public String toString() {
        switch(state) {
            case SUSCEPTIBLE:
                return "S";
            case INFECTED:
                return "I";
            case RECOVERED:
                return "R";
            default:
                throw new UnsupportedOperationException("Unknown state");
        }
    }
}

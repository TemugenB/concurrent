import java.util.concurrent.*;

public class Doctor implements Runnable {
    private static final long TREATMENT_TIME_MS = 1500;
    private static final long MAX_WAITING_TIME_MS = 3000;
    private static final int WAITING_LIST_LENGTH = 2;

    // Doctor:
    // TODO: Declare `waitingList`
    BlockingQueue<Person> waitingList; 

    public Doctor() {
        // Doctor:
        // TODO: Initialize `waitingList`
        waitingList = new ArrayBlockingQueue<>(WAITING_LIST_LENGTH);
    }

    @Override
    public void run() {
        // Doctor:
        // TODO: Cure first person on the waiting list (should take `TREATMENT_TIME_MS` milliseconds)
        // TODO: Sleep if waiting list is empty
        try {
            while(true){
                Person p = waitingList.take();
                if(p != null && p.getState() == State.INFECTED){
                    Thread.sleep(TREATMENT_TIME_MS);
                    p.cure();
                }
            }
        } catch (InterruptedException e) {}
    }

    public void requestAppointment(Person person) {
        // Doctor:
        // TODO: Attempt to get on the waiting list (with a timeout of `MAX_WAITING_TIME_MS` milliseconds)
        // TODO: Wake up doctor if successfully got on waiting list
        try { waitingList.offer(person, MAX_WAITING_TIME_MS, TimeUnit.MILLISECONDS); 
        } catch (InterruptedException e) {}
    }
}

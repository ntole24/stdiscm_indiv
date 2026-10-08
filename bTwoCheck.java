import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class bTwoCheck { 
    private long n; // The current number being check if it is prime
    private long x;

    // List of workers that will constantly have factors added to it to check if n is divisibly by it
    private List<Thread> workers;

    // Each worker will have its own queue of possible factors to check if n is divisible by them
    private List<BlockingQueue<Long>> queues;

    // Lock
    private final Object lock = new Object();

    // Variable that will represent the factor (if one exists) found that n is divisible by 
    private long factor = 0;
    
    // Volatile keyword is important so that as soon as these values get edited, they are instantly reflected in main memory
    private volatile boolean stop = false;
    private volatile boolean threadsDone = false;

    // Constructor
    public bTwoCheck(long n, long x) {
        this.n = n;
        this.x = x;
        this.queues = new ArrayList<>();
        this.workers = new ArrayList<>();

        for (int i = 0; i < x; i++) {
            queues.add(new ArrayBlockingQueue<>(16)); // bounded so we never flood memory
        }
    }

    // Helper function to provide a candidate factor to be checked
    private static long candidate(long k) {
        return k == 0 ? 2 : 2 * k + 1;
    }

    // Function that starts each thread to divide checking the possible factors for 1 given number
    public boolean isPrime() throws InterruptedException {
        if (n < 2) return false;

        // Start workers
        for (int i = 0; i < this.x; i++) {
            final int id = i;
            this.workers.add(new Thread(() -> workerLoop(id), "Thread " + i));
            this.workers.get(i).start();
        }

        scheduler_loop: // Loop to delegate factors to each thread 
        for (long k = 0; ; k++) {
            long c = candidate(k);
            
            if (c > n / c) break; // Factor is out of range of factors for n, so we don't need to add it to the queues
            
            // Round-robin style adding of each possible factor to each queue
            int targetThread = (int) (k % x);

            /* Attempt to add the current candidate to the target thread's queue. If unsuccessful, keep trying to add
            the candidate to the queue, only stopping if specified to do so using the stop variable */
            while (!this.queues.get(targetThread).offer(c, 20, TimeUnit.MILLISECONDS)) {
                if (this.stop) break scheduler_loop; // Queue was unable to be loaded but a possible factor was found already!
            }

            // Stop check here in case a different thread found a factor at this point
            if (stop) break;
        }
        this.threadsDone = true;

        for (Thread w: this.workers)
            w.join(); // ensure that all threads finish before continuing

        // No need to lock when returning, since at this point, all threads are finished!
        return this.factor == 0;
    }

    // Function that each worker loops through to constantly check its queue of actions 
    private void workerLoop(int id) {
        // inbox represents the factors that this loop will be checking, set from the .offer command in isPrime()
        BlockingQueue<Long> inbox = this.queues.get(id);
        try {
            while (!this.stop) { // Constantly loop through this while the factors are not done being checked
                Long c = inbox.poll(20, TimeUnit.MILLISECONDS); // ether returns a value or null if nothing found within 20 miliseconds
                
                // Don't finish the thread only until the thread's inbox is empty and the isPrime() has reached the threadsDone = true portino
                if (c == null) {
                    if (this.threadsDone && inbox.isEmpty()) return; 
                    continue;
                }

                // A factor is found!
                if (this.n % c == 0) { 
                    // Locks this critical section and prevents anyone else from touching it until the current thread here finishes it
                    synchronized (lock) {
                        if (factor == 0) { // first thread to get here wins
                            factor = c;
                        }
                    }

                    this.stop = true;

                    return;
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

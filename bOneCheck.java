import java.util.ArrayList;
import java.util.List;

public class bOneCheck { 
    private long y;
    private long x;
    private int printType; // Needed variable to ensure that printing happens instantly if printType == 1

    // List of workers that will be delegated a certain division of numbers to check
    private List<Thread> workers;

    /* If printing variation follows AII, this will be used to consolidate all the answers and combine them in the end 
    to be printed. */
    private List<List<Long>> answerLists;

    // Constructor
    public bOneCheck(long y, long x, int printType) {
        this.y = y;
        this.x = x;
        this.printType = printType;

        this.workers = new ArrayList<>();
        this.answerLists = new ArrayList<>();

        for (int i = 0; i < x; i++) {
            this.answerLists.add(new ArrayList<>());
        }
    }

    // Helper function to determine if a given long integer is prime
    private boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }
        
        if (n == 2) {
            return true;
        }
        
        if (n % 2 == 0) {
            return false;
        }
        
        // Check odd factors up to the square root of n
        for (long i = 3; i <= n / i; i += 2) {
            if (n % i == 0) {
                return false; // Found a factor, not prime
            }
        }

        return true;
    }

    // Function to start each worker on checking their divisions
    public void checkNumbers() throws InterruptedException {
        for (int i = 0; i < this.x; i++) {
            final int id = i;
            this.workers.add(new Thread(() -> workerAction(id), "Thread " + i));
            this.workers.get(i).start();
        }

        for (Thread w: this.workers)
            w.join(); // Ensure that all threads finish before continuing

        System.out.printf("END TIME: %d%n", System.currentTimeMillis() / 1000L);
    }
    
    // Function to define action that every worker takes once activated
    private void workerAction(int id) {
        // Calculate the range of numbers that each worker will check
        long division = y / x;
        long remainder = y % x;
       
        long bExtra = (id < remainder) ? id : remainder;
        long b = (division * id + 1) + bExtra; // beginning of range
        
        long eExtra = (id < remainder) ? id + 1 : remainder;
        long e = (division * (id + 1)) + eExtra; // end of range

        // Every worker iterates from the beginning to end of its specified range and check if each number is prime
        for (long i = e; i >= b; i--) {
            // Check if each number is prime or not
            if (isPrime(i)) {
                if (printType == 1) {
                    System.out.printf("Timestamp: %d Thread ID: %d Number: %d%n", System.currentTimeMillis() / 1000L, id, i);
                } else {
                    this.answerLists.get(id).add(i);
                }
            }

            // Delay on each check
            try {
                Thread.sleep(20);
            } catch (InterruptedException error) {
                System.out.println("Thread was interrupted");
            }
        }
    }

    // Function to return the consolidated list of answers back to the caller, assuming it is following printing variation AII
    public List<Long> getAnswerList() {
        List<Long> all = new ArrayList<>();

        for (List<Long> r : this.answerLists) 
            all.addAll(r);

        // OPTIONAL
        // Collections.sort(all);

        return all;
    }
}
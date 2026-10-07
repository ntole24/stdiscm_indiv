import java.util.ArrayList;
import java.util.List;

public class bOneCheck { 
    private int y;
    private int x;
    private int printType; // 0 -> immediate, 1 -> wait 

    private volatile Thread[] workers;

    private List<List<Integer>> answerLists;

    public bOneCheck(int y, int x, int printType) {
        this.y = y;
        this.x = x;
        this.printType = printType;

        this.workers = new Thread[x];
        this.answerLists = new ArrayList<>();

        for (int i = 0; i < x; i++) {
            this.answerLists.add(new ArrayList<>(y));
        }
    }

    private boolean isPrime(int n) {
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
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false; // Found a factor, not prime
            }
        }

        return true;
    }

    public void checkNumbers() throws InterruptedException {
        for (int i = 0; i < this.x; i++) {
            final int id = i;
            this.workers[i] = new Thread(() -> workerAction(id), "Thread " + i);
            this.workers[i].start();
        }

        for (Thread w: this.workers)
            w.join(); // ensure that all threads finish before continuing

        System.out.println("THREADS DONE");
    }
    
    private void workerAction(int id) {
        int division = y / x;
        int remainder = y % x;
        // Check what is the range u need to check
        // if index is less than remainder, then it has an extra!
        int bExtra = (id < remainder) ? id : remainder;
        int b = (division * id + 1) + bExtra;
        
        int eExtra = (id < remainder) ? id + 1 : remainder;
        int e = (division * (id + 1)) + eExtra;

        for (int i = b; i <= e; i++) {
            // Check if each number is prime
            if (isPrime(i)) {
                if (printType == 0) {
                    System.out.printf("Index: %d Timestamp: %d Number: %d\n", id, System.currentTimeMillis() / 1000L, i);
                } else {
                    // System.out.printf("Adding: %d\n", i);
                    this.answerLists.get(id).add(i);
                }
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException error) {
                System.out.println("Thread was interrupted");
            }
        }
    }

    public List<Integer> getAnswerList() {
        List<Integer> all = new ArrayList<>();

        for (List<Integer> r : this.answerLists) 
            all.addAll(r);

        // OPTIONAL
        // Collections.sort(all);

        return all;
    }
}
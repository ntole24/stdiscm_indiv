import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class bTwoCheck { 
    private int n;
    private int x; // thread count

    private BlockingQueue<Integer>[] queues;
    private Thread[] workers;
    private AtomicInteger factor = new AtomicInteger(0);
    private volatile boolean stop = false; // volatile keyword -> ensures that changes made to this variable are immediately in main memory
    private volatile boolean threadsDone = false;

    private int printType; // 0 -> immediate, 1 -> wait 

    private List<Integer> answerList;
    private FutureTask<Boolean> task;
    private Thread thread;

    public bTwoCheck(int n, int x, int printType) {
        this.n = n;
        this.x = x;
        this.queues = new BlockingQueue[x];
        this.workers = new Thread[x];

        for (int i = 0; i < x; i++) {
            queues[i] = new ArrayBlockingQueue<>(16); // bounded so we never flood memory
        }

        this.printType = printType;
    }

    private static int candidate(int k) {
        return k == 0 ? 2 : 2 * k + 1;
    }

    public boolean isPrime() throws InterruptedException {
        if (n < 2) return false;

        // Start workers
        for (int i = 0; i < this.x; i++) {
            final int id = i;
            this.workers[i] = new Thread(() -> workerLoop(id), "Thread " + i);
            this.workers[i].start();
        }

        scheduler_loop:
        for (int k = 0; ; k++) {
            int c = candidate(k);
            
            if (c * c > n) break; // factor is out of range of factors for the number na
            
            int targetThread = k % x;

            while (!this.queues[targetThread].offer(c, 20, TimeUnit.MILLISECONDS)) {
                if (this.stop) break scheduler_loop;
            }

            if (stop) break;
        }
        this.threadsDone = true;

        for (Thread w: this.workers)
            w.join(); // ensure that all threads finish before continuing

        return this.factor.get() == 0; // if it does == 0, that means no factor was found i.e. it is prime!
    }

    private void workerLoop(int id) {
        
        BlockingQueue<Integer> inbox = this.queues[id];
        try {
            while (!this.stop) {
                Integer c = inbox.poll(20, TimeUnit.MILLISECONDS); // ether returns a value or null if nothing found within 20 miliseconds
                // System.out.println("Worker " + id + " c: " + c);
                
                if (c == null) {
                    if (this.threadsDone && inbox.isEmpty()) return; // nothing left to do
                    continue;
                }
                System.out.println("Thread " + id + " checking " + c);

                if (this.n % c == 0) { // prime factor found!
                    if (this.factor.compareAndSet(0, c)) { // change factor to c (if it still equals to 0)
                        System.out.println("T" + id + " found factor " + c + " -> stopping all threads");
                    }

                    this.stop = true;

                    return;
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFactor() {
        return this.factor.get();
    }
}

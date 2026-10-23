import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Grouper {
    private final long n, t1, t2;
    private long t, h, d;                  // remaining players (guarded by lock)

    private final List<Thread> groupWorkers = new ArrayList<>();
    private final List<Group> groups = new ArrayList<>();

    // One mutex guards the resource pool AND every Group's state, so
    // "take 1t/1h/3d" and "mark group active" happen atomically, and
    // checkGroups() always sees a consistent snapshot.
    private final ReentrantLock lock = new ReentrantLock();

    public Grouper(long n, long t1, long t2, long t, long h, long d) {
        this.n = n;
        this.t1 = t1;
        this.t2 = t2;
        this.t = t;
        this.h = h;
        this.d = d;
        for (int i = 0; i < n; i++) groups.add(new Group(i));
    }

    /** Starts n group threads and blocks until every group has finished. */
    public void runGrouping() {
        CountDownLatch ready = new CountDownLatch((int) n); // each worker signals "I'm parked"
        CountDownLatch go    = new CountDownLatch(1);       // main opens this to release everyone

        for (int i = 0; i < n; i++) {
            final int id = i;
            Thread w = new Thread(() -> {
                ready.countDown(); // decrements the ready object by 1

                try {
                    go.await(); // block until go's value is less than 1
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                workerLoop(id);
            }, "group-" + id);

            groupWorkers.add(w);
            w.start();
        }

        try {
            ready.await(); // Wait for the countdownlatch to hit 0, meaning that all threads are set up at go.await()
            go.countDown(); // decrement the go countdownlatch, opening the gate for all of the threads

            for (Thread w : groupWorkers) {
                w.join();
            }

            logSummary();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // caller must hold the lock
    private boolean enoughResources() {
        return t >= 1 && h >= 1 && d >= 3;
    }

    private void logStatus(int id, boolean status) {
        String statusString = (status) ? "active" : "empty";

        System.out.println("Instance " + id + " status: " + statusString);
    }

    private void logSummary() {
        System.out.println();

        System.out.println("SUMMARY OF EACH PARTY");

        for (Group g: groups) {
            System.out.println("Party " + groups.indexOf(g));
            System.out.println("Total parties served: " + g.partiesServed);
            System.out.println("Total time serving: " + g.totalTime);
            System.out.println();
        }
    }

    private void workerLoop(int id) {
        Group g = groups.get(id);

        logStatus(id, g.active);

        while (true) {
            int ttl;

            // Critical section: claim players + mark active, atomically.
            lock.lock();
            try {
                if (!enoughResources()) 
                    return;   // nothing left, this slot retires

                // There is enough resources, so this group can run it!
                t -= 1;
                h -= 1;
                d -= 3;
                ttl = (int) ThreadLocalRandom.current().nextLong(t1, t2 + 1);
                g.ttl = ttl;
                g.active = true;

                logStatus(id, g.active);
            } finally {
                lock.unlock();
            }

            // Sleep OUTSIDE the lock so checkGroups() and other groups aren't blocked.
            try {
                Thread.sleep(1000L * ttl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                lock.lock();
                try { 
                    g.active = false; 
                } finally { 
                    lock.unlock(); 
                }

                return;
            }

            lock.lock();
            try {
                g.active = false;
                g.partiesServed++;
                g.totalTime += g.ttl;

                logStatus(id, g.active);
            } finally {
                lock.unlock();
            }
        }
    }
}
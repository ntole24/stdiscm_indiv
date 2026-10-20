import java.util.ArrayList;
import java.util.List;
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
        for (int i = 0; i < n; i++) {
            final int id = i;
            Thread w = new Thread(() -> workerLoop(id), "group-" + id);
            groupWorkers.add(w);
            w.start();
        }
        for (Thread w : groupWorkers) {
            try {
                w.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /** Safe to call from any thread at any time. */
    public void checkGroups() {
        lock.lock();
        try {
            for (Group g : groups) {
                System.out.printf("Group %d: %s%s%n", g.id,
                        g.active ? "ACTIVE (ttl=" + g.ttl + "s)" : "EMPTY",
                        " [served " + g.partiesServed + "]");
            }
            System.out.printf("Remaining -> t=%d h=%d d=%d%n", t, h, d);
        } finally {
            lock.unlock();
        }
    }

    // caller must hold the lock
    private boolean enoughResources() {
        return t >= 1 && h >= 1 && d >= 3;
    }

    private void workerLoop(int id) {
        Group g = groups.get(id);
        while (true) {
            int ttl;

            // Critical section: claim players + mark active, atomically.
            lock.lock();
            try {
                if (!enoughResources()) 
                    return;   // nothing left, this slot retires

                t -= 1;
                h -= 1;
                d -= 3;
                ttl = (int) ThreadLocalRandom.current().nextLong(t1, t2 + 1);
                g.ttl = ttl;
                g.active = true;
            } finally {
                lock.unlock();
            }

            // Sleep OUTSIDE the lock so checkGroups() and other groups aren't blocked.
            try {
                Thread.sleep(1000L * ttl);

                System.out.println("Worker " + id + " finished with its group!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                lock.lock();
                try { g.active = false; } finally { lock.unlock(); }
                return;
            }

            lock.lock();
            try {
                g.active = false;
                g.partiesServed++;
            } finally {
                lock.unlock();
            }
        }
    }
}
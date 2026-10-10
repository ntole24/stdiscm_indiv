import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class stressTest {

    // Values to be tested
    static final long[] X_VALUES = {1, 2, 4, 16, 64};     
    static final long[] Y_VALUES = {50, 100, 300, 1000};        

    public static void main(String[] args) throws Exception {
        int reps = (args.length > 0) ? Integer.parseInt(args[0]) : 3;

        // Build the list of (x, y) cases: the full matrix plus whatever is in config.txt
        List<long[]> cases = new ArrayList<>();
        for (long x : X_VALUES)
            for (long y : Y_VALUES)
                cases.add(new long[]{x, y});

        long[] cfg = main.readConfig(); // {x, y} from config.txt
        if (cfg != null) cases.add(cfg);

        // Warm-up so JIT compilation doesn't skew the first measured case
        runOne(2, 20);
        runTwo(2, 20);

        System.out.printf("%-6s %-6s %-12s %-12s %-10s %-8s%n",
                "x", "y", "bOne(ms)", "bTwo(ms)", "bOne/bTwo", "correct");

        for (long[] c : cases) {
            long x = c[0], y = c[1];
            List<Long> expected = referencePrimes(y);

            List<Long> oneTimes = new ArrayList<>();
            List<Long> twoTimes = new ArrayList<>();
            boolean ok = true;

            for (int r = 0; r < reps; r++) {
                Result a = runOne(x, y);
                Result b = runTwo(x, y);
                oneTimes.add(a.ms);
                twoTimes.add(b.ms);
                ok &= a.primes.equals(expected) && b.primes.equals(expected);
            }

            long oneMed = median(oneTimes);
            long twoMed = median(twoTimes);
            double ratio = twoMed == 0 ? Double.NaN : (double) oneMed / twoMed;

            if (c == cfg) // to check for the config case
                System.out.println("-----------------------CONFIG CASE-------------------------");
            
            System.out.printf("%-6d %-6d %-12d %-12d %-10.2f %-8s%n",
                    x, y, oneMed, twoMed, ratio, ok ? "PASS" : "FAIL");
        }
    }

    // Holds the elapsed time and the (sorted) primes found by one run
    static class Result {
        final long ms;
        final List<Long> primes;
        Result(long ms, List<Long> primes) { this.ms = ms; this.primes = primes; }
    }

    // Task division 1: each thread gets a range of numbers (printType 2 = collect, no printing)
    static Result runOne(long x, long y) throws InterruptedException {
        long start = System.nanoTime();
        bOneCheck checker = new bOneCheck(y, x, 2);
        checker.startThreads();
        List<Long> primes = checker.getAnswerList();
        long ms = (System.nanoTime() - start) / 1_000_000;
        Collections.sort(primes); // bOneCheck returns them unordered
        return new Result(ms, primes);
    }

    // Task division 2: for each number, threads split the candidate factors
    static Result runTwo(long x, long y) throws InterruptedException {
        List<Long> primes = new ArrayList<>();
        long start = System.nanoTime();
        for (long i = 2; i <= y; i++) {
            if (new bTwoCheck(i, x).isPrime()) primes.add(i);
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        return new Result(ms, primes);
    }

    // Ground truth via sieve of Eratosthenes
    static List<Long> referencePrimes(long y) {
        boolean[] composite = new boolean[(int) y + 1];
        List<Long> out = new ArrayList<>();
        for (int i = 2; i <= y; i++) {
            if (!composite[i]) {
                out.add((long) i);
                for (long j = (long) i * i; j <= y; j += i) composite[(int) j] = true;
            }
        }
        return out;
    }

    static long median(List<Long> values) {
        Long[] arr = values.toArray(new Long[0]);
        Arrays.sort(arr);
        return arr[arr.length / 2];
    }
}
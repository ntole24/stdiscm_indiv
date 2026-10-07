public class A1B2 {
    public static void main(String[] args) {
        long[] configs = configReader.readConfig();
        long x = configs[0], y = configs[1];
        
        for (long i = 2; i <= y; i++) {
            bTwoCheck scheduler = new bTwoCheck(i, x);

            try {
                boolean prime = scheduler.isPrime();
                
                if (prime) 
                    System.out.printf("Number: %d Timestamp: %d\n", i, System.currentTimeMillis() / 1000L);

            } catch (InterruptedException e) {
                System.out.println("Code was interrupted, reason: " + e);
            }
        }
    }
}
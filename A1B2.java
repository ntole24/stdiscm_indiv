public class A1B2 {
    public static void main(String[] args) {
        int y = 42;
        int x = 3;
        
        for (int i = 2; i <= y; i++) {
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
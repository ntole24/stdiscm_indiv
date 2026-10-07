import java.util.ArrayList;
import java.util.List;

public class A2B2 {
    public static void main(String[] args) {
        long[] configs = configReader.readConfig();
        long x = configs[0], y = configs[1];
        
        List<Long> answerList = new ArrayList<>();

        for (long i = 2; i <= y; i++) {
            bTwoCheck scheduler = new bTwoCheck(i, x);

            try {
                boolean prime = scheduler.isPrime();
                
                if (prime) 
                    System.out.printf("Number: %d\n", i);

            } catch (InterruptedException e) {
                System.out.println("Code was interrupted, reason: " + e);
            }
        }

        for (Long answer: answerList) {
            System.out.printf("Number: %d\n", answer);
        }
    }
}
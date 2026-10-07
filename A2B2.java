import java.util.ArrayList;
import java.util.List;

public class A2B2 {
    public static void main(String[] args) {
        int y = 42;
        int x = 3;
        
        List<Integer> answerList = new ArrayList<>(y);

        for (int i = 2; i <= y; i++) {
            bTwoCheck scheduler = new bTwoCheck(i, x);

            try {
                boolean prime = scheduler.isPrime();
                
                if (prime) 
                    System.out.printf("Number: %d\n", i);

            } catch (InterruptedException e) {
                System.out.println("Code was interrupted, reason: " + e);
            }
        }

        for (Integer answer: answerList) {
            System.out.printf("Number: %d\n", answer);
        }
    }
}
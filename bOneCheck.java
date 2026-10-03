import java.util.ArrayList;
import java.util.List;

public class bOneCheck extends Thread { 
    private int y;
    private int x;
    private int index;
    private int printType; // 0 -> immediate, 1 -> wait 

    private List<Integer> answerList;

    public bOneCheck(int y, int x, int index, int printType) {
        this.y = y;
        this.x = x;
        this.index = index;
        this.printType = printType;
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

    @Override
    public void run() {
        int division = y / x;
        int remainder = y % x;
        // Check what is the range u need to check
        // if index is less than remainder, then it has an extra!
        int bExtra = (this.index < remainder) ? this.index : remainder;
        int b = (division * index + 1) + bExtra;
        
        int eExtra = (this.index < remainder) ? this.index + 1 : remainder;
        int e = (division * (this.index + 1)) + eExtra;
                    
        if (printType == 1)
            this.answerList = new ArrayList<>(e - b + 1);

        for (int i = b; i <= e; i++) {
            // Check if each number is prime
            if (isPrime(i)) {
                if (printType == 0) {
                    System.out.printf("Index: %d Timestamp: %d Number: %d\n", this.index, System.currentTimeMillis() / 1000L, i);
                } else {
                    answerList.add(i);
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException error) {
                System.out.println("Thread was interrupted");
            }
        }

        if (printType == 1) {
            System.out.printf("Thread %d done. Answers: ", this.index);

            for (Integer i: answerList) {
                System.out.printf("%d ", i);
            }

            System.out.println();
        }
    }
}

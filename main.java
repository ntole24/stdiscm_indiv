import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        int y = 42;
        int x = 2;
        int diviType = 1;
        int printType = 1;
        
        if (diviType == 0)
            schemeOne(y, x, printType);
        else
            schemeTwo(y, x, printType);
    }

    static void schemeOne(int y, int x, int printType) {
        List<Thread> threadList = new ArrayList<>(x);        

        for (int i = 0; i < x; i++) {
            threadList.add(i, new bOneCheck(y, x, i, printType));
        }

        for (int i = 0; i < x; i++) {
            threadList.get(i).start();
        }
    }

    static void schemeTwo(int y, int x, int printType) {

        for (int i = 2; i <= y; i++) {
            bTwoCheck scheduler = new bTwoCheck(i, x, printType);

            try {
                boolean prime = scheduler.isPrime();

                if (prime) {
                    System.out.println(i + " is prime");
                } else {
                    System.out.println(i+ " is NOT prime, factor: " + scheduler.getFactor());
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception was thrown: " + e);
            }

        }

        // Idea:
        // There will be x amount of workers that will each be assigned a certain factor given a number leading up to y.
        // Create a pseudo scheduler that gets factors 
    }
}
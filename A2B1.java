import java.util.ArrayList;
import java.util.List;

public class A2B1 {
    public static void main(String[] args) {
        int y = 42;
        int x = 3;
        int printType = 1;
        
        bOneCheck checker = new bOneCheck(y, x, printType);

        try {
            checker.checkNumbers();

            List<Integer> answerList = checker.getAnswerList();

            for (Integer answer: answerList) {
                System.out.printf("Number: %d\n", answer);
            }
        } catch (InterruptedException e) {
            System.out.println("Interruption error: " + e);
        }
    }
}
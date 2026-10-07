import java.util.List;

public class A2B1 {
    public static void main(String[] args) {
        long[] configs = configReader.readConfig();
        long x = configs[0], y = configs[1];
        
        bOneCheck checker = new bOneCheck(y, x, 1);

        try {
            checker.checkNumbers();

            List<Long> answerList = checker.getAnswerList();

            for (long answer: answerList) {
                System.out.printf("Number: %d\n", answer);
            }
        } catch (InterruptedException e) {
            System.out.println("Interruption error: " + e);
        }
    }
}
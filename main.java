import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        boolean isValid = validArguments(args);

        if (!isValid) {
            System.out.println();
            System.out.println("Sample command input:");
            System.out.println("javac main.java && java main [printType] [taskDivision]");
            return;
        }
        
        long[] configs = configReader.readConfig();
        long x = configs[0], y = configs[1];
        
        int printType = Integer.parseInt(args[0]);
        int taskDivision = Integer.parseInt(args[1]);
        
        List<Long> answerList = new ArrayList<>();
        
        
        if (taskDivision == 1) {
            bOneCheck checker = new bOneCheck(y, x, printType);
            
            try {
                checker.checkNumbers();
                
                if (printType == 2) {
                    answerList = checker.getAnswerList();
                    
                    for (long answer: answerList) {
                        System.out.printf("Number: %d%n", answer);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Interruption error: " + e);
            }
        } else if (taskDivision == 2) {
            for (long i = 2; i <= y; i++) {
                bTwoCheck scheduler = new bTwoCheck(i, x);
                
                try {
                    boolean prime = scheduler.isPrime();
                    
                    if (prime) {
                        if (printType == 1)
                            System.out.printf("Number: %d Timestamp: %d%n", i, System.currentTimeMillis() / 1000L);
                        else if (printType == 2)
                            answerList.add(i);
                    } 
                    
                } catch (InterruptedException e) {
                    System.out.println("Code was interrupted, reason: " + e);
                }
            }
        }
    }

    public static boolean validArguments(String[] args) {
        int buffer1 = 0, buffer2 = 0;
    
        if (args.length < 2) {
            System.out.println("Please input 2 arguments.");
            return false;
        }
    
        if (args[0] == null || args[0].isEmpty() || args[1] == null || args[1].isEmpty()) {
            System.out.println("Please input two number inputs after the command to run the code.");
            return false;
        }
    
        try {
            buffer1 = Integer.parseInt(args[0]);
            buffer2 = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Please input a numerical input of either 1 or 2 for each argument.");
            return false;
        }
    
        if ((buffer1 != 1 && buffer1 != 2) || (buffer2 != 1 && buffer2 != 2)) {
            System.out.println("Please input either 1 or 2 in each of the arguments for the command.");
            return false;
        }
    
        return true;
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        boolean isValid = validArguments(args);

        if (!isValid) {
            System.out.println();
            System.out.println("Sample command input:");
            System.out.println("javac main.java && java main [printType] [taskDivision]");
            return;
        }
        
        long[] configs = readConfig(); 

        if (configs == null) {
            return;
        }

        long x = configs[0], y = configs[1];
        
        int printType = Integer.parseInt(args[0]);
        int taskDivision = Integer.parseInt(args[1]);
        
        List<Long> answerList = new ArrayList<>();
        
        long startTimeNumber = System.currentTimeMillis();
        String startTime = Instant.ofEpochMilli(startTimeNumber).atZone(ZoneId.of("Asia/Manila")).format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.printf("START TIME: %s%n%n", startTime);
        
        if (taskDivision == 1) {
            bOneCheck checker = new bOneCheck(y, x, printType);
            
            try {
                checker.startThreads();
                
                if (printType == 2) {
                    answerList = checker.getAnswerList();
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
                        if (printType == 1) {
                            long currentTimeNumber = System.currentTimeMillis();
                            String currentTime = Instant.ofEpochMilli(currentTimeNumber).atZone(ZoneId.of("Asia/Manila")).format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                            System.out.printf("Timestamp: %s Number: %d%n", currentTime, i);
                        }
                        else if (printType == 2)
                            answerList.add(i);
                    } 
                    
                } catch (InterruptedException e) {
                    System.out.println("Code was interrupted, reason: " + e);
                }
            }
        }

        if (printType == 2) {
            for (long answer: answerList) {
                System.out.printf("Number: %d%n", answer);
            }
        }

        long endTimeNumber = System.currentTimeMillis();
        String endTime = Instant.ofEpochMilli(endTimeNumber).atZone(ZoneId.of("Asia/Manila")).format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.printf("%nEND TIME: %s%n", endTime);
        System.out.printf("TOTAL TIME: %d ms%n%n", endTimeNumber - startTimeNumber);
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

    public static long[] readConfig() {
        long[] configs = new long[2];

        File file = new File("config.txt");

        int fileLength = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] buffer = line.split(" ");

                fileLength++;

                if (fileLength > 2) {
                    System.out.printf("ERROR: There must be exactly 2 lines in config.txt.%nOne line must be in the form \"x <number>\", and the other must be in the form \"y <number>\".%n");
                    return null;
                }

                if (buffer.length != 2) {
                    System.out.println("ERROR: Each line must be in the form \"x <number>\" or \"y <number>\".");
                    return null;
                }

                int index;
                if (buffer[0].equals("x")) {
                    index = 0;
                } else if (buffer[0].equals("y")) {
                    index = 1;
                } else {
                    System.out.println("ERROR: Invalid read in config file. Only replace integers, and do not add anything else.");
                    return null;
                }

                try {
                    configs[index] = Long.parseLong(buffer[1]);
                } catch (NumberFormatException e) {
                    // Distinguish "too big/small for a long" from "not a number"
                    if (buffer[1].matches("[+-]?\\d+")) {
                        System.out.println("ERROR: The value for " + buffer[0] + " (" + buffer[1] + ") is outside the range of a long ("
                                + Long.MIN_VALUE + " to " + Long.MAX_VALUE + ").");
                    } else {
                        System.out.println("ERROR: The value for " + buffer[0] + " (" + buffer[1] + ") is not a valid integer.");
                    }
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file could not be found.");
            e.printStackTrace();
            return null;
        }

        return configs;
    }
}

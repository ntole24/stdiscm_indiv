import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        boolean isValid = validArguments(args);

        if (!isValid) {
            System.out.println();
            System.out.println("Sample command input:");
            System.out.println("javac *.java && java main [t] [h] [d]");
            return;
        }
        
        long[] configs = readConfig(); 

        if (configs == null) {
            return;
        }

        
        long n = configs[0], t1 = configs[1], t2 = configs[2];
        long t = Long.parseLong(args[0]), h = Long.parseLong(args[1]), d = Long.parseLong(args[2]); 

        System.out.printf("n: %d t1: %d t2: %d\n", n, t1, t2);
        System.out.printf("t: %d h: %d d: %d\n",t, h, d );
                
        /* Run code here! */
    }

    public static boolean validArguments(String[] args) {
        long buffer1 = 0, buffer2 = 0, buffer3 = 0;
        // buffer1 = n, buffer2 = t1, buffer3 = t2
    
        if (args.length != 3) {
            System.out.println("Please input 3 arguments.");
            return false;
        }
    
        if (args[0] == null || args[0].isEmpty() || args[1] == null || args[1].isEmpty() || args[2] == null || args[2].isEmpty()) {
            System.out.println("Please input two number inputs after the command to run the code.");
            return false;
        }

        BigInteger min = BigInteger.ZERO;
        BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
    
        try {
            for (int i = 0; i < 3; i++) {
                BigInteger parsed = new BigInteger(args[i]);

                if (parsed.compareTo(min) < 0 || parsed.compareTo(max) > 0) {
                    System.out.println("Argument " + (i + 1) + " is out of range. Values must be between 0 and " + Long.MAX_VALUE + ".");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please input a numerical input for each argument.");
            return false;
        }
    
        if (buffer2 > buffer3) {
            System.out.println("Please input a value for the maximum time that is greater than or equal to the value of the minimum time.");
            return false;
        }
    
        return true;
    }

    public static long[] readConfig() {
        long[] configs = new long[3];

        File file = new File("config.txt");

        int fileLength = 0;

        BigInteger min = BigInteger.ZERO;
        BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] buffer = line.split(" ");

                fileLength++;
                System.out.println("Line: " + fileLength);
                System.out.println("buffer0: " + buffer[0] + " buffer1: " + buffer[1]);

                if (fileLength > 3) {
                    System.out.printf("ERROR: There must be exactly 3 lines in config.txt.%nOne line must be in the form \"n <number>\", \"t1 <number>\", and \"t2 <number>\" .%n");
                    return null;
                }

                if (buffer.length != 2) {
                    System.out.println("ERROR: Each line must be in the form \"<variable> <number>\".");
                    return null;
                }

                int index;

                if (buffer[0].equals("n")) {
                    index = 0;
                } else if (buffer[0].equals("t1")) {
                    index = 1;
                } else if (buffer[0].equals("t2")) {
                    index = 2;  
                } else {
                    System.out.println("ERROR: Invalid read in config file. Only replace integers, and do not add anything else.");
                    return null;
                }

                try {
                    BigInteger parsed = new BigInteger(buffer[1]);

                    if (parsed.compareTo(min) < 0 || parsed.compareTo(max) > 0) {
                        System.out.println((parsed) + " is out of range. Values must be between 0 and " + Long.MAX_VALUE + ".");

                        return null;
                    }

                    configs[index] = parsed.longValue();
                } catch (NumberFormatException e) {
                    System.out.println("Please input a numerical input for each argument in the config file.");
                    
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file could not be found.");
            e.printStackTrace();
            return null;
        }

        if (fileLength < 3) {
            System.out.printf("ERROR: There must be exactly 3 lines in config.txt.%nEach line must be of a form of either \"n <number>\", \"t1 <number>\", and \"t2 <number>\" .%n");
            return null;
        }

        return configs;
    }
}

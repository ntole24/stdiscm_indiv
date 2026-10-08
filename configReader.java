import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class configReader {
    public static long[] readConfig() {
        long[] configs = new long[2];

        File file = new File("config.txt");
    
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] buffer = line.split(" ");
    
                if (buffer[0].equals("x")) {
                    configs[0] = Long.parseLong(buffer[1]);
                } else if (buffer[0].equals("y")) {
                    configs[1] = Long.parseLong(buffer[1]);
                } else {
                    System.out.println("ERROR: Invalid read in config file. Only replace integers, and do not add anything else.");
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file could not be found.");
            e.printStackTrace();
        }

        return configs;
    }
}

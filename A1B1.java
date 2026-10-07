public class A1B1 {
    public static void main(String[] args) {
        long[] configs = configReader.readConfig();
        long x = configs[0], y = configs[1];
        
        bOneCheck checker = new bOneCheck(y, x, 0);

        try {
            checker.checkNumbers();
        } catch (InterruptedException e) {
            System.out.println("Interruption error: " + e);
        }
    }
}
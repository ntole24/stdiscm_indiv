public class A1B1 {
    public static void main(String[] args) {
        int y = 42;
        int x = 3;
        int printType = 0;
        
        bOneCheck checker = new bOneCheck(y, x, printType);

        try {
            checker.checkNumbers();
        } catch (InterruptedException e) {
            System.out.println("Interruption error: " + e);
        }
    }
}
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        int y = 42;
        int x = 13;
        int diviType = 0;
        int printType = 1;
        
        if (diviType == 0)
            schemeOne(y, x, printType);
        else
            schemeTwo(y, x, printType);

        /* t1.start();
        t2.start(); // test2
        t3.start(); // test
        t4.start(); */
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
        List<Thread> threadList = new ArrayList<>(x); 
        List<Integer> answerList = new ArrayList<>(y);
        

        for (int i = 2; i < y; i++) { // check each number
            if (i == 2) {
                threadList.add(0, new bTwoCheck(y, null, 0, printType));

                threadList.get(0).start();
            } else {

            }
        }
    }
}
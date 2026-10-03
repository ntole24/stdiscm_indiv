import java.util.List;

public class bTwoCheck extends Thread { 
    private int y;
    private List<Integer> possibleFactors;
    private int index;
    private int printType; // 0 -> immediate, 1 -> wait 

    private List<Integer> answerList;

    public bTwoCheck(int y, List<Integer> possibleFactors, int index, int printType) {
        this.y = y;
        this.possibleFactors = possibleFactors;
        this.index = index;
        this.printType = printType;
    }

    @Override
    public void run() {
        // yes
    }
}

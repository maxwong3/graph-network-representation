/**
 * A driver for CS1501 Project 4
 * @author    Dr. Farnan
 */
package cs1501_p4;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        NetAnalysis na = new NetAnalysis("build/resources/main/network_data2.txt"); 
        ArrayList<Integer> path = na.lowestLatencyPath(0, 3);
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i) + ",");
        }
        System.out.println();
        ArrayList<STE> lowestLatency = na.lowestAvgLatST();
        for (int i = 0; i < lowestLatency.size(); i++) {
            System.out.println(lowestLatency.get(i));
        }
    }
}
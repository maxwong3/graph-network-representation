/**
 * Basic tests for CS1501 Project 4
 * @author    Dr. Farnan
 */
package cs1501_p4;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.lang.IllegalArgumentException;
import java.util.ArrayList;

import static java.time.Duration.ofSeconds;

class BasicTests {
    NetAnalysis init_basic() {
        return new NetAnalysis("build/resources/test/network_data1.txt");
    }

    @Test
    @DisplayName("Check if it can read graph properly")
    void basic_read() {
        NetAnalysis na = init_basic();
    }

    @Test
    @DisplayName("Check lowest latency path")
    void basic_llp() {
        NetAnalysis na = init_basic();

        ArrayList<Integer> res = na.lowestLatencyPath(0, 4);

        ArrayList<Integer> exp = new ArrayList<Integer>();
        exp.add(0);
        exp.add(4);

        for (int i = 0; i < exp.size(); i++) {
            assertEquals(exp.get(i), res.get(i), "Incorrect vertex on path");
        }
    }

    @Test
    @DisplayName("Check bandwidth along the path")
    void basic_bap() {
        NetAnalysis na = init_basic();

        ArrayList<Integer> path = na.lowestLatencyPath(0, 4);
        int res = na.bandwidthAlongPath(path);

        assertEquals(100, res, "Incorrect bandwidth");
    }

    @Test
    @DisplayName("Check if it is connected only by copper cables")
    void basic_coc() {
        NetAnalysis na = init_basic();

        assertTrue(na.copperOnlyConnected());
    }

    @Test
    @DisplayName("Check if it survives any two failed vertices")
    void basic_stfv() {
        NetAnalysis na = init_basic();

        assertTrue(na.connectedTwoVertFail());
    }

    @Test
    @DisplayName("Check the lowest average latency spanning tree")
    void basic_lalst() {
        NetAnalysis na = init_basic();

        ArrayList<STE> res = na.lowestAvgLatST();

        ArrayList<STE> exp = new ArrayList<STE>();
        exp.add(new STE(0, 4));
        exp.add(new STE(1, 4));
        exp.add(new STE(2, 4));
        exp.add(new STE(3, 4));

        assertEquals(exp.size(), res.size(), "Incorrect number of spanning tree edges");
        for (STE i : exp) {
            boolean found = false;
            for (STE j : res) {
                if (i.equals(j)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Invalid spanning tree edge");
        }
    }

}

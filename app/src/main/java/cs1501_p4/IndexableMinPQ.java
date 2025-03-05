package cs1501_p4;
import java.util.ArrayList;

public class IndexableMinPQ<Key extends Comparable<Key>>{
    private int[] pq;
    private int N;
    private Key[] keys;
    
    public IndexableMinPQ(int maxN) {
        keys = (Key[])new Comparable[maxN+1];
        pq = new int[maxN+1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void insert(int v, Key key) {
        if (N < pq.length-1) {
            N++;
        }
        pq[N] = v;
        keys[N] = key;
        swim(N);
    }

    public int deleteMin() {
        int indexOfMin = pq[1];
        swap(1, N--);
        sink(1);
        return indexOfMin;
    }

    private void swim(int k) {
        while (k > 1 && firstIsLess(k, k/2)) {
            swap(k/2, k);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && firstIsLess(j+1, j)) j++;
            if (firstIsLess(k, j)) break;
            swap(k, j);
            k = j;
        }
    }

    private boolean firstIsLess(int i, int j) {
        return keys[i].compareTo(keys[j]) < 0;
    }

    private void swap(int i, int j) {
        int temp = pq[i];
        Key tempKey = keys[i];
        pq[i] = pq[j];
        pq[j] = temp;
        keys[i] = keys[j];
        keys[j] = tempKey;
    }
}
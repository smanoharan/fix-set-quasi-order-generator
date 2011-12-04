package quasiorder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

class Permutation
{
    public final ArrayList<TwoSwap> swaps;

    public Permutation(ArrayList<TwoSwap> swaps)
    {
        this.swaps = swaps;
    }

    // Given an array of int pairs (representing elements) find the list of permutations (by decomposition into TwoSwaps)
    public static ArrayList<Permutation> FromPermutationTable(int[][][] permutationTable)
    {
        ArrayList<Permutation> permutations = new ArrayList<Permutation>();

        for (int[][] permutation : permutationTable) // permTable = [ permutations ]
            permutations.add(FromPermutationTable(permutation));

        return permutations;
    }

    public static Permutation FromPermutationTable(int[][] permutationTable)
    {
        int len = permutationTable.length;
        int[] nextP = new int[len];
        for(int[] pair : permutationTable)
            nextP[pair[0]]=pair[1];

        BitSet vis = new BitSet(len);
        ArrayList<TwoSwap> twoSwaps = new ArrayList<TwoSwap>();
        while(true)
        {
            int start = vis.nextClearBit(0);
            if (start < 0 || start >= len) break;
            int cur = start;
            while(true)
            {
                vis.set(cur);
                int next = nextP[cur];
                if (next == start) break;
                twoSwaps.add(new TwoSwap(cur, next));
                cur = next;
            }
        }

        Collections.reverse(twoSwaps);
        return new Permutation(twoSwaps);
    }

}

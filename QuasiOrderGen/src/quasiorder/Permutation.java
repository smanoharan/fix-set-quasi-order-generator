package quasiorder;

import com.google.gson.Gson;

import java.io.Reader;
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

    public static void ToTwoSwaps(int[] pTable)
    {
        // int [] is of the form: pTable[a] is where a is moved to.
        // TODO
    }

    // TODO test
    public static ArrayList<Permutation> From2SwapJSON(Reader jsonReader)
    {
        // for now: input is a set of 2-swaps (in the form: int[][][])

        // parse JSON (which has arrays of arrays of arrays of strings)
        int[][][] permutations = ( new Gson()).fromJson(jsonReader, int[][][].class);

        // try closing the input-stream. If this fails, nothing we can do.
        try { jsonReader.close(); } catch (Exception e) {}

        // places to look
        ArrayList<Permutation> ps = new ArrayList<Permutation>();
        for(int[][] pi : permutations)
        {
            ArrayList<TwoSwap> swaps = new ArrayList<TwoSwap>();
            for(int[] pii : pi)
                swaps.add(new TwoSwap(pii[0], pii[1]));
            ps.add(new Permutation(swaps));
        }

        return ps;
    }

    // Given an array of int pairs (representing elements) find the list of permutations (by decomposition into TwoSwaps)
    public static ArrayList<Permutation> FromPermutationTable(int[][][] permutationTable)
    {
        ArrayList<Permutation> permutations = new ArrayList<Permutation>();

        for (int[][] permutation : permutationTable) // permTable = [ permutations ]
            permutations.add(FromPermutationTable(permutation));

        return permutations;
    }

    // TODO next step:
    //  include the json for automorphisms as part of *.in
    //  create and test a method to convert string[][] to int[][]
    //  call that method in rawGroup / Generate somewhere.

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

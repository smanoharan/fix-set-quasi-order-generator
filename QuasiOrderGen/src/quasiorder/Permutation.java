package quasiorder;

import com.google.gson.Gson;

import java.io.Reader;
import java.util.ArrayList;

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
    public static ArrayList<Permutation> FromJSON(Reader jsonReader)
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
}

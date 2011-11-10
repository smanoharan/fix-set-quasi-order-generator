package quasiorder;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

public class RawGroup
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;
    public final String[] Elements;
    public final String[][][] ConjugacyClasses;

    protected RawGroup(int numElements, int numSubgroups, int numConjugacyClasses, String[] elements, String[][][] conjugacyClasses)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        Elements = elements;
        ConjugacyClasses = conjugacyClasses;
    }

    public static RawGroup FromJSON(Reader jsonOutput) throws IOException
    {
        // parse JSON (which has arrays of arrays of arrays of strings)
        String[][][][] groupProp = ( new Gson()).fromJson(jsonOutput, String[][][][].class);

        // places to look:
        String[] elements = groupProp[0][0][0];
        int numElements = elements.length;
        String[][][] conjugacyClasses = groupProp[1];
        int numConjugacyClasses = conjugacyClasses.length;

        // calculate number of subgroups
        int NumSubgroups = 0;
        for(String[][] arr : conjugacyClasses) NumSubgroups += arr.length;

        return new RawGroup(numElements, NumSubgroups, numConjugacyClasses, elements, conjugacyClasses);
    }
}

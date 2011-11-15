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

    public static RawGroup FromJSON(Reader jsonReader) throws IOException
    {
        // parse JSON (which has arrays of arrays of arrays of strings)
        String[][][][] groupProp = ( new Gson()).fromJson(jsonReader, String[][][][].class);

        // try closing the input-stream. If this fails, nothing we can do.
        try { jsonReader.close(); } catch (Exception e) {}

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

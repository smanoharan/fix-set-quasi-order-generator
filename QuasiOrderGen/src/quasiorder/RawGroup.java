package quasiorder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class RawGroup
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;

    private RawGroup(int numElements, int numSubgroups, int numConjugacyClasses)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
    }

    // TODO test
    public static RawGroup FromGAPOutput(Reader gapOutput) throws IOException
    {
        BufferedReader bReader = new BufferedReader(gapOutput);

        // TODO

        bReader.close();
        return null; // TODO
    }
}

package quasiorder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuasiOrderGenFixture
{
    // 5 elements : { A, B, C, D, E }
    protected static final int NumElem = 5;
    protected static final int IA = 0;
    protected static final int IB = 1;
    protected static final int IC = 2;
    protected static final int ID = 3;
    protected static final int IE = 4;

    protected BitSet[] elementMasks;

    protected static BitSet StringToBitSet(String BitString)
    {
        BitSet result = new BitSet(BitString.length());

        for(int i=0;i<BitString.length();i++)
            if (BitString.charAt(i)=='1')
                result.set(i);

        return result;
    }

    static <T> void assertListEqual(Collection<T> actual, T... expected)
    {
        List<T> expectedList = new ArrayList<T>(expected.length);
        for(T b : expected) expectedList.add(b);

        assertEquals(expectedList.size(), actual.size());
        assertTrue(expectedList.containsAll(actual));
        assertTrue(actual.containsAll(expectedList));
    }
}

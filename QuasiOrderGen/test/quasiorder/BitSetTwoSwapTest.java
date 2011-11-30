package quasiorder;

import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class BitSetTwoSwapTest extends QuasiOrderGenFixture
{
    @Test
    public void Test2x2Swaps()
    {
        //  orig    swap(0,1)
        //  10      11
        //  11      01
        AssertSwappedBitSetIs(StringToBitSet("1011"), 0, 1, "1101", 2);
    }

    @Test
    public void Test3x3Swaps()
    {
        int numElem = 3;
        BitSet table = StringToBitSet("110"+"010"+"101");
        // orig     swap(0,1)   swap(0,2)   swap(1,2)
        // 110      100         101         101
        // 010      110         010         110
        // 101      011         011         001
        AssertSwappedBitSetIs(table, 0, 1, "100"+"110"+"011", numElem);
        AssertSwappedBitSetIs(table, 0, 2, "101"+"010"+"011", numElem);
        AssertSwappedBitSetIs(table, 1, 2, "101"+"110"+"001", numElem);
    }

    @Test
    public void Test4x4Swaps()
    {
        int numElem = 4;
        BitSet table = StringToBitSet("0110"+"1100"+"0111"+"0100");

        //  orig    swap(0,1)   swap(0,2)   swap(0,3)
        //  0110    1100        1101        0100
        //  1100    1010        0110        0101
        //  0111    1011        1100        1110
        //  0100    1000        0100        0110
        AssertSwappedBitSetIs(table, 0, 1, "1100"+"1010"+"1011"+"1000", numElem);
        AssertSwappedBitSetIs(table, 0, 2, "1101"+"0110"+"1100"+"0100", numElem);
        AssertSwappedBitSetIs(table, 0, 3, "0100"+"0101"+"1110"+"0110", numElem);

        //  orig    swap(1,2)   swap(1,3)   swap(2,3)
        //  0110    0110        0011        0101
        //  1100    0111        0001        1100
        //  0111    1010        0111        0100
        //  0100    0010        1001        0111
        AssertSwappedBitSetIs(table, 1, 2, "0110"+"0111"+"1010"+"0010", numElem);
        AssertSwappedBitSetIs(table, 1, 3, "0011"+"0001"+"0111"+"1001", numElem);
        AssertSwappedBitSetIs(table, 2, 3, "0101"+"1100"+"0100"+"0111", numElem);
    }

    // requires: i <= j
    private static void AssertSwappedBitSetIs(BitSet orig, int i, int j, String expected, int numElem)
    {
        BitSet tB = (BitSet)orig.clone();
        FixOrder.Perform2Swap(tB, i, j, numElem);
        assertEquals(String.format("swapping %d with %d", i, j), StringToBitSet(expected), tB);
    }
}

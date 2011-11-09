package quasiordergen;

import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class QuasiOrderGenToFamilySubgroupBitSet extends QuasiOrderGenFixture
{
    protected static final int NumSubgroups = 9;
    private static final int NumClasses = 5;
    BitSet[] conjugateClasses;

    @Before
    public void setUp()
    {

        // Conjugate classes: Note: Z,Y,X,W,... are subgroups.
        // 5 classes: { {Z}, {Y,X}, {W,V}, {U,T,S}, {R} }
        // So there are 1+2+2+3+1 = 9 subgroups
        conjugateClasses = new BitSet[NumClasses];
        conjugateClasses[0] = StringToBitSet("1"+"00"+"00"+"000"+"0");
        conjugateClasses[1] = StringToBitSet("0"+"11"+"00"+"000"+"0");
        conjugateClasses[2] = StringToBitSet("0"+"00"+"11"+"000"+"0");
        conjugateClasses[3] = StringToBitSet("0"+"00"+"00"+"111"+"0");
        conjugateClasses[4] = StringToBitSet("0"+"00"+"00"+"000"+"1");
    }

    @Test
    public void TestTrivialConjugacyClasses()
    {
        // class-set is empty:
        assertFamilyBitSetIsEqual(new BitSet(NumSubgroups), 0);

        // class-set is complete: 1.1111.1111 = 0x1ff
        BitSet expected = new BitSet(NumSubgroups);
        expected.set(0, NumSubgroups);
        assertFamilyBitSetIsEqual(expected,0x1ff);
    }

    @Test
    public void TestFamilySubgroupBitSetForSingletonConjugacyClass()
    {
        // class-set is singleton: so, family mask should equal class mask
        for (int i=0;i<NumClasses;i++)
            assertFamilyBitSetIsEqual(conjugateClasses[i], 1 << i);
    }

    @Test
    public void TestFamilySubgroupBitSetForFourElementConjugacyClasses()
    {
        // class-set is all but 1: so, family mask should be inverted class mask
        for (int i=0;i<NumClasses;i++)
        {
            BitSet expected = (BitSet)(conjugateClasses[i].clone());
            expected.flip(0,NumSubgroups);
            assertFamilyBitSetIsEqual(expected, ~(1 << i));
        }
    }

    @Test
    public void TestFamilySubgroupBitSetForTwoElementConjugacyClasses()
    {
        // class-set is a pair of elements
        // 5 classes: { {Z}, {Y,X}, {W,V}, {U,T,S}, {R} }

        // 0 & 1 : 0.0011 = 0x03
        assertFamilyBitSetIsEqual("1"+"11"+"00"+"000"+"0", 0x03);

        // 0 & 3 : 0.1001 = 0x09
        assertFamilyBitSetIsEqual("1"+"00"+"00"+"111"+"0", 0x09);

        // 1 & 2 : 0.0110 = 0x06
        assertFamilyBitSetIsEqual("0"+"11"+"11"+"000"+"0", 0x06);

        // 3 & 4 : 1.1000 = 0x18
        assertFamilyBitSetIsEqual("0"+"00"+"00"+"111"+"1", 0x18);

        // 2 & 4 : 1.0100 = 0x14
        assertFamilyBitSetIsEqual("0"+"00"+"11"+"000"+"1", 0x14);
    }

    @Test
    public void TestFamilySubgroupBitSetForThreeElementConjugacyClasses()
    {
        // class-set is a pair of elements
        // 5 classes: { {Z}, {Y,X}, {W,V}, {U,T,S}, {R} }

        // 0 & 1 & 2 : 0.0111 = 0x07
        assertFamilyBitSetIsEqual("1"+"11"+"11"+"000"+"0", 0x07);

        // 0 & 3 & 4 : 1.1001 = 0x19
        assertFamilyBitSetIsEqual("1"+"00"+"00"+"111"+"1", 0x19);

        // 1 & 2 & 3: 0.1110 = 0x0e
        assertFamilyBitSetIsEqual("0"+"11"+"11"+"111"+"0", 0x0e);

        // 1 & 3 & 4 : 1.1010 = 0x1a
        assertFamilyBitSetIsEqual("0"+"11"+"00"+"111"+"1", 0x1a);

        // 0 & 2 & 4 : 1.0101 = 0x15
        assertFamilyBitSetIsEqual("1"+"00"+"11"+"000"+"1", 0x15);
    }


    private void assertFamilyBitSetIsEqual(String expected, long conjugateMask)
    {
        assertFamilyBitSetIsEqual(StringToBitSet(expected), conjugateMask);
    }

    private void assertFamilyBitSetIsEqual(BitSet expected, long conjugateMask)
    {
        String message = "Conjugate mask: " + Long.toBinaryString(conjugateMask);
        assertEquals(message,  expected,  QuasiOrderGen.ToSubgroupFamilyBitSet(NumSubgroups, NumClasses, conjugateMask, conjugateClasses));
    }
}

package quasiorder;

import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class UnionClosedTest extends QuasiOrderGenFixture
{
    protected static final int NumSubgroups = 9;
    protected int[][] subgroupUnions;

    @Before
    public void setUp()
    {
        subgroupUnions = new int[][]
        {
            new int[] { 0,      1, 2, 3,    4, 5, 6,        7, 8 },  // I


            new int[] { 1,      1, 4, 5,    4, 5, -1,       7, 8 },  // I A
            new int[] { 2,      4, 2, 6,    4, 7, 6,        7, 8 },  // I B
            new int[] { 3,      5, 6, 3,    7, 5, -1,       7, 8 },  // I C

            new int[] { 4,      4, 4, 7,    4, 7, -1,       7, 8 },  // I A B
            new int[] { 5,      5, 7, 5,    7, 5, 8,        7, 8 },  // I A C
            new int[] { 6,      -1, 6, -1,   -1, 8, 6,      8, 8 },  // I B D <== note: changed BC to BD


            new int[] { 7,      7, 7, 7,    7, 7, 8,        7, 8 },  // I A B C
            new int[] { 8,      8, 8, 8,    8, 8, 8,        8, 8 },  // I A B C D
        };
    }
    
    @Test
    public void TestIntersectionWhenFamilyIsSingleton()
    {
        // family is singleton: so, always closed
        int familyMask = 1;
        for (int i=0;i<9;i++)
        {
            familyMask *= 2;
            assertUnionIsClosed(familyMask);
        }
    }

    @Test
    public void TestIntersectionWhenFamilyIsComplete() // Complete = all has elements in at least one subgroup.
    {
        // family is all subgroups. (mask = 1.1111.1111 (binary) = 0x1ff (hex))
        assertUnionIsClosed(0x1ff);

        // family is all subgroups but the first. Mask = 1.1111.1110 = 1fe
        assertUnionIsClosed(0x1fe);

        // family is all subgroups but the last. Mask = 0.1111.1111 = 0ff
        assertUnionIsNotClosed(0x0ff);

        // family is all but the first and last. Mask = 0.1111.1110 = 0fe
        assertUnionIsNotClosed(0x0fe);

        // family is all but the last two. Mask = 0.0111.1111 = 07f
        assertUnionIsNotClosed(0x07f);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeTwo()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBD,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {IA, IB} = {IABC} : mask = 0.0000.0110 = 006
        assertUnionIsNotClosed(0x006);

        // case 2  : ^ {IC, IAB} = {IABC} : mask = 0.0001.1000 = 018
        assertUnionIsNotClosed(0x018);

        // case 3  : ^ {IBD, IABC} = {IABCD} : mask = 0.1100.0000 = 0c0
        assertUnionIsNotClosed(0x0c0);

        // case 4  : ^ {IABCD, IAB} = {IAB} : mask = 1.0001.0000 = 110
        assertUnionIsClosed(0x110);

        // case 5  : ^ {IB, IABC} = {IB} : mask = 0.1000.0100 = 084
        assertUnionIsClosed(0x084);

        // case 6  : ^ {IBD, IAB} = {-1} : mask = 0.0101.0000 = 050
        assertUnionIsClosed(0x050);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeThree()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBD,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {I, IA, IB, IAB} = {I, IA, ...} : mask = 0.0001.0111 = 017
        assertUnionIsClosed(0x017);

        // case 2  : ^ {I, IC, IAB, IABC} = {I, IC, } : mask = 0.1001.1001 = 099
        assertUnionIsClosed(0x099);

        // case 3  : ^ {IAB, IAC, IABC} = {IA, ...} : mask = 0.1011.0000 = 0b0
        assertUnionIsClosed(0x0b0);
    }


    @Test
    public void TestIntersectionInFamiliesOfSizeFour()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBD,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {IA, IB, IC, IAB} = {I, ...} : mask = 0.0001.1110 = 01e
        assertUnionIsNotClosed(0x01e);

        // case 2  : ^ {IC, IAB, IAC, IABC} = {I, ...} : mask = 0.1011.1000 = 0b8
        assertUnionIsClosed(0x0b8);

        // case 3  : ^ {IC, IAB, IBD} = {I, IC, IB, IAB, ...} : mask = 0.0101.1000 = 058
        assertUnionIsNotClosed(0x058);
    }

    private void assertUnionIsClosed(int subgroupFamilyMask)
    {
        assertUnionIsClosed(true, subgroupFamilyMask);
    }

    private void assertUnionIsNotClosed(int subgroupFamilyMask)
    {
        assertUnionIsClosed(false, subgroupFamilyMask);
    }

    private void assertUnionIsClosed(boolean expected, int subgroupFamilyMask)
    {
        BitSet familyBitSet = GroupUtil.MaskToBitSet(subgroupFamilyMask, NumSubgroups);
        String message = "family=" + subgroupFamilyMask;
        assertEquals(message, expected, GroupUtil.isUnionClosed(subgroupUnions, GroupUtil.BitSetToList(familyBitSet), familyBitSet));
    }

}

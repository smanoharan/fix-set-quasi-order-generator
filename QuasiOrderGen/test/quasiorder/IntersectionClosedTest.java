package quasiorder;

import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class IntersectionClosedTest extends QuasiOrderGenFixture
{
    protected static final int NumSubgroups = 9;
    protected int[][] subgroupIntersections;

    @Before
    public void setUp()
    {
        subgroupIntersections = new int[][]
        {
            new int[] { 0,      0, 0, 0,    0, 0, 0,        0, 0 },  // I


            new int[] { 0,      1, 0, 0,    1, 1, 0,        1, 1 },  // I A
            new int[] { 0,      0, 2, 0,    2, 0, 2,        2, 2 },  // I B
            new int[] { 0,      0, 0, 3,    0, 3, 3,        3, 3 },  // I C

            new int[] { 0,      1, 2, 0,    4, 1, 2,        4, 4 },  // I A B
            new int[] { 0,      1, 0, 3,    1, 5, 3,        5, 5 },  // I A C
            new int[] { 0,      0, 2, 3,    2, 3, 6,        6, 6 },  // I B C


            new int[] { 0,      1, 2, 3,    4, 5, 6,        7, 7 },  // I A B C
            new int[] { 0,      1, 2, 3,    4, 5, 6,        7, 8 },  // I A B C D
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
            assertIntersectionIsClosed(familyMask);
        }
    }

    @Test
    public void TestIntersectionWhenFamilyIsComplete() // Complete = all has elements in at least one subgroup.
    {
        // family is all subgroups. (mask = 1.1111.1111 (binary) = 0x1ff (hex))
        assertIntersectionIsClosed(0x1ff);

        // family is all subgroups but the first. Mask = 1.1111.1110 = 1fe
        assertIntersectionIsNotClosed(0x1fe);

        // family is all subgroups but the last. Mask = 0.1111.1111 = 0ff
        assertIntersectionIsClosed(0x0ff);

        // family is all but the first and last. Mask = 0.1111.1110 = 0fe
        assertIntersectionIsNotClosed(0x0fe);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeTwo()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBC,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {IA, IB} = {I} : mask = 0.0000.0110 = 006
        assertIntersectionIsNotClosed(0x006);

        // case 2  : ^ {IC, IAB} = {I} : mask = 0.0001.1000 = 018
        assertIntersectionIsNotClosed(0x018);

        // case 3  : ^ {IBC, IABC} = {IBC} : mask = 0.1100.0000 = 0c0
        assertIntersectionIsClosed(0x0c0);

        // case 4  : ^ {IABCD, IAB} = {IAB} : mask = 1.0001.0000 = 110
        assertIntersectionIsClosed(0x110);

        // case 5  : ^ {IB, IABC} = {IB} : mask = 0.1000.0100 = 084
        assertIntersectionIsClosed(0x084);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeThree()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBC,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {I, IA, IB, IAB} = {I, IA, ...} : mask = 0.0001.0111 = 017
        assertIntersectionIsClosed(0x017);

        // case 2  : ^ {I, IC, IAB, IABC} = {I, IC, } : mask = 0.1001.1001 = 099
        assertIntersectionIsClosed(0x099);

        // case 3  : ^ {IAB, IAC, IABC} = {IA, ...} : mask = 0.1011.0000 = 0b0
        assertIntersectionIsNotClosed(0x0b0);
    }


    @Test
    public void TestIntersectionInFamiliesOfSizeFour()
    {
        // 9 subgroups :
        //  {
        //      I,
        //      IA, IB, IC,
        //      IAB, IAC, IBC,
        //      IABC, IABCD
        //  }

        // case 1  : ^ {IA, IB, IC, IAB} = {I, ...} : mask = 0.0001.1110 = 01e
        assertIntersectionIsNotClosed(0x01e);

        // case 2  : ^ {IC, IAB, IAC, IBC} = {I, ...} : mask = 0.1011.1000 = 0b8
        assertIntersectionIsNotClosed(0x0b8);

        // case 3  : ^ {IC, IAB, IBC, IABC} = {I, IC, IB, IAB, ...} : mask = 0.1101.1000 = 0d8
        assertIntersectionIsNotClosed(0x0d8);
    }

    private void assertIntersectionIsClosed(int subgroupFamilyMask)
    {
        assertIntersectionIsClosed(true, subgroupFamilyMask);
    }

    private void assertIntersectionIsNotClosed(int subgroupFamilyMask)
    {
        assertIntersectionIsClosed(false, subgroupFamilyMask);
    }

    private void assertIntersectionIsClosed(boolean expected, int subgroupFamilyMask)
    {
        BitSet familyBitSet = GroupUtil.MaskToBitSet(subgroupFamilyMask, NumSubgroups);
        String message = "family=" + subgroupFamilyMask;
        assertEquals(message, expected, GroupUtil.isIntersectionClosed(subgroupIntersections, familyBitSet));
    }

}

package quasiorder;

import org.junit.Before;
import org.junit.Test;
import java.util.BitSet;
import static org.junit.Assert.assertEquals;

public class IntersectionTrivialTest extends QuasiOrderGenFixture
{
    protected static final int NumSubgroups = 9;

    @Before
    public void setUp()
    {
        elementMasks = new BitSet[NumElem];

        // 9 subgroups :
        //  {
        //      {A}, {A,B}, {A,E},
        //      {A,B,C}, {A,C,E}, {A,D,E},
        //      {A,C,D,E}, {A,B,C,D}, {A,B,C,D,E}
        //  }
        elementMasks[IA] = StringToBitSet("111"+"111"+"111");
        elementMasks[IB] = StringToBitSet("010"+"100"+"011");
        elementMasks[IC] = StringToBitSet("000"+"110"+"111");
        elementMasks[ID] = StringToBitSet("000"+"001"+"111");
        elementMasks[IE] = StringToBitSet("001"+"011"+"101");
    }
    
    @Test
    public void TestIntersectionWhenFamilyIsSingleton()
    {
        // family is singleton: so, only trivial when family is {{A}} (when mask 000.000.001)
        int familyMask = 1;
        assertIntersectionIsTrivial(familyMask);
        for (int i=1;i<9;i++)
        {
            familyMask *= 2;
            assertIntersectionIsNotTrivial(familyMask);
        }
    }

    @Test
    public void TestIntersectionWhenFamilyIsComplete() // Complete = all has elements in at least one subgroup.
    {
        // family is all subgroups. (mask = 1.1111.1111 (binary) = 0x1ff (hex))
        assertIntersectionIsTrivial(0x1ff);

        // family is all subgroups but the first. Mask = 1.1111.1110 = 1fe
        assertIntersectionIsTrivial(0x1fe);

        // family is all subgroups but the last. Mask = 0.1111.1111 = 0ff
        assertIntersectionIsTrivial(0x0ff);

        // family is all but the first and last. Mask = 0.1111.1110 = 0fe
        assertIntersectionIsTrivial(0x0fe);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeTwo()
    {
        // 9 subgroups :
        //  {
        //      {A}, {A,B}, {A,E},
        //      {A,B,C}, {A,C,E}, {A,D,E},
        //      {A,C,D,E}, {A,B,C,D}, {A,B,C,D,E}
        //  }

        // case 1  : ^ {{A,B},{A,E}} = {[A]} : mask = 0.0000.0110 = 006
        assertIntersectionIsTrivial(0x006);

        // case 2  : ^ {{A,B,C},{A,C,E}} = {[A,C]} : mask = 0.0001.1000 = 018
        assertIntersectionIsNotTrivial(0x018);

        // case 3  : ^ {{A,C,D,E},{A,B,C,D}} = {[A,C,D]} : mask = 0.1100.0000 = 0c0
        assertIntersectionIsNotTrivial(0x0c0);

        // case 4  : ^ {{A,B,C,D,E},{A,C,E}} = {[A,C,E]} : mask = 1.0001.0000 = 110
        assertIntersectionIsNotTrivial(0x110);

        // case 5  : ^ {{A,E},{A,B,C,D}} = {[A]} : mask = 0.1000.0100 = 084
        assertIntersectionIsTrivial(0x084);
    }

    @Test
    public void TestIntersectionInFamiliesOfSizeThree()
    {
        // 9 subgroups :
        //  {
        //      {A}, {A,B}, {A,E},
        //      {A,B,C}, {A,C,E}, {A,D,E},
        //      {A,C,D,E}, {A,B,C,D}, {A,B,C,D,E}
        //  }

        // case 1  : ^ {{A,B},{A,E},{A,C,E}} = {[A]} : mask = 0.0001.0110 = 016
        assertIntersectionIsTrivial(0x016);

        // case 2  : ^ {{A,B,C},{A,C,E},{A,B,C,D}} = {[A,C]} : mask = 0.1001.1000 = 098
        assertIntersectionIsNotTrivial(0x098);

        // case 3  : ^ {{A,C,E},{A,D,E},{A,B,C,D}} = {[A]} : mask = 0.1011.0000 = 0b0
        assertIntersectionIsTrivial(0x0b0);
    }


    @Test
    public void TestIntersectionInFamiliesOfSizeFour()
    {
        // 9 subgroups :
        //  {
        //      {A}, {A,B}, {A,E},
        //      {A,B,C}, {A,C,E}, {A,D,E},
        //      {A,C,D,E}, {A,B,C,D}, {A,B,C,D,E}
        //  }

        // case 1  : ^ {{A,B},{A,E},{A,B,C},{A,C,E}} = {[A]} : mask = 0.0001.1110 = 01e
        assertIntersectionIsTrivial(0x01e);

        // case 2  : ^ {{A,B,C},{A,C,E},{A,D,E},{A,C,D,E}} = {[A]} : mask = 0.1011.1000 = 0b8
        assertIntersectionIsTrivial(0x0b8);

        // case 3  : ^ {{A,B,C},{A,C,E},{A,C,D,E},{A,B,C,D}} = {[A,C]} : mask = 0.1101.1000 = 0d8
        assertIntersectionIsNotTrivial(0x0d8);
    }

    private void assertIntersectionIsTrivial(int subgroupFamilyMask)
    {
        assertIntersectionIsTrivial(true, subgroupFamilyMask);
    }

    private void assertIntersectionIsNotTrivial(int subgroupFamilyMask)
    {
        assertIntersectionIsTrivial(false, subgroupFamilyMask);
    }

    private void assertIntersectionIsTrivial(boolean expected, int subgroupFamilyMask)
    {
        BitSet familyBitSet = GroupUtil.MaskToBitSet(subgroupFamilyMask, NumSubgroups);
        String message = "family=" + subgroupFamilyMask;
        assertEquals(message, expected, GroupUtil.isIntersectionTrivial(elementMasks, familyBitSet));
    }

}

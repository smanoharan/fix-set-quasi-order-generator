package quasiordergen;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class QuasiOrderGenIsRelatedTest extends QuasiOrderGenFixture
{
    @Before
    public void setUp()
    {
        elementMasks = new BitSet[NumElem];

        // 4 subgroups : { {A}, {A,B}, {A,B,C}, {A,B,C,D,E} }
        elementMasks[IA] = StringToBitSet("1111");   // A is in all subgroups
        elementMasks[IB] = StringToBitSet("0111");   // B is in sub-2,3,4
        elementMasks[IC] = StringToBitSet("0011");   // C is in sub-3 and sub-4
        elementMasks[ID] = StringToBitSet("0001");   // D is in sub-4 only
        elementMasks[IE] = StringToBitSet("0001");   // E is in sub-4 only
    }
    
    @Test
    public void testIsRelatedForSingletonFamilyIsACompleteGraph()
    {
        // family is singleton: (in this case, only the 3rd subgroup ({A,B,C}) is part of the family)
        BitSet subgroupFamilyMask = StringToBitSet("0010");

        List<Integer> inSub3 = new ArrayList<Integer>();
        List<Integer> notInSub3 = new ArrayList<Integer>();
        inSub3.add(IA);     // A is in sub-3
        inSub3.add(IB);     // B is in sub-3
        inSub3.add(IC);     // C is in sub-3
        notInSub3.add(ID);  // D is not in sub-3
        notInSub3.add(IE);  // E is not in sub-3

        // Check that :
        //  All elements \in sub-3 are related.
        for (int i : inSub3)
            for (int j : inSub3)
                assertIsRelated(i, j, subgroupFamilyMask);

        //  All elements not \in sub-3 are related.
        for (int i : notInSub3)
            for (int j : notInSub3)
                assertIsRelated(i, j, subgroupFamilyMask);

        //  All elements \in sub-3 are <= all elements not \in sub-3 (but not vice versa)
        for (int i : inSub3)
        {
            for (int j : notInSub3)
            {
                assertIsRelated(j, i, subgroupFamilyMask);
                assertIsNotRelated(i, j, subgroupFamilyMask);
            }
        }
    }

    @Test
    public void TestIsRelatedRelationIsAsExpectedWhenFamilyContainsAllSubgroups()
    {
        // family contains all subgroups:
        BitSet subgroupFamilyMask = StringToBitSet("1111");

        // expected relations:
        //      everything <= A
        for (int i=0;i<NumElem;i++)
            assertIsRelated(i, IA, subgroupFamilyMask);

        //      (all but A) <= B
        for (int i=0;i<NumElem;i++)
            if (i == IA) assertIsNotRelated(i, IB, subgroupFamilyMask);
            else assertIsRelated(i, IB, subgroupFamilyMask);

        //      (all but A,B) <= C
        for (int i=0;i<NumElem;i++)
            if (i == IA || i == IB) assertIsNotRelated(i, IC, subgroupFamilyMask);
            else assertIsRelated(i, IC, subgroupFamilyMask);

        //      (all but A,B,C) <= D,E
        for (int i=0;i<NumElem;i++)
        {
            if (i <= IC)
            {
                assertIsNotRelated(i, ID, subgroupFamilyMask);
                assertIsNotRelated(i, IE, subgroupFamilyMask);
            }
            else
            {
                assertIsRelated(i, ID, subgroupFamilyMask);
                assertIsRelated(i, IE, subgroupFamilyMask);
            }
        }
    }

    @Test
    public void TestIsRelatedRelationIsAsExpectedWhenFamilyContainsTwoSubgroups()
    {
        // subgroup family: {A,B}, {A,B,C}
        BitSet subgroupFamilyMask = StringToBitSet("0110");

        // expected relations:
        //      everything <= A,B
        for (int i=0;i<NumElem;i++)
        {
            assertIsRelated(i, IA, subgroupFamilyMask);
            assertIsRelated(i, IB, subgroupFamilyMask);
        }

        //      (all but A,B) <= C
        for (int i=0;i<NumElem;i++)
            if (i == IA || i == IB) assertIsNotRelated(i, IC, subgroupFamilyMask);
            else assertIsRelated(i, IC, subgroupFamilyMask);

        //      (only D,E) <= D,E
        for (int i=0;i<NumElem;i++)
        {
            if (i <= IC)
            {
                assertIsNotRelated(i, ID, subgroupFamilyMask);
                assertIsNotRelated(i, IE, subgroupFamilyMask);
            }
            else
            {
                assertIsRelated(i, ID, subgroupFamilyMask);
                assertIsRelated(i, IE, subgroupFamilyMask);
            }
        }
    }

    private void assertIsRelatedIsEqualTo(boolean expected, int i, int j, BitSet subgroupFamilyMask)
    {
        final int offset = (int)'A';
        char ci = (char)(i + offset);
        char cj = (char)(j + offset);
        String message = "family=" + subgroupFamilyMask + "; " + ci + "<=" + cj;
        assertEquals(message, expected, QuasiOrderGen.isRelated(elementMasks[i], elementMasks[j], subgroupFamilyMask));
    }

    private void assertIsRelated(int i, int j, BitSet subgroupFamily)
    {
        assertIsRelatedIsEqualTo(true, i, j, subgroupFamily);
    }

    private void assertIsNotRelated(int i, int j, BitSet subgroupFamily)
    {
        assertIsRelatedIsEqualTo(false, i, j, subgroupFamily);
    }
}

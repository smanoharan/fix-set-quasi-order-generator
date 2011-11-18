package quasiorder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NormalFaithfulTest extends QuasiOrderGenFixture
{
    @Test
    public void TestRelationsOfS2AreFaithful()
    {
        // S2: { (), (12) }
        int numElem = 2;

        // subgroup family: { {() (12) } ; expected: complete
        assertIsNotFaithful("1111", numElem);

        // subgroup family: { {()} , { () (12) } } ; expected: all but 1<=(12)
        assertIsFaithful("1011", numElem);
    }

    @Test
    public void TestRelationsOfZ4AreFaithful()
    {
        int numElem = 4;

        // subgroup family: { {0} } ; expected : all but 0<={1,2,3} ;
        assertIsFaithful("1000" + "1111" + "1111" + "1111", numElem);

        // whole group: expected: complete relation.
        assertIsNotFaithful("1111" + "1111" + "1111" + "1111", numElem);

        // subgroup family: { {0, 2} } : expected: (0,2) <-- (1,3)
        assertIsNotFaithful("1010" + "1111" + "1010" + "1111", numElem);

        // subgroup family: { {0}, {0,2} } : expected: (0) <-- (2) <-- (1,3)
        assertIsFaithful("1000" + "1111" + "1010" + "1111", numElem);
    }

    @Test
    public void TestRelationsOfS2AreNormal()
    {
        // S2: { (), (12) }
        String isSubgroupNormal = "11";

        // subgroup family: { {()} } ;
        assertIsNormal("10", isSubgroupNormal);

        // subgroup family: { {() (12) } ;
        assertIsNormal("01", isSubgroupNormal);

        // subgroup family: { {()} , { () (12) } } ;
        assertIsNormal("11", isSubgroupNormal);
    }


    @Test
    public void TestRelationsOfZ4AreNormal()
    {
        String inputGroup = "1001";

        // trivial group only
        assertIsNormal("1000", inputGroup);

        // trivial and whole group
        assertIsNormal("1001", inputGroup);

        // all but trivial and whole
        assertIsNotNormal("0110", inputGroup);

        // all groups
        assertIsNotNormal("1111", inputGroup);
    }

    private static void assertIsFaithful(String relation, int numElem)
    {
        assertIsFaithful(true, relation, numElem);
    }

    private static void assertIsNotFaithful(String relation, int numElem)
    {
        assertIsFaithful(false, relation, numElem);
    }

    private static void assertIsFaithful(boolean expected, String relation, int numElem)
    {
        assertEquals(expected, Generate.IsFaithful(StringToBitSet(relation), numElem));
    }

    private static void assertIsNormal(String familyMask, String isSubgroupNormal)
    {
        assertIsNormal(true, familyMask, isSubgroupNormal);
    }
    
    private static void assertIsNotNormal(String familyMask, String isSubgroupNormal)
    {
        assertIsNormal(false, familyMask, isSubgroupNormal);
    }

    private static void assertIsNormal(boolean expected, String familyMask, String IsSubgroupNormal)
    {
        assertEquals(expected, Generate.IsNormal(StringToBitSet(familyMask), StringToBitSet(IsSubgroupNormal)));
    }

}

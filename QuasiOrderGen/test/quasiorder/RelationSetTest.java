package quasiorder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class RelationSetTest extends QuasiOrderGenFixture
{
    private static final int NumElem = 4;
    private RelationSet relations;

    @Before
    public void Setup()
    {
        relations = new RelationSet();
    }

    @Test
    public void BuildRelationOfS2()
    {
        // S2: { (), (12) }
        int numElements = 2;
        int numSubgroups = 2;
        int numConjugacyClasses = 2;

        String[] elementNames = new String[] {"()", "(12)"};
        String[] subgroupNames = new String[] {"()", "() (12)"};
        BitSet[] conjugacyClasses = new BitSet[] {StringToBitSet("10"), StringToBitSet("01")};
        elementMasks = new BitSet[] {StringToBitSet("11"), StringToBitSet("01")};

        InputContainer inputGroup =
                new InputContainer(numElements, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupNames, conjugacyClasses);

        // subgroup family: { {()} } ; expected : all but 1<=(12)
        assertRelationEqual(inputGroup, "10", "1011");

        // subgroup family: { {() (12) } ; expected: complete
        assertRelationEqual(inputGroup, "01", "1111");

        // subgroup family: { {()} , { () (12) } } ; expected: all but 1<=(12)
        assertRelationEqual(inputGroup, "11", "1011");
    }

    @Test
    public void BuildRelationOfZ4()
    {
        // Z4: { 0, 1, 2, 3 } (under addition)
        int numElements = 4;

        // subgroups: {0}, {0,2}, Z4
        int numSubgroups = 3;
        int numConjugacyClasses = 3; // since abelian.

        String[] elementNames = new String[] {"0", "1", "2", "3"};
        String[] subgroupNames = new String[] {"T", "N", "G"}; // trivial, normal, (whole)group
        BitSet[] conjugacyClasses = new BitSet[] { StringToBitSet("100"), StringToBitSet("010"), StringToBitSet("001")};
        elementMasks = new BitSet[] { StringToBitSet("111"), StringToBitSet("001"), StringToBitSet("011"), StringToBitSet("001") };

        InputContainer inputGroup =
                new InputContainer(numElements, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupNames, conjugacyClasses);

        // subgroup family: { {0} } ; expected : all but 0<={1,2,3} ;
        assertRelationEqual(inputGroup, "100", "1000"+"1111"+"1111"+"1111");

        // whole group: expected: complete relation.
        assertRelationEqual(inputGroup, "001", "1111"+"1111"+"1111"+"1111");

        // subgroup family: { {0, 2} } : expected: (0,2) <-- (1,3)
        assertRelationEqual(inputGroup, "010", "1010"+"1111"+"1010"+"1111");

        // subgroup family: { {0}, {0,2} } : expected: (0) <-- (2) <-- (1,3)
        assertRelationEqual(inputGroup, "110", "1000"+"1111"+"1010"+"1111");
    }

    private static void assertRelationEqual(InputContainer input, String familyMask, String expectedRel)
    {
        String messsage = "familyMask: " + familyMask + " expectedRel: " + expectedRel;
        BitSet actual = RelationSet.BuildRelation(input, StringToBitSet(familyMask));
        assertEquals(messsage, StringToBitSet(expectedRel), actual);
    }

    @Test
    public void AddingDuplicatesChangesFamilyListButNotKeySet()
    {
        // add a single entry
        BitSet first = new BitSet(NumElem*NumElem);
        first.set(NumElem, NumElem*(NumElem-1)); // set some bits
        BitSet familyFirst = StringToBitSet("00110");
        relations.Add(first, familyFirst);

        // test only the first relation exists
        assertListEqual(relations.uniqRelations.keySet(), first);
        assertListEqual(relations.uniqRelations.get(first), familyFirst);

        // add the same entry again, with a different family
        BitSet second = (BitSet)first.clone();
        BitSet familySecond = StringToBitSet("10100");
        relations.Add(second, familySecond);

        // test that the first relation exists, but with two families:
        assertListEqual(relations.uniqRelations.keySet(), first);
        assertListEqual(relations.uniqRelations.keySet(), second);
        assertListEqual(relations.uniqRelations.get(first), familyFirst, familySecond);
        assertListEqual(relations.uniqRelations.get(second), familyFirst, familySecond);

        // add a different entry
        BitSet third = new BitSet(NumElem*NumElem);
        third.set(0);
        BitSet familyThird = StringToBitSet("00011");
        relations.Add(third, familyThird);

        // test that there are now two relations with appropriate families:
        assertListEqual(relations.uniqRelations.keySet(), first, third);
        assertListEqual(relations.uniqRelations.get(first), familyFirst, familySecond);
        assertListEqual(relations.uniqRelations.get(second), familyFirst, familySecond);
        assertListEqual(relations.uniqRelations.get(third), familyThird);
    }

    private <T> void assertListEqual(Collection<T> actual, T... expected)
    {
        List<T> expectedList = new ArrayList<T>(expected.length);
        for(T b : expected) expectedList.add(b);

        assertEquals(expectedList.size(), actual.size());
        assertTrue(expectedList.containsAll(actual));
        assertTrue(actual.containsAll(expectedList));
    }
}

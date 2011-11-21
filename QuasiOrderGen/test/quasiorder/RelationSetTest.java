package quasiorder;

import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

public class RelationSetTest extends QuasiOrderGenFixture
{
    private static final int NumElem = 4;
    private RelationSet relations;
    private String Colour = "red";

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
        BitSet[] subgroupMasks = new BitSet[] {StringToBitSet("10"), StringToBitSet("11")};
        elementMasks = new BitSet[] {StringToBitSet("11"), StringToBitSet("01")};
        int[][] subgroupIntersections = new int[][]{ new int[] {0, 0}, new int[] {0, 1}};
        int[][] subgroupUnions = new int[][]{ new int[]{0, 1}, new int[]{1, 1}};
        BitSet isSubgroupNormal = StringToBitSet("11");

        Group inputGroup = new Group(numElements, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupMasks, subgroupNames, subgroupIntersections,
                subgroupUnions, conjugacyClasses, isSubgroupNormal);

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
        BitSet[] subgroupMasks = new BitSet[] { StringToBitSet("1000"), StringToBitSet("1010"), StringToBitSet("1111")};
        BitSet[] conjugacyClasses = new BitSet[] { StringToBitSet("100"), StringToBitSet("010"), StringToBitSet("001")};
        elementMasks = new BitSet[] { StringToBitSet("111"), StringToBitSet("001"), StringToBitSet("011"), StringToBitSet("001") };
        int[][] subgroupIntersections = new int[][]{ new int[] {0, 0, 0}, new int[] {0, 1, 1}, new int[] {0, 1, 2} };
        int[][] subgroupUnions = new int[][]{new int[]{0, 1, 2}, new int[]{1, 1, 2}, new int[]{2, 2, 2}};
        BitSet isSubgroupNormal = StringToBitSet("111");

        Group inputGroup = new Group(numElements, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupMasks, subgroupNames,
                subgroupIntersections, subgroupUnions, conjugacyClasses, isSubgroupNormal);

        // subgroup family: { {0} } ; expected : all but 0<={1,2,3} ;
        assertRelationEqual(inputGroup, "100", "1000"+"1111"+"1111"+"1111");

        // whole group: expected: complete relation.
        assertRelationEqual(inputGroup, "001", "1111"+"1111"+"1111"+"1111");

        // subgroup family: { {0, 2} } : expected: (0,2) <-- (1,3)
        assertRelationEqual(inputGroup, "010", "1010"+"1111"+"1010"+"1111");

        // subgroup family: { {0}, {0,2} } : expected: (0) <-- (2) <-- (1,3)
        assertRelationEqual(inputGroup, "110", "1000"+"1111"+"1010"+"1111");
    }

    @Test
    public void AddingDuplicatesChangesFamilyListButNotKeySet()
    {
        // add a single entry
        BitSet first = new BitSet(NumElem*NumElem);
        first.set(NumElem, NumElem*(NumElem-1)); // set some bits
        BitSet familyFirst = StringToBitSet("00110");
        relations.Add(first, familyFirst, Colour, true, true);

        // test only the first relation exists
        assertListEqual(relations.RelationsFamilyMap.keySet(), first);
        assertListEqual(relations.RelationsFamilyMap.get(first), familyFirst);

        // add the same entry again, with a different family
        BitSet second = (BitSet)first.clone();
        BitSet familySecond = StringToBitSet("10100");
        relations.Add(second, familySecond, Colour, true, true);

        // test that the first relation exists, but with two families:
        assertListEqual(relations.RelationsFamilyMap.keySet(), first);
        assertListEqual(relations.RelationsFamilyMap.keySet(), second);
        assertListEqual(relations.RelationsFamilyMap.get(first), familyFirst, familySecond);
        assertListEqual(relations.RelationsFamilyMap.get(second), familyFirst, familySecond);

        // add a different entry
        BitSet third = new BitSet(NumElem*NumElem);
        third.set(0);
        BitSet familyThird = StringToBitSet("00011");
        relations.Add(third, familyThird, Colour, true, true);

        // test that there are now two relations with appropriate families:
        assertListEqual(relations.RelationsFamilyMap.keySet(), first, third);
        assertListEqual(relations.RelationsFamilyMap.get(first), familyFirst, familySecond);
        assertListEqual(relations.RelationsFamilyMap.get(second), familyFirst, familySecond);
        assertListEqual(relations.RelationsFamilyMap.get(third), familyThird);
    }
}

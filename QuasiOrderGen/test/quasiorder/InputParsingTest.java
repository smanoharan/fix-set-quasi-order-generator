package quasiorder;

import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class InputParsingTest extends QuasiOrderGenFixture
{
    public static final String JSON_STRING = "[\n" +
            "\t[\n" +
            "\t\t[ [ \"()\", \"(1,3)\", \"(1,2,3)\", \"(2,3)\", \"(1,3,2)\", \"(1,2)\" ] ]\n" +
            "\t], \n" +
            "\t[\n" +
            "\t\t[ [ \"()\" ] ],\n" +
            "\t\t[ [ \"()\", \"(2,3)\" ], [ \"()\", \"(1,2)\" ], [ \"()\", \"(1,3)\" ] ],\n" +
            "\t\t[ [ \"()\", \"(1,3,2)\", \"(1,2,3)\" ] ],\n" +
            "\t\t[ [ \"()\", \"(1,3,2)\", \"(1,2,3)\", \"(2,3)\", \"(1,3)\", \"(1,2)\" ] ]\n" +
            "\t], \n" +
            "\t[\n" +
            "\t\t[ " +
            "       [ \"()\", \"(1,3)\" ], " +
            "       [\"(1,3)\" , \"(2,3)\"], " +
            "       [\"(1,2,3)\", \"(1,3,2)\"], " +
            "       [\"(2,3)\" , \"()\"], " +
            "       [\"(1,3,2)\", \"(1,2)\"], " +
            "       [\"(1,2)\" , \"(1,2,3)\"] " +
            "   ],\n" +
            "\t\t[ " +
            "       [ \"()\", \"(1,3)\" ], " +
            "       [\"(1,3)\" , \"()\"], " +
            "       [\"(1,2,3)\", \"(1,2)\"], " +
            "       [\"(2,3)\" , \"(1,2,3)\"], " +
            "       [\"(1,3,2)\", \"(1,3,2)\"], " +
            "       [\"(1,2)\" , \"(2,3)\"] " +
            "   ],\n" +
            "\t\t[ " +
            "       [ \"()\", \"()\" ], " +
            "       [\"(1,3)\" , \"(1,2,3)\"], " +
            "       [\"(1,2,3)\", \"(1,3)\"], " +
            "       [\"(2,3)\" , \"(2,3)\"], " +
            "       [\"(1,3,2)\", \"(1,2)\"], " +
            "       [\"(1,2)\" , \"(1,3,2)\"] " +
            "   ]\n" +
            "\t], \n" +
            "]";

    public static final int NumElem = 6;
    public static final int NumSubgroups = 6;
    public static final int NumConjClasses = 4;
    public static final String[] UNSORTED_ELEMENTS = new String[] { "()", "(1,3)", "(1,2,3)", "(2,3)", "(1,3,2)", "(1,2)" };
    public static final String[] ELEMENTS = new String[] { "()", "(1,2)", "(1,3)", "(2,3)", "(1,2,3)", "(1,3,2)" };
    public static final String[][][] CONJUGACY_CLASSES = new String[][][] {
            { { "()" } },
            { { "()", "(2,3)" }, { "()", "(1,2)" }, { "()", "(1,3)" } },
            { { "()", "(1,3,2)", "(1,2,3)" } },
            { { "()", "(1,3,2)", "(1,2,3)", "(2,3)", "(1,3)", "(1,2)" } }
    };
    public static final String[] SUBGROUP_NAMES = new String[] {
            "()",
            "() (2,3)",
            "() (1,2)",
            "() (1,3)",
            "() (1,3,2) (1,2,3)",
            "() (1,3,2) (1,2,3) (2,3) (1,3) (1,2)"
    };

    @Test
    public void TestJSONOfS3ParsesCorrectly() throws Exception
    {
        Group.RawGroup actual = Group.FromJSON(new StringReader(JSON_STRING));

        assertEquals("NumElements:", NumElem, actual.NumElements);
        assertEquals("NumSubgroups:", NumSubgroups, actual.NumSubgroups);
        assertEquals("NumConjugacyClasses:", NumConjClasses, actual.NumConjugacyClasses);
        AssertArraysAreEqual("Element", UNSORTED_ELEMENTS, actual.Elements);

        for(int i=0;i<NumConjClasses;i++)
            for (int j=0;j<CONJUGACY_CLASSES[i].length;j++)
                AssertArraysAreEqual("Subgroup-" + i + "-" + j, CONJUGACY_CLASSES[i][j], actual.ConjugacyClasses[i][j]);
    }

    @Test
    public void TestS3IsProcessedCorrectlyWhenSorted() throws Exception
    {
        TestS3IsProcessedCorrectly(true, ELEMENTS, //elem: { "()", "(1,2)", "(1,3)", "(2,3)", "(1,2,3)", "(1,3,2)" };
                new BitSet[] // elem-masks
                {
                    StringToBitSet("111111"),   // ()
                    StringToBitSet("001001"),   // (12)
                    StringToBitSet("000101"),   // (13)
                    StringToBitSet("010001"),   // (23)
                    StringToBitSet("000011"),   // (123)
                    StringToBitSet("000011")    // (132)
                },
                new BitSet[] // subgroup-masks
                {
                    StringToBitSet("100000"),   // ()
                    StringToBitSet("100100"),   // () (23)
                    StringToBitSet("110000"),   // () (12)
                    StringToBitSet("101000"),   // () (13)
                    StringToBitSet("100011"),   // () (132) (123)
                    StringToBitSet("111111"),   // Whole Group
                },
                new int[][][]
                {
                        toPairs(0, 2, 2, 3, 4, 5, 3, 0, 5, 1, 1, 4), // (0 2 3) (4 5 1)
                        toPairs(0, 2, 2, 0, 4, 1, 3, 4, 5, 5, 1, 3), // (0 2) (1 3 4) (5)
                        toPairs(0, 0, 2, 4, 4, 2, 3, 3, 5, 1, 1, 5) // (0) (1 5) (3) (2 4)
                },
                toPermutationArr(
                        ToPermutation(4, 5, 1, 4, 2, 3, 0, 2),
                        ToPermutation(3, 4, 1, 3, 0, 2),
                        ToPermutation(2, 4, 1, 5)
                )
        );
    }

    @Test
    public void TestS3IsProcessedCorrectlyWhenNotSorted() throws Exception
    {
        TestS3IsProcessedCorrectly(false, UNSORTED_ELEMENTS, // elements: "()", "(1,3)", "(1,2,3)", "(2,3)", "(1,3,2)", "(1,2)"
                new BitSet[] // elem-masks
                {
                    StringToBitSet("111111"),   // ()
                    StringToBitSet("000101"),   // (13)
                    StringToBitSet("000011"),   // (123)
                    StringToBitSet("010001"),   // (23)
                    StringToBitSet("000011"),   // (132)
                    StringToBitSet("001001")    // (12)
                } ,
                new BitSet[] // subgroup-masks
                {
                    StringToBitSet("100000"),   // ()
                    StringToBitSet("100100"),   // () (23)
                    StringToBitSet("100001"),   // () (12)
                    StringToBitSet("110000"),   // () (13)
                    StringToBitSet("101010"),   // () (132) (123)
                    StringToBitSet("111111"),   // Whole Group
                },
                new int[][][]
                {
                    toPairs(0, 1, 1, 3, 2, 4, 3, 0, 4, 5, 5, 2),    // (0 1 3) (2 4 5)
                    toPairs(0, 1, 1, 0, 2, 5, 3, 2, 4, 4, 5, 3),    // (0 1) (2 5 3) (4)
                    toPairs(0, 0, 1, 2, 2, 1, 3, 3, 4, 5, 5, 4)    // (0) (1 2) (3) (4 5)
                },
                toPermutationArr(
                    ToPermutation(4, 5, 2, 4, 1, 3, 0, 1),
                    ToPermutation(3, 5, 2, 5, 0, 1),
                    ToPermutation(4, 5, 1, 2)
                )
        );
    }

    private static int[][] toPairs(int ... es)
    {
        int[][] pairs = new int[es.length/2][];
        for(int i=0;i<pairs.length;i++)
            pairs[i] = new int[] {es[2*i], es[2*i + 1]};
        return pairs;
    }

    private static ArrayList<Permutation> toPermutationArr(Permutation ... ps)
    {
        return new ArrayList<Permutation>(Arrays.asList(ps));
    }

    private void TestS3IsProcessedCorrectly(boolean sort, String[] elems, BitSet[] elemMasks, BitSet[] subgroupMasks,
                                            int[][][] expectedAutomorphismPermutationTable,
                                            ArrayList<Permutation> expectedAutomorphismPermutations) throws Exception
    {
        Group.RawGroup rawGroup = Group.FromJSON(new StringReader(JSON_STRING));
        Group actual = Group.FromRawGroup(rawGroup,sort);

        assertEquals("NumElements:", NumElem, actual.NumElements);
        assertEquals("NumSubgroups:", NumSubgroups, actual.NumSubgroups);
        assertEquals("NumConjugacyClasses:", NumConjClasses, actual.NumConjugacyClasses);

        AssertArraysAreEqual("Element", elems, actual.ElementNames);
        AssertArraysAreEqual("Subgroup-Names", SUBGROUP_NAMES, actual.SubgroupNames);
        AssertArraysAreEqual("Element Masks", actual.ElementMasks, elemMasks);
        AssertArraysAreEqual("SubgroupMasks", actual.SubgroupMasks, subgroupMasks);

        int[][] subgroupIntersections = new int[][]{
                new int[] {0, 0, 0, 0, 0, 0},
                new int[] {0, 1, 0, 0, 0, 1},
                new int[] {0, 0, 2, 0, 0, 2},
                new int[] {0, 0, 0, 3, 0, 3},
                new int[] {0, 0, 0, 0, 4, 4},
                new int[] {0, 1, 2, 3, 4, 5}
        };

        int[][] subgroupUnions = new int[][]{
                new int[] {0,  1,  2,  3,  4, 5},
                new int[] {1,  1, -1, -1, -1, 5},
                new int[] {2, -1,  2, -1, -1, 5},
                new int[] {3, -1, -1,  3, -1, 5},
                new int[] {4, -1, -1, -1,  4, 5},
                new int[] {5,  5,  5,  5,  5, 5}
        };

        for (int i=0;i<NumSubgroups;i++)
        {
            int[] actualSubgroupIntersection = actual.SubgroupIntersections[i];
            int[] actualSubgroupUnion=  actual.SubgroupUnions[i];
            for (int j=0;j<actualSubgroupIntersection.length;j++)
            {
                assertEquals("SubgroupUnions-"+i+"-"+j, subgroupUnions[i][j], actualSubgroupUnion[j]);
                assertEquals("SubgroupIntersections-"+i+"-"+j, subgroupIntersections[i][j], actualSubgroupIntersection[j]);
            }
        }

        AssertArraysAreEqual("Conjugacy Classes", actual.ConjugacyClasses,
                StringToBitSet("100000"),   // type:()
                StringToBitSet("011100"),   // type:(ab)
                StringToBitSet("000010"),   // type: (abc)
                StringToBitSet("000001"));  // type: G
        assertEquals("IsSubgroupNormal", StringToBitSet("100011"), actual.IsSubgroupNormal);

        for(int p=0;p<expectedAutomorphismPermutationTable.length;p++)
        {
            int[][] expPermutation = expectedAutomorphismPermutationTable[p];
            int[][] actPermutation = actual.Automorphisms[p];
            AssertPermutationTablesAreEqual("Automorphism-permutation-"+p, expPermutation, actPermutation);
        }

        AssertPermutationListsAreEqual("Automorphism-Permutations", expectedAutomorphismPermutations, actual.Permutations);
    }

    private static void AssertPermutationTablesAreEqual(String msg, int[][] expected, int[][] actual)
    {
        assertEquals(msg + "-length", expected.length, actual.length);
        for(int i=0;i<expected.length;i++)
        {
            int[] exp = expected[i];
            int[] act = actual[i];
            assertEquals(String.format("%s-%d-length", msg, i), exp.length, act.length);
            for(int j=0;j<exp.length;j++)
                assertEquals(String.format("%s-%d-%d", msg, i, j), exp[j], act[j]);
        }
    }

    @Test
    public void TestGenerateIntersectionOfTwoNonTrivialSubgroupsIsNonTrivial()
    {
        BitSet[] subgroups = new BitSet[]
        {
            StringToBitSet("1000"), //  0 : { A }
            StringToBitSet("1100"), //  1 : { A , B }
            StringToBitSet("1110"), //  2 : { A , B , C }
            StringToBitSet("1010"), //  3 : { A , C }
            StringToBitSet("1011"), //  4 : { A , C , D }
            StringToBitSet("1101"), //  5 : { A , B , D }
            StringToBitSet("1111")  //  6 : { A , B , C, D }
        };

        // case 1 : 1 ^ 3 = 0
        AssertIntersectionIsCorrect(1, 3, 0, subgroups);

        // case 2 : 1 ^ 5 = 1
        AssertIntersectionIsCorrect(1, 5, 1, subgroups);

        // case 3 : 2 ^ 4 = 3
        AssertIntersectionIsCorrect(2, 4, 3, subgroups);

        // case 4 : 2 ^ 5 = 1
        AssertIntersectionIsCorrect(2, 5, 1, subgroups);

        // case 2 : 5 ^ 6 = 5
        AssertIntersectionIsCorrect(5, 6, 5, subgroups);
    }

    private static void AssertIntersectionIsCorrect(int i1, int i2, int expectedIndex, BitSet[] subgroups)
    {
        int actualIndex = GroupUtil.GenerateCombination(i1, i2, subgroups, new Group.IBitSetOperation() {
            public void combine(BitSet b1, BitSet b2) {
                b1.and(b2);
            }
        }, true);
        String msg = "Intersection of " + subgroups[i1] + " and " + subgroups[i2] +
                ": expected " + subgroups[expectedIndex] + " actual:" + subgroups[actualIndex];
        assertEquals(msg, expectedIndex, actualIndex);
    }

    private static <T> void AssertArraysAreEqual(T arrayName, T[] expected, T... actual)
    {
        for (int i=0;i<expected.length;i++)
            assertEquals(arrayName+"-"+i, expected[i], actual[i]);
    }
}


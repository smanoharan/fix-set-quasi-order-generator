package quasiorder;

import org.junit.Test;

import java.io.StringReader;
import java.util.BitSet;

import static org.junit.Assert.assertEquals;

public class InputParsingTest extends QuasiOrderGenFixture
{
    public static final String JSON_STRING = "[\n" +
            "\t[\n" +
            "\t\t[ [ \"()\", \"(1,3)\", \"(1,2,3)\", \"(2,3)\", \"(1,3,2)\", \"(1,2)\" ] ]\n" +
            "\t] ,\n" +
            "\t[\n" +
            "\t\t[ [ \"()\" ] ],\n" +
            "\t\t[ [ \"()\", \"(2,3)\" ], [ \"()\", \"(1,2)\" ], [ \"()\", \"(1,3)\" ] ],\n" +
            "\t\t[ [ \"()\", \"(1,3,2)\", \"(1,2,3)\" ] ],\n" +
            "\t\t[ [ \"()\", \"(1,3,2)\", \"(1,2,3)\", \"(2,3)\", \"(1,3)\", \"(1,2)\" ] ]\n" +
            "\t]\n" +
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
        RawGroup actual = RawGroup.FromJSON(new StringReader(JSON_STRING));

        assertEquals("NumElements:", NumElem, actual.NumElements);
        assertEquals("NumSubgroups:", NumSubgroups, actual.NumSubgroups);
        assertEquals("NumConjugacyClasses:", NumConjClasses, actual.NumConjugacyClasses);
        assertArraysAreEqual("Element", UNSORTED_ELEMENTS, actual.Elements);

        for(int i=0;i<NumConjClasses;i++)
            for (int j=0;j< CONJUGACY_CLASSES[i].length;j++)
                assertArraysAreEqual("Subgroup-" + i + "-" + j, CONJUGACY_CLASSES[i][j], actual.ConjugacyClasses[i][j]);
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
                }
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
                }

        );
    }

    private void TestS3IsProcessedCorrectly(boolean sort, String[] elems, BitSet[] elemMasks, BitSet[] subgroupMasks) throws Exception
    {
        RawGroup rawGroup = RawGroup.FromJSON(new StringReader(JSON_STRING));
        Group actual = Group.FromRawGroup(rawGroup,sort);

        assertEquals("NumElements:", NumElem, actual.NumElements);
        assertEquals("NumSubgroups:", NumSubgroups, actual.NumSubgroups);
        assertEquals("NumConjugacyClasses:", NumConjClasses, actual.NumConjugacyClasses);

        assertArraysAreEqual("Element", elems, actual.ElementNames);
        assertArraysAreEqual("Subgroup-Names", SUBGROUP_NAMES, actual.SubgroupNames);
        assertArraysAreEqual("Element Masks", actual.ElementMasks, elemMasks);
        assertArraysAreEqual("SubgroupMasks", actual.SubgroupMasks, subgroupMasks);

        int[][] subgroupIntersections = new int[][]{
                new int[] {0, 0, 0, 0, 0, 0},
                new int[] {0, 1, 0, 0, 0, 1},
                new int[] {0, 0, 2, 0, 0, 2},
                new int[] {0, 0, 0, 3, 0, 3},
                new int[] {0, 0, 0, 0, 4, 4},
                new int[] {0, 1, 2, 3, 4, 5}
        };

        for (int i=0;i<NumSubgroups;i++)
        {
            int[] actualSubgroupIntersection = actual.SubgroupIntersections[i];
            for (int j=0;j<actualSubgroupIntersection.length;j++)
            {
                assertEquals("SubgroupIntersections-"+i+"-"+j, subgroupIntersections[i][j], actualSubgroupIntersection[j]);
            }
        }

        assertArraysAreEqual("Conjugacy Classes", actual.ConjugacyClasses,
                StringToBitSet("100000"),   // type:()
                StringToBitSet("011100"),   // type:(ab)
                StringToBitSet("000010"),   // type: (abc)
                StringToBitSet("000001"));  // type: G
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
        int actualIndex = Group.GenerateIntersection(i1, i2, subgroups);
        String msg = "Intersection of " + subgroups[i1] + " and " + subgroups[i2] +
                ": expected " + subgroups[expectedIndex] + " actual:" + subgroups[actualIndex];
        assertEquals(msg, expectedIndex, actualIndex);
    }


    private static <T> void assertArraysAreEqual(T arrayName, T[] expected, T... actual)
    {
        for (int i=0;i<expected.length;i++)
            assertEquals(arrayName+"-"+i, expected[i], actual[i]);
    }
}


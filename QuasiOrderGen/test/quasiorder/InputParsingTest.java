package quasiorder;

import org.junit.Test;

import java.io.StringReader;

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
    public void TestS3IsProcessedCorrectly() throws Exception
    {
        RawGroup rawGroup = RawGroup.FromJSON(new StringReader(JSON_STRING));
        Group actual = Group.FromRawGroup(rawGroup);

        assertEquals("NumElements:", NumElem, actual.NumElements);
        assertEquals("NumSubgroups:", NumSubgroups, actual.NumSubgroups);
        assertEquals("NumConjugacyClasses:", NumConjClasses, actual.NumConjugacyClasses);
        assertArraysAreEqual("Element", ELEMENTS, actual.ElementNames);
        assertArraysAreEqual("Subgroup-Names", SUBGROUP_NAMES, actual.SubgroupNames);

        assertArraysAreEqual("Element Masks", actual.ElementMasks,
                StringToBitSet("111111"),   // ()
                StringToBitSet("001001"),   // (12)
                StringToBitSet("000101"),   // (13)
                StringToBitSet("010001"),   // (23)
                StringToBitSet("000011"),   // (123)
                StringToBitSet("000011"));  // (132)

        assertArraysAreEqual("Conjugacy Classes", actual.ConjugacyClasses,
                StringToBitSet("100000"),   // type:()
                StringToBitSet("011100"),   // type:(ab)
                StringToBitSet("000010"),   // type: (abc)
                StringToBitSet("000001"));  // type: G
    }

    private static <T> void assertArraysAreEqual(T arrayName, T[] expected, T... actual)
    {
        for (int i=0;i<expected.length;i++)
            assertEquals(arrayName+"-"+i, expected[i], actual[i]);
    }
}


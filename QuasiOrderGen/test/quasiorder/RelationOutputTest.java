package quasiorder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RelationOutputTest extends QuasiOrderGenFixture
{
    private String[] ELEMENT_NAMES = new String[]{"()", "(1,2)"};
    private static final String OUTPUT_HEADER = "strict digraph {\nedge [ arrowhead=\"none\"; arrowtail=\"none\"]\n";
    private static final String OUTPUT_FOOTER = "\n}\n";
    private static final int MAX_ELEM = 4;
    private static final String[] COLORS = {"fillcolor=red", "fillcolor=blue", "fillcolor=green", "fillcolor=yellow"};

    @Test
    public void BuildRelationOfS2()
    {
        // S2: { (), (12) }
        int NumElem = 2;
        ELEMENT_NAMES = new String[] { "()", "(1,2)"};
        String elementHeader = "() [fillcolor=red]\n(1,2) [fillcolor=blue]\n";

        // relation: 10 11
        assertRelationOutputIs("1011", "()->()\n()->(1,2)\n(1,2)->(1,2)", elementHeader, NumElem);

        // relation: 11 11
        assertRelationOutputIs("1111", "()->()\n(1,2)->()\n()->(1,2)\n(1,2)->(1,2)", elementHeader, NumElem);
    }

    @Test
    public void BuildRelationOfZ4()
    {
        // Z4: { 0, 1, 2, 3 } (under addition)
        int NumElem = 4;
        ELEMENT_NAMES = new String[] {"0", "1", "2", "3"};
        String elementHeader = "0 [fillcolor=red]\n1 [fillcolor=blue]\n2 [fillcolor=green]\n3 [fillcolor=yellow]\n";

        // subgroup family: relation: all but 0<={1,2,3} ;
        assertRelationOutputIs("1000"+"1111"+"1111"+"1111",
                "0->0\n0->1\n1->1\n2->1\n3->1\n0->2\n1->2\n2->2\n3->2\n0->3\n1->3\n2->3\n3->3", elementHeader, NumElem);

        // whole group: expected: complete relation.
        assertRelationOutputIs("1111"+"1111"+"1111"+"1111",
                "0->0\n1->0\n2->0\n3->0\n0->1\n1->1\n2->1\n3->1\n0->2\n1->2\n2->2\n3->2\n0->3\n1->3\n2->3\n3->3", elementHeader, NumElem);

        // subgroup family: { {0, 2} } : expected: (0,2) <-- (1,3)
        assertRelationOutputIs("1010"+"1111"+"1010"+"1111",
                "0->0\n2->0\n0->1\n1->1\n2->1\n3->1\n0->2\n2->2\n0->3\n1->3\n2->3\n3->3", elementHeader, NumElem);

        // subgroup family: { {0}, {0,2} } : expected: (0) <-- (2) <-- (1,3)
        assertRelationOutputIs("1000"+"1111"+"1010"+"1111",
                "0->0\n0->1\n1->1\n2->1\n3->1\n0->2\n2->2\n0->3\n1->3\n2->3\n3->3", elementHeader, NumElem);
    }

    private void assertRelationOutputIs(String relation, String expected, String elementHeader, int NumElem)
    {
        assertEquals("Relation: " + relation, OUTPUT_HEADER + elementHeader + expected + OUTPUT_FOOTER,
            RelationFormat.PrintRelationEdges(StringToBitSet(relation), ELEMENT_NAMES, COLORS, NumElem));
    }
}

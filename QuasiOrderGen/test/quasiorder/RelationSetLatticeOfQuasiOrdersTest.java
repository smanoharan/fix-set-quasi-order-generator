package quasiorder;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;
public class RelationSetLatticeOfQuasiOrdersTest extends QuasiOrderGenFixture
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
        // possible relations: 1011 (x2) and 1111
        relations.Add(StringToBitSet("1011"), StringToBitSet("10"), Colour);
        relations.Add(StringToBitSet("1111"), StringToBitSet("01"), Colour);
        relations.Add(StringToBitSet("1011"), StringToBitSet("11"), Colour);
        relations.SortRelations();
        assertEquals(StringToBitSet("1011"), relations.GenerateOverallQuasiOrder());
    }

    @Test
    public void BuildRelationOfS3()
    {
        // possible relations: (family masks are not important here)
        // 0: 100 000 111 111 111 111 111 111 111 111 111 111 [ 31 ]
        // 1: 100 000 111 111 111 111 111 111 100 011 100 011 [ 25 ]
        // 2: 100 000 110 000 101 000 100 100 111 111 111 111 [ 19 ]
        // 3: 100 000 110 000 101 000 100 100 100 011 100 011 [ 13 ]

        // add these relations, in order in which they would be generated by the program (which is 2, 3, 0, 1)
        relations.Add(StringToBitSet("100"+"000"+"110"+"000"+"101"+"000"+"100"+"100"+"111"+"111"+"111"+"111"), StringToBitSet("10"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"110"+"000"+"101"+"000"+"100"+"100"+"100"+"011"+"100"+"011"), StringToBitSet("11"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"), StringToBitSet("00"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"111"+"111"+"111"+"111"+"111"+"111"+"100"+"011"+"100"+"011"), StringToBitSet("01"), Colour);
        relations.SortRelations();

        // expected overall relation:
        assertEquals(StringToBitSet("1000"+"1100"+"1010"+"1111"), relations.GenerateOverallQuasiOrder());
    }

    @Test
    public void BuildRelationOfDi6()
    {
        // possible relations: (family masks are not important here)
        // add these relations, in order in which they would be generated by the program (which is 3, 2, 1, 0)
        relations.Add(StringToBitSet("100"+"000"+"111"+"000"+"111"+"000"+"100"+"100"+"100"+"010"+"100"+"001"), StringToBitSet("11"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"111"+"111"+"111"+"111"+"100"+"100"+"100"+"010"+"100"+"001"), StringToBitSet("10"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"111"+"000"+"111"+"000"+"111"+"111"+"111"+"111"+"111"+"111"), StringToBitSet("01"), Colour);
        relations.Add(StringToBitSet("100"+"000"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"+"111"), StringToBitSet("00"), Colour);
        relations.SortRelations();

        // expected overall relation:
        assertEquals(StringToBitSet("1000"+"1100"+"1010"+"1111"), relations.GenerateOverallQuasiOrder());
    }

    @Test
    public void BuildRelationWhereCardinalityOfTwoRelationsAreEqual()
    {
        // relations here do not correspond to an acutal group: order of addition : 3, 1, 0, 2, 4
        relations.Add(StringToBitSet("1100"), StringToBitSet("011"), Colour);
        relations.Add(StringToBitSet("1110"), StringToBitSet("001"), Colour);
        relations.Add(StringToBitSet("1111"), StringToBitSet("000"), Colour);
        relations.Add(StringToBitSet("1101"), StringToBitSet("010"), Colour);
        relations.Add(StringToBitSet("1000"), StringToBitSet("100"), Colour);
        relations.SortRelations();

        // expected overall relation:
        assertEquals(StringToBitSet("10000"+"11000"+"10100"+"11110"+"11111"), relations.GenerateOverallQuasiOrder());
    }
}

package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;
import static quasiorder.QuasiOrderGenFixture.StringToBitSet;

@SuppressWarnings({"unchecked"})
public class N5SuperSetTestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // N5 Super-set-lattice: (n5 is 1,3,5,6,4 )
        title = "N5 Super set";
        //
        //             0
        //            / \
        //           /   \
        //          1     2
        //         / \   /
        //        3   \ /
        //        |    4
        //        5   / \
        //         \ /   \
        //          6     7
        //           \   /
        //            \ /
        //             8
        latOrder = 9;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible:
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0);
        joinReducible.set(1);
        joinReducible.set(4);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(4);
        meetReducible.set(6);
        meetReducible.set(8);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, JoinRed),
            toNodeAttrString(2, NotRed),
            toNodeAttrString(3, NotRed),
            toNodeAttrString(4, BothRed),
            toNodeAttrString(5, NotRed),
            toNodeAttrString(6, MeetRed),
            toNodeAttrString(7, NotRed),
            toNodeAttrString(8, MeetRed),
        };
        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0,   0, 0, 0,   0, 0, 0 }, // 0
            new int[] { 0, 1, 0,   1, 1, 1,   1, 1, 1 }, // 1
            new int[] { 0, 0, 2,   0, 2, 0,   2, 2, 2 }, // 2

            new int[] { 0, 1, 0,   3, 1, 3,   3, 1, 3 }, // 3
            new int[] { 0, 1, 2,   1, 4, 1,   4, 4, 4 }, // 4
            new int[] { 0, 1, 0,   3, 1, 5,   5, 1, 5 }, // 5

            new int[] { 0, 1, 2,   3, 4, 5,   6, 4, 6 }, // 6
            new int[] { 0, 1, 2,   1, 4, 1,   4, 7, 7 }, // 7
            new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 8
        };

        // expected meets:
        expectedMeet = new int[][]
        {
           new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 0
           new int[] { 1, 1, 4,   3, 4, 5,   6, 7, 8 }, // 1
           new int[] { 2, 4, 2,   6, 4, 6,   6, 7, 8 }, // 2

           new int[] { 3, 3, 6,   3, 6, 5,   6, 8, 8 }, // 3
           new int[] { 4, 4, 4,   6, 4, 6,   6, 7, 8 }, // 4
           new int[] { 5, 5, 6,   5, 6, 5,   6, 8, 8 }, // 5

           new int[] { 6, 6, 6,   6, 6, 6,   6, 8, 8 }, // 6
           new int[] { 7, 7, 7,   8, 7, 8,   8, 7, 8 }, // 7
           new int[] { 8, 8, 8,   8, 8, 8,   8, 8, 8 }, // 8
       };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // all except 2 <= 1;
        for (int i=3;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,1,latOrder));

        // 6 <= 2-5;
        for (int i=2;i<6;i++)
            lattice.set(ToSerialIndex(6,i,latOrder));

        // 4 <= 2 ; 5 <= 3 ; 7 <= 1,2,4
        lattice.set(ToSerialIndex(4,2,latOrder));
        lattice.set(ToSerialIndex(5,3,latOrder));
        lattice.set(ToSerialIndex(7,1,latOrder));
        lattice.set(ToSerialIndex(7,2,latOrder));
        lattice.set(ToSerialIndex(7,4,latOrder));

        // 8 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(8,i,latOrder));

        isModular = false;
        NonModularAElem = 2; // first N5 found will 0,2,
        NonModularBElem = 3;
        NonModularXElem = 5;
        NonModularAXJoinElem = 0;
        NonModularABMeetElem = 6;

        isDistributive = false;
        NonDistXElem = 5;
        NonDistYElem = 2;
        NonDistZElem = 3;
        NonDistYZMeetElem = 6;
        NonDistXYJoinElem = 0;
        NonDistXZJoinElem = 3;
        modDistMessage = "Modular: false\tDistributive: false" +
            String.format("%1$-50s","\t\tNot-modular: {5, 2, 3, 0, 6}") +
            String.format("%1$-50s","\t\tNot-distributive: {5, 2, 3, 0, 3, 6}");

        FilteringRelations = new ArrayList<FixOrder>();
        FilteringRelations.add(new FixOrder(new BitSet(), false, true)); // 0
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 1
        FilteringRelations.add(new FixOrder(new BitSet(), false, true)); // 2
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 3
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 4
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 5
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 6
        FilteringRelations.add(new FixOrder(new BitSet(), true, false)); // 7
        FilteringRelations.add(new FixOrder(new BitSet(), true, false)); // 8

        subgraphs = ToList(ToList(0, 4, 8), ToList(1, 2), ToList(3, 5), ToList(6, 7));

        FilteredFaithfulNormalLatOrder = 5;
        FilteredFaithfulNormalNames = new String[] { "1", "3", "4", "5", "6" };
        FilteredFaithfulNormalRelation = StringToBitSet("10000"+"11000"+"10100"+"11010"+"11111");
        FilteredFaithfulNormalSubGraphs = ToList(ToList(2, 0, 4), ToList(1, 3));

        FilteredFaithfulLatOrder = 7;
        FilteredFaithfulNames = new String[] {"1", "3", "4", "5", "6", "7", "8"};
        FilteredFaithfulRelation = StringToBitSet("1000000"+"1100000"+"1010000"+"1101000"+"1111100"+"1010010"+"1111111");
        FilteredFaithfulSubGraphs = ToList(ToList(2, 6, 0), ToList(1, 3), ToList(4, 5));

        FilteredNormalLatOrder = 7;
        FilteredNormalNames = new String[] {"0", "1", "2", "3", "4", "5", "6"};
        FilteredNormalRelation = StringToBitSet("1000000"+"1100000"+"1010000"+"1101000"+"1110100"+"1101010"+"1111111");
        FilteredNormalSubGraphs = ToList(ToList(0, 4, 6), ToList(1, 2), ToList(3, 5));
        //
        //             0
        //            / \
        //           /   \      // top figure-8 is normal
        //          1     2
        //         / \   /
        //        3   \ /       // n5 is normal-faithful
        //        |    4
        //        5   / \       // bottom figure-8 is faithful
        //         \ /   \
        //          6     7
        //           \   /
        //            \ /
        //             8

    }

}

package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;
import static quasiorder.QuasiOrderGenFixture.StringToBitSet;

public class M3SuperSetTestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // M3 super set
        title = "M3 Super set";
        //
        //           0
        //          / \
        //         1   2
        //         \ / | \
        //          3  4  5
        //           \ | /
        //             6
        latOrder = 7;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible:
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0);
        joinReducible.set(2);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(3);
        meetReducible.set(6);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, NotRed),
            toNodeAttrString(2, JoinRed),
            toNodeAttrString(3, MeetRed),
            toNodeAttrString(4, NotRed),
            toNodeAttrString(5, NotRed),
            toNodeAttrString(6, MeetRed)
        };
        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0,   0, 0, 0,   0 }, // 0
            new int[] { 0, 1, 0,   1, 0, 0,   1 }, // 1
            new int[] { 0, 0, 2,   2, 2, 2,   2 }, // 2

            new int[] { 0, 1, 2,   3, 2, 2,   3 }, // 3
            new int[] { 0, 0, 2,   2, 4, 2,   4 }, // 4
            new int[] { 0, 0, 2,   2, 2, 5,   5 }, // 5

            new int[] { 0, 1, 2,   3, 4, 5,   6 }, // 6
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] { 0, 1, 2,   3, 4, 5,   6 }, // 0
            new int[] { 1, 1, 3,   3, 6, 6,   6 }, // 1
            new int[] { 2, 3, 2,   3, 4, 5,   6 }, // 2

            new int[] { 3, 3, 3,   3, 6, 6,   6 }, // 3
            new int[] { 4, 6, 4,   6, 4, 6,   6 }, // 4
            new int[] { 5, 6, 5,   6, 6, 5,   6 }, // 5

            new int[] { 6, 6, 6,   6, 6, 6,   6 }, // 6

        };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // all but 1 <= 2
        for (int i=3;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,2,latOrder));

        // 3 <= 1 ;
        lattice.set(ToSerialIndex(3,1,latOrder));

        // 6 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(6,i,latOrder));

        isModular = true;
        NonModularAElem = -1;
        NonModularBElem = -1;
        NonModularXElem = -1;
        NonModularAXJoinElem = -1;
        NonModularABMeetElem = -1;

        isDistributive = false;
        NonDistXElem = 1;
        NonDistYElem = 4;
        NonDistZElem = 5;
        NonDistYZMeetElem = 6;
        NonDistXYJoinElem = 0;
        NonDistXZJoinElem = 0;
        modDistMessage = "Modular: true\tDistributive: false" +
                String.format("%1$-50s","\t\tNot-distributive: {1, 4, 5, 0, 0, 6}");

        // m3 is faithful normal, rest is neither.
        FilteringRelations = new ArrayList<FixOrder>();
        for (int i=0;i<2;i++)
            FilteringRelations.add(new FixOrder(new BitSet(), false, false));

        for (int i=2;i<latOrder;i++)
            FilteringRelations.add(new FixOrder(new BitSet(), true, true));

        FilteredFaithfulNormalLatOrder = 5;
        FilteredFaithfulNormalNames = new String[] { "2", "3", "4", "5", "6" };
        FilteredFaithfulNormalRelation = StringToBitSet("10000"+"11000"+"10100"+"10010"+"11111");

        FilteredFaithfulLatOrder = FilteredFaithfulNormalLatOrder;
        FilteredFaithfulNames = FilteredFaithfulNormalNames;
        FilteredFaithfulRelation = FilteredFaithfulNormalRelation;

        FilteredNormalLatOrder = FilteredFaithfulNormalLatOrder;
        FilteredNormalNames = FilteredFaithfulNormalNames;
        FilteredNormalRelation = FilteredFaithfulNormalRelation;
    }
}

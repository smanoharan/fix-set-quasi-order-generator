package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;
import static quasiorder.QuasiOrderGenFixture.StringToBitSet;

public class M3TestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // M3
        title = "M3";
        //
        //             0
        //           / | \
        //          1  2  3
        //           \ | /
        //             4
        latOrder = 5;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible:
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(4);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, NotRed),
            toNodeAttrString(2, NotRed),
            toNodeAttrString(3, NotRed),
            toNodeAttrString(4, MeetRed),
        };
        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0,   0, 0, 0,   0 }, // 0
            new int[] { 0,   1, 0, 0,   1 }, // 1
            new int[] { 0,   0, 2, 0,   2 }, // 2
            new int[] { 0,   0, 0, 3,   3 }, // 3
            new int[] { 0,   1, 2, 3,   4 }, // 4
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] { 0,   1, 2, 3,   4 }, // 0
            new int[] { 1,   1, 4, 4,   4 }, // 1
            new int[] { 2,   4, 2, 4,   4 }, // 2
            new int[] { 3,   4, 4, 3,   4 }, // 3
            new int[] { 4,   4, 4, 4,   4 }, // 4
        };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // 8 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(4,i,latOrder));

        isModular = true;
        NonModularAElem = -1;
        NonModularBElem = -1;
        NonModularXElem = -1;
        NonModularAXJoinElem = -1;
        NonModularABMeetElem = -1;

        isDistributive = false;
        NonDistXElem = 1;
        NonDistYElem = 2;
        NonDistZElem = 3;
        NonDistYZMeetElem = 4;
        NonDistXYJoinElem = 0;
        NonDistXZJoinElem = 0;
        modDistMessage = "Modular: true\tDistributive: false" +
            String.format("%1$-50s","\t\tNot-distributive: {1, 2, 3, 0, 0, 4}");

        // all are faithful, only 2 is normal
        FilteringRelations = new ArrayList<FixOrder>();
        for (int i=0;i<2;i++) FilteringRelations.add(new FixOrder(new BitSet(), true, false));
        FilteringRelations.add(new FixOrder(new BitSet(), true, true));
        for (int i=3;i<latOrder;i++) FilteringRelations.add(new FixOrder(new BitSet(), true, false));

        subgraphs = ToList(ToList(0, 4), ToList(1, 2, 3));

        FilteredFaithfulNormalLatOrder = 1;
        FilteredFaithfulNormalNames = new String[] { "2" };
        FilteredFaithfulNormalRelation = StringToBitSet("1");
        FilteredFaithfulNormalSubGraphs = ToList(ToList(0));

        FilteredFaithfulLatOrder = latOrder;
        FilteredFaithfulNames = new String[] {"0", "1", "2", "3", "4"};
        FilteredFaithfulRelation = lattice;
        FilteredFaithfulSubGraphs = subgraphs;

        FilteredNormalLatOrder = FilteredFaithfulNormalLatOrder;
        FilteredNormalNames = FilteredFaithfulNormalNames;
        FilteredNormalRelation = FilteredFaithfulNormalRelation;
        FilteredNormalSubGraphs = FilteredFaithfulNormalSubGraphs;
    }
}

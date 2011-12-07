package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;
import static quasiorder.QuasiOrderGenFixture.StringToBitSet;

public class Dihedral4FaithfulOnlyTestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // Dihedral 4 Faithful:
        title = "Dih 4 Faithful";
        //
        //        0
        //      / | \
        //     1  2  3
        //     | X X |
        //     4  5  6
        //      \ | /
        //        7
        //
        latOrder = 8;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible (and thus the node attr):
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0, 4);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(4,8);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, JoinRed),
            toNodeAttrString(2, JoinRed),
            toNodeAttrString(3, JoinRed),
            toNodeAttrString(4, MeetRed),
            toNodeAttrString(5, MeetRed),
            toNodeAttrString(6, MeetRed),
            toNodeAttrString(7, MeetRed)
        };

        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0, 0,     0, 0, 0, 0 }, // 0
            new int[] { 0, 1, 0, 0,     1, 1, 0, 1 }, // 1
            new int[] { 0, 0, 2, 0,     2, 0, 2, 2 }, // 2
            new int[] { 0, 0, 0, 3,     0, 3, 3, 3 }, // 3

            new int[] { 0, 1, 2, 0,     4, 1, 2, 4 }, // 4
            new int[] { 0, 1, 0, 3,     1, 5, 3, 5 }, // 5
            new int[] { 0, 0, 2, 3,     2, 3, 6, 6 }, // 6
            new int[] { 0, 1, 2, 3,     4, 5, 6, 7 }, // 7
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] { 0, 1, 2, 3,     4, 5, 6, 7 }, // 0
            new int[] { 1, 1, 4, 5,     4, 5, 7, 7 }, // 1
            new int[] { 2, 4, 2, 6,     4, 7, 6, 7 }, // 2
            new int[] { 3, 5, 6, 3,     7, 5, 6, 7 }, // 3

            new int[] { 4, 4, 4, 7,     4, 7, 7, 7 }, // 4
            new int[] { 5, 5, 7, 5,     7, 5, 7, 7 }, // 5
            new int[] { 6, 7, 6, 6,     7, 7, 6, 7 }, // 6
            new int[] { 7, 7, 7, 7,     7, 7, 7, 7 }, // 7
        };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // 4 <= 1,2 ; 5 <= 1, 3; 6 <= 2,3;
        lattice.set(ToSerialIndex(4,1,latOrder));
        lattice.set(ToSerialIndex(4,2,latOrder));
        lattice.set(ToSerialIndex(5,1,latOrder));
        lattice.set(ToSerialIndex(5,3,latOrder));
        lattice.set(ToSerialIndex(6,2,latOrder));
        lattice.set(ToSerialIndex(6,3,latOrder));

        // 7 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(7,i,latOrder));

        isModular = true;
        NonModularAElem = -1;
        NonModularBElem = -1;
        NonModularXElem = -1;
        NonModularAXJoinElem = -1;
        NonModularABMeetElem = -1;

        isDistributive = true;
        NonDistXElem = -1;
        NonDistYElem = -1;
        NonDistZElem = -1;
        NonDistYZMeetElem = -1;
        NonDistXYJoinElem = -1;
        NonDistXZJoinElem = -1;
        modDistMessage = "Modular: true\tDistributive: true";

        // all relations are faithful and all are normal:
        subGraphs = ToList(ToList(0,1,2), ToList(3,4,5), ToList(6,7));
        FilteringRelations = new ArrayList<FixOrder>();
        for (int i=0;i<latOrder;i++)
            FilteringRelations.add(new FixOrder(new BitSet(), true, true));

        FilteredFaithfulLatOrder = latOrder;
        FilteredFaithfulNames = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
        FilteredFaithfulColours = new String[]{"c-0", "c-1", "c-2", "c-3", "c-4", "c-5", "c-6", "c-7"};
        FilteredFaithfulGroupedNames = groupedNames;
        FilteredFaithfulRelation = lattice;
        FilteredFaithfulSubGraphs = subGraphs;
        FilteredNormalLatOrder = latOrder;
        FilteredNormalNames = FilteredFaithfulNames;
        FilteredNormalColours = FilteredFaithfulColours;
        FilteredNormalGroupedNames = groupedNames;
        FilteredNormalRelation = lattice;
        FilteredNormalSubGraphs = subGraphs;
        FilteredFaithfulNormalLatOrder = latOrder;
        FilteredFaithfulNormalNames = FilteredFaithfulNames;
        FilteredFaithfulNormalColours = FilteredFaithfulColours;
        FilteredFaithfulNormalGroupedNames = groupedNames;
        FilteredFaithfulNormalRelation = lattice;
        FilteredFaithfulNormalSubGraphs = subGraphs;

        CollapsedLatOrder = 5;
        CollapsedRelation = StringToBitSet("10000"+"11000"+"10100"+"11110"+"11111");
        CollapsedGroupedNames =  new String[] {"0", "1", "2", "\"_3_4_5\"", "\"_6_7\"" };
        CollapsedRepNames = new String[]{ "0", "1", "2", "3", "6"};
        CollapsedColours = new String[]{ "c-0", "c-1", "c-2", "c-3", "c-6"};
        CollapsedSubGraphs = ToList(ToList(0,1,2,3,4));

        CollapsedFaithfulNormalLatOrder = CollapsedLatOrder;
        CollapsedFaithfulNormalRelation = CollapsedRelation;
        CollapsedFaithfulNormalGroupedNames = CollapsedGroupedNames;
        CollapsedFaithfulNormalRepNames = CollapsedRepNames;
        CollapsedFaithfulNormalColours = CollapsedColours;
        CollapsedFaithfulNormalSubGraphs = CollapsedSubGraphs;

        //
        //        0
        //      / | \
        //     1  2  3
        //     | X X |
        //     4  5  6
        //      \ | /
        //        7
        //
    }
}

package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;
import static quasiorder.QuasiOrderGenFixture.StringToBitSet;

@SuppressWarnings({"unchecked"})
public class Figure8TestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // Figure 8 Case:
        title = "Figure 8 Lattice";
        //
        //        0
        //       / \
        //      1   2
        //       \ /
        //        3
        //       / \
        //      4   5
        //       \ /
        //        6
        //
        latOrder = 7;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible:
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0);
        joinReducible.set(3);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(3);
        meetReducible.set(6);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, NotRed),
            toNodeAttrString(2, NotRed),
            toNodeAttrString(3, BothRed),
            toNodeAttrString(4, NotRed),
            toNodeAttrString(5, NotRed),
            toNodeAttrString(6, MeetRed)
        };

        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0,   0, 0,   0,   0, 0,   0 }, // 0

            new int[] { 0,   1, 0,   1,   1, 1,   1 }, // 1
            new int[] { 0,   0, 2,   2,   2, 2,   2 }, // 2

            new int[] { 0,   1, 2,   3,   3, 3,   3 }, // 3

            new int[] { 0,   1, 2,   3,   4, 3,   4 }, // 4
            new int[] { 0,   1, 2,   3,   3, 5,   5 }, // 5

            new int[] { 0,   1, 2,   3,   4, 5,   6 }, // 6
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] { 0,   1, 2,   3,   4, 5,   6 }, // 0

            new int[] { 1,   1, 3,   3,   4, 5,   6 }, // 1
            new int[] { 2,   3, 2,   3,   4, 5,   6 }, // 2

            new int[] { 3,   3, 3,   3,   4, 5,   6 }, // 3

            new int[] { 4,   4, 4,   4,   4, 6,   6 }, // 4
            new int[] { 5,   5, 5,   5,   6, 5,   6 }, // 5

            new int[] { 6,   6, 6,   6,   6, 6,   6 }, // 6
        };


        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // 3 <= 1,2 ; 4,5 <= 1,2,3;
        lattice.set(ToSerialIndex(3,1,latOrder));
        lattice.set(ToSerialIndex(3,2,latOrder));
        lattice.set(ToSerialIndex(4,1,latOrder));
        lattice.set(ToSerialIndex(4,2,latOrder));
        lattice.set(ToSerialIndex(4,3,latOrder));
        lattice.set(ToSerialIndex(5,1,latOrder));
        lattice.set(ToSerialIndex(5,2,latOrder));
        lattice.set(ToSerialIndex(5,3,latOrder));

        // 6 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(6,i,latOrder));

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

        // all normal, but only the top 4 are faithful.
        FilteringRelations = new ArrayList<FixOrder>();
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 0
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 1
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 2
        FilteringRelations.add(new FixOrder(new BitSet(), true, true)); // 3
        FilteringRelations.add(new FixOrder(new BitSet(), false, true)); // 4
        FilteringRelations.add(new FixOrder(new BitSet(), false, true)); // 5
        FilteringRelations.add(new FixOrder(new BitSet(), false, true)); // 6

        subGraphs = ToList(ToList(0, 3, 6), ToList(1, 2), ToList(4, 5));
        groupedNames = new String[] {"0", "\"_1_2\"", "\"_1_2\"", "3", "\"_4_5\"", "\"_4_5\"", "6" };
        FilteredFaithfulLatOrder = 4;
        FilteredFaithfulNames = new String[]{"0", "1", "2", "3"};
        FilteredFaithfulColours = new String[]{"c-0", "c-1", "c-2", "c-3"};
        FilteredFaithfulGroupedNames = new String[]{"0", "\"_1_2\"", "\"_1_2\"", "3"};
        FilteredFaithfulRelation = StringToBitSet("1000"+"1100"+"1010"+"1111");
        FilteredFaithfulSubGraphs = ToList(ToList(0, 3), ToList(1, 2));

        FilteredNormalLatOrder = 7;
        FilteredNormalNames = new String[]{"0", "1", "2", "3", "4", "5", "6"};
        FilteredNormalColours = new String[]{"c-0", "c-1", "c-2", "c-3", "c-4", "c-5", "c-6"};
        FilteredNormalGroupedNames = groupedNames;
        FilteredNormalRelation = lattice;
        FilteredNormalSubGraphs = subGraphs;

        FilteredFaithfulNormalLatOrder = FilteredFaithfulLatOrder;
        FilteredFaithfulNormalNames = FilteredFaithfulNames;
        FilteredFaithfulNormalColours = FilteredFaithfulColours;
        FilteredFaithfulNormalGroupedNames = FilteredFaithfulGroupedNames;
        FilteredFaithfulNormalRelation = FilteredFaithfulRelation;
        FilteredFaithfulNormalSubGraphs = FilteredFaithfulSubGraphs;

        CollapsedLatOrder = 5;
        CollapsedRelation = StringToBitSet("10000"+"11000"+"11100"+"11110"+"11111");
        CollapsedGroupedNames = new String[] {"0", "\"_1_2\"", "3", "\"_4_5\"", "6" };
        CollapsedRepNames = new String[]{ "0", "1", "3", "4", "6"};
        CollapsedColours = new String[]{ "c-0", "c-1", "c-3", "c-4", "c-6"};
        CollapsedSubGraphs = ToList(ToList(0,1,2,3,4));

        CollapsedFaithfulNormalLatOrder = 3;
        CollapsedFaithfulNormalRelation = StringToBitSet("100110111");
        CollapsedFaithfulNormalGroupedNames = new String[] {"0", "\"_1_2\"", "3" };
        CollapsedFaithfulNormalRepNames = new String[] {"0", "1", "3"};
        CollapsedFaithfulNormalColours = new String[] {"c-0", "c-1", "c-3"};
        CollapsedFaithfulNormalSubGraphs = ToList(ToList(0,1,2));

        isCollapsedALattice = true;
        wholeNotLatI = -1;
        wholeNotLatJ = -1;
        wholeNotLatK = -1;
        wholeNotLatM = -1;
        wholeIsLatMessage = "Lattice: true";

        faithfulNormalIsLatMessage = "Lattice: true";
        isFaithfulNormalCollapsedALattice = true;
        faithfulNormalNotLatI = -1;
        faithfulNormalNotLatJ = -1;
        faithfulNormalNotLatK = -1;
        faithfulNormalNotLatM = -1;

        //
        //        0
        //       / \
        //      1   2
        //       \ /
        //        3
        //       / \
        //      4   5
        //       \ /
        //        6
        //
    }
}

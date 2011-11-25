package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

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
    }
}

package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class Dihedral6TestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // Dihedral 6:
        title = "Dih 6";
        //
        //       0
        //      / \
        //     2   1
        //     |  /|
        //     | / |
        //     |/  |
        //     3   4
        //      \ /
        //       5
        //
        latOrder = 6;
        lattice = new BitSet(latOrder*latOrder);

        // join & meet reducible:
        joinReducible = new BitSet(latOrder);
        joinReducible.set(0);
        joinReducible.set(1);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(3);
        meetReducible.set(5);

        nodeAttr = new String[]
        {
            toNodeAttrString(0, JoinRed),
            toNodeAttrString(1, JoinRed),
            toNodeAttrString(2, NotRed),
            toNodeAttrString(3, MeetRed),
            toNodeAttrString(4, NotRed),
            toNodeAttrString(5, MeetRed)
        };

        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0, 0, 0, 0 }, // 0
            new int[] { 0, 1, 0, 1, 1, 1 }, // 1
            new int[] { 0, 0, 2, 2, 0, 2 }, // 2
            new int[] { 0, 1, 2, 3, 1, 3 }, // 3
            new int[] { 0, 1, 0, 1, 4, 4 }, // 4
            new int[] { 0, 1, 2, 3, 4, 5 }  // 5
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] {0, 1, 2, 3, 4, 5 }, // 0
            new int[] {1, 1, 3, 3, 4, 5 }, // 1
            new int[] {2, 3, 2, 3, 5, 5 }, // 2
            new int[] {3, 3, 3, 3, 5, 5 }, // 3
            new int[] {4, 4, 5, 5, 4, 5 }, // 4
            new int[] {5, 5, 5, 5, 5, 5 }  // 5
        };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // 1<=0 ; 2 <= 0
        lattice.set(ToSerialIndex(1,0,latOrder));
        lattice.set(ToSerialIndex(2,0,latOrder));

        // 3 <= 0, 1, 2
        for (int i=0;i<3;i++)
            lattice.set(ToSerialIndex(3,i,latOrder));

        // 4 <= 0, 1
        lattice.set(ToSerialIndex(4, 0, latOrder));
        lattice.set(ToSerialIndex(4, 1, latOrder));

        // 5 <= all
        for (int i=0;i<5;i++)
            lattice.set(ToSerialIndex(5,i,latOrder));

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

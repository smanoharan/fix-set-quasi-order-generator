package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

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
        expectedModDistMessage = "Modular: true\tDistributive: true";
    }
}

package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class GridTestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // Grid
        title = "Grid";
        //
        //             0
        //            / \
        //           /   \
        //          1     2
        //         / \   / \
        //        /   \ /   \
        //       3     4     5
        //        \   / \   /
        //         \ /   \ /
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
        joinReducible.set(2);
        joinReducible.set(4);

        meetReducible = new BitSet(latOrder);
        meetReducible.set(4);
        meetReducible.set(6);
        meetReducible.set(7);
        meetReducible.set(8);



        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0,   0, 0, 0,   0, 0, 0 }, // 0
            new int[] { 0, 1, 0,   1, 1, 0,   1, 1, 1 }, // 1
            new int[] { 0, 0, 2,   0, 2, 2,   2, 2, 2 }, // 2

            new int[] { 0, 1, 0,   3, 1, 0,   3, 1, 3 }, // 3
            new int[] { 0, 1, 2,   1, 4, 2,   4, 4, 4 }, // 4
            new int[] { 0, 0, 2,   0, 2, 5,   2, 5, 5 }, // 5

            new int[] { 0, 1, 2,   3, 4, 2,   6, 4, 6 }, // 6
            new int[] { 0, 1, 2,   1, 4, 5,   4, 7, 7 }, // 7
            new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 8
        };

        // expected meets:
        expectedMeet = new int[][]
        {
           new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 0
           new int[] { 1, 1, 4,   3, 4, 7,   6, 7, 8 }, // 1
           new int[] { 2, 4, 2,   6, 4, 5,   6, 7, 8 }, // 2

           new int[] { 3, 3, 6,   3, 6, 8,   6, 8, 8 }, // 3
           new int[] { 4, 4, 4,   6, 4, 7,   6, 7, 8 }, // 4
           new int[] { 5, 7, 5,   8, 7, 5,   8, 7, 8 }, // 5

           new int[] { 6, 6, 6,   6, 6, 8,   6, 8, 8 }, // 6
           new int[] { 7, 7, 7,   8, 7, 7,   8, 7, 8 }, // 7
           new int[] { 8, 8, 8,   8, 8, 8,   8, 8, 8 }, // 8
       };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // 3 <= 1 ; 4 <= 1, 2 ; 5 <= 2
        lattice.set(ToSerialIndex(3,1,latOrder));
        lattice.set(ToSerialIndex(4,1,latOrder));
        lattice.set(ToSerialIndex(4,2,latOrder));
        lattice.set(ToSerialIndex(5,2,latOrder));

        // 6, 7 <= 1-5 ; but not 6 <= 5 and not 7 <= 3
        for (int i=1;i<6;i++)
        {
            lattice.set(ToSerialIndex(6,i,latOrder));
            lattice.set(ToSerialIndex(7,i,latOrder));
        }
        lattice.clear(ToSerialIndex(6,5,latOrder));
        lattice.clear(ToSerialIndex(7,3,latOrder));

        // 4 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(8,i,latOrder));

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

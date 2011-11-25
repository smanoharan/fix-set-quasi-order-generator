package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

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
        expectedModDistMessage = "Modular: true\tDistributive: false" +
            String.format("%1$-50s","\t\tNot-distributive: {1, 2, 3, 0, 0, 4}");
    }

}

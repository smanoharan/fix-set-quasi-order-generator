package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class N5TestCase extends LatticeTestCase
{
    public void SetupTestCase()
    {
        // N5:
        title = "N5";
        //          0
        //         / \
        //        1   \
        //        |    2
        //        3   /
        //         \ /
        //          4
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
            toNodeAttrString(4, MeetRed)
        };
        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0, 0, 0 }, // 0
            new int[] { 0, 1, 0, 1, 1 }, // 1
            new int[] { 0, 0, 2, 0, 2 }, // 2
            new int[] { 0, 1, 0, 3, 3 }, // 3
            new int[] { 0, 1, 2, 3, 4 }, // 4
        };

        // expected meets:
        expectedMeet = new int[][]
        {
            new int[] { 0, 1, 2, 3, 4 }, // 0
            new int[] { 1, 1, 4, 3, 4 }, // 1
            new int[] { 2, 4, 2, 4, 4 }, // 2
            new int[] { 3, 3, 4, 3, 4 }, // 3
            new int[] { 4, 4, 4, 4, 4 }, // 4
        };


        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // 3 <= 1;
        lattice.set(ToSerialIndex(3,1,latOrder));

        // 4 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(4,i,latOrder));

        isModular = false;
        NonModularAElem = 2;
        NonModularBElem = 1;
        NonModularXElem = 3;
        NonModularAXJoinElem = 0;
        NonModularABMeetElem = 4;

        isDistributive = false;
        NonDistXElem = 3;
        NonDistYElem = 1;
        NonDistZElem = 2;
        NonDistYZMeetElem = 4;
        NonDistXYJoinElem = 1;
        NonDistXZJoinElem = 0;
        modDistMessage = "Modular: false\tDistributive: false" +
            String.format("%1$-50s","\t\tNot-modular: {3, 2, 1, 0, 4}") +
            String.format("%1$-50s","\t\tNot-distributive: {3, 1, 2, 1, 0, 4}");
    }
}

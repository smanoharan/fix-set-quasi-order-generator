package quasiorder.LatticeTest.Lattice;

import org.junit.runners.Parameterized;
import quasiorder.QuasiOrderGenFixture;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static quasiorder.FixOrderSet.ToSerialIndex;

public class LatticeFixture extends QuasiOrderGenFixture
{
    protected BitSet lattice;
    protected int latOrder;
    protected int[][] expectedMeet;
    protected int[][] expectedJoin;
    protected final String title;

    protected boolean isModular;
    protected int NonModularAElem;
    protected int NonModularBElem;
    protected int NonModularXElem;
    protected int NonModularAXJoinElem;
    protected int NonModularABMeetElem;

    protected boolean isDistributive;
    protected int NonDistXElem;
    protected int NonDistYElem;
    protected int NonDistZElem;
    protected int NonDistXYJoinElem;
    protected int NonDistXZJoinElem;
    protected int NonDistYZMeetElem;

    protected static final String[][] TestLattices = new String[][]
    {
            new String[]{"Dihedral-6"},
            new String[]{"Edge Case"},
            new String[]{"Dihedral-4 faithful"},
            new String[]{"N-5"},
            new String[]{"N-5 Superset"},
            new String[]{"Grid lattice"},
            new String[]{"M-5"},
            new String[]{"M-5 Superset"}

    };

    @Parameterized.Parameters
    public static Collection TestCases() { return Arrays.asList(TestLattices); }

    public LatticeFixture(String latticeTitle)
    {
        this.title = latticeTitle;
        if      (latticeTitle.equals(TestLattices[0][0])) SetupDih6();
        else if (latticeTitle.equals(TestLattices[1][0])) SetupEdgeCaseLattice();
        else if (latticeTitle.equals(TestLattices[2][0])) SetupDih4FaithfulOnly();
        else if (latticeTitle.equals(TestLattices[3][0])) SetupN5();
        else if (latticeTitle.equals(TestLattices[4][0])) SetupN5Super();
        else if (latticeTitle.equals(TestLattices[5][0])) SetupGridLattice();
        else if (latticeTitle.equals(TestLattices[6][0])) SetupM5();
        else if (latticeTitle.equals(TestLattices[7][0])) SetupM5Super();
        else fail("Unknown test case: " + latticeTitle);
    }

    protected static void assertTableEquals(String title, int[][] expected, int[][] actual, int latOrder)
    {
        for(int i=0;i<latOrder;i++)
            for(int j=0;j<latOrder;j++)
                assertEquals(String.format("%s[%d,%d]", title, i, j), expected[i][j], actual[i][j]);
    }


    protected void SetupDih6()
    {
        // Dihedral 6:
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
    }

    protected void SetupEdgeCaseLattice()
    {
        // Edge Case:
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
    }

    protected void SetupDih4FaithfulOnly()
    {
        // Dihedral 4 Faithful:
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
    }

    protected void SetupN5()
    {
        // N5:
        //
        //          0
        //         / \
        //        1   \
        //        |    2
        //        3   /
        //         \ /
        //          4
        latOrder = 5;
        lattice = new BitSet(latOrder*latOrder);

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
    }

    protected void SetupN5Super()
    {
        // N5 Super-set-lattice: (n5 is 1,3,5,6,4 )
        //
        //             0
        //            / \
        //           /   \
        //          1     2
        //         / \   /
        //        3   \ /
        //        |    4
        //        5   / \
        //         \ /   \
        //          6     7
        //           \   /
        //            \ /
        //             8
        latOrder = 9;
        lattice = new BitSet(latOrder*latOrder);

        // expected joins:
        expectedJoin = new int[][]
        {
            new int[] { 0, 0, 0,   0, 0, 0,   0, 0, 0 }, // 0
            new int[] { 0, 1, 0,   1, 1, 1,   1, 1, 1 }, // 1
            new int[] { 0, 0, 2,   0, 2, 0,   2, 2, 2 }, // 2

            new int[] { 0, 1, 0,   3, 1, 3,   3, 1, 3 }, // 3
            new int[] { 0, 1, 2,   1, 4, 1,   4, 4, 4 }, // 4
            new int[] { 0, 1, 0,   3, 1, 5,   5, 1, 5 }, // 5

            new int[] { 0, 1, 2,   3, 4, 5,   6, 4, 6 }, // 6
            new int[] { 0, 1, 2,   1, 4, 1,   4, 7, 7 }, // 7
            new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 8
        };

        // expected meets:
        expectedMeet = new int[][]
        {
           new int[] { 0, 1, 2,   3, 4, 5,   6, 7, 8 }, // 0
           new int[] { 1, 1, 4,   3, 4, 5,   6, 7, 8 }, // 1
           new int[] { 2, 4, 2,   6, 4, 6,   6, 7, 8 }, // 2

           new int[] { 3, 3, 6,   3, 6, 5,   6, 8, 8 }, // 3
           new int[] { 4, 4, 4,   6, 4, 6,   6, 7, 8 }, // 4
           new int[] { 5, 5, 6,   5, 6, 5,   6, 8, 8 }, // 5

           new int[] { 6, 6, 6,   6, 6, 6,   6, 8, 8 }, // 6
           new int[] { 7, 7, 7,   8, 7, 8,   8, 7, 8 }, // 7
           new int[] { 8, 8, 8,   8, 8, 8,   8, 8, 8 }, // 8
       };

        // i <= i for all i;
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,i,latOrder));

        // all <= 0
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,0,latOrder));

        // all except 2 <= 1;
        for (int i=3;i<latOrder;i++)
            lattice.set(ToSerialIndex(i,1,latOrder));

        // 6 <= 2-5;
        for (int i=2;i<6;i++)
            lattice.set(ToSerialIndex(6,i,latOrder));

        // 4 <= 2 ; 5 <= 3 ; 7 <= 1,2,4
        lattice.set(ToSerialIndex(4,2,latOrder));
        lattice.set(ToSerialIndex(5,3,latOrder));
        lattice.set(ToSerialIndex(7,1,latOrder));
        lattice.set(ToSerialIndex(7,2,latOrder));
        lattice.set(ToSerialIndex(7,4,latOrder));

        // 8 <= all
        for (int i=0;i<latOrder;i++)
            lattice.set(ToSerialIndex(8,i,latOrder));

        isModular = false;
        NonModularAElem = 2; // first N5 found will 0,2,
        NonModularBElem = 3;
        NonModularXElem = 5;
        NonModularAXJoinElem = 0;
        NonModularABMeetElem = 6;

        isDistributive = false;
        NonDistXElem = 5;
        NonDistYElem = 2;
        NonDistZElem = 3;
        NonDistYZMeetElem = 6;
        NonDistXYJoinElem = 0;
        NonDistXZJoinElem = 3;
    }

    protected void SetupGridLattice()
    {
        // Grid
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
    }

    protected void SetupM5()
    {
        // Grid
        //
        //             0
        //           / | \
        //          1  2  3
        //           \ | /
        //             4
        latOrder = 5;
        lattice = new BitSet(latOrder*latOrder);

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
    }
    
    protected void SetupM5Super()
    {
        // Grid
        //
        //          0
        //         /  \
        //        1    2
        //         \ / | \
        //          3  4  5
        //           \ | /
        //             6
        latOrder = 7;
        lattice = new BitSet(latOrder*latOrder);

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
    }
}

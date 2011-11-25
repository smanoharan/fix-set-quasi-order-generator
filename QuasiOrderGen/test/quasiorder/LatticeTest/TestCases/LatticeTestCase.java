package quasiorder.LatticeTest.TestCases;

import java.util.BitSet;

public abstract class LatticeTestCase
{
    public BitSet lattice;
    public int latOrder;
    public BitSet joinReducible;
    public BitSet meetReducible;
    public int[][] expectedMeet;
    public int[][] expectedJoin;
    public String title;
    public String[] names;
    public String[] colors;

    public boolean isModular;
    public int NonModularAElem;
    public int NonModularBElem;
    public int NonModularXElem;
    public int NonModularAXJoinElem;
    public int NonModularABMeetElem;

    public boolean isDistributive;
    public int NonDistXElem;
    public int NonDistYElem;
    public int NonDistZElem;
    public int NonDistXYJoinElem;
    public int NonDistXZJoinElem;
    public int NonDistYZMeetElem;

    public String expectedModDistMessage;

    public void SetupAll()
    {
        SetupTestCase();

        names = new String[latOrder];
        colors = new String[latOrder];
        for (int i=0;i<latOrder;i++)
        {
            names[i] = ""+i;
            colors[i] = "c-" + i; // arbitrary colour names
        }

    }

    public abstract void SetupTestCase();
}

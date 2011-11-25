package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.BitSet;

public abstract class LatticeTestCase
{
    protected static final String BothRed = "-";
    protected static final String NotRed  = "bold";
    protected static final String JoinRed = "dotted";
    protected static final String MeetRed = "dashed";

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

    public String modDistMessage;
    public String[] nodeAttr;


    public ArrayList<FixOrder> FilteringRelations;
    public int FilteredFaithfulLatOrder;
    public BitSet FilteredFaithfulRelation;
    public String[] FilteredFaithfulNames;

    public int FilteredNormalLatOrder;
    public BitSet FilteredNormalRelation;
    public String[] FilteredNormalNames;

    public int FilteredFaithfulNormalLatOrder;
    public BitSet FilteredFaithfulNormalRelation;
    public String[] FilteredFaithfulNormalNames;

    public void SetupAll()
    {
        SetupTestCase(); // must be done first, in order init latOrder.
        names = new String[latOrder];
        colors = new String[latOrder];
        for (int i=0;i<latOrder;i++)
        {
            names[i] = ""+i;
            colors[i] = "c-" + i; // arbitrary colour names
        }
    }

    protected abstract void SetupTestCase();

    protected String toNodeAttrString(int index, String secondStyle)
    {
        return secondStyle.equals(BothRed) ? String.format("fillcolor=\"c-%d\"", index) :
            String.format("fillcolor=\"c-%d\"; peripheries=2; style=\"filled,%s\"", index, secondStyle);
    }
}

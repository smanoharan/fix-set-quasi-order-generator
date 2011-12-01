package quasiorder.LatticeTest.TestCases;

import quasiorder.FixOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;

public abstract class LatticeTestCase
{
    protected static final String BothRed = "";
    protected static final String NotRed  = ",solid";
    protected static final String JoinRed = ",dotted";
    protected static final String MeetRed = ",dashed";

    public BitSet lattice;
    public int latOrder;
    public BitSet joinReducible;
    public BitSet meetReducible;
    public int[][] expectedMeet;
    public int[][] expectedJoin;
    public String title;
    public String[] names;
    public String[] colors;
    public LinkedList<ArrayList<Integer>> subgraphs;

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
    public LinkedList<ArrayList<Integer>> FilteredFaithfulSubGraphs;

    public int FilteredNormalLatOrder;
    public BitSet FilteredNormalRelation;
    public String[] FilteredNormalNames;
    public LinkedList<ArrayList<Integer>> FilteredNormalSubGraphs;

    public int FilteredFaithfulNormalLatOrder;
    public BitSet FilteredFaithfulNormalRelation;
    public String[] FilteredFaithfulNormalNames;
    public LinkedList<ArrayList<Integer>> FilteredFaithfulNormalSubGraphs;

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
        return String.format("fillcolor=\"c-%d\"; peripheries=%d; style=\"filled%s\"",
                index, (secondStyle.equals(BothRed) ? 1 : 2), secondStyle);
    }

    protected ArrayList<Integer> ToList(Integer ... parts)
    {
        return new ArrayList<Integer>(Arrays.asList(parts));
    }

    protected LinkedList<ArrayList<Integer>> ToList(ArrayList<Integer> ... parts)
    {
        return new LinkedList<ArrayList<Integer>>(Arrays.asList(parts));
    }
}

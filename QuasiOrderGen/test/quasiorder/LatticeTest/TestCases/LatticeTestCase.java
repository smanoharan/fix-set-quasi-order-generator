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
    public String[] groupedNames;
    public String[] colours;
    public LinkedList<ArrayList<Integer>> subGraphs;

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
    public String[] FilteredFaithfulColours;
    public String[] FilteredFaithfulGroupedNames;
    public LinkedList<ArrayList<Integer>> FilteredFaithfulSubGraphs;

    public int FilteredNormalLatOrder;
    public BitSet FilteredNormalRelation;
    public String[] FilteredNormalGroupedNames;
    public String[] FilteredNormalNames;
    public String[] FilteredNormalColours;
    public LinkedList<ArrayList<Integer>> FilteredNormalSubGraphs;

    public int FilteredFaithfulNormalLatOrder;
    public BitSet FilteredFaithfulNormalRelation;
    public String[] FilteredFaithfulNormalGroupedNames;
    public String[] FilteredFaithfulNormalColours;
    public String[] FilteredFaithfulNormalNames;
    public LinkedList<ArrayList<Integer>> FilteredFaithfulNormalSubGraphs;

    public int CollapsedLatOrder;
    public BitSet CollapsedRelation;
    public String[] CollapsedGroupedNames;
    public String[] CollapsedRepNames;
    public String[] CollapsedColours;
    public LinkedList<ArrayList<Integer>> CollapsedSubGraphs;

    public boolean isCollapsedALattice;
    public int wholeNotLatI;
    public int wholeNotLatJ;
    public int wholeNotLatK;
    public int wholeNotLatM;
    public String wholeIsLatMessage;

    public int CollapsedFaithfulNormalLatOrder;
    public BitSet CollapsedFaithfulNormalRelation;
    public String[] CollapsedFaithfulNormalGroupedNames;
    public String[] CollapsedFaithfulNormalRepNames;
    public String[] CollapsedFaithfulNormalColours;
    public LinkedList<ArrayList<Integer>> CollapsedFaithfulNormalSubGraphs;

    public boolean isFaithfulNormalCollapsedALattice;
    public int faithfulNormalNotLatI;
    public int faithfulNormalNotLatJ;
    public int faithfulNormalNotLatK;
    public int faithfulNormalNotLatM;
    public String faithfulNormalIsLatMessage;

    public void SetupAll()
    {
        SetupTestCase(); // must be done first, in order init latOrder.
        names = new String[latOrder];
        colours = new String[latOrder];

        for (int i=0;i<latOrder;i++)
        {
            names[i] = ""+i;
            colours[i] = "c-" + i; // arbitrary colour names
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

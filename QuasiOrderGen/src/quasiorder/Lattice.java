package quasiorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;

import static quasiorder.FixOrderSet.ToSerialIndex;

public class Lattice
{
    public final BitSet latBit;
    public final int latOrder;
    public final String[] names;
    public final String[] colours;
    public String[] nodeAttr;
    public final LinkedList<ArrayList<Integer>> subGraphs;

    public Lattice(BitSet lattice, int latOrder, String[] names, String[] colors, LinkedList<ArrayList<Integer>> subGraphs)
    {
        this.latBit = lattice;
        this.latOrder = latOrder;
        this.names = names;
        this.subGraphs = subGraphs;
        this.colours = colors;
        this.nodeAttr = ToColorAttributeStrings(colors, latOrder);
    }

    protected static String[] ToColorAttributeStrings(String[] colors, int latOrder)
    {
        String[] colAttr = new String[latOrder];
        for(int i=0;i<latOrder;i++)
            colAttr[i] = "style=filled; fillcolor=\"" + colors[i] + "\"";
        return colAttr;
    }

    public static interface INameSelector
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames);
    }

    public static INameSelector FullPartNameSelector = new INameSelector()
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames)
        {
            StringBuilder newnameSB = new StringBuilder("\"");

            for(Integer i : part) newnameSB.append("_").append(individualNames[i]);
            newnameSB.append('"');
            return newnameSB.toString();
        }
    };

    public static INameSelector RepNameSelector  = new INameSelector()
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames)
        {
            return part.isEmpty() ? "-" : individualNames[part.get(0)];
        }
    };

    private static class FoldedEntry implements Comparable<FoldedEntry>
    {
        public final String name;
        public final String colour;
        public final ArrayList<Integer> oldIndices;

        public FoldedEntry(String name, String colour, int oldIndex)
        {
            this(name, colour, SingletonList(oldIndex));
        }

        private static ArrayList<Integer> SingletonList(int i)
        {
            ArrayList<Integer> res = new ArrayList<Integer>();
            res.add(i);
            return res;
        }

        public FoldedEntry(String name, String colour, ArrayList<Integer> oldIndices)
        {
            this.name = name;
            this.colour = colour;
            this.oldIndices = oldIndices;
        }

        public int compareTo(FoldedEntry o)
        {
            return this.oldIndices.get(0) - o.oldIndices.get(0);
        }
    }

    /**
     * Collapse a lattice by its subgroup equivalence relation.
     * Name of each element is chosen as per the nameSelector.
     * The colour/nodeAttributes of a lattice are chosen to be an arbitrary representative's.
     *
     * @param lat The lattice to collapse
     * @param nameSelector A name selector
     * @return A new, collapsed lattice. (Original is unmodified).
     */
    public static Lattice CollapseBy(Lattice lat, INameSelector nameSelector)
    {
        // setup grouped names, from subgraph:
        int collapsedLatOrder = lat.subGraphs.size() - 1 + (lat.subGraphs.get(0).size());
        FoldedEntry[] foldedEntries = new FoldedEntry[collapsedLatOrder];

        boolean first = true;
        int cur = 0;
        for (ArrayList<Integer> part : lat.subGraphs)
        {
            if (first)
            {
                first = false;
                for (Integer i : part)
                    foldedEntries[cur++] = new FoldedEntry(lat.names[i], lat.colours[i], i);
            }
            else
            {
                String partName = nameSelector.ChooseName(part, lat.names);
                foldedEntries[cur++] = new FoldedEntry(partName, lat.colours[part.get(0)], part);
            }
        }

        String[] collapsedNames = new String[collapsedLatOrder];
        String[] collapsedColours = new String[collapsedLatOrder];
        int[] newIndex = new int[lat.latOrder];

        Arrays.sort(foldedEntries);
        for(int i=0;i<collapsedLatOrder;i++)
        {
            collapsedNames[i] = foldedEntries[i].name;
            collapsedColours[i] = foldedEntries[i].colour;
            for (Integer o : foldedEntries[i].oldIndices) newIndex[o] = i;
        }

        // now everything is a singleton
        LinkedList<ArrayList<Integer>> collapsedSubGraph = new LinkedList<ArrayList<Integer>>();
        ArrayList<Integer> all = new ArrayList<Integer>();
        for(int i=0;i<collapsedLatOrder;i++) all.add(i);
        collapsedSubGraph.add(all);

        // build the collapsed relation:
        BitSet collapsedRelation = new BitSet(collapsedLatOrder*collapsedLatOrder);
        for (int k = lat.latBit.nextSetBit(0); k >= 0; k = lat.latBit.nextSetBit(k+1))
        {
            int oldI = k / lat.latOrder;
            int oldJ = k % lat.latOrder;
            collapsedRelation.set(ToSerialIndex(newIndex[oldI], newIndex[oldJ], collapsedLatOrder));
        }

        return new Lattice(collapsedRelation, collapsedLatOrder, collapsedNames, collapsedColours, collapsedSubGraph);
    }

    /**
     * Filter the lattice of all fix-orders as per the include bitmap. The element is kept, if it's index in the bitmap is 1.
     *
     * @param latOrder The number of elements
     * @param names The name of each fix order (for output purposes).
     * @param colours The color of each fix order (for output purposes).
     * @param relation The bitset showing the lattice formed by all fix orders
     * @param subGraphs The set of all subGraphs of the lattice
     * @param include A bitmap to filter by.
     * @return The lattice of the fix orders satisfying the conditions.
     */
    public static Lattice FilterBy(int latOrder, String[] names, String[] colours, BitSet relation,
                                   LinkedList<ArrayList<Integer>> subGraphs, BitSet include)
    {
        int filteredLatOrder = include.cardinality();
        int[] oldIndex = new int[filteredLatOrder];
        int[] newIndex = new int[latOrder];
        String[] filteredNames = new String[filteredLatOrder];
        String[] filteredColours = new String[filteredLatOrder];
        BitSet filteredRelation = new BitSet(filteredLatOrder*filteredLatOrder);

        // create maps  new-index <=> old-index
        for (int old = include.nextSetBit(0), cur = 0; old >= 0; old = include.nextSetBit(old+1), cur++)
        {
            oldIndex[cur] = old;
            newIndex[old] = cur;
        }

        // copy over the selected names and attributes
        for(int i=0;i<filteredLatOrder;i++)
        {
            int oldI = oldIndex[i];
            filteredNames[i] = names[oldI];
            filteredColours[i] = colours[oldI];
        }

        // copy over the selected relation elements
        for(int i=0;i<filteredLatOrder;i++)
            for(int j=0;j<filteredLatOrder;j++)
                if (relation.get(ToSerialIndex(oldIndex[i], oldIndex[j], latOrder)))
                    filteredRelation.set(ToSerialIndex(i, j, filteredLatOrder));

        // setup filtered SubGraphs:
        LinkedList<ArrayList<Integer>> filteredSubGraphs = new LinkedList<ArrayList<Integer>>();
        ArrayList<Integer> singletons = new ArrayList<Integer>();
        filteredSubGraphs.add(singletons);

        boolean oldSingletonPartition = true;
        for(ArrayList<Integer> part : subGraphs)
        {
            ArrayList<Integer> inclPart = new ArrayList<Integer>();
            for(Integer oldI : part)
                if (include.get(oldI))
                    inclPart.add(newIndex[oldI]);

            if (oldSingletonPartition)
            {
                singletons.addAll(inclPart);
                oldSingletonPartition = false;
            }
            else if (inclPart.size()==1) singletons.add(inclPart.get(0));
            else if (inclPart.size()>1) filteredSubGraphs.add(inclPart);
        }

        return new Lattice(filteredRelation, filteredLatOrder, filteredNames, filteredColours, filteredSubGraphs);
    }

    public static BitSet includeBy(ArrayList<FixOrder> fixOrders, boolean faithfulOnly, boolean normalOnly)
    {
        int numFixOrders = fixOrders.size();
        BitSet include = new BitSet(numFixOrders);
        for(int i=0;i<numFixOrders;i++)
        {
            FixOrder f = fixOrders.get(i);
            if ((!faithfulOnly || f.isFaithful) && (!normalOnly || f.isNormal))
                include.set(i);
        }
        return include;
    }
}

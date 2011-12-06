package quasiorder;

import java.util.ArrayList;
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

    public static String[] ToColorAttributeStrings(String[] colors, int latOrder)
    {
        String[] colAttr = new String[latOrder];
        for(int i=0;i<latOrder;i++)
            colAttr[i] = "style=filled; fillcolor=\"" + colors[i] + "\"";
        return colAttr;
    }

    static interface INameSelector
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames);
    }

    static class FullPartNameSelector implements INameSelector
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames)
        {
            StringBuilder newnameSB = new StringBuilder("\"");

            for(Integer i : part) newnameSB.append("_").append(individualNames[i]);
            newnameSB.append('"');
            return newnameSB.toString();
        }
    }

    static class RepNameSelector implements INameSelector
    {
        public String ChooseName(ArrayList<Integer> part, String[] individualNames)
        {
            return part.isEmpty() ? "-" : individualNames[part.get(0)];
        }
    }

    // Fold by subGraphs, resulting in one element per subgraph
    public static Lattice CollapseBy(Lattice lat, LinkedList<ArrayList<Integer>> subGraphs, INameSelector nameSelector)
    {
        // for now, just change the names, don't collapse the lattice. TODO collapse the lattice.

        // setup grouped names, from subgraph:
        boolean first = true;
        String[] collapsedNames = new String[lat.latOrder]; // TODO change to collapsedLatOrder
        String[] collapsedColours = new String[lat.latOrder]; // TODO change to collapsedLatOrder
        System.arraycopy(lat.names, 0, collapsedNames, 0, lat.latOrder); // Then this will change as well.
        System.arraycopy(lat.colours, 0, collapsedColours, 0, lat.latOrder); // Then this will change as well.

        for (ArrayList<Integer> part : lat.subGraphs)
        {
            if (first) { first = false; continue; } // skip singletons (TODO may need changing when collapsing)

            String partName = nameSelector.ChooseName(part, lat.names);
            for(Integer i : part)
            {
                collapsedNames[i] = partName;
                collapsedColours[i] = lat.colours[i]; // TODO change
            }
        }

        // now everything is a singleton
        LinkedList<ArrayList<Integer>> collapsedSubGraph = new LinkedList<ArrayList<Integer>>();
        ArrayList<Integer> all = new ArrayList<Integer>();
        for(int i=0;i<lat.latOrder;i++) all.add(i); // TODO change
        collapsedSubGraph.add(all);

        //return new Lattice(lat.latBit, lat.latOrder, collapsedNames, collapsedColours, collapsedSubGraph, null, null);
        return new Lattice(lat.latBit, lat.latOrder, lat.names, lat.colours, lat.subGraphs);
    }

    public static Lattice FilterBy(Lattice lat, BitSet include)
    {
        return FilterBy(lat.latOrder, lat.names, lat.colours, lat.latBit, lat.subGraphs, include);
    }

    public static Lattice FilterBy(int latOrder, String[] names, String[] nodeAttrs, BitSet relation,
                                   LinkedList<ArrayList<Integer>> subGraphs, BitSet include)
    {
        int filteredLatOrder = include.cardinality();
        int[] oldIndex = new int[filteredLatOrder];
        int[] newIndex = new int[latOrder];
        String[] filteredNames = new String[filteredLatOrder];
        String[] filteredNodeAttrs = new String[filteredLatOrder];
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
            filteredNodeAttrs[i] = nodeAttrs[oldI];
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

        return new Lattice(filteredRelation, filteredLatOrder, filteredNames, filteredNodeAttrs, filteredSubGraphs);
    }

    public static Lattice FormatBy(Lattice lat) // TODO add some parameters
    {
        return null; // TODO
    }


    /**
     * Filter the lattice of all fix-orders by certain conditions (i.e. whether to include unfaithful and non-normal ones).
     *
     * @param fixOrders The set of all fix-orders
     * @param fullRelation The bitset showing the lattice formed by all fix orders
     * @param numFixOrders The number of elements
     * @param faithfulOnly Whether to include faithful elements only.
     * @param normalOnly Whether to include normal elements only.
     * @param fixOrderNames The name of each fix order (for output purposes).
     * @param colors The color of each fix order (for output purposes).
     * @return The lattice of the fix orders satisfying the conditions.
     */
    public static Lattice Filter3By(ArrayList<FixOrder> fixOrders, BitSet fullRelation, int numFixOrders,
                                   boolean faithfulOnly, boolean normalOnly, String[] fixOrderNames, String[] colors, LinkedList<ArrayList<Integer>> subGraphs)
    {
        BitSet include = includeBy(fixOrders, faithfulOnly, normalOnly);
        Lattice lat = new Lattice(fullRelation, numFixOrders, fixOrderNames, colors, subGraphs);
        return CollapseBy(FilterBy(lat, include), subGraphs, new FullPartNameSelector());
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

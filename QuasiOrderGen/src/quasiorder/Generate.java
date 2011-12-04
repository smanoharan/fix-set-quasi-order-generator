package quasiorder;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Generate all possible (faithful) Quasi-Orders on a given group
 *  (specified by it's elements, subgroups and subgroup conjugacy classes)
 *
 * @author Siva Manoharan [avismanoharan@hotmail.com]
 */
public class Generate
{
    private static final int REL_MAX_SIZE = 500;
    private static int iterCount;

    public static void main(String[] args)
    {
        Group inputGroup = null;
        String title = null;

        try
        {
            // parse command line arguments:
            if (args.length < 1 || args.length > 7)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 1-7.");
                System.err.println("Usage: java quasiorder.Generate title [-s] [-o] [-t] [-f] [-n]");
                System.err.println("\t-s means automatically sort the elements");
                System.err.println("\t-o means include graph files for all quasi-orders (in the file \"<title>.q<N>.lat\"");
                System.err.println("\t-t means do not output lattices for relations larger than " + REL_MAX_SIZE + " fix-orders.");
                System.err.println();
                System.err.println("\tThe title is the name of the group. <prefix>.in will be shortened to <prefix>");
                System.err.println("\tThe input file is assumed to be \"<title>.in\".");
                System.err.println("\tThe raw output will be placed in \"<title>.out\".");
                System.err.println("\tThe lattice of all fix-set quasi-orders will be placed in \"<title>.<type>.lat\" and \"<title>.json\".");
                return;
            }

            boolean sortElements = false;
            boolean outputAllGraphs = false;
            boolean thresholdRelationsBySize = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else if (arg.equals("-o")) outputAllGraphs = true;
                else if (arg.equals("-t")) thresholdRelationsBySize = true;
                else title = (arg.endsWith(".in")) ? arg.substring(0, arg.length() - 3) : arg;
            }

            if (title == null) throw new RuntimeException("Title not included!");

            // read and validate input:
            inputGroup = Group.FromRawGroup(Group.FromJSON(new FileReader(title + ".in")), sortElements);
            inputGroup.Validate(System.err);

            // process:
            long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
            FixOrderSet relations = new FixOrderSet();

            iterCount = 0;
            long maxIter = numSubsets / 2;
            if (maxIter==0) ProcessConjugacyFamily(inputGroup, relations, 1); // only 1 conj-class.
            else for (long s=0;s<maxIter;s++) ProcessConjugacyFamily(inputGroup, relations, (maxIter | s));

            PrintAllOutput(inputGroup, relations, title, outputAllGraphs, thresholdRelationsBySize);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n" + e.getMessage());
        }
    }

    // TODO test - somehow?
    private static void ProcessConjugacyFamily(Group inputGroup, FixOrderSet relations, long ccMask)
    {
        BitSet familyMask = GroupUtil.ToSubgroupFamilyBitSet(inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses,
                ccMask, inputGroup.ConjugacyClasses);

        List<Integer> family = GroupUtil.BitSetToList(familyMask);
        boolean isIntersectionClosed = GroupUtil.isIntersectionClosed(inputGroup.SubgroupIntersections, family, familyMask);
        boolean isUnionClosed = GroupUtil.isUnionClosed(inputGroup.SubgroupUnions, family, familyMask);

        // Note: This is still NOT unique as Union of 3 maybe a subgroup while Union of any two pairs in that 3 are not subgroups!
        if (isIntersectionClosed && isUnionClosed)
        {
            iterCount++;
            BitSet relation = FixOrderSet.BuildRelation(inputGroup, familyMask);
            relations.Add(FixOrder.FromRelation(relation, familyMask, inputGroup), familyMask);
        }
    }

    private static void PrintAllOutput(Group inputGroup, FixOrderSet relations, String title, boolean allGraphs, boolean thresholdRelationsBySize) throws IOException
    {
        // create the output streams:
        PrintWriter rawOutput = new PrintWriter(title + ".out");

        // sort all relations, by cardinality (size).
        relations.SortRelations();

        // print families:
        String colours[] = new String[relations.FixOrders.size()];
        int curIndex = 0;
        for (FixOrder b : relations.FixOrders)
        {
            RelationFormat.PrintSubgroupFamilyList(inputGroup, relations.FixOrderToFamilyMap.get(b), curIndex, rawOutput);
            colours[curIndex] = b.isFaithful ? (b.isNormal ? "chartreuse1" : "yellow") : (b.isNormal ? "cadetblue1" : "gray");
            curIndex++;
        }

        // print quasi-orders:
        curIndex=0;
        for(FixOrder b : relations.FixOrders)
            RelationFormat.PrintRelation(b.Relation, inputGroup.ElementNames, inputGroup.NumElements, curIndex++, rawOutput);

        // output all graphs (if needed)
        if (allGraphs)
        {
            curIndex=0;
            for(FixOrder b : relations.FixOrders)
            {
                PrintWriter graphWriter = new PrintWriter(title + ".g" + curIndex++ + ".lat");
                graphWriter.println(RelationFormat.PrintRelationEdges(b.Relation, inputGroup.ElementNames, colours, new LinkedList<ArrayList<Integer>>(), inputGroup.NumElements));
                graphWriter.close();
            }
        }

        // print the lattice of all fix-set quasi-orders:
        if (!thresholdRelationsBySize || relations.FixOrders.size() < REL_MAX_SIZE)
            PrintLatticeOfAllFixSetQuasiOrders(inputGroup, rawOutput, title, relations, colours);
        else System.err.println("Skipped lattice: size=" + relations.FixOrderToFamilyMap.keySet().size() + " is too big");

        // print summary
        String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                relations.FixOrderToFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

        rawOutput.println("\n\n" + summaryString);
        System.err.println(summaryString);

        rawOutput.flush();
        rawOutput.close();
    }

    private static void PrintLatticeOfAllFixSetQuasiOrders(
            Group inputGroup, PrintWriter rawOutput, String title,
            FixOrderSet relations, String[] colors) throws IOException
    {
        String[] latTypes = new String[] { "all", "faithful", "normal", "faithful-normal"};
        int numLatTypes = latTypes.length;

        int numRels = relations.FixOrders.size();
        String[] relNames = new String[numRels];
        for (int i=0;i<numRels;i++)
            relNames[i] = Integer.toString(i);

        BitSet overallRelation = relations.GenerateOverallQuasiOrder();
        LinkedList<ArrayList<Integer>> subgraphs = PartitionBy(relations.FixOrders, inputGroup.Permutations, inputGroup.NumElements);

        PrintWriter modDistOutput = new PrintWriter(title + ".md");
        rawOutput.println("\n\n" + "Lattice of all fix set quasi orders: ");
        for (int i=0;i<numLatTypes;i++)
        {
            Lattice lat = Lattice.FilterBy(relations.FixOrders, overallRelation, numRels, ((i & 1) == 1), i >= 2, relNames, colors, subgraphs);

            // ungrouped lattice
            RelationFormat.PrintRelationEdges(lat, String.format("%s.%s.lat", title, latTypes[i]), false, false, false);
            modDistOutput.println(String.format("ungrouped: %1$-50s %2$s", latTypes[i], lat.ModDistCheckMessage()));

            // grouped lattice
            RelationFormat.PrintRelationEdges(lat, String.format("%s.col1.%s.lat", title, latTypes[i]), true, false, true);
            RelationFormat.PrintRelationEdges(lat, String.format("%s.col2.%s.lat", title, latTypes[i]), false, true, true);
        }
        modDistOutput.close();
    }

    /**
     * Partition fix-orders by equivalence under automorphisms.
     *
     * @param automorphMaps The automorphisms of the group
     * @param numElem The number of elements in the group
     * @return A list of partitions, each of which is a list of integers (indices of elements). The first partition lists all singletons.
     */
    public static LinkedList<ArrayList<Integer>> PartitionBy(ArrayList<FixOrder> fixOrders, ArrayList<Permutation> automorphMaps, int numElem)
    {
        int numFixOrders = fixOrders.size();
        LinkedList<ArrayList<Integer>> parts = new LinkedList<ArrayList<Integer>>();
        ArrayList<Integer> singletons = new ArrayList<Integer>();
        parts.add(singletons);

        // init each element to its own group:
        int[] res = new int[numFixOrders];
        for (int i=0;i<numFixOrders;i++)
            res[i]=i;

        // for each size, for each element of that size, combine elements if automorphism-equivalent
        int startIndex = 0;
        while(startIndex<numFixOrders)
        {
            int currentSize = fixOrders.get(startIndex).Cardinality;
            int endIndex = startIndex;
            while(endIndex<numFixOrders && fixOrders.get(endIndex).Cardinality == currentSize) { endIndex++; }

            for(int i=startIndex;i<endIndex;i++)
            {
                // combine relations
                for (int j=i+1;j<endIndex;j++)
                {
                    if (res[i]!=res[j] && isAutomorphismEquivalent(fixOrders.get(i).Relation, fixOrders.get(j).Relation, automorphMaps, numElem))
                    {
                        int oldGroup = res[j];
                        for (int k=j;k<endIndex;k++)
                            if (res[k]==oldGroup)
                                res[k]=res[i];
                    }
                }
            }

            // generate the partitions from the numbers
            for(int i=startIndex;i<endIndex;i++)
            {
                if (res[i]==-1) continue; // already checked

                ArrayList<Integer> partition = new ArrayList<Integer>();
                partition.add(i);
                for(int j=i+1;j<endIndex;j++)
                {
                    if (res[j]==res[i])
                    {
                        partition.add(j);
                        res[j]=-1;
                    }
                }
                if (partition.size() == 1) singletons.add(i);
                else parts.add(partition);
            }

            startIndex = endIndex;
        }

        return parts;
    }

    /**
     * Check if the first bitSet becomes the second when rotated by any automorphism
     * @param automorphMaps The automorphisms of the group.
     * @param numElem The number of elements in the relation.
     * @return Whether the two BitSets are equivalent via automorphism.
     */
    public static boolean isAutomorphismEquivalent(BitSet first, BitSet second, ArrayList<Permutation> automorphMaps, int numElem)
    {
        for(Permutation p : automorphMaps)
            if (isAutomorphismEquivalent(first, second, p, numElem))
                return true;

        return false;
    }

    // Check if the two bitsets become the same when rotated by the permutation
    private static boolean isAutomorphismEquivalent(BitSet first, BitSet second, Permutation p, int numElem)
    {
        BitSet c = (BitSet)first.clone();
        for(TwoSwap t : p.swaps)
            FixOrder.Perform2Swap(c, t.i, t.j, numElem);

       return (c.equals(second));
    }

}


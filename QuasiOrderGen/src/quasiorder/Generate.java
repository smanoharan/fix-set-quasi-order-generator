package quasiorder;

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
    private static final String INPUT_EXT = ".in";
    private static final int REL_MAX_SIZE = 500;
    private static int iterCount = 0;

    /** For processing and storing of args */
    private static class CommandLineArgs
    {
        private static final int MAX_ARGS = 6;
        private static final int MIN_ARGS = 1;

        public final boolean sortElements;
        public final boolean outputAllGraphs;
        public final boolean thresholdRelationsBySize;
        public final String title;

        private CommandLineArgs(boolean sortElements, boolean outputAllGraphs, boolean thresholdRelationsBySize, String title)
        {
            this.sortElements = sortElements;
            this.outputAllGraphs = outputAllGraphs;
            this.thresholdRelationsBySize = thresholdRelationsBySize;
            this.title = title;
        }

        public static CommandLineArgs ParseArguments(String[] args) throws IllegalArgumentException
        {
            if (args.length < MIN_ARGS || args.length > MAX_ARGS)
                throw ExceptionWith("Incorrect number of arguments (%d). Expected %d - %d", args.length, MIN_ARGS, MAX_ARGS);

            boolean sortElements = false;
            boolean outputAllGraphs = false;
            boolean thresholdRelationsBySize = false;
            String title = null;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else if (arg.equals("-o")) outputAllGraphs = true;
                else if (arg.equals("-t")) thresholdRelationsBySize = true;
                else if (arg.startsWith("-")) throw ExceptionWith("Unknown flag: %s", arg);
                else title = (arg.endsWith(INPUT_EXT)) ? RemoveSuffix(arg, INPUT_EXT) : arg;
            }

            if (title == null) throw ExceptionWith("Title was not specified.");

            return new CommandLineArgs(sortElements, outputAllGraphs, thresholdRelationsBySize, title);
        }

        private static String RemoveSuffix(String orig, String suffix)
        {
            return orig.substring(0, orig.length() - suffix.length());
        }

        private static IllegalArgumentException ExceptionWith(String formatString, Object... args)
        {
            return new IllegalArgumentException(String.format(formatString, args));
        }

        public static void PrintUsageMessage()
        {
            System.err.println("Usage: java quasiorder.Generate title [-s] [-o] [-t] [-n]");
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
    }

    public static void main(String[] args)
    {
        try
        {
            CommandLineArgs parsedArgs = CommandLineArgs.ParseArguments(args);
            Group inputGroup = Group.FromFile(parsedArgs.title + ".in", parsedArgs.sortElements);

            FixOrderSet fixOrders = GenerateAllFixOrders(inputGroup);
            PrintAllOutput(inputGroup, fixOrders, parsedArgs);
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Error parsing input: " + e.getMessage());
            CommandLineArgs.PrintUsageMessage();
        }
        catch(IOException e)
        {
            System.err.println("An error occurred:\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static FixOrderSet GenerateAllFixOrders(Group inputGroup)
    {
        FixOrderSet relations = new FixOrderSet();
        long numSubsets = (1 << inputGroup.NumConjugacyClasses);    // 2^M

        long maxIter = numSubsets / 2;
        if (maxIter==0) ProcessConjugacyFamily(inputGroup, relations, 1); // only 1 conj-class.
        else for (long s=0;s<maxIter;s++)
                ProcessConjugacyFamily(inputGroup, relations, (maxIter | s));

        return relations;
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

    private static void PrintAllOutput(Group inputGroup, FixOrderSet relations, CommandLineArgs args) throws IOException
    {
        // create the output streams:
        PrintWriter rawOutput = new PrintWriter(args.title + ".out");

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
        if (args.outputAllGraphs)
        {
            curIndex=0;
            for(FixOrder b : relations.FixOrders)
            {
                PrintWriter graphWriter = new PrintWriter(args.title + ".g" + curIndex++ + ".lat");
                graphWriter.println(RelationFormat.PrintRelationEdges(b.Relation, inputGroup.ElementNames, colours, new LinkedList<ArrayList<Integer>>(), inputGroup.NumElements));
                graphWriter.close();
            }
        }

        // print the lattice of all fix-set quasi-orders:
        if (!args.thresholdRelationsBySize || relations.FixOrders.size() < REL_MAX_SIZE)
            PrintLatticeOfAllFixSetQuasiOrders(inputGroup, rawOutput, args.title, relations, colours);
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
            Lattice lat = Lattice.Filter3By(relations.FixOrders, overallRelation, numRels, ((i & 1) == 1), i >= 2, relNames, colors, subgraphs);

            // ungrouped lattice
            RelationFormat.PrintRelationEdges(lat, String.format("%s.%s.lat", title, latTypes[i]), false, RelationFormat.OutputNamingConvention.full, false);
            // TODO fix modDistOutput.println(String.format("ungrouped: %1$-50s %2$s", latTypes[i], lat.ModDistCheckMessage()));

            // grouped lattice
            RelationFormat.PrintRelationEdges(lat, String.format("%s.col1.%s.lat", title, latTypes[i]), true, RelationFormat.OutputNamingConvention.full, true);
            RelationFormat.PrintRelationEdges(lat, String.format("%s.col2.%s.lat", title, latTypes[i]), false, RelationFormat.OutputNamingConvention.grouped, true);
            RelationFormat.PrintRelationEdges(lat, String.format("%s.col3.%s.lat", title, latTypes[i]), false, RelationFormat.OutputNamingConvention.representative, true);

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


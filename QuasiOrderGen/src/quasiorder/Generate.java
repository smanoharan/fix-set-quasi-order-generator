package quasiorder;

import java.io.FileWriter;
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
    private static final int REL_MAX_SIZE = 2000;
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

    private static String[] ToStandardNames(int numRels)
    {
        String[] relNames = new String[numRels];
                for (int i=0;i<numRels;i++)
                    relNames[i] = Integer.toString(i);
        return relNames;
    }

    static interface IOutputMode
    {
        void PrintOut(Group inputGroup, FixOrderSet fixOrders, CommandLineArgs parsedArgs) throws IOException;
    }

    static IOutputMode VerboseOutputMode = new IOutputMode()
    {
        public void PrintOut(Group inputGroup, FixOrderSet relations, CommandLineArgs args) throws IOException
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

        final String SEP = ", ";
        private void PrintLatticeOfAllFixSetQuasiOrders(
                Group inputGroup, PrintWriter rawOutput, String title,
                FixOrderSet relations, String[] colors) throws IOException
        {
            String[] latTypes = new String[] { "all", "faithful", "normal", "faithful-normal"};
            int numLatTypes = latTypes.length;

            int numRels = relations.FixOrders.size();
            String[] relNames = ToStandardNames(numRels);

            BitSet overallRelation = relations.GenerateOverallQuasiOrder();
            LinkedList<ArrayList<Integer>> subgraphs = AutomorphismHandler.PartitionBy(relations.FixOrders, inputGroup.Permutations, inputGroup.NumElements);

            PrintWriter modDistOutput = new PrintWriter(title + ".md");
            PrintWriter isLatOutput = new PrintWriter(title + ".isl");
            rawOutput.println("\n\n" + "Lattice of all fix set quasi orders: ");

            StringBuilder ungrouped = new StringBuilder();
            StringBuilder autoGrouped = new StringBuilder();
            StringBuilder ugIsModIsDist = new StringBuilder();
            StringBuilder agIsLattice = new StringBuilder();

            for (int i=0;i<numLatTypes;i++)
            {
                BitSet include = Lattice.includeBy(relations.FixOrders, (i & 1) == 1, i >= 2);
                Lattice lat = Lattice.FilterBy(numRels, relNames, colors, overallRelation, subgraphs, include);
                ungrouped.append(SEP).append(lat.latOrder);

                // ungrouped lattice - show mjd features.
                MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
                RelationFormat.PrintRelationEdgesWithoutSubGraphs(mjdLat, String.format("%s.%s.lat", title, latTypes[i]));

                FlagMessagePair isM = mjdLat.ModularCheckMessage();
                FlagMessagePair isD = mjdLat.DistributiveCheckMessage();
                modDistOutput.println(String.format("%1$-20s %2$s", latTypes[i], mjdLat.ModDistCheckMessage(isM, isD)));
                ugIsModIsDist.append(SEP).append(isM.flag).append(SEP).append(isD.flag);
                // modDistOutput.println(String.format("%1$-20s %2$s", latTypes[i], mjdLat.M5Message())); // ignore M5 inclusiveness

                // grouped lattice - show only colours: 1) subgroups only ; 2) collapse + full-names ; 3) collapse + rep-names
                RelationFormat.PrintRelationEdgesWithSubGraphs(lat, String.format("%s.col1.%s.lat", title, latTypes[i]));

                Lattice colFull = Lattice.CollapseBy(lat, Lattice.FullPartNameSelector);
                RelationFormat.PrintRelationEdgesWithoutSubGraphs(colFull, String.format("%s.col2.%s.lat", title, latTypes[i]));
                FlagMessagePair isLatColFull = MeetJoinDeterminedLattice.LatCheckMessage(colFull);
                isLatOutput.println(String.format("full: %1$-20s %2$s", latTypes[i], isLatColFull.message));

                Lattice colRep = Lattice.CollapseBy(lat, Lattice.RepNameSelector);
                RelationFormat.PrintRelationEdgesWithoutSubGraphs(colRep, String.format("%s.col3.%s.lat", title, latTypes[i]));
                isLatOutput.println(String.format("rep : %1$-20s %2$s", latTypes[i], MeetJoinDeterminedLattice.LatCheckMessage(colRep).message));

                autoGrouped.append(SEP).append(colFull.latOrder);
                agIsLattice.append(SEP).append(isLatColFull.flag);

            }
            modDistOutput.close();
            isLatOutput.close();

            // print: title, subgroup count, conjugacy classes of subgroups, ungrouped lattice, automorphism-grouped lattice
            StringBuilder outputSB = new StringBuilder();
            outputSB.append(title).append(SEP).append(inputGroup.NumSubgroups).append(SEP).append(inputGroup.NumConjugacyClasses);
            outputSB.append(ungrouped).append(autoGrouped).append(ugIsModIsDist).append(agIsLattice);
            System.out.println(outputSB.toString());
        }
    };

    static IOutputMode ModularDistributivityMinimalOutputMode = new IOutputMode()
    {
        public void PrintOut(Group inputGroup, FixOrderSet relations, CommandLineArgs args) throws IOException
        {
            PrintWriter pw = new PrintWriter(new FileWriter("md-result.txt", true));
            relations.SortRelations();

            // print the lattice of all fix-set quasi-orders:
            if (!args.thresholdRelationsBySize || relations.FixOrders.size() < REL_MAX_SIZE)
                PrintModDistStatusOnly(args.title, relations, pw);
            else System.out.println("Skipped lattice: size=" + relations.FixOrderToFamilyMap.keySet().size() + " is too big");

            // print summary
            String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                    relations.FixOrderToFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

            System.err.println(summaryString);
            pw.close();
        }

        private void PrintModDistStatusOnly(String title, FixOrderSet relations, PrintWriter output) throws IOException
        {
            int numRels = relations.FixOrders.size();
            String[] relNames = ToStandardNames(numRels);

            BitSet overallRelation = relations.GenerateOverallQuasiOrder();
            Lattice lat = new Lattice(overallRelation, numRels, relNames, relNames, new LinkedList<ArrayList<Integer>>());

            MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
            boolean wanted = mjdLat.IsModular() && !mjdLat.IsDistributive();
            output.println(title + ": " + wanted);
            System.err.println(wanted);

            // optionally: include M5 inclusion test
            //modDistOutput.println(mjdLat.ModularCheckMessage());
            //modDistOutput.println(mjdLat.DistributiveCheckMessage());
            //modDistOutput.println(mjdLat.M5Message());
        }
    };

    static IOutputMode ModularDistributivityCompleteOutputMode = new IOutputMode()
    {
        public void PrintOut(Group inputGroup, FixOrderSet relations, CommandLineArgs args) throws IOException
        {
            PrintWriter pw = new PrintWriter(new FileWriter("md-result.txt", true));
            relations.SortRelations();

            // print the lattice of all fix-set quasi-orders:
            if (!args.thresholdRelationsBySize || relations.FixOrders.size() < REL_MAX_SIZE)
                PrintModDistOfFixSetQuasiOrders(pw, inputGroup, args.title, relations);
            else System.out.println("Skipped lattice: size=" + relations.FixOrderToFamilyMap.keySet().size() + " is too big");

            // print summary
            String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                    relations.FixOrderToFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

            System.err.println(summaryString);
            pw.close();
        }

        private void PrintModDistOfFixSetQuasiOrders(PrintWriter output, Group inputGroup, String title, FixOrderSet relations) throws IOException
        {
            String[] latTypes = new String[] { "all", "faithful", "normal", "faithful-normal"};
            int numLatTypes = latTypes.length;

            int numRels = relations.FixOrders.size();
            String[] relNames = ToStandardNames(numRels);

            BitSet overallRelation = relations.GenerateOverallQuasiOrder();
            LinkedList<ArrayList<Integer>> subgraphs = AutomorphismHandler.PartitionBy(relations.FixOrders, inputGroup.Permutations, inputGroup.NumElements);

            for (int i=0;i<numLatTypes;i++)
            {
                BitSet include = Lattice.includeBy(relations.FixOrders, (i & 1) == 1, i >= 2);
                Lattice lat = Lattice.FilterBy(numRels, relNames, relNames, overallRelation, subgraphs, include);

                MeetJoinDeterminedLattice mjdLat = MeetJoinDeterminedLattice.FromLattice(lat);
                boolean wanted = mjdLat.IsModular() && !mjdLat.IsDistributive();
                String msg = String.format("%s %s %s", title, latTypes[i], wanted);
                output.println(msg);
                System.err.println(msg);
            }
        }
    };

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

    private static FixOrderSet GenerateAllFixOrders(Group inputGroup)
    {
        FixOrderSet relations = new FixOrderSet();
        long numSubsets = (1L << inputGroup.NumConjugacyClasses);    // 2^M

        long maxIter = numSubsets / 2L;
        if (maxIter==0) ProcessConjugacyFamily(inputGroup, relations, 1); // only 1 conj-class.
        else for (long s=0;s<maxIter;s++)
                ProcessConjugacyFamily(inputGroup, relations, (maxIter | s));

        return relations;
    }

    public static class AutomorphismHandler
    {


        /**
         * Partition fix-orders by equivalence under automorphisms.
         *
         * @param fixOrders The list of all fix-orders in this group.
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
         * @param first The first bitSet
         * @param second The second bitSet
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

    public static void main(String[] args)
    {
        try
        {
            CommandLineArgs parsedArgs = CommandLineArgs.ParseArguments(args);
            Group inputGroup = Group.FromFile(parsedArgs.title + ".in", parsedArgs.sortElements);

            FixOrderSet fixOrders = GenerateAllFixOrders(inputGroup);
            VerboseOutputMode.PrintOut(inputGroup, fixOrders, parsedArgs);
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
}


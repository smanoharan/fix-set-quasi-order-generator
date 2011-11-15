package quasiorder;

import java.io.*;
import java.util.BitSet;
import java.util.List;

/**
 * Generate all possible (faithful) Quasi-Orders on a given group
 *  (specified by it's elements, subgroups and subgroup conjugacy classes)
 *
 * @author Siva Manoharan [avismanoharan@hotmail.com]
 */
public class Generate
{
    public static void main(String[] args)
    {
        Group inputGroup = null;
        String title = "group";
        PrintWriter rawOutput = null;
        PrintWriter latOutput = null;

        // input:
        try
        {
            if (args.length > 2)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 0-2.");
                System.err.println("Usage: java quasiorder.Generate title [-s]");
                System.err.println("\t-s means automatically sort the elements");
                System.err.println("\tThe input file is assumed to be \"<title>.in\".");
                System.err.println("\tThe raw output will be placed in \"<title>.out\".");
                System.err.println("\tThe lattice of all fix-set quasi-orders will be placed in \"<title>.lat\".");
                return;
            }

            boolean sortElements = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else title = arg;
            }

            // read and validate input:
            inputGroup = Group.FromRawGroup(RawGroup.FromJSON(new FileReader(title + ".in")), sortElements);
            inputGroup.Validate(System.err);

            rawOutput = new PrintWriter(title + ".out");
            latOutput = new PrintWriter(title + ".lat");
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n"+e.getMessage());
            e.printStackTrace();
            return;
        }

        // process:
        long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
        RelationSet relations = new RelationSet();

        int iterCount = 0;
        long maxIter = numSubsets / 4;
        long minimumClasses = (1 | (maxIter << 1));
        for (long s=0;s<maxIter;s++)
        {
            long ccMask = (minimumClasses | (s << 1));
            BitSet familyMask = GroupUtil.ToSubgroupFamilyBitSet(
                    inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses,
                    ccMask, inputGroup.ConjugacyClasses);

            List<Integer> family = GroupUtil.BitSetToList(familyMask);
            boolean isIntersectionClosed = GroupUtil.isIntersectionClosed(inputGroup.SubgroupIntersections, family, familyMask);
            boolean isUnionClosed = GroupUtil.isUnionClosed(inputGroup.SubgroupUnions, family, familyMask);

            // Note: This is still NOT unique as Union of 3 maybe a subgroup while Union of any two pairs in that 3 are not subgroups!
            if (isIntersectionClosed && isUnionClosed)
            {
                iterCount++;
                relations.Add(RelationSet.BuildRelation(inputGroup, familyMask), familyMask);

                rawOutput.println(String.format("%1s \t\t %2s \t\t (%3s) \t\t " + familyMask,
                        isIntersectionClosed, Long.toBinaryString(ccMask), Long.toBinaryString(s)));
            }

        }

        // output? TODO
        relations.SortRelations();

        // print families:
        int curIndex = 0;
        for (FixedBitSet b : relations.Relations)
            OutputFormatter.PrintSubgroupFamilyList(inputGroup, relations.RelationsFamilyMap.get(b.Relation), curIndex++, rawOutput);

        // print quasi-orders:
        curIndex=0;
        for(FixedBitSet b : relations.Relations)
            OutputFormatter.PrintRelation(b.Relation, inputGroup.ElementNames, inputGroup.NumElements, curIndex++, rawOutput);

        // print the lattice of all fix-set quasi-orders:
        int numRels = relations.Relations.size();
        String[] relNames = new String[numRels];
        for (int i=0;i<numRels;i++)
            relNames[i] = Integer.toString(i);

        rawOutput.println("\n\n" + "Lattice of all fix set quasi orders: ");
        BitSet fixSetQOLattice = relations.GenerateOverallQuasiOrder();
        OutputFormatter.PrintRelation(fixSetQOLattice, relNames, numRels, 0, rawOutput);
        latOutput.println(OutputFormatter.PrintRelationEdges(fixSetQOLattice, relNames, numRels));

        String summaryString = String.format("Found %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                relations.RelationsFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups);

        rawOutput.println("\n\n"+summaryString);
        System.err.println(summaryString);

        rawOutput.flush(); rawOutput.close();
        latOutput.flush(); latOutput.close();
    }
}


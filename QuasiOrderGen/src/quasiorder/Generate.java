package quasiorder;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
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
       
        Reader inputReader = null;
        Group inputGroup = null;

        // input:
        try
        {
            if (args.length > 2)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 0-2.");
                System.err.println("Usage: java quasiorder.Generate [-s] [inputfile]");
                System.err.println("If inputfile is omitted, input is assumed to be from stdin.");
                System.err.println("-s means automatically sort the elements");
                return;
            }

            boolean sortElements = false;
            for (String arg : args)
            {
                if (arg.equals("-s")) sortElements = true;
                else inputReader = new FileReader(args[0]);
            }

            // if no file is specified, read from std-in.
            if (inputReader==null)
                inputReader = new InputStreamReader(System.in);

            inputGroup = Group.FromRawGroup(RawGroup.FromJSON(inputReader), sortElements);

            // Check group is valid:
            inputGroup.Validate(System.out);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:\n\n"+e.getMessage());
            e.printStackTrace();
            return;
        }
        finally
        {
            if (inputReader != null)
            {
                try { inputReader.close(); } catch (Exception e) {} // Nothing we can do here.
            }
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

                System.out.println(
                    String.format("%1s \t\t %2s \t\t (%3s) \t\t " + familyMask,
                        isIntersectionClosed, Long.toBinaryString(ccMask), Long.toBinaryString(s)));
            }

        }

        // output? TODO
        relations.SortRelations();

        // print families:
        int curIndex = 0;
        for (FixedBitSet b : relations.Relations)
            OutputFormatter.PrintSubgroupFamilyList(inputGroup, relations.RelationsFamilyMap.get(b.Relation), curIndex++);

        // print quasi-orders:
        curIndex=0;
        for(FixedBitSet b : relations.Relations)
            OutputFormatter.PrintRelation(b.Relation, inputGroup.ElementNames, inputGroup.NumElements, curIndex++);

        // print the lattice of all fix-set quasi-orders:
        int numRels = relations.Relations.size();
        String[] relNames = new String[numRels];
        for (int i=0;i<numRels;i++)
            relNames[i] = Integer.toString(i);

        System.out.println("\n\n" + "Lattice of all fix set quasi orders: ");
        BitSet fixSetQOLattice = relations.GenerateOverallQuasiOrder();
        OutputFormatter.PrintRelation(fixSetQOLattice, relNames, numRels, 0);
        System.out.println(OutputFormatter.PrintRelationEdges(fixSetQOLattice, relNames, numRels));

        System.out.println(String.format("\n\nFound %d unique relations, from %d investigated relations, [ out of 2^%d or 2^%d ]",
                relations.RelationsFamilyMap.keySet().size(), iterCount, inputGroup.NumConjugacyClasses, inputGroup.NumSubgroups));


    }
}


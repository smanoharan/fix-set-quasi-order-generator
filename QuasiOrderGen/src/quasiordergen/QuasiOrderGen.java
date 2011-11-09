package quasiordergen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map;

/**
 * Generate all possible (faithful) Quasi-Orders on a given group
 *  (specified by it's elements, subgroups and subgroup conjugacy classes)
 *
 * @author Siva Manoharan [avismanoharan@hotmail.com]
 */
public class QuasiOrderGen 
{
    public static void main(String[] args)
    {
       
        BufferedReader inputStream = null;
        InputContainer inputGroup = null;

        // input:
        try
        {
            if (args.length > 1)
            {
                System.err.println("Error. Incorrect number of arguments. Expected: 1.");
                System.err.println("Usage: java quasiordergen.QuasiOrderGen [inputfile]");
                System.err.println("If inputfile is omitted, input is assumed to be from stdin.");
                return;
            }
            else if (args.length==1)
            {
                // input is from a file
                inputStream = new BufferedReader(new FileReader(args[0]));
            }
            else
            {
                // input is from stdin
                inputStream = new BufferedReader(new InputStreamReader(System.in));
            }

            inputGroup = InputContainer.FromInput(inputStream);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }

        // process:
        long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
        Hashtable<BitSet, ArrayList<BitSet>> uniqRelations = new Hashtable<BitSet, ArrayList<BitSet>>();

        for (long s=1;s<numSubsets;s++)
        {
            BitSet familyMask = ToSubgroupFamilyBitSet(inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses, s, inputGroup.ConjugacyClasses);
            ConsiderSubgroupFamily(inputGroup, familyMask, uniqRelations);

        }


        // output? TODO
        for (Map.Entry<BitSet, ArrayList<BitSet>> e : uniqRelations.entrySet())
        {
            OutputFormatter.PrintRelation(e.getKey(), e.getValue(), inputGroup);
        }

        System.out.println("Found " + uniqRelations.keySet().size() + " unique relations (out of maximum possible " + (1 << inputGroup.NumConjugacyClasses) + ");");
    }

    public static int ToIndex(int i, int j, int length)
    {
        return i*length + j;
    }

    /**
     * Convert a mask representing the subgroup conjugacy classes to placed in this family,
     *  into a mask representing the subgroups in this family.
     * @param numSubgroups Number of subgroups.
     * @param numConjugacyClasses Number of subgroup conjugacy classes.
     * @param conjugacyMask A mask representing the conjugacy classes to be placed into this family.
     * @param conjugacyClasses The masks of all subgroup conjugacy classes in this group.
     * @return A mask representing the subgroups in this family.
     */
    public static BitSet ToSubgroupFamilyBitSet(int numSubgroups, int numConjugacyClasses, long conjugacyMask, BitSet[] conjugacyClasses)
    {
        // convert conjugacy class to a family-bitset.
        BitSet familyMask = new BitSet(numSubgroups);
        long classMask = 1;
        for (int c=0;c<numConjugacyClasses;c++)
        {
            if (0 != (classMask & conjugacyMask))
                familyMask.or(conjugacyClasses[c]);
            
            classMask *= 2;
        }
        return familyMask;
    }

    // TODO test
    public static void ConsiderSubgroupFamily(InputContainer inputGroup, BitSet familyMask,
                                              Hashtable<BitSet, ArrayList<BitSet>> uniqRelations)
    {
        // check if intersection is trivial
        if (!isIntersectionTrivial(inputGroup.ElementMasks, familyMask)) return;

        // TODO represent relation is some compact way
        // ideas:
        //  Start with all items in their own cluster
        //  if g1 <= g2 && g2 <= g1 ; place g1 and g2 in the same cluster.
        //  Then there is a partial order on the clusters
        //  Output it (for now)
        //  implement this using a disjoint set data structure.

        int NE = inputGroup.NumElements;
        BitSet relation = new BitSet(NE*NE);

        for (int i=0;i<NE;i++)
        {
            // (i,i) is automatically present
            relation.set(ToIndex(i,i,NE));

            for (int j=i+1;j<NE;j++)
            {
                boolean ij = isRelated(inputGroup.ElementMasks[i], inputGroup.ElementMasks[j], familyMask);
                boolean ji = isRelated(inputGroup.ElementMasks[j], inputGroup.ElementMasks[i], familyMask);

                if (ij) relation.set(ToIndex(i,j,NE));
                if (ji) relation.set(ToIndex(j,i,NE));
            }
        }

        //  process:
        //      create Relation - add to list ( // if a<=b && b<=a group them together)
        //      [Optionally] Find all unique relations from list

        //  output:
        //      A list of relations
        //      [Optionally] Convert it to a displayable form.
        //

        if (uniqRelations.containsKey(relation))
        {
            uniqRelations.get(relation).add(familyMask);
        }
        else
        {
            ArrayList<BitSet> families = new ArrayList<BitSet>();
            families.add(familyMask);
            uniqRelations.put(relation, families);
        }
    }

    /**
     * Determine if g1 <= g2 (i.e. if (g1,g2) \in Relation ) given their subgroup memberships
     *  and the subgroup family under consideration.
     *
     * @param g1Mask A bitmask representing g1's subgroup membership. The i'th bit is 1 iff g1 \in subgroup_i
     * @param g2Mask A bitmask representing g2's subgroup membership. The i'th bit is 1 iff q2 \in subgroup_i
     * @param subgroupFamilyMask A bitmask representing the subgroup family under consideration. The i'th bit is 1 iff subgroup_i \in family
     * @return whether g1 <= g2
     */
    public static boolean isRelated(BitSet g1Mask, BitSet g2Mask, BitSet subgroupFamilyMask)
    {
        // start with b: 1 iff g1 \in sub-i
        BitSet b = (BitSet)g1Mask.clone();

        // 'AND' with subgroup family: 1 iff ( g1 \in sub-i && sub-i \in family )
        b.and(subgroupFamilyMask);

        // 'AND' with NOT of g2Mask: 1 iff ( g1 \in sub-i && sub-i \in family  && g2 \in sub-i)
        b.andNot(g2Mask);

        // if any bits are set, the relation is false (there is a counter example)
        return b.isEmpty();
    }

    /**
     * Determine if the intersection of all the subgroups in this family is trivial (i.e. = {1})
     * Assumes that the elements[0] is the unity element (1).
     * Note: If the intersection is empty (which should never occur as all subgroups must contain 1), then the intersection is not trivial.
     *
     * @param elements The bitmasks representing the subgroup memberships of each element in the group.
     * @param subgroupFamilyMask The bitmask representing the subgroups in this family.
     * @return Whether the intersection of all the subgroups is trivial.
     */
    public static boolean isIntersectionTrivial(BitSet[] elements, BitSet subgroupFamilyMask)
    {
        int numElem = elements.length;
        for (int i=1;i<numElem;i++) // ignore elements[0] - unity.
        {
            BitSet b = (BitSet)subgroupFamilyMask.clone();
            b.and(elements[i]); // if elem was in all subsets, b would not have changed here.
            if (b.equals(subgroupFamilyMask)) return false;
        }
        return true;
    }

    /**
     * Convert a mask (given as a long) into a BitSet. Only consider the N least significant bits, where N=length
     * @param mask The bitmask, as a long
     * @param length Number of bits of the long to consider
     * @return A BitSet representing the last N bits of the bitmask.
     */
    protected static BitSet MaskToBitSet(long mask, int length)
    {
        BitSet result = new BitSet(length);

        long bitValue = 1;
        for (int i=0;i<length;i++)
        {
            if (0 != (mask & bitValue)) result.set(i);
            bitValue *= 2;
        }
        return result;
    }
}


package quasiordergen;

import java.io.*;
import java.util.BitSet;
import java.util.HashMap;

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

        // parse input:
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

            inputGroup = ReadInput(inputStream);
        }
        catch(Exception e)
        {
            System.err.println("An error occurred:");
            System.err.println(e.getMessage());
            return;
        }

        // TODO List:
        //  Input -- done, needs testing / potential for improvement
        //  Processing -- overall, conjugate closure.
        //  Output -- todo

        long numSubsets = (1 << inputGroup.NumConjugacyClasses); // 2^M
        for (long s=1;s<numSubsets;s++)
        {
            BitSet familyMask = ToSubgroupFamilyBitSet(inputGroup.NumSubgroups, inputGroup.NumConjugacyClasses, s, inputGroup.ConjugacyClasses);
            ConsiderSubgroupFamily(inputGroup, familyMask);
        }

        //  process:
        //      create Relation - add to list ( // if a<=b && b<=a group them together)
        //      [Optionally] Find all unique relations from list

        //  output:        
        //      A list of relations 
        //      [Optionally] Convert it to a displayable form.
        //
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
    public static void ConsiderSubgroupFamily(InputContainer inputGroup, BitSet familyMask)
    {
        System.out.println(familyMask);

        // check if intersection is trivial
        if (!isIntersectionTrivial(inputGroup.ElementMasks, familyMask)) return;

        // TODO temp:
        System.out.println(familyMask);

        // represent relation is some compact way
        // TODO
    }

    // TODO test
    private static InputContainer ReadInput(BufferedReader input) throws IOException, NumberFormatException
    {
        // ignore the first line, which is a textual description of the group:
        input.readLine();

        // lines 2-4: number of group elements=N, number of subgroups=K, number of conjugacy-classes=M
        int numElem             = Integer.parseInt(input.readLine());
        int numSubgroups        = Integer.parseInt(input.readLine());
        int numConjugacyClasses = Integer.parseInt(input.readLine());

        // next N lines: name of each element (a string)
        // map each group element name to an index
        HashMap<String, Integer> elementIndexMap = new HashMap<String, Integer>();
        for (int i=0;i<numElem;i++)
            elementIndexMap.put(input.readLine().trim(), i);

        // Next K+M lines: Represents the conjugacy classes.
        //  Each class starts with Ji which is the number of subgroups in each class.
        //      For each class, Ji lines follow, each describing a subgroup, as a space separated list.

        // create bitsets to store memberships:
        BitSet[] conjugacyClasses = new BitSet[numConjugacyClasses];
        BitSet[] elementMasks = new BitSet[numElem];

        for (int i=0;i<numElem;i++)
            elementMasks[i] = new BitSet(numSubgroups);

        int curSubgroupIndex = 0;
        for (int m=0;m<numConjugacyClasses;m++)
        {
            int J = Integer.parseInt(input.readLine());

            // assign these subgroups to the correct conjugacy class
            conjugacyClasses[m] = new BitSet(numSubgroups);
            conjugacyClasses[m].set(curSubgroupIndex, curSubgroupIndex+J);

            // modify the bitSets as per corresponding subgroups.
            for (int i=0;i<J;i++)
                ParseLine(input.readLine(), curSubgroupIndex++, elementMasks, elementIndexMap);
        }

        return new InputContainer(numElem, numSubgroups, numConjugacyClasses, elementMasks, conjugacyClasses);
    }

    // TODO Test
    private static void ParseLine(String line, int subgroupIndex, BitSet[] elementMasks, HashMap<String, Integer> elementIndexMap)
    {
        // set each element in this line to be a member of the corresponding subgroup.
        for (String elem : line.split(" "))
            elementMasks[elementIndexMap.get(elem)].set(subgroupIndex);
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


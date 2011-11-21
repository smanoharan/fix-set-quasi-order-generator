package quasiorder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class GroupUtil
{
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

    public static List<Integer> BitSetToList(BitSet b)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i=b.nextSetBit(0); i>=0; i=b.nextSetBit(i+1))
            list.add(i);
        return list;
    }

    /**
     * Check if the intersection of all possible pairs of subgroups in the family are present in the family.
     *
     * @param subgroupIntersections The map of (a,b)->c where c is the intersection of a and b.
     * @param subgroupFamily A list of items, representing each subgroup in the family.
     * @param subgroupFamilyMask The mask representing which subgroups are in this family
     * @return whether this family is intersection closed.
     */
    public static boolean isIntersectionClosed(int[][] subgroupIntersections, List<Integer> subgroupFamily, BitSet subgroupFamilyMask)
    {
        return isOperationClosed(subgroupIntersections, subgroupFamily, subgroupFamilyMask);
    }

    /**
     * Check if Union is closed over all subgroups, only when the result is a subgroup.
     * @param subgroupUnions A map of (a,b)->c where c is the union of a and b or is -1.
     * @param subgroupFamily
     * @param familyMask
     * @return
     */
    public static boolean isUnionClosed(int[][] subgroupUnions, List<Integer> subgroupFamily, BitSet familyMask)
    {
        return isOperationClosed(subgroupUnions, subgroupFamily, familyMask);
    }

    /**
     * Check if the operation is closed over this family of subgroups.
     *
     * @param operation The operation to check.
     * @param subgroupFamily The family of subgroups, as a list.
     * @param subgroupFamilyMask The family of subgroups as a bitset.
     * @return Whether the operation is closed.
     */
    public static boolean isOperationClosed(int[][] operation, List<Integer> subgroupFamily, BitSet subgroupFamilyMask)
    {
        int len = subgroupFamily.size();
        for (int i=0;i<len;i++)
        {
            for(int j=i+1;j<len;j++)
            {
                int result = operation[subgroupFamily.get(i)][subgroupFamily.get(j)];
                if (result != -1 && !subgroupFamilyMask.get(result))
                    return false;
            }
        }

        return true;
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
     * Convert a mask (given as a long) into a BitSet. Only consider the N least significant bits, where N=length
     *
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

    /**
     * Generate the pairwise unions of each pair of subgroups,
     *  and then generate a map from SxS->S, where S is the index of the subgroup.
     *
     * @param numSubgroups Number of subgroups in this group.
     * @param subgroupMasks The membership masks of these subgroups.
     * @return The map (a,b)=>c, where c = indexOf(sub[a] U sub[b]) or -1 if sub[a] U sub[b] is not a subgroup
     */
    public static int[][] GenerateUnions(int numSubgroups, BitSet[] subgroupMasks)
    {
        return GenerateCombinations(numSubgroups, subgroupMasks, new Group.IBitSetOperation()
        {
            public void combine(BitSet b1, BitSet b2) { b1.or(b2); }
        }, true);
    }

    /**
     * Determine the intersections of each pair of subgroups.
     * @param numSubgroups The number of subgroups in this group.
     * @param subgroupMasks The membership masks of each subgroup (i.e. which elements are in each subgroup).
     * @return An array where if  entry(i,j) = k ; then the intersection of the i'th and j'th subgroup is the k'th (all indices 0-based).
     */
    public static int[][] GenerateIntersections(int numSubgroups, BitSet[] subgroupMasks)
    {
        return GenerateCombinations(numSubgroups, subgroupMasks, new Group.IBitSetOperation()
        {
            public void combine(BitSet b1, BitSet b2) { b1.and(b2); }
        }, false);
    }

    /**
     * Determine the combinations of each pair of subgroups, using a bit-op (which modifies the first bitset).
     * @param numSubgroups The number of subgroups in this group.
     * @param subgroupMasks The membership masks of each subgroup (i.e. which elements are in each subgroup).
     * @param bitOp A function which modifies the first operator by combining it with the second.
     * @param ignoreMissing If a.combine(b) results in a bitset which is not a subgroup, this is considered "Missing". If true, return -1. Else throw exception.
     * @return An array where if  entry(i,j) = k ; then the combination of the i'th and j'th subgroup is the k'th (all indices 0-based).
     */
    static int[][] GenerateCombinations(int numSubgroups, BitSet[] subgroupMasks, Group.IBitSetOperation bitOp, boolean ignoreMissing)
    {
        int[][] res = new int[numSubgroups][numSubgroups];
        for (int i=0;i<numSubgroups;i++)
        {
            res[i][i] = i;
            for (int j=i+1;j<numSubgroups;j++)
            {
                int intersection = GenerateCombination(i, j, subgroupMasks, bitOp, ignoreMissing);
                res[i][j] = intersection;
                res[j][i] = intersection;
            }
        }
        return res;
    }

    /**
     * Calculate the combination of the two given subgroups, using the specified bit-op.
     * @param s1 The index of the first subgroup
     * @param s2 The index of the second
     * @param subgroups The subgroup membership masks
     * @param bitOp A function which modifies the first operator by combining it with the second.
     * @param ignoreMissing If a.combine(b) results in a bitset which is not a subgroup, this is considered "Missing". If true, return -1. Else throw exception.
     * @return the index of the subgroup which is equal to (s1 intersect s2).
     */
    static int GenerateCombination(int s1, int s2, BitSet[] subgroups, Group.IBitSetOperation bitOp, boolean ignoreMissing)
    {
        // find all elements that are in both sub1 and sub2.
        BitSet s12 = (BitSet)subgroups[s1].clone();
        bitOp.combine(s12, subgroups[s2]);

        for (int i=0;i<subgroups.length;i++)
            if (s12.equals(subgroups[i]))
                return i;

        if (ignoreMissing) return -1;
        else throw new RuntimeException("Error: Subgroup not found" + subgroups[s1] + " ^ " + subgroups[s2] + " not a subgroup");
    }
}

package quasiorder;

import java.util.BitSet;

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
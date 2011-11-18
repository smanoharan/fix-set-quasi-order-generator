package quasiorder;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;

class Group
{
    public final int NumElements;
    public final int NumSubgroups;
    public final int NumConjugacyClasses;
    public final BitSet[] ElementMasks;
    public final String[] ElementNames;
    public final String[] SubgroupNames;
    public final BitSet[] ConjugacyClasses;
    public final BitSet[] SubgroupMasks;
    public final int[][] SubgroupIntersections;
    public final int[][] SubgroupUnions;
    public final BitSet IsSubgroupNormal;

    public Group(
            int numElements, int numSubgroups, int numConjugacyClasses,
            BitSet[] elementMasks, String[] elementNames, BitSet[] subgroupMasks, String[] subgroupNames, int[][] subgroupIntersections, int[][] subgroupUnions, BitSet[] conjugacyClasses, BitSet conjugacyClassNormal)
    {
        NumElements = numElements;
        NumSubgroups = numSubgroups;
        NumConjugacyClasses = numConjugacyClasses;
        ElementMasks = elementMasks;
        ElementNames = elementNames;
        SubgroupMasks = subgroupMasks;
        SubgroupNames = subgroupNames;
        SubgroupIntersections = subgroupIntersections;
        SubgroupUnions = subgroupUnions;
        ConjugacyClasses = conjugacyClasses;
        IsSubgroupNormal = conjugacyClassNormal;
    }

    /**
     * Create a group by processing a RawGroup.
     * @param rawgroup a group which is parsed json, with no processing.
     * @param sortElem Whether or not to sort the elements.
     * @return A fully processed group.
     */
    public static Group FromRawGroup(RawGroup rawgroup, boolean sortElem)
    {
        // counts of each type:
        int numElem = rawgroup.NumElements;
        int numSubgroups = rawgroup.NumSubgroups;
        int numConjugacyClasses = rawgroup.NumConjugacyClasses;

        // element names:
        HashMap<String, Integer> elementIndexMap = new HashMap<String, Integer>();
        String[] elementNames = rawgroup.Elements;

        if (sortElem)
        {
            // sort the names (first by length, then lexically)
            Arrays.sort(elementNames, new Comparator<String>()
            {
                public int compare(String o1, String o2)
                {
                    int diff = (o1.length() - o2.length());
                    return diff == 0 ? diff = o1.compareTo(o2) : diff;
                }
            });
        }

        for (int i=0;i<numElem;i++)
            elementIndexMap.put(rawgroup.Elements[i], i);

        BitSet[] conjugacyClasses = new BitSet[numConjugacyClasses];
        BitSet[] elementMasks = new BitSet[numElem];
        BitSet[] subgroupMasks = new BitSet[numSubgroups]; 

        for (int i=0;i<numElem;i++)
            elementMasks[i] = new BitSet(numSubgroups);

        for (int i=0;i<numSubgroups;i++)
            subgroupMasks[i] = new BitSet(numElem);

        int curSubgroupIndex = 0;
        BitSet isSubgroupNormal = new BitSet(numSubgroups);
        String[] subgroupNames = new String[numSubgroups];

        for (int m=0;m<numConjugacyClasses;m++)
        {
            String[][] conjClass = rawgroup.ConjugacyClasses[m];
            int conjClassSize = conjClass.length;

            // determine if subgroup is normal (which is iff conj-class is singleton):
            if (conjClassSize==1) isSubgroupNormal.set(curSubgroupIndex);

            // assign subgroups correct conjugacy class
            conjugacyClasses[m] = new BitSet(numSubgroups);
            conjugacyClasses[m].set(curSubgroupIndex, curSubgroupIndex+conjClassSize);

            // assign elements to corresponding subgroups & build up subgroup-name.
            for (int i=0;i<conjClassSize;i++)
            {
                StringBuilder subgroupName = new StringBuilder();
                for(String elem : conjClass[i])
                {
                    subgroupName.append(elem);
                    subgroupName.append(' ');

                    // set the subgroup membership, and the element membership.
                    int elemIndex = elementIndexMap.get(elem);
                    elementMasks[elemIndex].set(curSubgroupIndex);
                    subgroupMasks[curSubgroupIndex].set(elemIndex);
                }
                subgroupNames[curSubgroupIndex] = subgroupName.toString().trim();
                curSubgroupIndex++;
            }
        }

        // calculate the intersection of each pair of subgroups
        int[][] subgroupIntersections = GenerateIntersections(numSubgroups, subgroupMasks);
        int[][] subgroupUnions = GenerateUnions(numSubgroups, subgroupMasks);

        return new Group(numElem, numSubgroups, numConjugacyClasses,
                elementMasks, elementNames, subgroupMasks, subgroupNames,
                subgroupIntersections, subgroupUnions, conjugacyClasses, isSubgroupNormal);
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
        return GenerateCombinations(numSubgroups, subgroupMasks, new IBitSetOperation()
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
        return GenerateCombinations(numSubgroups, subgroupMasks, new IBitSetOperation()
        {
            public void combine(BitSet b1, BitSet b2) { b1.and(b2); }
        }, false);
    }

    interface IBitSetOperation
    {
        void combine(BitSet b1, BitSet b2);
    }

    /**
     * Determine the combinations of each pair of subgroups, using a bit-op (which modifies the first bitset).
     * @param numSubgroups The number of subgroups in this group.
     * @param subgroupMasks The membership masks of each subgroup (i.e. which elements are in each subgroup).
     * @param bitOp A function which modifies the first operator by combining it with the second.
     * @param ignoreMissing If a.combine(b) results in a bitset which is not a subgroup, this is considered "Missing". If true, return -1. Else throw exception.
     * @return An array where if  entry(i,j) = k ; then the combination of the i'th and j'th subgroup is the k'th (all indices 0-based).
     */
    private static int[][] GenerateCombinations(int numSubgroups, BitSet[] subgroupMasks, IBitSetOperation bitOp, boolean ignoreMissing)
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
    static int GenerateCombination(int s1, int s2, BitSet[] subgroups, IBitSetOperation bitOp, boolean ignoreMissing)
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

    public void Validate(PrintStream out) throws Exception
    {
        // check identity: must be an element of each subgroup.
        out.println("Checking identity: \"" + ElementNames[0] + "\"");
        int nextClearBit = ElementMasks[0].nextClearBit(0);
        if (nextClearBit >= 0 && nextClearBit < NumSubgroups) throw new Exception("Trivial element is not a member of some subgroup(" + nextClearBit + "): " + ElementMasks[0]);

        // check subgroups: 1st must be trivial, last must have all subgroups.
        out.println("Checking subgroups: Count=" + NumSubgroups);

        BitSet trivialSubgroup = SubgroupMasks[0];
        int nextSetBit = trivialSubgroup.nextSetBit(1);
        if (!trivialSubgroup.get(0) || (nextSetBit >= 0 && nextSetBit < NumElements))
            throw new Exception("1st subgroup is not trivial");

        BitSet fullGroup = SubgroupMasks[SubgroupMasks.length-1];
        nextClearBit = fullGroup.nextClearBit(1);
        if (nextClearBit >= 0 && nextClearBit < NumElements) throw new Exception("Last subgroup is not the whole group.");
    }
}

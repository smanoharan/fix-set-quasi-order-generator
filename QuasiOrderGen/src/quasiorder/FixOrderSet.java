package quasiorder;

import java.util.*;

/**
 * Holds a set of relations.
 * Automatically handles duplicates.
 *  That is, creates a linked list of families for each relation.
 */
public class FixOrderSet
{
    public final Hashtable<FixOrder, ArrayList<BitSet>> FixOrderToFamilyMap;
    public final ArrayList<FixOrder> FixOrders;

    public FixOrderSet()
    {
        this.FixOrderToFamilyMap = new Hashtable<FixOrder, ArrayList<BitSet>>();
        this.FixOrders = new ArrayList<FixOrder>();
    }

    /**
     * Add a relation to this relation set.
     * If this relation already exists in this set, relation is not added again.
     *  However, the family mask will be added to the list of family masks for this relation.
     *
     * @param fixOrder The relation to add.
     * @param familyMask The familyMask which generated this relation.
     */
    public void Add(FixOrder fixOrder, BitSet familyMask)
    {
        if (FixOrderToFamilyMap.containsKey(fixOrder))
        {
            FixOrderToFamilyMap.get(fixOrder).add(familyMask);
        }
        else
        {
            ArrayList<BitSet> families = new ArrayList<BitSet>();
            families.add(familyMask);
            FixOrderToFamilyMap.put(fixOrder, families);
            FixOrders.add(fixOrder);
        }
    }

    /**
     * Build up the relation corresponding to this family-of-subgroups mask.
     *
     * @param inputGroup The processed input
     * @param familyMask A mask representing which subgroups are in this family.
     * @return The relation determined by this family.
     */
    public static BitSet BuildRelation(Group inputGroup, BitSet familyMask)
    {
        int len = inputGroup.NumElements;
        BitSet relation = new BitSet(len*len);

        for (int i=0;i<len;i++)
        {
            relation.set(FixOrderSet.ToSerialIndex(i, i, len)); // i <= i holds for all i

            for (int j=i+1;j<len;j++)
            {
                boolean ij = GroupUtil.isRelated(inputGroup.ElementMasks[i], inputGroup.ElementMasks[j], familyMask);
                boolean ji = GroupUtil.isRelated(inputGroup.ElementMasks[j], inputGroup.ElementMasks[i], familyMask);

                if (ij) relation.set(ToSerialIndex(i, j, len));
                if (ji) relation.set(ToSerialIndex(j, i, len));
            }
        }

        return relation;
    }

    /**
     * Convert a 2D index ((i,j) in a table) to a 1D index.
     *
     * @param i The row index
     * @param j The column index
     * @param rowLength The length of a row
     * @return The index as a 1D index.
     */
    public static int ToSerialIndex(int i, int j, int rowLength)
    {
        return i*rowLength + j;
    }

    /**
     * Sort the relations in this set by cardinality of the bit-sets
     */
    public void SortRelations()
    {
        Collections.sort(FixOrders);
    }


    /**
     * Generates the lattice of fix-set quasi-orders.
     * @return The bitset representing this lattice/relation.
     */
    public BitSet GenerateOverallQuasiOrder()
    {
        // start with the unique relations (ordered by number of set bits)
        int numRels = FixOrders.size();
        BitSet result = new BitSet(numRels*numRels);

        for(int i=0;i<numRels;i++)
        {
            result.set(ToSerialIndex(i,i,numRels));
            FixOrder eI = FixOrders.get(i);
            for(int j=i+1;j<numRels;j++)
            {
                // determine the order: card[i] >= card[j] is guaranteed by sorting.
                // if cardinality is equal then relations must be different (so a and b are not related).
                FixOrder eJ = FixOrders.get(j);
                if (eI.Cardinality != eJ.Cardinality)
                {
                    // only possible relation is (j,i) which occurs when j <= i;
                    BitSet jCopy = (BitSet)eJ.Relation.clone();
                    jCopy.and(eI.Relation);
                    if (jCopy.equals(eJ.Relation))
                        result.set(ToSerialIndex(j,i,numRels));
                }
            }
        }

        return result;
    }
}

package quasiorder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;

/**
 * Holds a set of relations.
 * Automatically handles duplicates.
 *  That is, creates a linked list of families for each relation.
 */
public class RelationSet
{
    public final Hashtable<BitSet, ArrayList<BitSet>> uniqRelations;

    public RelationSet()
    {
        this.uniqRelations = new Hashtable<BitSet, ArrayList<BitSet>>();
    }

    /**
     * Add a relation to this relation set.
     * If this relation already exists in this set, relation is not added again.
     *  However, the family mask will be added to the list of family masks for this relation.
     *
     * @param rel The relation to add.
     * @param familyMask The familyMask which generated this relation.
     */
    public void Add(BitSet rel, BitSet familyMask)
    {
        if (uniqRelations.containsKey(rel))
        {
            uniqRelations.get(rel).add(familyMask);
        }
        else
        {
            ArrayList<BitSet> families = new ArrayList<BitSet>();
            families.add(familyMask);
            uniqRelations.put(rel, families);
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
            relation.set(RelationSet.ToSerialIndex(i, i, len)); // i <= i holds for all i

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

}

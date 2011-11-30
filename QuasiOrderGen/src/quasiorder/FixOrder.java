package quasiorder;

import java.util.BitSet;

import static quasiorder.FixOrderSet.ToSerialIndex;

// Is a BitSet which is not expected to be modified any further,
//  so can be used to cache cardinality.
public class FixOrder implements Comparable<FixOrder>
{
    public final BitSet Relation;
    public final int Cardinality;
    public final boolean isFaithful;
    public final boolean isNormal;

    public static FixOrder FromRelation(BitSet relation, BitSet familyMask, Group inputGroup)
    {
        boolean isNormal = IsNormal(familyMask, inputGroup.IsSubgroupNormal);
        boolean isFaithful = IsFaithful(relation, inputGroup.NumElements);
        return new FixOrder(relation, isFaithful, isNormal);
    }

    public FixOrder(BitSet relation, boolean isFaithful, boolean isNormal)
    {
        this.Cardinality = relation.cardinality();
        this.Relation = relation;
        this.isFaithful = isFaithful;
        this.isNormal = isNormal;
    }

    static boolean IsFaithful(BitSet rel, int numElem)
    {
       int nextBit = rel.nextSetBit(1);
       return nextBit >= numElem || nextBit == -1;
    }

    static boolean IsNormal(BitSet familyMask, BitSet normalMask)
    {
        BitSet ft = (BitSet)familyMask.clone();
        ft.and(normalMask);
        return ft.equals(familyMask);
    }

    public int compareTo(FixOrder o)
    {
        return o.Cardinality - this.Cardinality; // this is a DESCENDING order!
    }

    @Override
    public boolean equals(Object other)
    {
        return ((other instanceof FixOrder) && this.Relation.equals(((FixOrder)other).Relation));
    }

    @Override
    public int hashCode()
    {
        return this.Relation.hashCode();
    }

    // requires i <= j
    public static void Perform2Swap(BitSet orig, int i, int j, int numElem)
    {

        // swap the two rows
        int iS = ToSerialIndex(i, 0, numElem);
        int jS = ToSerialIndex(j, 0, numElem);
        for (int x=0;x<numElem;x++)
            Swap(orig, iS+x, jS+x);

        // swap the two columns
        iS = ToSerialIndex(0, i, numElem);
        jS = ToSerialIndex(0, j, numElem);
        for(int y=0;y<numElem;y++)
            Swap(orig, y*numElem+iS, y*numElem+jS);

    }

    // Swap the elements at e1 and e2 in b
    private static void Swap(BitSet b, int e1, int e2)
    {
        boolean t = b.get(e1); b.set(e1, b.get(e2)); b.set(e2, t);
    }
}

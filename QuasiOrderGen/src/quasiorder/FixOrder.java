package quasiorder;

import java.util.BitSet;

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
}

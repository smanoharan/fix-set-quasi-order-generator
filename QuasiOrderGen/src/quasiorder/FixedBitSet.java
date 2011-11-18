package quasiorder;

import java.util.BitSet;

// Is a BitSet which is not expected to be modified any further,
//  so can be used to cache cardinality.
class FixedBitSet implements Comparable<FixedBitSet>
{
    public final BitSet Relation;
    public final int Cardinality;
    public final String Colour;

    public FixedBitSet(BitSet relation, String colour)
    {
        Colour = colour;
        this.Cardinality = relation.cardinality();
        this.Relation = relation;
    }

    public int compareTo(FixedBitSet o)
    {
        return o.Cardinality - this.Cardinality; // this is a DESCENDING order!
    }
}

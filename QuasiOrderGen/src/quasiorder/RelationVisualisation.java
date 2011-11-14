package quasiorder;

public class RelationVisualisation
{
    // TODO:
    //  Naive: Remove cycles first.
    //  Find reflexive reduction (remove a[[i][i])
    //  Then transitive reduction:
    //      For each edge (A-->B)
    //          For each vertex C:
    //              If A<=C && C<=B && (A!=C && B!=C) then
    //                  remove edge A-->B.


    // Better yet:
    //  output | tred | dot | toPDF ==> generates diagram as a pdf
}

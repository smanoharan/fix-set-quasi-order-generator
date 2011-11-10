ExportGroup:= function(f,g)

	local csg, first, es, cr, c;
	csg:=ConjugacyClassesSubgroups(g);

	PrintTo(f,"[\n\t[ [", List(g,String), "] ],\n\t[");

	first:=true;
	for cr in csg
	do
		es:=[];
		for c in cr
		do
			Add(es, List(c,String));
		od;

		if first then
			AppendTo(f, "\n\t\t", es);
			first:=false;
		else
			AppendTo(f, ",\n\t\t", es);
		fi;
	od;

	AppendTo(f,"\n\t]\n]\n");
end;

ExportGroup("E:/temp.txt",SymmetricGroup(4));


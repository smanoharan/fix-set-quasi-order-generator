homedata:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/data/g-";
homesp:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/spdata/g-";

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


ExportAllGroups := function(path, lb, ub)
	local logPath, i, counter, g, gid;	

	logPath:=Concatenation(path, "names.txt");
	LogTo(logPath);
	for i in [ lb .. ub ] do SmallGroupsInformation(i); od;
	LogTo();

	for i in [ lb .. ub ]
	do
        	counter:=1;
        	for g in AllSmallGroups(i)
        	do
                	gid:=Concatenation(String(i), "-", String(counter));
                	ExportGroup(Concatenation(path, gid, ".in"), g);
                	counter:=counter+1;
        	od;
	od;
end;

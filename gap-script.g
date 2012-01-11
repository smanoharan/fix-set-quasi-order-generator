homedata:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/data/g-";
homesp:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/spdata/g-";
homeW:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/wholedata/g-";
homeR:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/refData/g-";
homeRS:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/refData/skip/g-";
homeDih:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/dihData/g-";
homeCSV:= "/home/siva/summer-schol/src/groupsfixsetquasiorder/trunk/csvdata/g-";


ImageOf := function(ag, i)
	return Concatenation("\t\t\t[ \"", String(i), "\", \"", String(Image(ag, i)), "\" ]");
end; 

EmptyAuto := function(f,g)
	AppendTo(f,"\t[ ]\n");	
end;

ExportTableOf := function(f,g)
	local Outer, p, gi, firstP, firstG;
	Outer:=Filtered(AutomorphismGroup(g), i -> not IsInnerAutomorphism(i));

	AppendTo(f,"\t[");	
	firstP:=true;
	for p in Outer do
		if firstP then 
			firstP:=false;
		else
			AppendTo(f,",");
		fi;

		AppendTo(f,"\n\t\t[\n");
		
		firstG:=true;
		for gi in g do
			if firstG then 
				firstG:=false;
			else
				AppendTo(f,",\n");	
			fi;
			AppendTo(f,ImageOf(p, gi));
		od;
		AppendTo(f,"\n\t\t]");
	od;
	AppendTo(f,"\n\t]");
end;

ExportWithGroup:= function(f,g,h,i)

	local csg, first, es, cr, c;
	csg:=ConjugacyClassesSubgroups(g);
	
	if i(csg) then
		return;
	fi;

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

	AppendTo(f,"\n\t],\n");
	h(f,g);
	AppendTo(f, "\n]\n");
end;

ExportGroup:=function(f,g)
	ExportWithGroup(f, g, ExportTableOf, csg -> false );
	# ExportWithGroup(f, g, EmptyAuto, csg -> Length(csg) <= 20);
end;

CountExportAllGroups := function(lb, ub)
	local procCount, skipCount, total, i, g;	

	procCount:=0;
	skipCount:=0;
	total:=0;
	
	for i in [ lb .. ub ]
	do
		Print(String(i), "\n");
        	for g in AllSmallGroups(i)
        	do
			if (Length(ConjugacyClassesSubgroups(g)) <= 20) then
				procCount:=procCount+1;
			else
				skipCount:=skipCount+1;
			fi;
			total:=total+1;
        	od;
	od;
	Print("Proc: ", String(procCount), "; Skip: ", String(skipCount), "; Total: ", String(total),  "\n");
end;

ExportAllGroups := function(path, lb, ub, doLog)
	local logPath, i, counter, g, gid;	

	if doLog then
		logPath:=Concatenation(path, "names.txt");
		LogTo(logPath);
		for i in [ lb .. ub ] do SmallGroupsInformation(i); od;
		LogTo();
	fi;

	for i in [ lb .. ub ]
	do
		Print(String(i), "\n");
        	counter:=1;
        	for g in AllSmallGroups(i)
        	do
                	gid:=Concatenation(String(i), "-", String(counter));
                	ExportGroup(Concatenation(path, gid, ".in"), g);
                	counter:=counter+1;
        	od;
	od;
end;

ExportAllDihedralGroups := function(lb, ub)
	local i, j;

	for i in [ lb .. ub ]
	do
		j:=i*2;
		ExportGroup(Concatenation(homeDih, "D-", String(j), ".in"), DihedralGroup(j)); 
	od;
end;

Export8FactorDihedralGroups := function(lb, ub)
	local i, j;
	for i in [ lb .. ub ]
	do
		j := i*8 + 4;
		ExportGroup(Concatenation(homeDih, "D-", String(j), ".in"), DihedralGroup(j));
	od;
end; 

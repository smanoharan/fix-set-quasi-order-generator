README - Fix Order Generator
=========================================================
Requirements :
-	A GNU/Linux OS (more specifically, an OS which supports bash scripting). 
-	The latest JRE (Java Runtime Environment). 

-	To get this software to work on Windows, the bash scripts (*.sh files) 
	must be translated into Windows .bat format. 
-	The actual source code is in Java and will work on all platforms have a JVM.
=========================================================
Installation:
- 	Download and install :
	- 	GAP System
	- 	GraphViz
	- 	JRE

	- 	The first two must be installed such that the PATH variable 
		contains their directories, so that they can be invoked by name.

-	Unzip the source file into a directory (referred as FOG_ROOT from here onwards)
	- 	The zip file already contains the compiled class files, so no need to recompile.
	- 	TODO

=========================================================
Running the Fix Order Generator:
-	Use GAP to generate input data 
	-	run GAP
	-	execute ExtractGroup(<LOCATION>, <GROUP>); with the desired GROUP, which will be placed in LOCATION.
		- 	For example: ExtractGroup("/home/me/dir/", SymmetricGroup(3));
		-	see gap-script.g in FOG_ROOT for other Extract functions and examples.

- 	Process the data using FOG
	-	 execute FOG_ROOT/processEach.sh <LOCATION> <OUTPUT-type>; 
		This will process the files in LOCATION and produce *.OUTPUT-type files.
		- 	For example: processEach.sh home/me/dir png
		-	Output type must be one supported by the dot tool of GraphViz (read the man page for dot)
	- This will result in images in the LOCATION\out folder.
- Done.


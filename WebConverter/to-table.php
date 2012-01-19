<?php 
	require('common.php'); 
	$showIndex = true;
	
	for ($i=3;$i<$argc;$i++)
		if ($argv[$i]=="ni") $showIndex = false;

	$groupType = $argv[2];
	$maxOrder = $argv[3];
	$startID = $argv[4];	
	$title = getPageTitle($groupType, $maxOrder);
?>
<!DOCTYPE html>
<html>
<head>
	<title><?php echo $title; ?></title>
	<link rel="stylesheet" type="text/css" href="table.css" />
</head>
<body>
<?php
	echo "<span class='floatRight'><a href='index.html'><strong>Back to Home</strong></a></span>\n";
	echo "<a name='top'><h1>$title</h1></a>\n<hr>\n";
	if ($showIndex)
	{
		echo "<h2>Index:</h2>\n<ul>\n";
	
		echo"<h4><li><a href='#Count'>Number of fix-orders</a><ul>\n";
		echoIndex($startID, $maxOrder, $groupType, "count");
		echo "</ul></li></h4>\n<h4><li><a href='#Props'>Properties of fix-orders</a><ul>\n";
		echoIndex($startID, $maxOrder, $groupType, "props");
		echo "</ul></li></h4>\n</ul>\n<hr>\n";
	}

	$file = fopen($argv[1], "r");
	$tables = processCSV($file);
	fclose($file);
	
	echo "<a name='Count'><h2>Number of Fix-Orders:</h2></a>\n<br/>\n";

	$headers = buildCountTableHeaders($showIndex);
	foreach ($tables as $id => $table) 
		echoTable($table, $id, $groupType, $headers, 0, "count", $showIndex);

	echo "<hr>\n<a name='Props'><h2>Properties of Fix-Orders:</h2></a>\n<br/>\n";

	$headers = buildPropTableHeaders($showIndex);
	foreach($tables as $id => $table)
		echoTable($table, $id, $groupType, $headers, 1, "props", $showIndex);
	unset($table);
?>
</body>
</html>

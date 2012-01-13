<?php 
	require('common.php'); 
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
	echo "<h1>$title</h1>\n<hr>\n";
	echo "<h2>Index:</h2>\n<ul>\n";
	
	echo"<h4><li><a href='#Count'>Number of fix-orders</a><ul>\n";
	echoIndex($startID, $maxOrder, $groupType, "count");
	echo "</ul></li></h4>\n<h4><li><a href='#Props'>Properties of fix-orders</a><ul>\n";
	echoIndex($startID, $maxOrder, $groupType, "props");
	echo "</ul></li></h4>\n</ul>\n<hr>\n";

	$file = fopen($argv[1], "r");
	$tables = processCSV($file);
	fclose($file);
	
	echo "<a name='Count'><h2>Number of Fix-Orders:</h2></a>\n<br/>\n";

	$headers = buildCountTableHeaders();
	foreach ($tables as $id => $table) 
		echoTable($table, $id, $groupType, $headers, 0, "count");

	echo "<hr>\n<a name='Props'><h2>Properties of Fix-Orders:</h2></a>\n<br/>\n";

	$headers = buildPropTableHeaders();
	foreach($tables as $id => $table)
		echoTable($table, $id, $groupType, $headers, 1, "props");
	unset($table);
?>
</body>
</html>

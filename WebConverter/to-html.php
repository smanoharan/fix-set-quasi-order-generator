<?php
	$groupType = "All";
	$maxOrder = 15;
	$startID = 1;

	function echoIndex($start, $end, $type)
	{
		echo "<h2>Index:</h2>\n<ul>\n";
		for ($i=$start;$i<=$end;$i++)
		{
			$tableName = tableIdToName($type, $i);
			echo "\t<li><a href='#table$i'>$tableName</a></li>\n";
		}
		unset($i);
		echo "</ul>\n";	
	}

	function echoTable($data, $id, $type, $headers)
	{
		$tableName = tableIdToName($type, $id);
		echo "<a name='table$id'><h2>$tableName</h2></a>\n<table>";

		echoHeaders($headers);

		foreach ($data as $row)
		{
			echo "<tr>";
			foreach ($row as $cell) 
			{
				echo "<td>$cell</td>";
			}
			unset($cell);
			echo "</tr>\n";
		}
		unset($row);
		echo "</table><br/>";
	}

	function gapIDtoOrder($gapID)
	{
		$parts = explode("-", $gapID);
		return $parts[0];
	}

	function processCSV($file)
	{
		$result = array();
		$lastTableID = 1; // This assumes that all datasets have a table with id=1 as the first.
		$curTable = array();
		
		while( ($row = fgetcsv($file)) !== FALSE)
		{
			$data = processRow($row);
			$curID = gapIDtoOrder($row[0]);
			if ($lastTableID != $curID)
			{
				$result[$lastTableID] = $curTable;
				$curTable = array($data);
				$lastTableID = $curID;
			} 
			else
			{
				$curTable[] = $data;
			}
		}

		$result[$lastTableID] = $curTable;	
		return $result;
	}

	// headers: GAP-ID; 
	$NO = "Number of";

	function buildTableHeaders()
	{
		$overall = array( 
			"Group" => 3, "Distinct fix-orders" => 4, 
			"Fix-orders when collapsed by group automorphisms" => 4);

		$normal = array(
			"GAP-ID", "subgroups", "conjugacy classes of subgroups", 
			"all", "faithful only", "normal only", "faithful-normal only", 	// distinct
			"all", "faithful only", "normal only", "faithful-normal only" 	// automorphism
			);

		return array($overall, $normal);
	}
	
	function echoHeaders($headers)
	{
		echo "<thead>\n<tr>\n";

		// overall:
		foreach ($headers[0] as $name => $colspan)
			echo "\t<th colspan='$colspan'>$name</th>\n";
		echo "</tr><tr>";

		// normal
		/*foreach ($headers[22] as $name => $colspan)
		{
			$colstr = ($colspan == 1 ? "rowspan='2'" : " colspan='$colspan'" );
			echo "\t<th $colstr>$name</th>\n";
		}
		echo "</tr><tr>";*/

		// subdivision
		foreach ($headers[1] as $name)
			echo "\t<th>$name</th>\n";
		echo "</tr>\n</thead>\n";
		unset($name);
		unset($colspan);
	}

	function processRow($row)
	{
		// Convert the CSV array of fields into html content, to be placed into table cells
		// Here is where hyperlinking etc can take place.
		//	for ($c=0; $c<$num; $c++) echo $data[$c] . "\n";
		

		array_splice($row,11);
		return $row;
	}

	function tableIdToName($type, $id)
	{
		return "$type groups of order $id";
	}

	function getPageTitle($type, $max)
	{
		return "Table of Results - $type groups of order upto $max";
	}
	
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
	echoIndex($startID, $maxOrder, $groupType);
	echo "<hr>\n";

	$file = fopen($argv[1], "r");
	$tables = processCSV($file);
	fclose($file);
	
	$headers = buildTableHeaders();
	foreach ($tables as $id => $table) 
		echoTable($table, $id, $groupType, $headers);
	unset($table);
?>
</body>
</html>

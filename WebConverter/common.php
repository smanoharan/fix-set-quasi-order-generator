<?php
	$groupType = "All";
	$maxOrder = 15;
	$startID = 1;

	function echoIndex($start, $end, $type, $aTarget)
	{
		for ($i=$start;$i<=$end;$i++)
		{
			$tableName = tableIdToName($type, $i);
			echo "\t<li><a href='#$aTarget$i'>$tableName</a></li>\n";
		}
		unset($i);
	}

	function echoTable($data, $id, $type, $headers, $rowGroupID, $aTarget)
	{
		$tableName = tableIdToName($type, $id);
		echo "<a name='$aTarget$id'><h3 class='tableTitle'>$tableName:</h3></a>\n<table>";

		echoHeaders($headers);

		foreach ($data as $row)
		{
			echo "<tr>";
			//var_dump($row);
			foreach ($row[$rowGroupID] as $cell) 
			{
				echo "<td>$cell</td>";
			}
			unset($cell);
			echo "</tr>\n";
		}
		unset($row);
		echo "</table><br/><br/><br/>";
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

	function buildCountTableHeaders()
	{
		$overall = array( 
			"Group" => 4, "Number of distinct fix-orders" => 4, 
			"Number of fix-orders when collapsed by group automorphisms" => 4);

		$normal = array(
			"GAP-ID", "Lattice Diagrams", "subgroups", "conjugacy classes of subgroups", 
			"all", "faithful only", "normal only", "faithful-normal only", 	// distinct
			"all", "faithful only", "normal only", "faithful-normal only" 	// automorphism
			);

		return array($overall, $normal);
	}
	
	function buildPropTableHeaders()
	{
		$overall = array(
			"Group" => 2, "All fix-orders" => 2, "Faithful only fix-orders" => 2, 
			"Normal only fix-orders" => 2, "Faithful-normal only fix-orders" => 2 ); 

		$normal = array("GAP-ID", "Lattice Diagrams");
		for ($i=0;$i<4;$i++) { $normal[] = "Modular?"; $normal[] = "Distributive?"; }
		unset($i);

		return array($overall, $normal);
	}
	
	function echoHeaders($headers)
	{
		echo "<thead>\n<tr>\n";

		// overall:
		foreach ($headers[0] as $name => $colspan)
			echo "\t<th colspan='$colspan'>$name</th>\n";
		echo "</tr>\n<tr>";

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
		$counts = $row;
		$id = $counts[0];
		$props = array_merge(array($id), array_splice($counts, 11)); // prepend the gap-id
	
		array_splice($props, 9);	
		for($i=1;$i<count($props);$i++) $props[$i] = trueFalseToYesNo($props[$i]);
		unset($i);

		$latLink_c = "<a href='latdiag.html?$id?count'>View Diagrams</a>\n";
		$latLink_p = "<a href='latdiag.html?$id?props'>View Diagrams</a>\n";
		array_splice($counts, 1, 0, array($latLink_c));		
		array_splice($props, 1, 0, array($latLink_p));		

		return array($counts, $props); 
	}

	function trueFalseToYesNo($str)
	{
		if ($str == "true") return "Yes";
		else if ($str == "false") return "No";
		else throw new Exception("Not True or false");
	}

	function tableIdToName($type, $id)
	{
		return "$type groups of order $id";
	}

	function getPageTitle($type, $max)
	{
		return "Table of Results - $type groups of order upto $max";
	}
?>
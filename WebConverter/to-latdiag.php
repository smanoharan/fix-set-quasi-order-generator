<?php require('common.php'); ?>
<!DOCTYPE html>
<html>
<head>
	<title>Fix-Orders</title>
	<link rel="stylesheet" type="text/css" href="table.css" />
	<script type="text/javascript">
		
	function processURL() 
	{
		var params = document.URL.split("?");
		
		if (params.length < 2) alert("No group specified for lattice diagrams");		
		else 
		{
			var gID = params[1];
			var order = gID.split("-")[0];
			document.getElementById("return-top").href = "index.html#" + params[2] + order;
			document.getElementById("return-bot").href = "index.html#" + params[2] + order;
			
			var title = "Fix-orders of group " + gID;
			document.title = title;
			document.getElementById("titleH1").innerHTML = title;
		
			var cells = document.getElementsByTagName("td");
			for (var i=0;i<cells.length;i++)
			{
				var ci = cells[i];
				var imgElem = document.createElement('img');
				imgElem.src = "img/g-" + gID + "." + ci.id;
				ci.appendChild(imgElem);
			}
		}
	}

	window.onload = processURL;
	</script>
</head>
<body>
<h1 id="titleH1">Fix Orders</h1>
<hr>
<a name='latdiagTable'><h3 class='tableTitle'>Lattice Diagrams:</h3></a>
<a class='floatRight' id='return-top' href="index.html"><h3 style="display:inline;">Back to index</h3></a>
<table>
<thead class="hasRowHeaders"><tr>
	<th colspan="2">Type</th>
	<?php
		$colHeaders = array("All", "Faithful only", "Normal only", "Faithful Normal only");
		foreach ($colHeaders as $col) echo "<th>$col</th>";
	?>
</tr></thead>
<tbody>
	<?php
		$cols = array("all", "faithful", "normal", "faithful-normal");
		$rows = array(
			"" => "<th colspan='2'>Distinct fix-orders</th>", 
			"col1." => "<th rowspan=2>Automorphism collapsed</th><th>Before Collapsing</th>", 
			"col3." => "<th>After Collapsing</th>");

		$ext = "svg";
		foreach ($rows as $row => $header)
		{
			echo "\t<tr>$header";
			foreach ($cols as $col) echo "<td id='$row$col.$ext'></td>";
			echo "</tr>\n";
		}
	?>
</tbody>
</table>
<hr>
<a id='return-bot' href="index.html">Back to index</a>
</body>
</html>

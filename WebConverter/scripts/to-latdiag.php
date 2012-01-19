<?php require('common.php'); ?>
<!DOCTYPE html>
<html>
<head>
	<title>Fix-Orders</title>
	<link rel="stylesheet" type="text/css" href="table.css" />
	<script type="text/javascript" src="jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="lat.js"></script>
</head>
<body>
<h1 id="titleH1">Fix Orders</h1>
<hr>
<?php 
	$colHeaders = array("All", "Faithful only", "Normal only", "Faithful Normal only");
?>
<h3>Display options:</h3>
<form name="display">
<table>
<tr>
	<th>Show columns:</th>
	<td>
	<?php
		$colid = 0;
		foreach($colHeaders as $col)
		{
			$id = "col$colid";
			$onclick = "setVis()";
			echo "<input type=checkbox name='c$id' id='$id' onclick=\"$onclick\" checked> $col ";
			$colid++;
		} 
	?>
	</td>
</tr>
<tr>
	<th>Show rows:</th>
	<td>	
		<?php
			$heads = array('Distinct fix-orders', 'Before collapsing', 'After collapsing');
			$rowid = 0;
			foreach($heads as $h)
			{
				$id = "row$rowid";
				$onclick = "setVis()";
				echo "<input type=checkbox name='c$id' id='$id' onclick=\"$onclick\" checked> $h ";
				$rowid++;
			}
		?>
	</td>
</tr>
</table>
</form>
<hr>
<a name='latdiagTable'><h3 class='tableTitle'>Lattice Diagrams:</h3></a>
<a class='floatRight' id='return-top' href="index.html"><h3 style="display:inline;">Back to index</h3></a>
<table>
<thead class="hasRowHeaders"><tr>
	<th colspan="2">Type</th>
	<?php
		$colid = 0;
		foreach ($colHeaders as $col) 
		{
			echo "<th class='col$colid'>$col</th>";
			$colid++;
		}
	?>
</tr></thead>
<tbody>
	<?php
		$cols = array("all", "faithful", "normal", "faithful-normal");
		$rows = array(
			"" => "<th colspan='2' class='row0'>Distinct fix-orders</th>", 
			"col1." => "<th rowspan=2 id='autoH'>Automorphism collapsed</th><th class='row1'>Before Collapsing</th>", 
			"col3." => "<th class='row2'>After Collapsing</th>");

		$ext = "svg";
		$rowid = 0;
		foreach ($rows as $row => $header)
		{
			echo "\t<tr>$header";
			$colid = 0;
			foreach ($cols as $col) 
			{
				echo "<td id='$row$col.$ext' class='row$rowid col$colid'></td>";
				$colid++;
			}
			echo "</tr>\n";
			$rowid++;
		}
	?>
</tbody>
</table>
<hr>
<a id='return-bot' href="index.html">Back to index</a>
</body>
</html>

<?php

$row = 1;
$file = fopen($argv[1], "r");
while( ($data = fgetcsv($file)) !== FALSE)
{
	$num = count($data);
	echo "$num fields in line $row \n";
	$row++;

	for ($c=0; $c<$num; $c++) echo $data[$c] . "\n";
	echo "\n\n";
}
fclose($file);
?>

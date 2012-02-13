php -f scripts/to-table.php -- data/general-15.csv All 15 1 all15 > out/all15.html
php -f scripts/to-table.php -- data/selected.csv Selected 31 16 selected > out/selected.html 
php -f scripts/to-table.php -- data/dih.sorted.csv Dihedral 126 1 dih ni > out/dih.html
php -f scripts/to-table.php -- data/symalt.csv Symmetric/Alternating 120 1 symalt ni > out/symalt.html 
php -f scripts/to-latdiag.php > out/latdiag.html
zip export/no-images.zip -q out/*
ls -l ./ export

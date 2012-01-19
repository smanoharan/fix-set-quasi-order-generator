php -f to-table.php -- data/general-15.csv All 15 1 > out/all15.html
php -f to-table.php -- data/selected.csv Selected 31 16 > out/selected.html 
php -f to-table.php -- data/dih.sorted.csv Dihedral 126 1 ni > out/dih.html
php -f to-table.php -- data/symalt.csv Symmetric/Alternating 120 1 ni > out/symalt.html 
php -f to-latdiag.php > out/latdiag.html
zip export/no-images.zip -q out/
ls -l ./ export

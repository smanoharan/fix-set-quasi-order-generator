echo "Cleaning input: "
for file in $1/*.in
do
	perl -pi -e 's/\\\n//g' $file
	perl -pi -e 's/"<identity> of ..."/"1"/g' $file
done

egrep "^[[:space:]]+[0-9]+|There" $1/g-names.txt > $1/group-names.txt 
rm $1/g-names.txt

echo "Processing each input file:"
for file in $1/*.in
do
	echo ""
	echo "Processing: $file"
	./generate.sh $file
	
done

echo "Generating output files:"
for file in $1/*.lat
do
	echo ""
	echo "Drawing: $file"
	tred $file | dot -T$2 > ${file%.*}.$2
done

mkdir $1/out
mv $1/*.$2 $1/out/
mkdir $1/md
mv $1/*.md $1/md/
grep false $1/md/* | grep true | wc
mkdir $1/out/full/
cp $1/out/*.all.$2 $1/out/full/

echo "Done."

echo "Cleaning input: "
for file in $1/*.in
do
	perl -pi -e 's/\\\n//g' $file
	perl -pi -e 's/"<identity> of ..."/"1"/g' $file
done

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
	tred $file | dot | gvcolor | dot -T$2 > ${file%.*}.$2
done

mv $1/*.$2 $1/out/

echo "Done."

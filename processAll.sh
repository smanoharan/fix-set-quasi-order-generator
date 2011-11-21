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
	
	cp $file ${file%.*}.all.in	
	./generate.sh ${file%.*}.all.in
	
	cp $file ${file%.*}.normal.in	
	./generate.sh ${file%.*}.normal.in -n
	
	cp $file ${file%.*}.faithful.in	
	./generate.sh ${file%.*}.faithful.in -f
	
	cp $file ${file%.*}.normal-faithful.in	
	./generate.sh ${file%.*}.normal-faithful.in -f -n
	
done

echo "Generating output files:"
for file in $1/*.full.lat
do
	echo ""
	echo "Drawing: $file"
	tred $file | dot | gvcolor | dot -T$2 > ${file%.*}.$2
done

mv $1/*.$2 $1/out/

echo "Done."

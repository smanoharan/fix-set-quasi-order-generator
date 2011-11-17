echo "Cleaning up files..."
./cleanInput.sh $1n

echo "Processing each input file:"
for file in $1/*.in
do
	echo ""
	echo "Processing: $file"
	./generate.sh $file
	tred ${file%.*}.full.lat | dot -T$2 > ${file%.*}.$2
done

echo "Done."

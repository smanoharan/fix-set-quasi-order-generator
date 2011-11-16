echo "Cleaning up files..."
./cleanInput.sh $1.in

echo "Processing each input file:"
for file in $1/*.in
do
	echo ""
	echo "Processing: $file"
	./generate.sh $file
done

echo "Done."

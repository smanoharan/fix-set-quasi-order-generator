./generate.sh $1
tred $1.lat | dot | gvcolor | dot -Tpdf > $1.pdf

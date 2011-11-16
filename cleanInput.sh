perl -pi -e 's/\\\n//' $1/*
perl -pi -e 's/"<identity> of ..."/"1"/' $1/*
perl -pi -e 's/*//' $1/*


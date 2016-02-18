max=${1-10}
for i in `seq $max`
do
    echo "echo $i"
    >&2 echo "echo error $i"
done
exitcode=${2-0}
exit $exitcode

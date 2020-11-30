read -p "Do you really want to delete all database records? (y/n):" YN
if [ "${YN}" = "y" ]
then
	cnt=`pgrep java | wc -l`
	if [ $cnt = 0 ]
	then
		echo "A Java process is running, abort."
	else
		sbt update clean
		rm *.db
		rm -r logs
		rm -r target
		rm -r project/project
		rm -r project/target
	fi
fi

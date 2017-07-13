#!/bin/bash
# relase sql2java.jar and .vm files modified to master branch
exit_on_error(){
	if [  $? -ne 0 ]
	then
		echo "exit for error:$1 " 
		exit -1
	fi
}
if [ ! -d build ]
then
	mkdir -p build
	exit_on_error
fi
sql2java_jar=lib/sql2java.jar
echo update $sql2java_jar to master branch
git checkout master
git checkout dev $sql2java_jar
exit_on_error
echo update manager.java.vm to master branch
git checkout dev src/templates/velocity/java5/perschema/manager.java.vm
exit_on_error
git checkout dev
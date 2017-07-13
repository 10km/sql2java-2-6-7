#!/bin/bash
# build source java file and update .class file to sql2java.jar
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
if [ -z "$(which javac)" ] 
then
	echo not found javac
	exit -1
fi
column="net/sourceforge/sql2java/Column"
sql2java_jar=lib/sql2java.jar
echo comiple $column.java
pushd sql2java.jar.src
javac -verbose -target 1.6 -g -cp ../$sql2java_jar -d ../build -encoding utf-8 $column.java
exit_on_error
cp -fr --parents $column.java ../build
popd
echo update $column.class and source to $sql2java_jar
pushd build
jar uf ../$sql2java_jar $column.class $column.java
exit_on_error
popd
cp release.sh build -fr
 
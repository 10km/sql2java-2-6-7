# sql2java-2-6-7 改进版

[master](../../tree/master)分支与原版的[sql2java 2.6.7][1]保持接口一致.

此分支是基于[master](../../tree/master)分支的改进版本,此分支不会与master合并 

## 使用方式改进

sql2java 原本的使用方式是修改 `src/config/sql2java.properties` 文件中的参数来定制生成代码，生成的代码固定在`src/java`下.

此分支修改了 build.xml 中,允许使用者在不修改sql2java代码的情况下完成代码生成，提供更好的项目管理的方便性。

可以通过命令行参数来指定生成代码的位置,properties文件位置,数据库驱动(jdbc driver)位置。
### ant
ant 命令行执行 builx.xml 脚本示例如下：
    
	cd sql2java-2-6-7
	ant rebuild \
		-Dsql2java-config=../sql2java.properties \
		-Dgenerated-src=../src/main/java \
		-Ddriver-jar=../lib/mysql-connector-java-5.1.43-bin.jar
`driver-jar`，`driver-lib`是新增加的参数，用于指定jdbc driver.

### maven
[pom.xml](pom.xml)提供了 maven 命令行执行 build.xml 的支持,使用方式如下：

	cd sql2java-2-6-7
	mvn  generate-sources \
		-Dsql2java-config=../sql2java.properties \
		-Dgenerated-src=../src/main/java \
		-Ddriver-jar=../lib/mysql-connector-java-5.1.43-bin.jar

**注意:**

上述参数指定的路径以及properties文件中涉及路径的参数，如果是相对路径，则都以 sql2java 根目录为基准。

比如：

sql2java-config指定的properties文件可以在任何位置,但是properties文件中在用 `codewriter.destdir`指定生成代码的位置时，则还是以sql2java根目录为基准，例如:

	codewriter.destdir=../src/main
	# 指定为 sql2java 同级的src/main为生成代码的路径
	# 实际生成的代码在 ../src/main/java
	# 这里的值应该与-Dgenerated-src中指定的值保持同步(少java)

## 应用示例

参见应用sql2java-custom生成ORM代码的项目：

facelog :[https://gitee.com/l0km/facelog/tree/master/db](https://gitee.com/l0km/facelog/tree/master/db)

facedb :[https://gitee.com/l0km/facedb/tree/master/db](https://gitee.com/l0km/facedb/tree/master/db)

## author
	GuYaDong 10km0811@sohu.com



[1]:https://nchc.dl.sourceforge.net/project/sql2java/sql2java-distribution/sql2java%202.6.7/sql2java-2-6-7.zip

# sql2java-2-6-7开发分支

此分支是基于[master](../../tree/master)分支的开发分支,用于修改sql2java.jar包中问题，此分支不会与master合并

更新后的jar包手工更新到master分支，更新方式：

	# 切换到 master branch
	git checkout master
	git pull
	# 检出 dev branch 的 sql2java.jar
	git checkout dev lib/sql2java.jar
 	git add lib/sql2java.jar
	git commit -m "update lib/sql2java.jar"
	# 推送到远程仓库
	git push

# author
	GuYaDong 10km0811@sohu.com

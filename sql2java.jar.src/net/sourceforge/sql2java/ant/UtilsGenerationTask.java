/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java.ant;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.sql2java.Main;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class UtilsGenerationTask extends Task {

	private String propertyFile;

	public void execute() throws BuildException {
		System.out.println("UtilsGenerationTask: " + propertyFile);
		String args[] = {propertyFile};
		Map map = new HashMap();
		map.put("check.database", "false");
		map.put("write.only.per.schema.templates", "true");
		Main.main(args, map);
	}

	public void setPropertyFile(String msg) {
		propertyFile = msg;
	}
}
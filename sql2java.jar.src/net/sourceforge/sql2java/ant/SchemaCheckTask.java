/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java.ant;

import java.io.PrintStream;
import java.util.HashMap;
import net.sourceforge.sql2java.Main;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SchemaCheckTask extends Task {
	private String propertyFile;

	public void execute() throws BuildException {
		System.out.println("SchemaCheckTask: " + this.propertyFile);
		String[] args = new String[]{this.propertyFile};
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("check.only.database", "true");
		Main.main((String[]) args, map);
	}

	public void setPropertyFile(String msg) {
		this.propertyFile = msg;
	}
}
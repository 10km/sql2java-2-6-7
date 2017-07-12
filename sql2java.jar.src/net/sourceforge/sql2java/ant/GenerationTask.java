/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java.ant;

import java.io.PrintStream;
import net.sourceforge.sql2java.Main;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GenerationTask extends Task {

	private String propertyFile;

	public void execute() throws BuildException {
		System.out.println("GenerationTask: " + propertyFile);
		String args[] = {propertyFile};
		Main.main(args);
	}

	public void setPropertyFile(String msg) {
		propertyFile = msg;
	}
}
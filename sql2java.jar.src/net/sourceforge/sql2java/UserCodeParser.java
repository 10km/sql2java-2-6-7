/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;

public class UserCodeParser {
	private static final String START = "// ";
	private static final String BLOCK_BEGIN = "+";
	private static final String BLOCK_END = "-";
	private static final String LINE_SEP = System.getProperty("line.separator");
	private Hashtable codeHash;
	private String filename;
	private boolean isNew;

	public UserCodeParser(String filename) throws Exception {
		this.parse(filename);
	}

	public String getFilename() {
		return this.filename;
	}

	public boolean isNew() {
		return this.isNew;
	}

	public void parse(String parsedFileName) throws Exception {
		this.codeHash = new Hashtable();
		boolean inBlock = false;
		String blockName = null;
		StringBuffer code = new StringBuffer();
		this.isNew = true;
		File file = new File(parsedFileName);
		if (file.exists()) {
			this.filename = parsedFileName;
			this.isNew = false;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				if (inBlock) {
					code.append(line).append(LINE_SEP);
				}
				if (line.indexOf("// ") != -1) {
					if (inBlock) {
						if (line.equals("// " + blockName + "-")) {
							this.codeHash.put(blockName, code.toString());
							inBlock = false;
						}
					} else {
						blockName = this.parseName(line);
						if (!"".equals(blockName)) {
							inBlock = true;
							code.setLength(0);
							code.append(line).append(LINE_SEP);
						}
					}
				}
				line = reader.readLine();
			}
			reader.close();
		}
	}

	private String parseName(String line) {
		int startPos = line.indexOf("// ");
		if (startPos == -1) {
			return "";
		}
		if ((startPos += "// ".length()) >= line.length() + 1) {
			return "";
		}
		int endPos = line.lastIndexOf("+", startPos);
		if (endPos != line.length() - "+".length()) {
			return "";
		}
		String name = line.substring(startPos, endPos);
		return name.trim();
	}

	public boolean hasBlock(String name) {
		return this.codeHash.get(name) != null;
	}

	public String getBlock(String name) {
		String code = null;
		if (name != null) {
			code = (String) this.codeHash.get(name);
		}
		if (code == null) {
			code = this.generateNewBlock(name);
			this.codeHash.put(name, code);
		}
		return code;
	}

	public String[] getBlockNames() {
		String[] list = new String[this.codeHash.size()];
		int i = 0;
		Enumeration e = this.codeHash.keys();
		while (e.hasMoreElements()) {
			list[i++] = (String) e.nextElement();
		}
		return list;
	}

	private String generateNewBlock(String name) {
		StringBuffer str = new StringBuffer(512);
		str.append("// ");
		str.append(name);
		str.append("+");
		str.append(LINE_SEP).append(LINE_SEP);
		str.append("// ");
		str.append(name);
		str.append("-");
		return str.toString();
	}
}
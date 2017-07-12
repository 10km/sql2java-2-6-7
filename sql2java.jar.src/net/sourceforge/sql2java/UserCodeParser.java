/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java;

import java.io.*;
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
		parse(filename);
	}

	public String getFilename() {
		return filename;
	}

	public boolean isNew() {
		return isNew;
	}

	public void parse(String parsedFileName) throws Exception {
		codeHash = new Hashtable();
		boolean inBlock = false;
		String blockName = null;
		StringBuffer code = new StringBuffer();
		isNew = true;
		File file = new File(parsedFileName);
		if (file.exists()) {
			filename = parsedFileName;
			isNew = false;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (inBlock)
					code.append(line).append(LINE_SEP);
				if (line.indexOf("// ") == -1)
					continue;
				if (inBlock) {
					if (line.equals("// " + blockName + "-")) {
						codeHash.put(blockName, code.toString());
						inBlock = false;
					}
					continue;
				}
				blockName = parseName(line);
				if (!"".equals(blockName)) {
					inBlock = true;
					code.setLength(0);
					code.append(line).append(LINE_SEP);
				}
			}

			reader.close();
		}
	}

	private String parseName(String line) {
		int startPos = line.indexOf("// ");
		if (startPos == -1)
			return "";
		startPos += "// ".length();
		if (startPos >= line.length() + 1)
			return "";
		int endPos = line.lastIndexOf("+", startPos);
		if (endPos != line.length() - "+".length()) {
			return "";
		} else {
			String name = line.substring(startPos, endPos);
			return name.trim();
		}
	}

	public boolean hasBlock(String name) {
		return codeHash.get(name) != null;
	}

	public String getBlock(String name) {
		String code = null;
		if (name != null)
			code = (String) codeHash.get(name);
		if (code == null) {
			code = generateNewBlock(name);
			codeHash.put(name, code);
		}
		return code;
	}

	public String[] getBlockNames() {
		String list[] = new String[codeHash.size()];
		int i = 0;
		for (Enumeration e = codeHash.keys(); e.hasMoreElements();)
			list[i++] = (String) e.nextElement();

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
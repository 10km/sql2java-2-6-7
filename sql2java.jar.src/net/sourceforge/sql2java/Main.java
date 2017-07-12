/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import net.sourceforge.sql2java.CodeWriter;
import net.sourceforge.sql2java.ConfigHelper;
import net.sourceforge.sql2java.Database;

public class Main {
	private static Properties prop;

	public static void main(String[] argv) {
		Main.main(argv, null);
	}

	public static void main(String[] argv, Map overideFileProperties) {
		if (argv == null || argv.length < 1) {
			System.err.println("Usage: java net.sourceforge.sql2java.Main <properties filename>");
			System.exit(1);
		}
		prop = new Properties();
		try {
			prop.load(new FileInputStream(argv[0]));
			CodeWriter cw = new CodeWriter(null, prop);
			cw.log("database properties initialization");
			Database db = new Database();
			db.setDriver(Main.getProperty("jdbc.driver"));
			db.setUrl(Main.getProperty("jdbc.url"));
			db.setUsername(Main.getProperty("jdbc.username"));
			db.setPassword(Main.getProperty("jdbc.password"));
			db.setCatalog(Main.getProperty("jdbc.catalog"));
			db.setSchema(Main.getProperty("jdbc.schema"));
			db.setTableNamePattern(Main.getProperty("jdbc.tablenamepattern"));
			db.setActiveConnections(Main.getProperty("jdbc.active", "10"));
			db.setIdleConnections(Main.getProperty("jdbc.idle", "5"));
			db.setMaxWait(Main.getProperty("jdbc.maxwait", "120000"));
			CodeWriter writer = new CodeWriter(db, prop);
			if (overideFileProperties != null) {
				prop.putAll(overideFileProperties);
			}
			if ("false".equalsIgnoreCase(Main.getProperty("jdbc.oracle.retrieve.remarks"))) {
				db.setOracleRetrieveRemarks(false);
			} else {
				db.setOracleRetrieveRemarks(true);
			}
			String tt = Main.getProperty("jdbc.tabletypes", "TABLE");
			StringTokenizer st = new StringTokenizer(tt, ",; \t");
			ArrayList<String> al = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				al.add(st.nextToken().trim());
			}
			db.setTableTypes(al.toArray(new String[al.size()]));
			db.load();
			if (argv.length > 1) {
				writer.setDestinationFolder(argv[1]);
			}
			writer.process();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static String getProperty(String property) {
		String res = ConfigHelper.getGlobalProperty((String) property);
		if (res != null) {
			return res.trim();
		}
		String s = prop.getProperty(property);
		return s != null ? s.trim() : s;
	}

	public static String getProperty(String property, String default_val) {
		String s = Main.getProperty(property);
		if (s == null) {
			return default_val;
		}
		return s;
	}

	public static boolean isInArray(String[] ar, String code) {
		if (ar == null) {
			return false;
		}
		for (int i = 0; i < ar.length; ++i) {
			if (!code.equalsIgnoreCase(ar[i]))
				continue;
			return true;
		}
		return false;
	}
}
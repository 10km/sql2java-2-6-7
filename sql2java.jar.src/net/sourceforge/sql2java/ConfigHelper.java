/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.sql2java.CodeWriter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class ConfigHelper {
	private static Document doc = null;

	public static String getTableProperty(String table, String propertyName) {
		return ConfigHelper.getXPathProperty("//table[@name='" + table.toLowerCase() + "']",
				propertyName.toLowerCase());
	}

	public static String getColumnProperty(String table, String column, String propertyName) {
		return ConfigHelper.getXPathProperty(
				"//table[@name='" + table.toLowerCase() + "']/column[@name='" + column.toLowerCase() + "']",
				propertyName);
	}

	public static String getProperty(String propertyName) {
		return ConfigHelper.getXPathProperty("//sql2java", propertyName.toLowerCase());
	}

	public static String getGlobalProperty(String propertyName) {
		return ConfigHelper.getProperty(propertyName);
	}

	public static String getXPathProperty(String xPathQuery, String propertyName) {
		if (doc == null) {
			return null;
		}
		String result = null;
		try {
			XPath servletPath = XPath.newInstance(xPathQuery);
			List nodes = servletPath.selectNodes(doc);
			if (nodes == null) {
				return null;
			}
			Iterator i = nodes.iterator();
			if (i.hasNext()) {
				Element item = (Element) i.next();

				if (item.getAttribute(propertyName) != null) {
					result = item.getAttribute(propertyName).getValue();
					if (result != null) {
						return result;
					}
				}

				if (item.getChild(propertyName) == null) {
					return null;
				}

				result = item.getChild(propertyName).getTextTrim();
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return null;
	}

	static {
		try {
			String filename = CodeWriter.getProperty((String) "sql2java.xml", (String) "sql2java.xml");
			if (!new File(filename).isFile() && !new File(filename = "src/sql2java.xml").isFile()) {
				filename = "src/config/sql2java.xml";
			}
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(new File(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
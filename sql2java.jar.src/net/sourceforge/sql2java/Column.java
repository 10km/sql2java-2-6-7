/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.text.*;
import java.util.*;

public class Column implements Cloneable, Comparable {

	public static final int M_ARRAY = 0;
	public static final int M_BIGDECIMAL = 1;
	public static final int M_BOOLEAN = 2;
	public static final int M_BYTES = 3;
	public static final int M_CLOB = 4;
	public static final int M_SQLDATE = 5;
	public static final int M_UTILDATE = 6;
	public static final int M_DOUBLE = 7;
	public static final int M_FLOAT = 8;
	public static final int M_BLOB = 9;
	public static final int M_INTEGER = 10;
	public static final int M_LONG = 11;
	public static final int M_REF = 12;
	public static final int M_STRING = 13;
	public static final int M_TIME = 14;
	public static final int M_TIMESTAMP = 15;
	public static final int M_URL = 16;
	public static final int M_OBJECT = 17;
	public static final int M_CALENDAR = 18;
	private String catalog;
	private String schema;
	private String tableName;
	private String name;
	private String remarks;
	private String defaultValue;
	private int size;
	private int decDigits;
	private int radix;
	private int nullable;
	private int ordinal;
	private short type;
	private boolean isPrimaryKey;
	private String strCheckingType;
	private Database db;
	private List foreignKeys;
	private List importedKeys;
	private String typeName;
	private static Random rand = new Random();

	public Column() {
		strCheckingType = "";
		foreignKeys = new Vector();
		importedKeys = new Vector();
		typeName = "";
	}

	public String toString() {
		return "\n --------- " + tableName + "." + name + " --------- " + "\n schema        = " + schema
				+ "\n tableName     = " + tableName + "\n catalog       = " + catalog + "\n remarks       = " + remarks
				+ "\n defaultValue  = " + defaultValue + "\n decDigits     = " + decDigits + "\n radix         = "
				+ radix + "\n nullable      = " + nullable + "\n ordinal       = " + ordinal + "\n size          = "
				+ size + "\n type          = " + type + " " + "\n isPrimaryKey  = " + (isPrimaryKey ? "true" : "false");
	}

	public void setCheckingType(String strValue) {
		strCheckingType = strValue;
	}

	public String getCheckingType() {
		return strCheckingType;
	}

	public void setDatabase(Database db) {
		this.db = db;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setName(String name) {
		this.name = null != name ? name.replaceAll("\\W", "") : "";
	}

	public void setType(short type) {
		this.type = type;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setDecimalDigits(int decDigits) {
		this.decDigits = decDigits;
	}

	public void setRadix(int radix) {
		this.radix = radix;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public void setRemarks(String remarks) {
		if (remarks != null)
			this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setOrdinalPosition(int ordinal) {
		this.ordinal = ordinal;
	}

	public void isPrimaryKey(boolean isKey) {
		isPrimaryKey = isKey;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getTableName() {
		return tableName;
	}

	public String getName() {
		return name;
	}

	public short getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public int getDecimalDigits() {
		return decDigits;
	}

	public int getRadix() {
		return radix;
	}

	public int getNullable() {
		return nullable;
	}

	public String getNullableAsString() {
		return getNullable() == 0 ? "null not allowed" : "nullable";
	}

	public int getOrdinalPosition() {
		return ordinal;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public String getFullName() {
		return tableName + "." + getName();
	}

	public String getConstName() {
		return getName().toUpperCase();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private void tuoe() {
		throw new UnsupportedOperationException(
				"Not supported yet: " + getTableName() + "." + getName() + " " + getJavaTypeAsTypeName());
	}

	private void tiae() {
		throw new IllegalArgumentException("No primary type associated: " + getTableName() + "." + getName());
	}

	public int getMappedType() {
		switch (getType()) {
			case 2003 :
				return 0;

			case -5 :
				return 11;

			case -2 :
				return 3;

			case -7 :
				return 2;

			case 2004 :
				return 9;

			case 16 : // '\020'
				return 2;

			case 1 : // '\001'
				return 13;

			case 2005 :
				return 4;

			case 70 : // 'F'
				return 16;

			case 91 : // '['
				if ("java.util.Date".equals(CodeWriter.dateClassName))
					return 6;
				if ("java.sql.Date".equals(CodeWriter.dateClassName))
					return 5;
				if ("java.util.Calendar".equals(CodeWriter.dateClassName))
					return 18;
				tuoe();
				// fall through

			case 3 : // '\003'
				return getDecimalDigits() <= 0 ? 11 : 1;

			case 2001 :
				return 17;

			case 8 : // '\b'
				return 7;

			case 6 : // '\006'
				return 7;

			case 4 : // '\004'
				return getTypeName().equalsIgnoreCase("INT UNSIGNED") ? 11 : 10;

			case 2000 :
				return 17;

			case -4 :
				return 3;

			case -1 :
				return 13;

			case 2 : // '\002'
				return getDecimalDigits() <= 0 ? 11 : 1;

			case 1111 :
				return 17;

			case 7 : // '\007'
				return 8;

			case 2006 :
				return 12;

			case 5 : // '\005'
				return 10;

			case 2002 :
				return 17;

			case 92 : // '\\'
				if ("java.util.Date".equals(CodeWriter.timeClassName))
					return 6;
				if ("java.sql.Time".equals(CodeWriter.timeClassName))
					return 14;
				if ("java.util.Calendar".equals(CodeWriter.timeClassName))
					return 18;
				tuoe();
				// fall through

			case 93 : // ']'
				if ("java.util.Date".equals(CodeWriter.timestampClassName))
					return 6;
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName))
					return 15;
				if ("java.util.Calendar".equals(CodeWriter.timestampClassName))
					return 18;
				tuoe();
				// fall through

			case -6 :
				return 10;

			case -3 :
				return 3;

			case 12 : // '\f'
				return 13;

			default :
				tuoe();
				return -1;
		}
	}

	public String getQuerySetMethod() {
		switch (getType()) {
			case 2003 :
				return "setArray";

			case -5 :
				return "setBigDecimal";

			case -2 :
				return "setBytes";

			case -7 :
				return "setBoolean";

			case 2004 :
				return "setBlob";

			case 16 : // '\020'
				return "setBoolean";

			case 1 : // '\001'
				return "setString";

			case 2005 :
				return "setClob";

			case 70 : // 'F'
				return "setURL";

			case 91 : // '['
				return "setDate";

			case 3 : // '\003'
				return getDecimalDigits() <= 0 ? "setLong" : "setBigDecimal";

			case 2001 :
				return "setObject";

			case 8 : // '\b'
				return "setDouble";

			case 6 : // '\006'
				return "setDouble";

			case 4 : // '\004'
				return getTypeName().equalsIgnoreCase("INT UNSIGNED") ? "setLong" : "setInt";

			case 2000 :
				return "setObject";

			case -4 :
				return "setBytes";

			case -1 :
				return "setString";

			case 2 : // '\002'
				return getDecimalDigits() <= 0 ? "setLong" : "setBigDecimal";

			case 1111 :
				return "setObject";

			case 7 : // '\007'
				return "setFloat";

			case 2006 :
				return "setRef";

			case 5 : // '\005'
				return "setInt";

			case 2002 :
				return "setObject";

			case 92 : // '\\'
				if ("java.util.Date".equals(CodeWriter.timeClassName))
					return "setDate";
				if ("java.sql.Time".equals(CodeWriter.timeClassName))
					return "setTime";
				tuoe();
				// fall through

			case 93 : // ']'
				if ("java.util.Date".equals(CodeWriter.timestampClassName))
					return "setDate";
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName))
					return "setTimestamp";
				tuoe();
				// fall through

			case -6 :
				return "setInt";

			case -3 :
				return "setBytes";

			case 12 : // '\f'
				return "setString";

			default :
				tuoe();
				return "setObject";
		}
	}

	public String getJavaType() {
		switch (getMappedType()) {
			case 0 : // '\0'
				return "Array";

			case 1 : // '\001'
				return "java.math.BigDecimal";

			case 2 : // '\002'
				return "Boolean";

			case 3 : // '\003'
				return "byte[]";

			case 4 : // '\004'
				return "Clob";

			case 5 : // '\005'
				return "java.sql.Date";

			case 6 : // '\006'
				return "java.util.Date";

			case 7 : // '\007'
				return "Double";

			case 8 : // '\b'
				return "Float";

			case 10 : // '\n'
				return "Integer";

			case 11 : // '\013'
				return "Long";

			case 12 : // '\f'
				return "Ref";

			case 13 : // '\r'
				return "String";

			case 14 : // '\016'
				return "java.sql.Time";

			case 15 : // '\017'
				return "java.sql.Timestamp";

			case 16 : // '\020'
				return "java.net.URL";

			case 17 : // '\021'
				return "Object";

			case 18 : // '\022'
				return "java.util.Calendar";

			case 9 : // '\t'
			default :
				tiae();
				break;
		}
		return null;
	}

	public boolean hasPrimaryType() {
		return getJavaPrimaryType() != null;
	}

	public String getJavaPrimaryType() throws IllegalArgumentException {
		int decimalDigits = getDecimalDigits();
		if ((type == 3 || type == 2) && decimalDigits == 0) {
			if (size == 1)
				return "boolean";
			if (size < 3)
				return "byte";
			if (size < 5)
				return "short";
			if (size < 10)
				return "int";
			if (size < 19)
				return "long";
		}
		switch (getMappedType()) {
			case 2 : // '\002'
				return "boolean";

			case 5 : // '\005'
				return "long";

			case 6 : // '\006'
				return "long";

			case 7 : // '\007'
				return "double";

			case 8 : // '\b'
				return "float";

			case 10 : // '\n'
				return "int";

			case 11 : // '\013'
				return "long";

			case 14 : // '\016'
				return "long";

			case 15 : // '\017'
				return "long";

			case 3 : // '\003'
			case 4 : // '\004'
			case 9 : // '\t'
			case 12 : // '\f'
			case 13 : // '\r'
			default :
				return null;
		}
	}

	public String getJavaTypeAsTypeName() {
		switch (getType()) {
			case 2003 :
				return "Types.ARRAY";

			case -5 :
				return "Types.BIGINT";

			case -2 :
				return "Types.BINARY";

			case -7 :
				return "Types.BIT";

			case 2004 :
				return "Types.BLOB";

			case 16 : // '\020'
				return "Types.BOOLEAN";

			case 1 : // '\001'
				return "Types.CHAR";

			case 2005 :
				return "Types.CLOB";

			case 70 : // 'F'
				return "Types.DATALINK";

			case 91 : // '['
				return "Types.DATE";

			case 3 : // '\003'
				return "Types.DECIMAL";

			case 2001 :
				return "Types.DISTINCT";

			case 8 : // '\b'
				return "Types.DOUBLE";

			case 6 : // '\006'
				return "Types.FLOAT";

			case 4 : // '\004'
				return "Types.INTEGER";

			case 2000 :
				return "Types.JAVA_OBJECT";

			case -4 :
				return "Types.LONGVARBINARY";

			case -1 :
				return "Types.LONGVARCHAR";

			case 0 : // '\0'
				return "Types.NULL";

			case 2 : // '\002'
				return "Types.NUMERIC";

			case 1111 :
				return "Types.OTHER";

			case 7 : // '\007'
				return "Types.REAL";

			case 2006 :
				return "Types.REF";

			case 5 : // '\005'
				return "Types.SMALLINT";

			case 2002 :
				return "Types.STRUCT";

			case 92 : // '\\'
				return "Types.TIME";

			case 93 : // ']'
				return "Types.TIMESTAMP";

			case -6 :
				return "Types.TINYINT";

			case -3 :
				return "Types.VARBINARY";

			case 12 : // '\f'
				return "Types.VARCHAR";
		}
		return "unkown SQL type " + getType();
	}

	public boolean isColumnNumeric() {
		switch (getMappedType()) {
			case 1 : // '\001'
			case 7 : // '\007'
			case 8 : // '\b'
			case 10 : // '\n'
			case 11 : // '\013'
				return true;

			case 2 : // '\002'
			case 3 : // '\003'
			case 4 : // '\004'
			case 5 : // '\005'
			case 6 : // '\006'
			case 9 : // '\t'
			default :
				return false;
		}
	}

	public boolean isString() {
		return 13 == getMappedType();
	}

	public boolean isDate() {
		switch (getMappedType()) {
			case 6 : // '\006'
			case 14 : // '\016'
			case 15 : // '\017'
				return true;
		}
		return false;
	}

	public boolean isCalendar() {
		return getMappedType() == 18;
	}

	public boolean hasCompareTo() throws Exception {
		switch (getMappedType()) {
			case 0 : // '\0'
				return false;

			case 1 : // '\001'
				return true;

			case 2 : // '\002'
				return false;

			case 3 : // '\003'
				return false;

			case 4 : // '\004'
				return false;

			case 5 : // '\005'
				return true;

			case 6 : // '\006'
				return true;

			case 7 : // '\007'
				return true;

			case 8 : // '\b'
				return true;

			case 10 : // '\n'
				return true;

			case 11 : // '\013'
				return true;

			case 12 : // '\f'
				return false;

			case 13 : // '\r'
				return true;

			case 14 : // '\016'
				return true;

			case 15 : // '\017'
				return true;

			case 16 : // '\020'
				return false;

			case 17 : // '\021'
				return false;

			case 9 : // '\t'
			default :
				return false;
		}
	}

	public boolean useEqualsInSetter() throws Exception {
		switch (getMappedType()) {
			case 2 : // '\002'
				return true;
		}
		return false;
	}

	public String getResultSetMethodObject(String pos) {
		return getResultSetMethodObject("rs", pos);
	}

	public String getResultSetMethodObject(String resultSet, String pos) {
		switch (getMappedType()) {
			case 0 : // '\0'
				return resultSet + ".getArray(" + pos + ")";

			case 11 : // '\013'
				return CodeWriter.MGR_CLASS + ".getLong(" + resultSet + ", " + pos + ")";

			case 3 : // '\003'
				return resultSet + ".getBytes(" + pos + ")";

			case 9 : // '\t'
				return resultSet + ".getBlob(" + pos + ")";

			case 2 : // '\002'
				return CodeWriter.MGR_CLASS + ".getBoolean(" + resultSet + ", " + pos + ")";

			case 13 : // '\r'
				return resultSet + ".getString(" + pos + ")";

			case 4 : // '\004'
				return resultSet + ".getClob(" + pos + ")";

			case 16 : // '\020'
				return resultSet + ".getURL(" + pos + ")";

			case 1 : // '\001'
				return resultSet + ".getBigDecimal(" + pos + ")";

			case 7 : // '\007'
				return CodeWriter.MGR_CLASS + ".getDouble(" + resultSet + ", " + pos + ")";

			case 8 : // '\b'
				return CodeWriter.MGR_CLASS + ".getFloat(" + resultSet + ", " + pos + ")";

			case 10 : // '\n'
				return CodeWriter.MGR_CLASS + ".getInteger(" + resultSet + ", " + pos + ")";

			case 17 : // '\021'
				return resultSet + ".getObject(" + pos + ")";

			case 12 : // '\f'
				return resultSet + ".getRef(" + pos + ")";

			case 5 : // '\005'
				return resultSet + ".getDate(" + pos + ")";

			case 14 : // '\016'
				return resultSet + ".getTime(" + pos + ")";

			case 15 : // '\017'
				return resultSet + ".getTimestamp(" + pos + ")";

			case 6 : // '\006'
				switch (getType()) {
					case 92 : // '\\'
						return resultSet + ".getTime(" + pos + ")";

					case 93 : // ']'
						return resultSet + ".getTimestamp(" + pos + ")";

					case 91 : // '['
						return resultSet + ".getDate(" + pos + ")";

					default :
						tuoe();
						break;
				}
				// fall through

			case 18 : // '\022'
				return CodeWriter.MGR_CLASS + ".getCalendar(" + resultSet + ", " + pos + ")";

			default :
				tuoe();
				return null;
		}
	}

	public String getPreparedStatementMethod(String var, int pos) {
		return getPreparedStatementMethod(var, String.valueOf(pos));
	}

	public String getPreparedStatementMethod(String var, String pos) {
		StringBuffer sb = new StringBuffer();
		StringBuffer end = new StringBuffer();
		end.append(pos).append(", ").append(var).append(");");
		if ('"' != var.charAt(0)) {
			sb.append("if (").append(var).append(" == null) { ps.setNull(").append(pos).append(", ")
					.append(getJavaTypeAsTypeName()).append("); } else { ");
			end.append(" }");
		}
		switch (getMappedType()) {
			case 0 : // '\0'
				return sb.append("ps.setArray(").append(end).toString();

			case 11 : // '\013'
				return sb.append(CodeWriter.MGR_CLASS).append(".setLong(ps, ").append(end).toString();

			case 3 : // '\003'
				return sb.append("ps.setBytes(").append(end).toString();

			case 9 : // '\t'
				return sb.append("ps.setBlob(").append(end).toString();

			case 2 : // '\002'
				return sb.append(CodeWriter.MGR_CLASS).append(".setBoolean(ps, ").append(end).toString();

			case 13 : // '\r'
				return sb.append("ps.setString(").append(end).toString();

			case 4 : // '\004'
				return sb.append("ps.setClob(").append(end).toString();

			case 16 : // '\020'
				return sb.append("ps.setURL(").append(end).toString();

			case 1 : // '\001'
				return sb.append("ps.setBigDecimal(").append(end).toString();

			case 7 : // '\007'
				return sb.append(CodeWriter.MGR_CLASS).append(".setDouble(ps, ").append(end).toString();

			case 10 : // '\n'
				return sb.append(CodeWriter.MGR_CLASS).append(".setInteger(ps, ").append(end).toString();

			case 17 : // '\021'
				return sb.append("ps.setObject(").append(end).toString();

			case 8 : // '\b'
				return sb.append(CodeWriter.MGR_CLASS).append(".setFloat(ps, ").append(end).toString();

			case 5 : // '\005'
				return sb.append("ps.setDate(").append(end).toString();

			case 14 : // '\016'
				return sb.append("ps.setTime(").append(end).toString();

			case 15 : // '\017'
				return sb.append("ps.setTimestamp(").append(end).toString();

			case 6 : // '\006'
				switch (getType()) {
					case 93 : // ']'
						return sb.append("ps.setTimestamp(").append(pos).append(", new java.sql.Timestamp(").append(var)
								.append(".getTime())); }").toString();

					case 91 : // '['
						return sb.append("ps.setDate(").append(pos).append(", new java.sql.Date(").append(var)
								.append(".getTime())); }").toString();

					case 92 : // '\\'
						return sb.append("ps.setTime(").append(pos).append(", new java.sql.Time(").append(var)
								.append(".getTime())); }").toString();
				}
				return null;

			case 18 : // '\022'
				return sb.append(CodeWriter.MGR_CLASS).append(".setCalendar(ps, ").append(end).toString();

			case 12 : // '\f'
				sb.setLength(0);
				sb.append("ps.setRef(").append(end);
				sb.setLength(sb.length() - 2);
				return sb.toString();
		}
		sb.setLength(0);
		sb.append("ps.setObject(").append(end);
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}

	public String getStringConvertionMethod() {
		switch (getMappedType()) {
			case 1 : // '\001'
				return "new java.math.BigDecimal";

			case 2 : // '\002'
				return "new Boolean";

			case 5 : // '\005'
				return "new java.sql.Date";

			case 7 : // '\007'
				return "new Double";

			case 8 : // '\b'
				return "new Float";

			case 10 : // '\n'
				return "new Integer";

			case 11 : // '\013'
				return "new Long";

			case 13 : // '\r'
				return "";

			case 6 : // '\006'
			case 14 : // '\016'
			case 15 : // '\017'
				if ("java.util.GregorianCalendar".equals(CodeWriter.dateClassName))
					return "GregorianDate";
				else
					return CodeWriter.MGR_CLASS + ".getDateFromString";

			case 0 : // '\0'
			case 3 : // '\003'
			case 4 : // '\004'
			case 9 : // '\t'
			case 12 : // '\f'
			case 16 : // '\020'
			case 17 : // '\021'
			default :
				System.err.println(
						" unknown mapped type " + getMappedType() + " (" + getType() + ") for " + getFullName());
				return "";
		}
	}

	public String getDefaultWidget() {
		if (isForeignKey())
			return "SelectWidget";
		if (isString() && (getSize() > 200 || getSize() == -1))
			return "TextAreaWidget";
		switch (getMappedType()) {
			case 2 : // '\002'
				return "BooleanWidget";

			case 5 : // '\005'
			case 6 : // '\006'
			case 14 : // '\016'
			case 15 : // '\017'
				return "DateWidget";

			case 1 : // '\001'
			case 7 : // '\007'
			case 8 : // '\b'
			case 10 : // '\n'
			case 11 : // '\013'
				return "NumericWidget";

			case 0 : // '\0'
			case 3 : // '\003'
			case 4 : // '\004'
			case 12 : // '\f'
			case 13 : // '\r'
			case 16 : // '\020'
			case 17 : // '\021'
				return "InputWidget";

			case 9 : // '\t'
			default :
				System.err.println("type unknown for " + getFullName());
				break;
		}
		return "";
	}

	public boolean isVersion() {
		if (!CodeWriter.optimisticLockType.equalsIgnoreCase("timestamp"))
			return false;
		if (!getName().equalsIgnoreCase(CodeWriter.optimisticLockColumn))
			return false;
		return getMappedType() == 11 || getMappedType() == 13;
	}

	public Table getTable() {
		return db.getTable(getTableName());
	}

	public void addForeignKey(Column col) {
		foreignKeys.add(col);
		getTable().addForeignKey(this);
	}

	public List getForeignKeys() {
		return foreignKeys;
	}

	public void addImportedKey(Column col) {
		importedKeys.add(col);
		getTable().addImportedKey(col);
	}

	public List getImportedKeys() {
		return importedKeys;
	}

	public int countImportedKeys() {
		return importedKeys.size();
	}

	public boolean isImportedKey() {
		return countImportedKeys() > 0;
	}

	public Column getForeignColumn() {
		return (Column) foreignKeys.get(0);
	}

	public int countForeignKeys() {
		return foreignKeys.size();
	}

	public boolean isForeignKey() {
		return countForeignKeys() > 0;
	}

	public String getPropertyTag() {
		return (getTableName() + "." + getName()).toLowerCase();
	}

	public String getDefaultRules() {
		String rule = "";
		if (getNullable() == 0 && !isPrimaryKey())
			rule = rule + " nullnotallowed";
		else
			rule = rule + " nullallowed";
		if (getType() == 91 || getType() == 93)
			rule = rule + " dateformat";
		return rule;
	}

	public boolean columnFor(String webElement) {
		String includeProperty = ConfigHelper
				.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "include");
		String excludeProperty = ConfigHelper
				.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "exclude");
		String exclude[] = CodeWriter.getExplodedString(excludeProperty);
		String include[] = CodeWriter.getExplodedString(includeProperty);
		if (exclude.length == 0 && include.length == 0)
			return getDefaultIncludeFor(webElement);
		if (Main.isInArray(include, getName()))
			return true;
		if (Main.isInArray(exclude, getName()))
			return false;
		return include.length == 0;
	}

	public boolean getDefaultIncludeFor(String webElement) {
		return true;
	}

	public String getDefaultValue()
    {
        if(Boolean.valueOf(CodeWriter.getProperty("codewriter.generate.defaultvalue", "false")).booleanValue())
            return "";
        String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "defaultValue");
        if(xmlDefaultValue != null && !"".equals(xmlDefaultValue))
            return xmlDefaultValue;
        if(null == defaultValue) goto _L2; else goto _L1
_L1:
        if(!isColumnNumeric()) goto _L4; else goto _L3
_L3:
        double value = Double.parseDouble(defaultValue);
        getMappedType();
        JVM INSTR tableswitch 1 11: default 170
    //                   1 136
    //                   2 170
    //                   3 170
    //                   4 170
    //                   5 170
    //                   6 170
    //                   7 153
    //                   8 153
    //                   9 170
    //                   10 136
    //                   11 136;
           goto _L5 _L6 _L5 _L5 _L5 _L5 _L5 _L7 _L7 _L5 _L6 _L6
_L6:
        return generateNewAssignation(getJavaType(), defaultValue, String.valueOf(value));
_L7:
        return generateNewAssignation(getJavaType(), String.valueOf(value), defaultValue);
_L5:
        return "";
        NumberFormatException nfe;
        nfe;
        return "";
_L4:
        if(!isDate()) goto _L9; else goto _L8
_L8:
        java.util.Date date = SimpleDateFormat.getInstance().parse(defaultValue);
        return "= SimpleDateFormat.getInstance().parse(" + defaultValue + "); // '" + SimpleDateFormat.getInstance().format(date) + "'";
        ParseException pe;
        pe;
        return "= null; // '" + defaultValue + "'";
_L9:
        if(isString())
            return "".equals(defaultValue) ? "" : "= \"" + defaultValue + '"';
        if(2 == getMappedType())
            return "= Boolean.valueOf(\"" + ("1".equals(defaultValue) ? "true" : defaultValue) + "\").booleanValue(); // '" + defaultValue + "'";
_L2:
        return defaultValue != null ? "= " + defaultValue : "";
    }

	private String generateNewAssignation(String type, String parameter, String comment) {
		StringBuffer sb = new StringBuffer(70);
		sb.append("= new ").append(type);
		sb.append('(').append(parameter).append("); // '").append(comment).append("'");
		return sb.toString();
	}

	public String getRemarks() {
		String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "description");
		if (xmlDefaultValue != null && !"".equals(xmlDefaultValue))
			return xmlDefaultValue;
		else
			return remarks != null ? remarks : "";
	}

	public String getJavaName() {
		return convertName(getName());
	}

	public String getSampleData() {
		if (getNullable() > 1 && rand.nextInt(20) == 10)
			return "";
		if (isColumnNumeric())
			return "" + rand.nextInt(100);
		if (isDate()) {
			Calendar rightNow = Calendar.getInstance();
			rightNow.set(2000 + rand.nextInt(20), 1 + rand.nextInt(12), 1 + rand.nextInt(28), rand.nextInt(23),
					rand.nextInt(60), rand.nextInt(60));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			return dateFormat.format(rightNow.getTime());
		} else {
			return StringUtilities.getSampleString(getSize());
		}
	}

	private String escape(String s) {
		return StringUtilities.escape(s);
	}

	private String escape() {
		return escape(getName());
	}

	public String convertName(String columnName) {
		return StringUtilities.convertName(columnName, true);
	}

	public String convertName(Column col) {
		return convertName(col.getName());
	}

	public String convertName() {
		return convertName(name);
	}

	public String getImportedKeyVarName() {
		return convertName(escape() + "_collection");
	}

	public String getGetMethod() {
		return convertName("get_" + escape());
	}

	public String getSetMethod() {
		return convertName("set_" + escape());
	}

	public String getModifiedMethod() {
		return convertName("is_" + escape() + "_modified");
	}

	public String getInitializedMethod() {
		return convertName("is_" + escape() + "_initialized");
	}

	public String getWidgetMethod() {
		return convertName("get_" + escape() + "_widget");
	}

	public String getVarName() {
		return convertName(escape());
	}

	public String getModifiedVarName() {
		return convertName(escape() + "_is_modified");
	}

	public String getInitializedVarName() {
		return convertName(escape() + "_is_initialized");
	}

	public String getImportedKeyModifiedVarName() {
		return convertName(escape() + "_collection_is_modified");
	}

	public String getImportedKeyInitializedVarName() {
		return convertName(escape() + "_collection_is_initialized");
	}

	public String getImportedKeyInitializedMethod() {
		return convertName("is_" + escape() + "_collection_initialized");
	}

	public String getImportedKeyGetMethod() {
		return convertName("get_" + escape() + "_collection");
	}

	public String getImportedKeyAddMethod() {
		return convertName("add_" + escape() + "");
	}

	public String getImportedKeySetMethod() {
		return convertName("set_" + escape() + "_collection");
	}

	public String getImportedKeyModifiedMethod() {
		return convertName("is_" + escape() + "_collection_modified");
	}

	public String getForeignKeyVarName() {
		return convertName(escape() + "_object");
	}

	public String getForeignKeyModifiedVarName() {
		return convertName(escape() + "_object_is_modified");
	}

	public String getForeignKeyInitializedVarName() {
		return convertName(escape() + "_object_is_initialized");
	}

	public String getForeignKeyInitializedMethod() {
		return convertName("is_" + escape() + "_object_initialized");
	}

	public String getForeignKeyGetMethod(String col) {
		return convertName("get_" + escape() + "_object");
	}

	public String getForeignKeySetMethod(String col) {
		return convertName("set_" + escape() + "_object");
	}

	public String getForeignKeyModifiedMethod(String col) {
		return convertName("is_" + escape() + "_object_modified");
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int compareTo(Object obj) {
		return ((Column) obj).ordinal - ordinal;
	}

}
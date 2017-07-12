/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

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
		this.strCheckingType = "";

		this.foreignKeys = new Vector();
		this.importedKeys = new Vector();
		this.typeName = "";
	}

	public String toString() {
		return "\n --------- " + this.tableName + "." + this.name + " --------- " + "\n schema        = " + this.schema
				+ "\n tableName     = " + this.tableName + "\n catalog       = " + this.catalog + "\n remarks       = "
				+ this.remarks + "\n defaultValue  = " + this.defaultValue + "\n decDigits     = " + this.decDigits
				+ "\n radix         = " + this.radix + "\n nullable      = " + this.nullable + "\n ordinal       = "
				+ this.ordinal + "\n size          = " + this.size + "\n type          = " + this.type + " "
				+ "\n isPrimaryKey  = " + ((this.isPrimaryKey) ? "true" : "false");
	}

	public void setCheckingType(String strValue) {
		this.strCheckingType = strValue;
	}
	public String getCheckingType() {
		return this.strCheckingType;
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
		this.name = ((null == name) ? "" : name.replaceAll("\\W", ""));
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
		this.isPrimaryKey = isKey;
	}

	public String getCatalog() {
		return this.catalog;
	}
	public String getSchema() {
		return this.schema;
	}
	public String getTableName() {
		return this.tableName;
	}
	public String getName() {
		return this.name;
	}
	public short getType() {
		return this.type;
	}
	public int getSize() {
		return this.size;
	}
	public int getDecimalDigits() {
		return this.decDigits;
	}
	public int getRadix() {
		return this.radix;
	}
	public int getNullable() {
		return this.nullable;
	}
	public String getNullableAsString() {
		return ((getNullable() != 0) ? "nullable" : "null not allowed");
	}
	public int getOrdinalPosition() {
		return this.ordinal;
	}
	public boolean isPrimaryKey() {
		return this.isPrimaryKey;
	}
	public String getFullName() {
		return this.tableName + "." + getName();
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
			case 16 :
				return 2;
			case 1 :
				return 13;
			case 2005 :
				return 4;
			case 70 :
				return 16;
			case 91 :
				if ("java.util.Date".equals(CodeWriter.dateClassName))
					return 6;
				if ("java.sql.Date".equals(CodeWriter.dateClassName))
					return 5;
				if ("java.util.Calendar".equals(CodeWriter.dateClassName))
					return 18;
				tuoe();
			case 3 :
				return ((getDecimalDigits() > 0) ? 1 : 11);
			case 2001 :
				return 17;
			case 8 :
				return 7;
			case 6 :
				return 7;
			case 4 :
				return ((getTypeName().equalsIgnoreCase("INT UNSIGNED")) ? 11 : 10);
			case 2000 :
				return 17;
			case -4 :
				return 3;
			case -1 :
				return 13;
			case 2 :
				return ((getDecimalDigits() > 0) ? 1 : 11);
			case 1111 :
				return 17;
			case 7 :
				return 8;
			case 2006 :
				return 12;
			case 5 :
				return 10;
			case 2002 :
				return 17;
			case 92 :
				if ("java.util.Date".equals(CodeWriter.timeClassName))
					return 6;
				if ("java.sql.Time".equals(CodeWriter.timeClassName))
					return 14;
				if ("java.util.Calendar".equals(CodeWriter.timeClassName))
					return 18;
				tuoe();
			case 93 :
				if ("java.util.Date".equals(CodeWriter.timestampClassName))
					return 6;
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName))
					return 15;
				if ("java.util.Calendar".equals(CodeWriter.timestampClassName))
					return 18;
				tuoe();
			case -6 :
				return 10;
			case -3 :
				return 3;
			case 12 :
				return 13;
		}
		tuoe();

		return -1;
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
			case 16 :
				return "setBoolean";
			case 1 :
				return "setString";
			case 2005 :
				return "setClob";
			case 70 :
				return "setURL";
			case 91 :
				return "setDate";
			case 3 :
				return ((getDecimalDigits() > 0) ? "setBigDecimal" : "setLong");
			case 2001 :
				return "setObject";
			case 8 :
				return "setDouble";
			case 6 :
				return "setDouble";
			case 4 :
				return ((getTypeName().equalsIgnoreCase("INT UNSIGNED")) ? "setLong" : "setInt");
			case 2000 :
				return "setObject";
			case -4 :
				return "setBytes";
			case -1 :
				return "setString";
			case 2 :
				return ((getDecimalDigits() > 0) ? "setBigDecimal" : "setLong");
			case 1111 :
				return "setObject";
			case 7 :
				return "setFloat";
			case 2006 :
				return "setRef";
			case 5 :
				return "setInt";
			case 2002 :
				return "setObject";
			case 92 :
				if ("java.util.Date".equals(CodeWriter.timeClassName))
					return "setDate";
				if ("java.sql.Time".equals(CodeWriter.timeClassName))
					return "setTime";
				tuoe();
			case 93 :
				if ("java.util.Date".equals(CodeWriter.timestampClassName))
					return "setDate";
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName))
					return "setTimestamp";
				tuoe();
			case -6 :
				return "setInt";
			case -3 :
				return "setBytes";
			case 12 :
				return "setString";
		}
		tuoe();

		return "setObject";
	}

	public String getJavaType() {
		switch (getMappedType()) {
			case 0 :
				return "Array";
			case 1 :
				return "java.math.BigDecimal";
			case 2 :
				return "Boolean";
			case 3 :
				return "byte[]";
			case 4 :
				return "Clob";
			case 5 :
				return "java.sql.Date";
			case 6 :
				return "java.util.Date";
			case 7 :
				return "Double";
			case 8 :
				return "Float";
			case 10 :
				return "Integer";
			case 11 :
				return "Long";
			case 12 :
				return "Ref";
			case 13 :
				return "String";
			case 14 :
				return "java.sql.Time";
			case 15 :
				return "java.sql.Timestamp";
			case 16 :
				return "java.net.URL";
			case 17 :
				return "Object";
			case 18 :
				return "java.util.Calendar";
			case 9 :
		}
		tiae();

		return null;
	}

	public boolean hasPrimaryType() {
		return (getJavaPrimaryType() != null);
	}

	public String getJavaPrimaryType() throws IllegalArgumentException {
		int decimalDigits = getDecimalDigits();
		if ((((this.type == 3) || (this.type == 2))) && (decimalDigits == 0)) {
			if (this.size == 1) {
				return "boolean";
			}
			if (this.size < 3) {
				return "byte";
			}
			if (this.size < 5) {
				return "short";
			}
			if (this.size < 10) {
				return "int";
			}
			if (this.size < 19) {
				return "long";
			}

		}

		switch (getMappedType()) {
			case 2 :
				return "boolean";
			case 5 :
				return "long";
			case 6 :
				return "long";
			case 7 :
				return "double";
			case 8 :
				return "float";
			case 10 :
				return "int";
			case 11 :
				return "long";
			case 14 :
				return "long";
			case 15 :
				return "long";
			case 3 :
			case 4 :
			case 9 :
			case 12 :
			case 13 :
		}
		return null;
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
			case 16 :
				return "Types.BOOLEAN";
			case 1 :
				return "Types.CHAR";
			case 2005 :
				return "Types.CLOB";
			case 70 :
				return "Types.DATALINK";
			case 91 :
				return "Types.DATE";
			case 3 :
				return "Types.DECIMAL";
			case 2001 :
				return "Types.DISTINCT";
			case 8 :
				return "Types.DOUBLE";
			case 6 :
				return "Types.FLOAT";
			case 4 :
				return "Types.INTEGER";
			case 2000 :
				return "Types.JAVA_OBJECT";
			case -4 :
				return "Types.LONGVARBINARY";
			case -1 :
				return "Types.LONGVARCHAR";
			case 0 :
				return "Types.NULL";
			case 2 :
				return "Types.NUMERIC";
			case 1111 :
				return "Types.OTHER";
			case 7 :
				return "Types.REAL";
			case 2006 :
				return "Types.REF";
			case 5 :
				return "Types.SMALLINT";
			case 2002 :
				return "Types.STRUCT";
			case 92 :
				return "Types.TIME";
			case 93 :
				return "Types.TIMESTAMP";
			case -6 :
				return "Types.TINYINT";
			case -3 :
				return "Types.VARBINARY";
			case 12 :
				return "Types.VARCHAR";
		}
		return "unkown SQL type " + getType();
	}

	public boolean isColumnNumeric() {
		switch (getMappedType()) {
			case 1 :
			case 7 :
			case 8 :
			case 10 :
			case 11 :
				return true;
			case 2 :
			case 3 :
			case 4 :
			case 5 :
			case 6 :
			case 9 :
		}
		return false;
	}

	public boolean isString() {
		return (13 == getMappedType());
	}

	public boolean isDate() {
		switch (getMappedType()) {
			case 6 :
			case 14 :
			case 15 :
				return true;
		}
		return false;
	}

	public boolean isCalendar() {
		return (getMappedType() == 18);
	}

	public boolean hasCompareTo() throws Exception {
		switch (getMappedType()) {
			case 0 :
				return false;
			case 1 :
				return true;
			case 2 :
				return false;
			case 3 :
				return false;
			case 4 :
				return false;
			case 5 :
				return true;
			case 6 :
				return true;
			case 7 :
				return true;
			case 8 :
				return true;
			case 10 :
				return true;
			case 11 :
				return true;
			case 12 :
				return false;
			case 13 :
				return true;
			case 14 :
				return true;
			case 15 :
				return true;
			case 16 :
				return false;
			case 17 :
				return false;
			case 9 :
		}
		return false;
	}

	public boolean useEqualsInSetter() throws Exception {
		switch (getMappedType()) {
			case 2 :
				return true;
		}
		return false;
	}

	public String getResultSetMethodObject(String pos) {
		return getResultSetMethodObject("rs", pos);
	}

	public String getResultSetMethodObject(String resultSet, String pos) {
		switch (getMappedType()) {
			case 0 :
				return resultSet + ".getArray(" + pos + ")";
			case 11 :
				return CodeWriter.MGR_CLASS + ".getLong(" + resultSet + ", " + pos + ")";
			case 3 :
				return resultSet + ".getBytes(" + pos + ")";
			case 9 :
				return resultSet + ".getBlob(" + pos + ")";
			case 2 :
				return CodeWriter.MGR_CLASS + ".getBoolean(" + resultSet + ", " + pos + ")";
			case 13 :
				return resultSet + ".getString(" + pos + ")";
			case 4 :
				return resultSet + ".getClob(" + pos + ")";
			case 16 :
				return resultSet + ".getURL(" + pos + ")";
			case 1 :
				return resultSet + ".getBigDecimal(" + pos + ")";
			case 7 :
				return CodeWriter.MGR_CLASS + ".getDouble(" + resultSet + ", " + pos + ")";
			case 8 :
				return CodeWriter.MGR_CLASS + ".getFloat(" + resultSet + ", " + pos + ")";
			case 10 :
				return CodeWriter.MGR_CLASS + ".getInteger(" + resultSet + ", " + pos + ")";
			case 17 :
				return resultSet + ".getObject(" + pos + ")";
			case 12 :
				return resultSet + ".getRef(" + pos + ")";
			case 5 :
				return resultSet + ".getDate(" + pos + ")";
			case 14 :
				return resultSet + ".getTime(" + pos + ")";
			case 15 :
				return resultSet + ".getTimestamp(" + pos + ")";
			case 6 :
				switch (getType()) {
					case 92 :
						return resultSet + ".getTime(" + pos + ")";
					case 93 :
						return resultSet + ".getTimestamp(" + pos + ")";
					case 91 :
						return resultSet + ".getDate(" + pos + ")";
				}
				tuoe();
			case 18 :
				return CodeWriter.MGR_CLASS + ".getCalendar(" + resultSet + ", " + pos + ")";
		}
		tuoe();

		return null;
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
			case 0 :
				return "ps.setArray(" + end;
			case 11 :
				return CodeWriter.MGR_CLASS + ".setLong(ps, " + end;
			case 3 :
				return "ps.setBytes(" + end;
			case 9 :
				return "ps.setBlob(" + end;
			case 2 :
				return CodeWriter.MGR_CLASS + ".setBoolean(ps, " + end;
			case 13 :
				return "ps.setString(" + end;
			case 4 :
				return "ps.setClob(" + end;
			case 16 :
				return "ps.setURL(" + end;
			case 1 :
				return "ps.setBigDecimal(" + end;
			case 7 :
				return CodeWriter.MGR_CLASS + ".setDouble(ps, " + end;
			case 10 :
				return CodeWriter.MGR_CLASS + ".setInteger(ps, " + end;
			case 17 :
				return "ps.setObject(" + end;
			case 8 :
				return CodeWriter.MGR_CLASS + ".setFloat(ps, " + end;
			case 5 :
				return "ps.setDate(" + end;
			case 14 :
				return "ps.setTime(" + end;
			case 15 :
				return "ps.setTimestamp(" + end;
			case 6 :
				switch (getType()) {
					case 93 :
						return "ps.setTimestamp(" + pos + ", new java.sql.Timestamp(" + var + ".getTime())); }";
					case 91 :
						return "ps.setDate(" + pos + ", new java.sql.Date(" + var + ".getTime())); }";
					case 92 :
						return "ps.setTime(" + pos + ", new java.sql.Time(" + var + ".getTime())); }";
				}
				return null;
			case 18 :
				return CodeWriter.MGR_CLASS + ".setCalendar(ps, " + end;
			case 12 :
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
			case 1 :
				return "new java.math.BigDecimal";
			case 2 :
				return "new Boolean";
			case 5 :
				return "new java.sql.Date";
			case 7 :
				return "new Double";
			case 8 :
				return "new Float";
			case 10 :
				return "new Integer";
			case 11 :
				return "new Long";
			case 13 :
				return "";
			case 6 :
			case 14 :
			case 15 :
				if ("java.util.GregorianCalendar".equals(CodeWriter.dateClassName)) {
					return "GregorianDate";
				}
				return CodeWriter.MGR_CLASS + ".getDateFromString";
			case 0 :
			case 3 :
			case 4 :
			case 9 :
			case 12 :
			case 16 :
			case 17 :
		}
		System.err.println(" unknown mapped type " + getMappedType() + " (" + getType() + ") for " + getFullName());
		return "";
	}

	public String getDefaultWidget() {
		if (isForeignKey()) {
			return "SelectWidget";
		}
		if ((isString()) && (((getSize() > 200) || (getSize() == -1)))) {
			return "TextAreaWidget";
		}

		switch (getMappedType()) {
			case 2 :
				return "BooleanWidget";
			case 5 :
			case 6 :
			case 14 :
			case 15 :
				return "DateWidget";
			case 1 :
			case 7 :
			case 8 :
			case 10 :
			case 11 :
				return "NumericWidget";
			case 0 :
			case 3 :
			case 4 :
			case 12 :
			case 13 :
			case 16 :
			case 17 :
				return "InputWidget";
			case 9 :
		}
		System.err.println("type unknown for " + getFullName());
		return "";
	}

	public boolean isVersion() {
		if (!(CodeWriter.optimisticLockType.equalsIgnoreCase("timestamp"))) {
			return false;
		}
		if (!(getName().equalsIgnoreCase(CodeWriter.optimisticLockColumn))) {
			return false;
		}

		return ((getMappedType() == 11) || (getMappedType() == 13));
	}

	public Table getTable() {
		return this.db.getTable(getTableName());
	}

	public void addForeignKey(Column col) {
		this.foreignKeys.add(col);
		getTable().addForeignKey(this);
	}

	public List getForeignKeys() {
		return this.foreignKeys;
	}

	public void addImportedKey(Column col) {
		this.importedKeys.add(col);
		getTable().addImportedKey(col);
	}

	public List getImportedKeys() {
		return this.importedKeys;
	}

	public int countImportedKeys() {
		return this.importedKeys.size();
	}

	public boolean isImportedKey() {
		return (countImportedKeys() > 0);
	}

	public Column getForeignColumn() {
		return ((Column) this.foreignKeys.get(0));
	}

	public int countForeignKeys() {
		return this.foreignKeys.size();
	}

	public boolean isForeignKey() {
		return (countForeignKeys() > 0);
	}

	public String getPropertyTag() {
		return getTableName() + "." + getName().toLowerCase();
	}

	public String getDefaultRules() {
		String rule = "";
		if ((getNullable() == 0) && (!(isPrimaryKey())))
			rule = rule + " nullnotallowed";
		else {
			rule = rule + " nullallowed";
		}
		if ((getType() == 91) || (getType() == 93)) {
			rule = rule + " dateformat";
		}
		return rule;
	}

	public boolean columnFor(String webElement) {
		String includeProperty = ConfigHelper
				.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "include");
		String excludeProperty = ConfigHelper
				.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "exclude");
		String[] exclude = CodeWriter.getExplodedString(excludeProperty);
		String[] include = CodeWriter.getExplodedString(includeProperty);
		if ((exclude.length == 0) && (include.length == 0)) {
			return getDefaultIncludeFor(webElement);
		}
		if (Main.isInArray(include, getName())) {
			return true;
		}
		if (Main.isInArray(exclude, getName())) {
			return false;
		}

		return (include.length == 0);
	}

	public boolean getDefaultIncludeFor(String webElement) {
		return true;
	}

	public String getDefaultValue() {
		if (Boolean.valueOf(CodeWriter.getProperty("codewriter.generate.defaultvalue", "false")).booleanValue())
			return "";

		String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "defaultValue");
		if ((xmlDefaultValue != null) && (!("".equals(xmlDefaultValue)))) {
			return xmlDefaultValue;
		}
		if (null != this.defaultValue) {
			if (isColumnNumeric())
				try {
					double value = Double.parseDouble(this.defaultValue);
					switch (getMappedType()) {
						case 1 :
						case 10 :
						case 11 :
							return generateNewAssignation(getJavaType(), this.defaultValue, String.valueOf(value));
						case 7 :
						case 8 :
							return generateNewAssignation(getJavaType(), String.valueOf(value), this.defaultValue);
						case 2 :
						case 3 :
						case 4 :
						case 5 :
						case 6 :
						case 9 :
					}
					return "";
				} catch (NumberFormatException nfe) {
					return "";
				}
			if (isDate())
				try {
					Date date = SimpleDateFormat.getInstance().parse(this.defaultValue);
					return "= SimpleDateFormat.getInstance().parse(" + this.defaultValue + "); // '"
							+ SimpleDateFormat.getInstance().format(date) + "'";
				} catch (ParseException pe) {
					return "= null; // '" + this.defaultValue + "'";
				}
			if (isString())
				return "= \"" + this.defaultValue + '"';
			if (2 == getMappedType()) {
				return "= Boolean.valueOf(\"" + (("1".equals(this.defaultValue)) ? "true" : this.defaultValue)
						+ "\").booleanValue(); // '" + this.defaultValue + "'";
			}
		}

		return "= " + this.defaultValue;
	}

	private String generateNewAssignation(String type, String parameter, String comment) {
		StringBuffer sb = new StringBuffer(70);
		sb.append("= new ").append(type);
		sb.append('(').append(parameter).append("); // '").append(comment).append("'");

		return sb.toString();
	}

	public String getRemarks() {
		String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "description");
		if ((xmlDefaultValue != null) && (!("".equals(xmlDefaultValue)))) {
			return xmlDefaultValue;
		}
		return ((this.remarks == null) ? "" : this.remarks);
	}

	public String getJavaName() {
		return convertName(getName());
	}

	public String getSampleData() {
		if ((getNullable() > 1) && (rand.nextInt(20) == 10)) {
			return "";
		}
		if (isColumnNumeric()) {
			return "" + rand.nextInt(100);
		}
		if (isDate()) {
			Calendar rightNow = Calendar.getInstance();
			rightNow.set(2000 + rand.nextInt(20), 1 + rand.nextInt(12), 1 + rand.nextInt(28), rand.nextInt(23),
					rand.nextInt(60), rand.nextInt(60));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			return dateFormat.format(rightNow.getTime());
		}

		return StringUtilities.getSampleString(getSize());
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
		return convertName(this.name);
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
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int compareTo(Object obj) {
		return (((Column) obj).ordinal - this.ordinal);
	}
}
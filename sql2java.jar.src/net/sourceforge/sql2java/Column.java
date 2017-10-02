/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.sql2java.CodeWriter;
import net.sourceforge.sql2java.ConfigHelper;
import net.sourceforge.sql2java.Database;
import net.sourceforge.sql2java.Main;
import net.sourceforge.sql2java.StringUtilities;
import net.sourceforge.sql2java.Table;

public class Column implements Cloneable, Comparable<Column> {
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
    /**
     * Indicates that the column might not allow <code>NULL</code> values.
     * <P>
     * A possible value for the column
     * <code>NULLABLE</code>
     * in the <code>ResultSet</code> returned by the method
     * <code>getColumns</code>.
     */
	public static final int columnNoNulls = 0;
    /**
     * Indicates that the column definitely allows <code>NULL</code> values.
     * <P>
     * A possible value for the column
     * <code>NULLABLE</code>
     * in the <code>ResultSet</code> returned by the method
     * <code>getColumns</code>.
     */
	public static final int columnNullable = 1;

    /**
     * Indicates that the nullability of columns is unknown.
     * <P>
     * A possible value for the column
     * <code>NULLABLE</code>
     * in the <code>ResultSet</code> returned by the method
     * <code>getColumns</code>.
     */
	public static final int columnNullableUnknown = 2;
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
	private String strCheckingType = "";
	private String autoincrement;
	private Database db;
	private List<Column> foreignKeys = new Vector<Column>();
	private List<Column> importedKeys = new Vector<Column>();
	private String typeName = "";
	private static Random rand = new Random();

	public String toString() {
		return "\n --------- " + this.tableName + "." + this.name + " --------- " + "\n schema        = " + this.schema
				+ "\n tableName     = " + this.tableName + "\n catalog       = " + this.catalog + "\n remarks       = "
				+ this.remarks + "\n defaultValue  = " + this.defaultValue + "\n decDigits     = " + this.decDigits
				+ "\n radix         = " + this.radix + "\n nullable      = " + this.nullable + "\n ordinal       = "
				+ this.ordinal + "\n size          = " + this.size + "\n type          = " + this.type + " "
				+ "\n isPrimaryKey  = " + (this.isPrimaryKey ? "true" : "false");
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
		this.name = null == name ? "" : name.replaceAll("\\W", "");
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
		if (remarks != null) {
			this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
		}
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
		return this.getNullable() != 0 ? "nullable" : "null not allowed";
	}

	public int getOrdinalPosition() {
		return this.ordinal;
	}

	public boolean isPrimaryKey() {
		return this.isPrimaryKey;
	}

	public String getFullName() {
		return this.tableName + "." + this.getName();
	}

	public String getConstName() {
		return this.getName().toUpperCase();
	}

	public String getIDConstName() {
		return (this.tableName + "_ID_" + this.getName()).toUpperCase();
	}
	public String getIDMaskConstName() {
		return (this.tableName + "_ID_" + this.getName()).toUpperCase() + "_MASK";
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private void tuoe() {
		throw new UnsupportedOperationException("Not supported yet: " + this.getTableName() + "." + this.getName() + " "
				+ this.getJavaTypeAsTypeName());
	}

	private void tiae() {
		throw new IllegalArgumentException("No primary type associated: " + this.getTableName() + "." + this.getName());
	}

	public int getMappedType() {
		switch (this.getType()) {
			case Types.ARRAY : {
				return M_ARRAY;
			}
			case Types.BIGINT : {
				return M_LONG;
			}
			case Types.BINARY : {
				return M_BYTES;
			}
			case Types.BIT : {
				return M_BOOLEAN;
			}
			case Types.BLOB : {
				return M_BLOB;
			}
			case Types.BOOLEAN : {
				return M_BOOLEAN;
			}
			case Types.CHAR : {
				return M_STRING;
			}
			case Types.CLOB : {
				return M_CLOB;
			}
			case Types.DATALINK : {
				return M_URL;
			}
			case Types.DATE : {
				if ("java.util.Date".equals(CodeWriter.dateClassName)) {
					return M_UTILDATE;
				}
				if ("java.sql.Date".equals(CodeWriter.dateClassName)) {
					return M_SQLDATE;
				}
				if ("java.util.Calendar".equals(CodeWriter.dateClassName)) {
					return M_CALENDAR;
				}
				this.tuoe();
			}
			case Types.DECIMAL : {
				return this.getDecimalDigits() > 0 ? M_BIGDECIMAL : M_LONG;
			}
			case Types.DISTINCT : {
				return M_OBJECT;
			}
			case Types.DOUBLE : {
				return M_DOUBLE;
			}
			case Types.FLOAT : {
				return M_DOUBLE;
			}
			case Types.INTEGER : {
				return this.getTypeName().equalsIgnoreCase("INT UNSIGNED") ? M_LONG : M_INTEGER;
			}
			case Types.JAVA_OBJECT : {
				return M_OBJECT;
			}
			case Types.LONGVARBINARY : {
				return M_BYTES;
			}
			case Types.LONGVARCHAR : {
				return M_STRING;
			}
			case Types.NUMERIC : {
				return this.getDecimalDigits() > 0 ? M_BIGDECIMAL : M_LONG;
			}
			case Types.OTHER : {
				return M_OBJECT;
			}
			case Types.REAL : {
				return M_FLOAT;
			}
			case Types.REF : {
				return M_REF;
			}
			case Types.SMALLINT : {
				return M_INTEGER;
			}
			case Types.STRUCT : {
				return M_OBJECT;
			}
			case Types.TIME : {
				if ("java.util.Date".equals(CodeWriter.timeClassName)) {
					return M_UTILDATE;
				}
				if ("java.sql.Time".equals(CodeWriter.timeClassName)) {
					return M_TIME;
				}
				if ("java.util.Calendar".equals(CodeWriter.timeClassName)) {
					return M_CALENDAR;
				}
				this.tuoe();
			}
			case Types.TIMESTAMP : {
				if ("java.util.Date".equals(CodeWriter.timestampClassName)) {
					return M_UTILDATE;
				}
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) {
					return M_TIMESTAMP;
				}
				if ("java.util.Calendar".equals(CodeWriter.timestampClassName)) {
					return M_CALENDAR;
				}
				this.tuoe();
			}
			case Types.TINYINT : {
				return M_INTEGER;
			}
			case Types.VARBINARY : {
				return M_BYTES;
			}
			case Types.VARCHAR : {
				return M_STRING;
			}
		}
		this.tuoe();
		return -1;
	}

	public String getQuerySetMethod() {
		switch (this.getType()) {
			case Types.ARRAY : {
				return "setArray";
			}
			case Types.BIGINT : {
				return "setBigDecimal";
			}
			case Types.BINARY : {
				return "setBytes";
			}
			case Types.BIT : {
				return "setBoolean";
			}
			case Types.BLOB : {
				return "setBlob";
			}
			case Types.BOOLEAN : {
				return "setBoolean";
			}
			case Types.CHAR : {
				return "setString";
			}
			case Types.CLOB : {
				return "setClob";
			}
			case Types.DATALINK : {
				return "setURL";
			}
			case Types.DATE : {
				return "setDate";
			}
			case Types.DECIMAL : {
				return this.getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
			}
			case Types.DISTINCT : {
				return "setObject";
			}
			case Types.DOUBLE : {
				return "setDouble";
			}
			case Types.FLOAT : {
				return "setDouble";
			}
			case Types.INTEGER : {
				return this.getTypeName().equalsIgnoreCase("INT UNSIGNED") ? "setLong" : "setInt";
			}
			case Types.JAVA_OBJECT : {
				return "setObject";
			}
			case Types.LONGVARBINARY : {
				return "setBytes";
			}
			case Types.LONGVARCHAR : {
				return "setString";
			}
			case Types.NUMERIC : {
				return this.getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
			}
			case Types.OTHER : {
				return "setObject";
			}
			case Types.REAL : {
				return "setFloat";
			}
			case Types.REF : {
				return "setRef";
			}
			case Types.SMALLINT : {
				return "setInt";
			}
			case Types.STRUCT : {
				return "setObject";
			}
			case Types.TIME : {
				if ("java.util.Date".equals(CodeWriter.timeClassName)) {
					return "setDate";
				}
				if ("java.sql.Time".equals(CodeWriter.timeClassName)) {
					return "setTime";
				}
				this.tuoe();
			}
			case Types.TIMESTAMP : {
				if ("java.util.Date".equals(CodeWriter.timestampClassName)) {
					return "setDate";
				}
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) {
					return "setTimestamp";
				}
				this.tuoe();
			}
			case Types.TINYINT : {
				return "setInt";
			}
			case Types.VARBINARY : {
				return "setBytes";
			}
			case Types.VARCHAR : {
				return "setString";
			}
		}
		this.tuoe();
		return "setObject";
	}

	public String getJavaType() {
		switch (this.getMappedType()) {
			case M_ARRAY : {
				return "Array";
			}
			case M_BIGDECIMAL : {
				return "java.math.BigDecimal";
			}
			case M_BOOLEAN : {
				return "Boolean";
			}
			case M_BYTES : {
				return CodeWriter.binaryClassName;
			}
			case M_CLOB : {
				// map Clob to java.lang.String
				return "String";
			}
			case M_SQLDATE : {
				return "java.sql.Date";
			}
			case M_UTILDATE : {
				return "java.util.Date";
			}
			case M_DOUBLE : {
				return "Double";
			}
			case M_FLOAT : {
				return "Float";
			}
			case M_BLOB : {
				return CodeWriter.binaryClassName;
			}
			case M_INTEGER : {
				return "Integer";
			}
			case M_LONG : {
				return "Long";
			}
			case M_REF : {
				return "Ref";
			}
			case M_STRING : {
				return "String";
			}
			case M_TIME : {
				return "java.sql.Time";
			}
			case M_TIMESTAMP : {
				return "java.sql.Timestamp";
			}
			case M_URL : {
				return "java.net.URL";
			}
			case M_OBJECT : {
				return "Object";
			}
			case M_CALENDAR : {
				return "java.util.Calendar";
			}
		}
		this.tiae();
		return null;
	}

	public boolean hasPrimaryType() {
		return this.getJavaPrimaryType() != null;
	}

	public String getJavaPrimaryType() throws IllegalArgumentException {
		int decimalDigits = this.getDecimalDigits();
		if ((this.type == Types.DECIMAL || this.type == Types.NUMERIC) && decimalDigits == 0) {
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
		switch (this.getMappedType()) {
			case M_BOOLEAN : {
				return "boolean";
			}
			case M_SQLDATE : {
				return "long";
			}
			case M_UTILDATE : {
				return "long";
			}
			case M_DOUBLE : {
				return "double";
			}
			case M_FLOAT : {
				return "float";
			}
			case M_INTEGER : {
				return "int";
			}
			case M_LONG : {
				return "long";
			}
			case M_TIME : {
				return "long";
			}
			case M_TIMESTAMP : {
				return "long";
			}
		}
		return null;
	}

	public String getJavaTypeAsTypeName() {
		switch (this.getType()) {
			case Types.ARRAY : {
				return "Types.ARRAY";
			}
			case Types.BIGINT : {
				return "Types.BIGINT";
			}
			case Types.BINARY : {
				return "Types.BINARY";
			}
			case Types.BIT : {
				return "Types.BIT";
			}
			case Types.BLOB : {
				return "Types.BLOB";
			}
			case Types.BOOLEAN : {
				return "Types.BOOLEAN";
			}
			case Types.CHAR : {
				return "Types.CHAR";
			}
			case Types.CLOB : {
				return "Types.CLOB";
			}
			case Types.DATALINK : {
				return "Types.DATALINK";
			}
			case Types.DATE : {
				return "Types.DATE";
			}
			case Types.DECIMAL : {
				return "Types.DECIMAL";
			}
			case Types.DISTINCT : {
				return "Types.DISTINCT";
			}
			case Types.DOUBLE : {
				return "Types.DOUBLE";
			}
			case Types.FLOAT : {
				return "Types.FLOAT";
			}
			case Types.INTEGER : {
				return "Types.INTEGER";
			}
			case Types.JAVA_OBJECT : {
				return "Types.JAVA_OBJECT";
			}
			case Types.LONGVARBINARY : {
				return "Types.LONGVARBINARY";
			}
			case Types.LONGVARCHAR : {
				return "Types.LONGVARCHAR";
			}
			case Types.NULL : {
				return "Types.NULL";
			}
			case Types.NUMERIC : {
				return "Types.NUMERIC";
			}
			case Types.OTHER : {
				return "Types.OTHER";
			}
			case Types.REAL : {
				return "Types.REAL";
			}
			case Types.REF : {
				return "Types.REF";
			}
			case Types.SMALLINT : {
				return "Types.SMALLINT";
			}
			case Types.STRUCT : {
				return "Types.STRUCT";
			}
			case Types.TIME : {
				return "Types.TIME";
			}
			case Types.TIMESTAMP : {
				return "Types.TIMESTAMP";
			}
			case Types.TINYINT : {
				return "Types.TINYINT";
			}
			case Types.VARBINARY : {
				return "Types.VARBINARY";
			}
			case Types.VARCHAR : {
				return "Types.VARCHAR";
			}
		}
		return "unkown SQL type " + this.getType();
	}

	public boolean isColumnNumeric() {
		switch (this.getMappedType()) {
			case M_BIGDECIMAL :
			case M_DOUBLE :
			case M_FLOAT :
			case M_INTEGER :
			case M_LONG : {
				return true;
			}
		}
		return false;
	}

	public boolean isString() {
		return M_STRING == this.getMappedType();
	}

	public boolean isDate() {
		switch (this.getMappedType()) {
			case M_UTILDATE :
			case M_TIME :
			case M_TIMESTAMP : {
				return true;
			}
		}
		return false;
	}

	public boolean isCalendar() {
		return this.getMappedType() == M_CALENDAR;
	}

	public boolean hasCompareTo() throws Exception {
		switch (this.getMappedType()) {
			case M_ARRAY : {
				return false;
			}
			case M_BIGDECIMAL : {
				return true;
			}
			case M_BOOLEAN : {
				return true;
			}
			case M_BYTES : {
				return CodeWriter.binaryIsByteBuffer();
			}
			case M_CLOB : {
				// Clob map to java.langString that has compareTo
				return true;
			}
			case M_SQLDATE : {
				return true;
			}
			case M_UTILDATE : {
				return true;
			}
			case M_DOUBLE : {
				return true;
			}
			case M_FLOAT : {
				return true;
			}
			case M_BLOB : {
				// Blob map to byte[] that has not compareTo
				return false;
			}
			case M_INTEGER : {
				return true;
			}
			case M_LONG : {
				return true;
			}
			case M_REF : {
				return false;
			}
			case M_STRING : {
				return true;
			}
			case M_TIME : {
				return true;
			}
			case M_TIMESTAMP : {
				return true;
			}
			case M_URL : {
				return false;
			}
			case M_OBJECT : {
				return false;
			}
		}
		return false;
	}

	public boolean useEqualsInSetter() throws Exception {
		switch (this.getMappedType()) {
			case M_BOOLEAN : {
				return true;
			}
			case M_BLOB : {
				// Blob map to byte[] that can use equals Object.method method to compare
				return true;
			}
		}
		return false;
	}

	public String getResultSetMethodObject(String pos) {
		return this.getResultSetMethodObject("rs", pos);
	}

	public String getResultSetMethodObject(String resultSet, String pos) {
		switch (this.getMappedType()) {
			case M_ARRAY : {
				return resultSet + ".getArray(" + pos + ")";
			}
			case M_LONG : {
				return CodeWriter.MGR_CLASS + ".getLong(" + resultSet + ", " + pos + ")";
			}
			case M_BYTES : {
				return CodeWriter.MGR_CLASS + ".getBytes(" + resultSet + ", " + pos + ")";
			}
			case M_BLOB : {
				return CodeWriter.MGR_CLASS + ".getBlob(" + resultSet + ", " + pos + ")";
			}
			case M_BOOLEAN : {
				return CodeWriter.MGR_CLASS + ".getBoolean(" + resultSet + ", " + pos + ")";
			}
			case M_STRING : {
				return resultSet + ".getString(" + pos + ")";
			}
			case M_CLOB : {
				return CodeWriter.MGR_CLASS + ".getClob(" + resultSet + ", " + pos + ")";
			}
			case M_URL : {
				return resultSet + ".getURL(" + pos + ")";
			}
			case M_BIGDECIMAL : {
				return resultSet + ".getBigDecimal(" + pos + ")";
			}
			case M_DOUBLE : {
				return CodeWriter.MGR_CLASS + ".getDouble(" + resultSet + ", " + pos + ")";
			}
			case M_FLOAT : {
				return CodeWriter.MGR_CLASS + ".getFloat(" + resultSet + ", " + pos + ")";
			}
			case M_INTEGER : {
				return CodeWriter.MGR_CLASS + ".getInteger(" + resultSet + ", " + pos + ")";
			}
			case M_OBJECT : {
				return resultSet + ".getObject(" + pos + ")";
			}
			case M_REF : {
				return resultSet + ".getRef(" + pos + ")";
			}
			case M_SQLDATE : {
				return resultSet + ".getDate(" + pos + ")";
			}
			case M_TIME : {
				return resultSet + ".getTime(" + pos + ")";
			}
			case M_TIMESTAMP : {
				return resultSet + ".getTimestamp(" + pos + ")";
			}
			case M_UTILDATE : {
				switch (this.getType()) {
					case Types.TIME : {
						return resultSet + ".getTime(" + pos + ")";
					}
					case Types.TIMESTAMP : {
						return resultSet + ".getTimestamp(" + pos + ")";
					}
					case Types.DATE : {
						return resultSet + ".getDate(" + pos + ")";
					}
				}
				this.tuoe();
			}
			case M_CALENDAR : {
				return CodeWriter.MGR_CLASS + ".getCalendar(" + resultSet + ", " + pos + ")";
			}
		}
		this.tuoe();
		return null;
	}

	public String getPreparedStatementMethod(String var, int pos) {
		return this.getPreparedStatementMethod(var, String.valueOf(pos));
	}

	public String getPreparedStatementMethod(String var, String pos) {
		StringBuffer sb = new StringBuffer();
		StringBuffer end = new StringBuffer();
		end.append(pos).append(", ").append(var).append(");");
        Pattern p = Pattern.compile("^((?:\"%+\"\\s*\\+)*)([\\w\\. \\(\\)-]*)((?:\\+\\s*\"%+\")*)$");
        Matcher m = p.matcher(var);
        if(!m.matches()){
        	throw new IllegalArgumentException(String.format("Not match found %s", var));
        }
		String v = m.group(2);
		sb.append("if (").append(v).append(" == null) { ps.setNull(").append(pos).append(", ")
				.append(this.getJavaTypeAsTypeName()).append("); } else { ");
		end.append(" }");
		switch (this.getMappedType()) {
			case M_ARRAY : {
				return sb.append("ps.setArray(").append(end).toString();
			}
			case M_LONG : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setLong(ps, ").append(end).toString();
			}
			case M_BYTES : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setBytes("+this.getJavaTypeAsTypeName()+",ps, ").append(end).toString();
			}
			case M_BLOB : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setBlob(ps, ").append(end).toString();
			}
			case M_BOOLEAN : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setBoolean(ps, ").append(end).toString();
			}
			case M_STRING : {
				return sb.append("ps.setString(").append(end).toString();
			}
			case M_CLOB : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setClob(ps, ").append(end).toString();
			}
			case M_URL : {
				return sb.append("ps.setURL(").append(end).toString();
			}
			case M_BIGDECIMAL : {
				return sb.append("ps.setBigDecimal(").append(end).toString();
			}
			case M_DOUBLE : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setDouble(ps, ").append(end).toString();
			}
			case M_INTEGER : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setInteger(ps, ").append(end).toString();
			}
			case M_OBJECT : {
				return sb.append("ps.setObject(").append(end).toString();
			}
			case M_FLOAT : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setFloat(ps, ").append(end).toString();
			}
			case M_SQLDATE : {
				return sb.append("ps.setDate(").append(end).toString();
			}
			case M_TIME : {
				return sb.append("ps.setTime(").append(end).toString();
			}
			case M_TIMESTAMP : {
				return sb.append("ps.setTimestamp(").append(end).toString();
			}
			case M_UTILDATE : {
				switch (this.getType()) {
					case Types.TIMESTAMP : {
						return sb.append("ps.setTimestamp(").append(pos).append(", new java.sql.Timestamp(").append(var)
								.append(".getTime())); }").toString();
					}
					case Types.DATE : {
						return sb.append("ps.setDate(").append(pos).append(", new java.sql.Date(").append(var)
								.append(".getTime())); }").toString();
					}
					case Types.TIME : {
						return sb.append("ps.setTime(").append(pos).append(", new java.sql.Time(").append(var)
								.append(".getTime())); }").toString();
					}
				}
				return null;
			}
			case M_CALENDAR : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setCalendar(ps, ").append(end).toString();
			}
			case M_REF : {
				sb.setLength(0);
				sb.append("ps.setRef(").append(end);
				sb.setLength(sb.length() - 2);
				return sb.toString();
			}
		}
		sb.setLength(0);
		sb.append("ps.setObject(").append(end);
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}

	public String getStringConvertionMethod() {
		switch (this.getMappedType()) {
			case M_BIGDECIMAL : {
				return "new java.math.BigDecimal";
			}
			case M_BOOLEAN : {
				return "new Boolean";
			}
			case M_SQLDATE : {
				return "new java.sql.Date";
			}
			case M_DOUBLE : {
				return "new Double";
			}
			case M_FLOAT : {
				return "new Float";
			}
			case M_INTEGER : {
				return "new Integer";
			}
			case M_LONG : {
				return "new Long";
			}
			case M_STRING : {
				return "";
			}
			case M_UTILDATE :
			case M_TIME :
			case M_TIMESTAMP : {
				if ("java.util.GregorianCalendar".equals(CodeWriter.dateClassName)) {
					return "GregorianDate";
				}
				return CodeWriter.MGR_CLASS + ".getDateFromString";
			}
		}
		System.err.println(
				" unknown mapped type " + this.getMappedType() + " (" + this.getType() + ") for " + this.getFullName());
		return "";
	}

	public String getDefaultWidget() {
		if (this.isForeignKey()) {
			return "SelectWidget";
		}
		if (this.isString() && (this.getSize() > 200 || this.getSize() == -1)) {
			return "TextAreaWidget";
		}
		switch (this.getMappedType()) {
			case M_BOOLEAN : {
				return "BooleanWidget";
			}
			case M_SQLDATE :
			case M_UTILDATE :
			case M_TIME :
			case M_TIMESTAMP : {
				return "DateWidget";
			}
			case M_BIGDECIMAL :
			case M_DOUBLE :
			case M_FLOAT :
			case M_INTEGER :
			case M_LONG : {
				return "NumericWidget";
			}
			case M_ARRAY :
			case M_BYTES :
			case M_CLOB :
			case M_REF :
			case M_STRING :
			case M_URL :
			case M_OBJECT : {
				return "InputWidget";
			}
		}
		System.err.println("type unknown for " + this.getFullName());
		return "";
	}

	public boolean isVersion() {
		if (!CodeWriter.optimisticLockType.equalsIgnoreCase("timestamp")) {
			return false;
		}
		if (!this.getName().equalsIgnoreCase(CodeWriter.optimisticLockColumn)) {
			return false;
		}
		if (this.getMappedType() == 11 || this.getMappedType() == 13) {
			return true;
		}
		return false;
	}

	public Table getTable() {
		return this.db.getTable(this.getTableName());
	}

	public void addForeignKey(Column col, String fkName, short keySeq) {
		this.foreignKeys.add(col);
		this.getTable().addForeignKey(this, fkName,keySeq);
	}

	public List<Column> getForeignKeys() {
		return this.foreignKeys;
	}

	public void addImportedKey(Column col) {
		this.importedKeys.add(col);
		this.getTable().addImportedKey(col);
	}

	public List<Column> getImportedKeys() {
		return this.importedKeys;
	}

	public int countImportedKeys() {
		return this.importedKeys.size();
	}

	public boolean isImportedKey() {
		if (this.countImportedKeys() > 0) {
			return true;
		}
		return false;
	}

	public Column getForeignColumn() {
		return (Column) this.foreignKeys.get(0);
	}

	public int countForeignKeys() {
		return this.foreignKeys.size();
	}

	public boolean isForeignKey() {
		if (this.countForeignKeys() > 0) {
			return true;
		}
		return false;
	}

	public String getPropertyTag() {
		return (this.getTableName() + "." + this.getName()).toLowerCase();
	}

	public String getDefaultRules() {
		String rule = "";
		rule = this.getNullable() == 0 && !this.isPrimaryKey() ? rule + " nullnotallowed" : rule + " nullallowed";
		if (this.getType() == 91 || this.getType() == 93) {
			rule = rule + " dateformat";
		}
		return rule;
	}

	public boolean columnFor(String webElement) {
		String includeProperty = ConfigHelper.getXPathProperty(
				(String) ("//table[@name='" + this.getTableName() + "']/frontend/" + webElement), (String) "include");
		String excludeProperty = ConfigHelper.getXPathProperty(
				(String) ("//table[@name='" + this.getTableName() + "']/frontend/" + webElement), (String) "exclude");
		String[] exclude = CodeWriter.getExplodedString((String) excludeProperty);
		String[] include = CodeWriter.getExplodedString((String) includeProperty);
		if (exclude.length == 0 && include.length == 0) {
			return this.getDefaultIncludeFor(webElement);
		}
		if (Main.isInArray((String[]) include, (String) this.getName())) {
			return true;
		}
		if (Main.isInArray((String[]) exclude, (String) this.getName())) {
			return false;
		}
		if (include.length == 0) {
			return true;
		}
		return false;
	}

	public boolean getDefaultIncludeFor(String webElement) {
		return true;
	}

	public String getDefaultValue() {
		if (Boolean.valueOf(CodeWriter.getProperty((String) "codewriter.generate.defaultvalue", (String) "false"))
				.booleanValue()) {
			return "";
		}
		String xmlDefaultValue = ConfigHelper.getColumnProperty((String) this.getTableName(), (String) this.getName(),
				(String) "defaultValue");
		if (xmlDefaultValue != null && !"".equals(xmlDefaultValue)) {
			return xmlDefaultValue;
		}
		if (null != this.defaultValue) {
			if (this.isColumnNumeric()) {
				try {
					double value = Double.parseDouble(this.defaultValue);
					switch (this.getMappedType()) {
						case 1 :
						case 10 :
						case 11 : {
							return this.generateNewAssignation(this.getJavaType(), this.defaultValue,
									String.valueOf(value));
						}
						case 7 :
						case 8 : {
							return this.generateNewAssignation(this.getJavaType(), String.valueOf(value),
									this.defaultValue);
						}
					}
					return "";
				} catch (NumberFormatException nfe) {
					return "";
				}
			}
			if (this.isDate()) {
				try {
					Date date = SimpleDateFormat.getInstance().parse(this.defaultValue);
					return "= SimpleDateFormat.getInstance().parse(" + this.defaultValue + "); // '"
							+ SimpleDateFormat.getInstance().format(date) + "'";
				} catch (ParseException pe) {
					return "= null; // '" + this.defaultValue + "'";
				}
			}
			if (this.isString()) {
				return "".equals(this.defaultValue) ? "" : "= \"" + this.defaultValue + '\"';
			}
			if (2 == this.getMappedType()) {
				return "= Boolean.valueOf(\"" + ("1".equals(this.defaultValue) ? "true" : this.defaultValue)
						+ "\").booleanValue(); // '" + this.defaultValue + "'";
			}
		}
		return this.defaultValue == null ? "" : "= " + this.defaultValue;
	}

	private String generateNewAssignation(String type, String parameter, String comment) {
		StringBuffer sb = new StringBuffer(70);
		sb.append("= new ").append(type);
		sb.append('(').append(parameter).append("); // '").append(comment).append("'");
		return sb.toString();
	}

	public String getRemarks() {
		String xmlDefaultValue = ConfigHelper.getColumnProperty((String) this.getTableName(), (String) this.getName(),
				(String) "description");
		if (xmlDefaultValue != null && !"".equals(xmlDefaultValue)) {
			return xmlDefaultValue;
		}
		return this.remarks == null ? "" : this.remarks;
	}

	public String getJavaName() {
		return this.convertName(this.getName());
	}

	public String getSampleData() {
		if (this.getNullable() > 1 && rand.nextInt(20) == 10) {
			return "";
		}
		if (this.isColumnNumeric()) {
			return "" + rand.nextInt(100);
		}
		if (this.isDate()) {
			Calendar rightNow = Calendar.getInstance();
			rightNow.set(2000 + rand.nextInt(20), 1 + rand.nextInt(12), 1 + rand.nextInt(28), rand.nextInt(23),
					rand.nextInt(60), rand.nextInt(60));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			return dateFormat.format(rightNow.getTime());
		}
		return StringUtilities.getSampleString((int) this.getSize());
	}

	private String escape(String s) {
		return StringUtilities.escape((String) s);
	}

	private String escape() {
		return this.escape(this.getName());
	}

	public String convertName(String columnName) {
		return StringUtilities.convertName((String) columnName, true);
	}

	public String convertName(Column col) {
		return this.convertName(col.getName());
	}

	public String convertName() {
		return this.convertName(this.name);
	}

	public String getImportedKeyVarName() {
		return this.convertName(this.escape() + "_collection");
	}

	public String getGetMethod() {
		return this.convertName("get_" + this.escape());
	}

	public String getSetMethod() {
		return this.convertName("set_" + this.escape());
	}

	public String getModifiedMethod() {
		return this.convertName("check_" + this.escape() + "_modified");
	}

	public String getInitializedMethod() {
		return this.convertName("check_" + this.escape() + "_initialized");
	}

	public String bitAndExpression(String varName){
		if(this.getTable().countColumns()>64){
			int pos = getOrdinalPosition()-1;
			return String.format("(%s[%d] & (1L << %d))",varName,pos>>6,pos & 0x3f);
		}else{
			return String.format("(%s & %s)", varName,getIDMaskConstName());
		}
	}
	
	public String bitORAssignExpression(String varName){
		if(this.getTable().countColumns()>64){
			int pos = getOrdinalPosition()-1;
			return String.format("%s[%d] |= (1L << %d)",varName,pos>>6,pos & 0x3f);
		}else{
			return String.format("%s |= %s", varName,getIDMaskConstName());
		}
	}	
	public String bitResetAssignExpression(String varName){
		if(this.getTable().countColumns()>64){
			int pos = getOrdinalPosition()-1;
			return String.format("%s[%d] &= (~(1L << %d))",varName,pos>>6,pos & 0x3f);
		}else{
			return String.format("%s &= (~%s)", varName,getIDMaskConstName());
		}
	}
	public String getWidgetMethod() {
		return this.convertName("get_" + this.escape() + "_widget");
	}

	public String getVarName() {
		return this.convertName(this.escape());
	}
	
	public String getFullVarName() {
		return this.convertName(this.getTable().asCoreClassNSP() +"_" + this.name );
	}
	public String getModifiedVarName() {
		return this.convertName(this.escape() + "_is_modified");
	}

	public String getInitializedVarName() {
		return this.convertName(this.escape() + "_is_initialized");
	}

	public String getImportedKeyModifiedVarName() {
		return this.convertName(this.escape() + "_collection_is_modified");
	}

	public String getImportedKeyInitializedVarName() {
		return this.convertName(this.escape() + "_collection_is_initialized");
	}

	public String getImportedKeyInitializedMethod() {
		return this.convertName("is_" + this.escape() + "_collection_initialized");
	}

	public String getImportedKeyGetMethod() {
		return this.convertName("get_" + this.escape() + "_collection");
	}

	public String getImportedKeyAddMethod() {
		return this.convertName("add_" + this.escape() + "");
	}

	public String getImportedKeySetMethod() {
		return this.convertName("set_" + this.escape() + "_collection");
	}

	public String getImportedKeyModifiedMethod() {
		return this.convertName("is_" + this.escape() + "_collection_modified");
	}

	public String getForeignKeyVarName() {
		return this.convertName(this.escape() + "_object");
	}

	public String getForeignKeyModifiedVarName() {
		return this.convertName(this.escape() + "_object_is_modified");
	}

	public String getForeignKeyInitializedVarName() {
		return this.convertName(this.escape() + "_object_is_initialized");
	}

	public String getForeignKeyInitializedMethod() {
		return this.convertName("is_" + this.escape() + "_object_initialized");
	}

	public String getForeignKeyGetMethod(String col) {
		return this.convertName("get_" + this.escape() + "_object");
	}

	public String getForeignKeySetMethod(String col) {
		return this.convertName("set_" + this.escape() + "_object");
	}

	public String getForeignKeyModifiedMethod(String col) {
		return this.convertName("is_" + this.escape() + "_object_modified");
	}
	
	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int compareTo(Column obj) {
		return  this.ordinal - obj.ordinal;
	}

	public String getAutoincrement() {
		return autoincrement;
	}

	public void setAutoincrement(String autoincrement) {
		this.autoincrement = autoincrement;
	}
	public boolean isAutoincrement(){
		return "YES".equals(this.autoincrement);
	}
	public boolean isNotNull(){
		return columnNoNulls  == this.nullable ;
	}
}
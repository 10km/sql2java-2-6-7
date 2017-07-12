/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import net.sourceforge.sql2java.CodeWriter;
import net.sourceforge.sql2java.ConfigHelper;
import net.sourceforge.sql2java.Database;
import net.sourceforge.sql2java.Main;
import net.sourceforge.sql2java.StringUtilities;
import net.sourceforge.sql2java.Table;

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
	private String strCheckingType = "";
	private Database db;
	private List foreignKeys = new Vector();
	private List importedKeys = new Vector();
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
			case 2003 : {
				return 0;
			}
			case -5 : {
				return 11;
			}
			case -2 : {
				return 3;
			}
			case -7 : {
				return 2;
			}
			case 2004 : {
				return 9;
			}
			case 16 : {
				return 2;
			}
			case 1 : {
				return 13;
			}
			case 2005 : {
				return 4;
			}
			case 70 : {
				return 16;
			}
			case 91 : {
				if ("java.util.Date".equals(CodeWriter.dateClassName)) {
					return 6;
				}
				if ("java.sql.Date".equals(CodeWriter.dateClassName)) {
					return 5;
				}
				if ("java.util.Calendar".equals(CodeWriter.dateClassName)) {
					return 18;
				}
				this.tuoe();
			}
			case 3 : {
				return this.getDecimalDigits() > 0 ? 1 : 11;
			}
			case 2001 : {
				return 17;
			}
			case 8 : {
				return 7;
			}
			case 6 : {
				return 7;
			}
			case 4 : {
				return this.getTypeName().equalsIgnoreCase("INT UNSIGNED") ? 11 : 10;
			}
			case 2000 : {
				return 17;
			}
			case -4 : {
				return 3;
			}
			case -1 : {
				return 13;
			}
			case 2 : {
				return this.getDecimalDigits() > 0 ? 1 : 11;
			}
			case 1111 : {
				return 17;
			}
			case 7 : {
				return 8;
			}
			case 2006 : {
				return 12;
			}
			case 5 : {
				return 10;
			}
			case 2002 : {
				return 17;
			}
			case 92 : {
				if ("java.util.Date".equals(CodeWriter.timeClassName)) {
					return 6;
				}
				if ("java.sql.Time".equals(CodeWriter.timeClassName)) {
					return 14;
				}
				if ("java.util.Calendar".equals(CodeWriter.timeClassName)) {
					return 18;
				}
				this.tuoe();
			}
			case 93 : {
				if ("java.util.Date".equals(CodeWriter.timestampClassName)) {
					return 6;
				}
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) {
					return 15;
				}
				if ("java.util.Calendar".equals(CodeWriter.timestampClassName)) {
					return 18;
				}
				this.tuoe();
			}
			case -6 : {
				return 10;
			}
			case -3 : {
				return 3;
			}
			case 12 : {
				return 13;
			}
		}
		this.tuoe();
		return -1;
	}

	public String getQuerySetMethod() {
		switch (this.getType()) {
			case 2003 : {
				return "setArray";
			}
			case -5 : {
				return "setBigDecimal";
			}
			case -2 : {
				return "setBytes";
			}
			case -7 : {
				return "setBoolean";
			}
			case 2004 : {
				return "setBlob";
			}
			case 16 : {
				return "setBoolean";
			}
			case 1 : {
				return "setString";
			}
			case 2005 : {
				return "setClob";
			}
			case 70 : {
				return "setURL";
			}
			case 91 : {
				return "setDate";
			}
			case 3 : {
				return this.getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
			}
			case 2001 : {
				return "setObject";
			}
			case 8 : {
				return "setDouble";
			}
			case 6 : {
				return "setDouble";
			}
			case 4 : {
				return this.getTypeName().equalsIgnoreCase("INT UNSIGNED") ? "setLong" : "setInt";
			}
			case 2000 : {
				return "setObject";
			}
			case -4 : {
				return "setBytes";
			}
			case -1 : {
				return "setString";
			}
			case 2 : {
				return this.getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
			}
			case 1111 : {
				return "setObject";
			}
			case 7 : {
				return "setFloat";
			}
			case 2006 : {
				return "setRef";
			}
			case 5 : {
				return "setInt";
			}
			case 2002 : {
				return "setObject";
			}
			case 92 : {
				if ("java.util.Date".equals(CodeWriter.timeClassName)) {
					return "setDate";
				}
				if ("java.sql.Time".equals(CodeWriter.timeClassName)) {
					return "setTime";
				}
				this.tuoe();
			}
			case 93 : {
				if ("java.util.Date".equals(CodeWriter.timestampClassName)) {
					return "setDate";
				}
				if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) {
					return "setTimestamp";
				}
				this.tuoe();
			}
			case -6 : {
				return "setInt";
			}
			case -3 : {
				return "setBytes";
			}
			case 12 : {
				return "setString";
			}
		}
		this.tuoe();
		return "setObject";
	}

	public String getJavaType() {
		switch (this.getMappedType()) {
			case 0 : {
				return "Array";
			}
			case 1 : {
				return "java.math.BigDecimal";
			}
			case 2 : {
				return "Boolean";
			}
			case 3 : {
				return "byte[]";
			}
			case 4 : {
				return "java.sql.Clob";
			}
			case 5 : {
				return "java.sql.Date";
			}
			case 6 : {
				return "java.util.Date";
			}
			case 7 : {
				return "Double";
			}
			case 8 : {
				return "Float";
			}
			case 9 : {
				return "java.sql.Blob";
			}
			case 10 : {
				return "Integer";
			}
			case 11 : {
				return "Long";
			}
			case 12 : {
				return "Ref";
			}
			case 13 : {
				return "String";
			}
			case 14 : {
				return "java.sql.Time";
			}
			case 15 : {
				return "java.sql.Timestamp";
			}
			case 16 : {
				return "java.net.URL";
			}
			case 17 : {
				return "Object";
			}
			case 18 : {
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
		if ((this.type == 3 || this.type == 2) && decimalDigits == 0) {
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
			case 2 : {
				return "boolean";
			}
			case 5 : {
				return "long";
			}
			case 6 : {
				return "long";
			}
			case 7 : {
				return "double";
			}
			case 8 : {
				return "float";
			}
			case 10 : {
				return "int";
			}
			case 11 : {
				return "long";
			}
			case 14 : {
				return "long";
			}
			case 15 : {
				return "long";
			}
		}
		return null;
	}

	public String getJavaTypeAsTypeName() {
		switch (this.getType()) {
			case 2003 : {
				return "Types.ARRAY";
			}
			case -5 : {
				return "Types.BIGINT";
			}
			case -2 : {
				return "Types.BINARY";
			}
			case -7 : {
				return "Types.BIT";
			}
			case 2004 : {
				return "Types.BLOB";
			}
			case 16 : {
				return "Types.BOOLEAN";
			}
			case 1 : {
				return "Types.CHAR";
			}
			case 2005 : {
				return "Types.CLOB";
			}
			case 70 : {
				return "Types.DATALINK";
			}
			case 91 : {
				return "Types.DATE";
			}
			case 3 : {
				return "Types.DECIMAL";
			}
			case 2001 : {
				return "Types.DISTINCT";
			}
			case 8 : {
				return "Types.DOUBLE";
			}
			case 6 : {
				return "Types.FLOAT";
			}
			case 4 : {
				return "Types.INTEGER";
			}
			case 2000 : {
				return "Types.JAVA_OBJECT";
			}
			case -4 : {
				return "Types.LONGVARBINARY";
			}
			case -1 : {
				return "Types.LONGVARCHAR";
			}
			case 0 : {
				return "Types.NULL";
			}
			case 2 : {
				return "Types.NUMERIC";
			}
			case 1111 : {
				return "Types.OTHER";
			}
			case 7 : {
				return "Types.REAL";
			}
			case 2006 : {
				return "Types.REF";
			}
			case 5 : {
				return "Types.SMALLINT";
			}
			case 2002 : {
				return "Types.STRUCT";
			}
			case 92 : {
				return "Types.TIME";
			}
			case 93 : {
				return "Types.TIMESTAMP";
			}
			case -6 : {
				return "Types.TINYINT";
			}
			case -3 : {
				return "Types.VARBINARY";
			}
			case 12 : {
				return "Types.VARCHAR";
			}
		}
		return "unkown SQL type " + this.getType();
	}

	public boolean isColumnNumeric() {
		switch (this.getMappedType()) {
			case 1 :
			case 7 :
			case 8 :
			case 10 :
			case 11 : {
				return true;
			}
		}
		return false;
	}

	public boolean isString() {
		return 13 == this.getMappedType();
	}

	public boolean isDate() {
		switch (this.getMappedType()) {
			case 6 :
			case 14 :
			case 15 : {
				return true;
			}
		}
		return false;
	}

	public boolean isCalendar() {
		return this.getMappedType() == 18;
	}

	public boolean hasCompareTo() throws Exception {
		switch (this.getMappedType()) {
			case 0 : {
				return false;
			}
			case 1 : {
				return true;
			}
			case 2 : {
				return false;
			}
			case 3 : {
				return false;
			}
			case 4 : {
				return false;
			}
			case 5 : {
				return true;
			}
			case 6 : {
				return true;
			}
			case 7 : {
				return true;
			}
			case 8 : {
				return true;
			}
			case 10 : {
				return true;
			}
			case 11 : {
				return true;
			}
			case 12 : {
				return false;
			}
			case 13 : {
				return true;
			}
			case 14 : {
				return true;
			}
			case 15 : {
				return true;
			}
			case 16 : {
				return false;
			}
			case 17 : {
				return false;
			}
		}
		return false;
	}

	public boolean useEqualsInSetter() throws Exception {
		switch (this.getMappedType()) {
			case 2 : {
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
			case 0 : {
				return resultSet + ".getArray(" + pos + ")";
			}
			case 11 : {
				return CodeWriter.MGR_CLASS + ".getLong(" + resultSet + ", " + pos + ")";
			}
			case 3 : {
				return resultSet + ".getBytes(" + pos + ")";
			}
			case 9 : {
				return resultSet + ".getBlob(" + pos + ")";
			}
			case 2 : {
				return CodeWriter.MGR_CLASS + ".getBoolean(" + resultSet + ", " + pos + ")";
			}
			case 13 : {
				return resultSet + ".getString(" + pos + ")";
			}
			case 4 : {
				return resultSet + ".getClob(" + pos + ")";
			}
			case 16 : {
				return resultSet + ".getURL(" + pos + ")";
			}
			case 1 : {
				return resultSet + ".getBigDecimal(" + pos + ")";
			}
			case 7 : {
				return CodeWriter.MGR_CLASS + ".getDouble(" + resultSet + ", " + pos + ")";
			}
			case 8 : {
				return CodeWriter.MGR_CLASS + ".getFloat(" + resultSet + ", " + pos + ")";
			}
			case 10 : {
				return CodeWriter.MGR_CLASS + ".getInteger(" + resultSet + ", " + pos + ")";
			}
			case 17 : {
				return resultSet + ".getObject(" + pos + ")";
			}
			case 12 : {
				return resultSet + ".getRef(" + pos + ")";
			}
			case 5 : {
				return resultSet + ".getDate(" + pos + ")";
			}
			case 14 : {
				return resultSet + ".getTime(" + pos + ")";
			}
			case 15 : {
				return resultSet + ".getTimestamp(" + pos + ")";
			}
			case 6 : {
				switch (this.getType()) {
					case 92 : {
						return resultSet + ".getTime(" + pos + ")";
					}
					case 93 : {
						return resultSet + ".getTimestamp(" + pos + ")";
					}
					case 91 : {
						return resultSet + ".getDate(" + pos + ")";
					}
				}
				this.tuoe();
			}
			case 18 : {
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
		if ('\"' != var.charAt(0)) {
			sb.append("if (").append(var).append(" == null) { ps.setNull(").append(pos).append(", ")
					.append(this.getJavaTypeAsTypeName()).append("); } else { ");
			end.append(" }");
		}
		switch (this.getMappedType()) {
			case 0 : {
				return sb.append("ps.setArray(").append(end).toString();
			}
			case 11 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setLong(ps, ").append(end).toString();
			}
			case 3 : {
				return sb.append("ps.setBytes(").append(end).toString();
			}
			case 9 : {
				return sb.append("ps.setBlob(").append(end).toString();
			}
			case 2 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setBoolean(ps, ").append(end).toString();
			}
			case 13 : {
				return sb.append("ps.setString(").append(end).toString();
			}
			case 4 : {
				return sb.append("ps.setClob(").append(end).toString();
			}
			case 16 : {
				return sb.append("ps.setURL(").append(end).toString();
			}
			case 1 : {
				return sb.append("ps.setBigDecimal(").append(end).toString();
			}
			case 7 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setDouble(ps, ").append(end).toString();
			}
			case 10 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setInteger(ps, ").append(end).toString();
			}
			case 17 : {
				return sb.append("ps.setObject(").append(end).toString();
			}
			case 8 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setFloat(ps, ").append(end).toString();
			}
			case 5 : {
				return sb.append("ps.setDate(").append(end).toString();
			}
			case 14 : {
				return sb.append("ps.setTime(").append(end).toString();
			}
			case 15 : {
				return sb.append("ps.setTimestamp(").append(end).toString();
			}
			case 6 : {
				switch (this.getType()) {
					case 93 : {
						return sb.append("ps.setTimestamp(").append(pos).append(", new java.sql.Timestamp(").append(var)
								.append(".getTime())); }").toString();
					}
					case 91 : {
						return sb.append("ps.setDate(").append(pos).append(", new java.sql.Date(").append(var)
								.append(".getTime())); }").toString();
					}
					case 92 : {
						return sb.append("ps.setTime(").append(pos).append(", new java.sql.Time(").append(var)
								.append(".getTime())); }").toString();
					}
				}
				return null;
			}
			case 18 : {
				return sb.append(CodeWriter.MGR_CLASS).append(".setCalendar(ps, ").append(end).toString();
			}
			case 12 : {
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
			case 1 : {
				return "new java.math.BigDecimal";
			}
			case 2 : {
				return "new Boolean";
			}
			case 5 : {
				return "new java.sql.Date";
			}
			case 7 : {
				return "new Double";
			}
			case 8 : {
				return "new Float";
			}
			case 10 : {
				return "new Integer";
			}
			case 11 : {
				return "new Long";
			}
			case 13 : {
				return "";
			}
			case 6 :
			case 14 :
			case 15 : {
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
			case 2 : {
				return "BooleanWidget";
			}
			case 5 :
			case 6 :
			case 14 :
			case 15 : {
				return "DateWidget";
			}
			case 1 :
			case 7 :
			case 8 :
			case 10 :
			case 11 : {
				return "NumericWidget";
			}
			case 0 :
			case 3 :
			case 4 :
			case 12 :
			case 13 :
			case 16 :
			case 17 : {
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

	public void addForeignKey(Column col) {
		this.foreignKeys.add(col);
		this.getTable().addForeignKey(this);
	}

	public List getForeignKeys() {
		return this.foreignKeys;
	}

	public void addImportedKey(Column col) {
		this.importedKeys.add(col);
		this.getTable().addImportedKey(col);
	}

	public List getImportedKeys() {
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
		return StringUtilities.convertName((String) columnName, (boolean) true);
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
		return this.convertName("is_" + this.escape() + "_modified");
	}

	public String getInitializedMethod() {
		return this.convertName("is_" + this.escape() + "_initialized");
	}

	public String getWidgetMethod() {
		return this.convertName("get_" + this.escape() + "_widget");
	}

	public String getVarName() {
		return this.convertName(this.escape());
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

	public int compareTo(Object obj) {
		return ((Column) obj).ordinal - this.ordinal;
	}
}
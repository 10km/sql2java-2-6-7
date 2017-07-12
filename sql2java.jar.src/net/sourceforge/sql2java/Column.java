/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Column implements Cloneable, Comparable
/*     */ {
/*     */   public static final int M_ARRAY = 0;
/*     */   public static final int M_BIGDECIMAL = 1;
/*     */   public static final int M_BOOLEAN = 2;
/*     */   public static final int M_BYTES = 3;
/*     */   public static final int M_CLOB = 4;
/*     */   public static final int M_SQLDATE = 5;
/*     */   public static final int M_UTILDATE = 6;
/*     */   public static final int M_DOUBLE = 7;
/*     */   public static final int M_FLOAT = 8;
/*     */   public static final int M_BLOB = 9;
/*     */   public static final int M_INTEGER = 10;
/*     */   public static final int M_LONG = 11;
/*     */   public static final int M_REF = 12;
/*     */   public static final int M_STRING = 13;
/*     */   public static final int M_TIME = 14;
/*     */   public static final int M_TIMESTAMP = 15;
/*     */   public static final int M_URL = 16;
/*     */   public static final int M_OBJECT = 17;
/*     */   public static final int M_CALENDAR = 18;
/*     */   private String catalog;
/*     */   private String schema;
/*     */   private String tableName;
/*     */   private String name;
/*     */   private String remarks;
/*     */   private String defaultValue;
/*     */   private int size;
/*     */   private int decDigits;
/*     */   private int radix;
/*     */   private int nullable;
/*     */   private int ordinal;
/*     */   private short type;
/*     */   private boolean isPrimaryKey;
/*  40 */   private String strCheckingType = "";
/*     */   private Database db;
/*  42 */   private List foreignKeys = new java.util.Vector();
/*  43 */   private List importedKeys = new java.util.Vector();
/*  44 */   private String typeName = "";
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  50 */     return "\n --------- " + this.tableName + "." + this.name + " --------- " + "\n schema        = " + this.schema + "\n tableName     = " + this.tableName + "\n catalog       = " + this.catalog + "\n remarks       = " + this.remarks + "\n defaultValue  = " + this.defaultValue + "\n decDigits     = " + this.decDigits + "\n radix         = " + this.radix + "\n nullable      = " + this.nullable + "\n ordinal       = " + this.ordinal + "\n size          = " + this.size + "\n type          = " + this.type + " " + "\n isPrimaryKey  = " + (this.isPrimaryKey ? "true" : "false");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  65 */   public void setCheckingType(String strValue) { this.strCheckingType = strValue; }
/*  66 */   public String getCheckingType() { return this.strCheckingType; }
/*  67 */   public void setDatabase(Database db) { this.db = db; }
/*  68 */   public void setCatalog(String catalog) { this.catalog = catalog; }
/*  69 */   public void setSchema(String schema) { this.schema = schema; }
/*  70 */   public void setTableName(String tableName) { this.tableName = tableName; }
/*  71 */   public void setName(String name) { this.name = (null == name ? "" : name.replaceAll("\\W", "")); }
/*  72 */   public void setType(short type) { this.type = type; }
/*  73 */   public void setSize(int size) { this.size = size; }
/*  74 */   public void setDecimalDigits(int decDigits) { this.decDigits = decDigits; }
/*  75 */   public void setRadix(int radix) { this.radix = radix; }
/*  76 */   public void setNullable(int nullable) { this.nullable = nullable; }
/*     */   
/*  78 */   public void setRemarks(String remarks) { if (remarks != null)
/*  79 */       this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
/*     */   }
/*     */   
/*  82 */   public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
/*  83 */   public void setOrdinalPosition(int ordinal) { this.ordinal = ordinal; }
/*  84 */   public void isPrimaryKey(boolean isKey) { this.isPrimaryKey = isKey; }
/*     */   
/*  86 */   public String getCatalog() { return this.catalog; }
/*  87 */   public String getSchema() { return this.schema; }
/*  88 */   public String getTableName() { return this.tableName; }
/*  89 */   public String getName() { return this.name; }
/*  90 */   public short getType() { return this.type; }
/*  91 */   public int getSize() { return this.size; }
/*  92 */   public int getDecimalDigits() { return this.decDigits; }
/*  93 */   public int getRadix() { return this.radix; }
/*  94 */   public int getNullable() { return this.nullable; }
/*  95 */   public String getNullableAsString() { return getNullable() != 0 ? "nullable" : "null not allowed"; }
/*  96 */   public int getOrdinalPosition() { return this.ordinal; }
/*  97 */   public boolean isPrimaryKey() { return this.isPrimaryKey; }
/*  98 */   public String getFullName() { return this.tableName + "." + getName(); }
/*  99 */   public String getConstName() { return getName().toUpperCase(); }
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 102 */     return super.clone();
/*     */   }
/*     */   
/*     */   private void tuoe() {
/* 106 */     throw new UnsupportedOperationException("Not supported yet: " + getTableName() + "." + getName() + " " + getJavaTypeAsTypeName());
/*     */   }
/*     */   
/*     */   private void tiae() {
/* 110 */     throw new IllegalArgumentException("No primary type associated: " + getTableName() + "." + getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMappedType()
/*     */   {
/* 117 */     switch (getType()) {
/* 118 */     case 2003:  return 0;
/* 119 */     case -5:  return 11;
/* 120 */     case -2:  return 3;
/* 121 */     case -7:  return 2;
/* 122 */     case 2004:  return 9;
/* 123 */     case 16:  return 2;
/* 124 */     case 1:  return 13;
/* 125 */     case 2005:  return 4;
/* 126 */     case 70:  return 16;
/*     */     case 91: 
/* 128 */       if ("java.util.Date".equals(CodeWriter.dateClassName)) return 6;
/* 129 */       if ("java.sql.Date".equals(CodeWriter.dateClassName)) return 5;
/* 130 */       if ("java.util.Calendar".equals(CodeWriter.dateClassName)) return 18;
/* 131 */       tuoe();
/* 132 */     case 3:  return getDecimalDigits() > 0 ? 1 : 11;
/* 133 */     case 2001:  return 17;
/* 134 */     case 8:  return 7;
/* 135 */     case 6:  return 7;
/*     */     case 4: 
/* 137 */       return getTypeName().equalsIgnoreCase("INT UNSIGNED") ? 11 : 10;
/* 138 */     case 2000:  return 17;
/* 139 */     case -4:  return 3;
/* 140 */     case -1:  return 13;
/*     */     
/*     */ 
/*     */     case 2: 
/* 144 */       return getDecimalDigits() > 0 ? 1 : 11;
/* 145 */     case 1111:  return 17;
/* 146 */     case 7:  return 8;
/* 147 */     case 2006:  return 12;
/* 148 */     case 5:  return 10;
/* 149 */     case 2002:  return 17;
/*     */     case 92: 
/* 151 */       if ("java.util.Date".equals(CodeWriter.timeClassName)) return 6;
/* 152 */       if ("java.sql.Time".equals(CodeWriter.timeClassName)) return 14;
/* 153 */       if ("java.util.Calendar".equals(CodeWriter.timeClassName)) return 18;
/* 154 */       tuoe();
/*     */     case 93: 
/* 156 */       if ("java.util.Date".equals(CodeWriter.timestampClassName)) return 6;
/* 157 */       if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) return 15;
/* 158 */       if ("java.util.Calendar".equals(CodeWriter.timestampClassName)) return 18;
/* 159 */       tuoe();
/* 160 */     case -6:  return 10;
/* 161 */     case -3:  return 3;
/* 162 */     case 12:  return 13; }
/* 163 */     tuoe();
/*     */     
/* 165 */     return -1;
/*     */   }
/*     */   
/*     */   public String getQuerySetMethod()
/*     */   {
/* 170 */     switch (getType()) {
/* 171 */     case 2003:  return "setArray";
/* 172 */     case -5:  return "setBigDecimal";
/* 173 */     case -2:  return "setBytes";
/* 174 */     case -7:  return "setBoolean";
/* 175 */     case 2004:  return "setBlob";
/* 176 */     case 16:  return "setBoolean";
/* 177 */     case 1:  return "setString";
/* 178 */     case 2005:  return "setClob";
/* 179 */     case 70:  return "setURL";
/* 180 */     case 91:  return "setDate";
/* 181 */     case 3:  return getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
/* 182 */     case 2001:  return "setObject";
/* 183 */     case 8:  return "setDouble";
/* 184 */     case 6:  return "setDouble";
/*     */     case 4: 
/* 186 */       return getTypeName().equalsIgnoreCase("INT UNSIGNED") ? "setLong" : "setInt";
/* 187 */     case 2000:  return "setObject";
/* 188 */     case -4:  return "setBytes";
/* 189 */     case -1:  return "setString";
/*     */     
/*     */ 
/*     */     case 2: 
/* 193 */       return getDecimalDigits() > 0 ? "setBigDecimal" : "setLong";
/* 194 */     case 1111:  return "setObject";
/* 195 */     case 7:  return "setFloat";
/* 196 */     case 2006:  return "setRef";
/* 197 */     case 5:  return "setInt";
/* 198 */     case 2002:  return "setObject";
/*     */     case 92: 
/* 200 */       if ("java.util.Date".equals(CodeWriter.timeClassName)) return "setDate";
/* 201 */       if ("java.sql.Time".equals(CodeWriter.timeClassName)) return "setTime";
/* 202 */       tuoe();
/*     */     case 93: 
/* 204 */       if ("java.util.Date".equals(CodeWriter.timestampClassName)) return "setDate";
/* 205 */       if ("java.sql.Timestamp".equals(CodeWriter.timestampClassName)) return "setTimestamp";
/* 206 */       tuoe();
/* 207 */     case -6:  return "setInt";
/* 208 */     case -3:  return "setBytes";
/* 209 */     case 12:  return "setString"; }
/* 210 */     tuoe();
/*     */     
/* 212 */     return "setObject";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getJavaType()
/*     */   {
/* 220 */     switch (getMappedType()) {
/*     */     case 0: 
/* 222 */       return "Array";
/* 223 */     case 1:  return "java.math.BigDecimal";
/* 224 */     case 2:  return "Boolean";
/* 225 */     case 3:  return "byte[]";
/* 226 */     case 4:  return "Clob";
/* 227 */     case 5:  return "java.sql.Date";
/* 228 */     case 6:  return "java.util.Date";
/* 229 */     case 7:  return "Double";
/* 230 */     case 8:  return "Float";
/* 231 */     case 10:  return "Integer";
/* 232 */     case 11:  return "Long";
/* 233 */     case 12:  return "Ref";
/* 234 */     case 13:  return "String";
/* 235 */     case 14:  return "java.sql.Time";
/* 236 */     case 15:  return "java.sql.Timestamp";
/* 237 */     case 16:  return "java.net.URL";
/* 238 */     case 17:  return "Object";
/* 239 */     case 18:  return "java.util.Calendar"; }
/* 240 */     tiae();
/*     */     
/* 242 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasPrimaryType()
/*     */   {
/* 250 */     return getJavaPrimaryType() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getJavaPrimaryType()
/*     */     throws IllegalArgumentException
/*     */   {
/* 259 */     int decimalDigits = getDecimalDigits();
/* 260 */     if (((this.type == 3) || (this.type == 2)) && (decimalDigits == 0)) {
/* 261 */       if (this.size == 1) {
/* 262 */         return "boolean";
/*     */       }
/* 264 */       if (this.size < 3) {
/* 265 */         return "byte";
/*     */       }
/* 267 */       if (this.size < 5) {
/* 268 */         return "short";
/*     */       }
/* 270 */       if (this.size < 10) {
/* 271 */         return "int";
/*     */       }
/* 273 */       if (this.size < 19) {
/* 274 */         return "long";
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 280 */     switch (getMappedType()) {
/*     */     case 2: 
/* 282 */       return "boolean";
/* 283 */     case 5:  return "long";
/* 284 */     case 6:  return "long";
/* 285 */     case 7:  return "double";
/* 286 */     case 8:  return "float";
/* 287 */     case 10:  return "int";
/* 288 */     case 11:  return "long";
/* 289 */     case 14:  return "long";
/* 290 */     case 15:  return "long";
/*     */     }
/*     */     
/* 293 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getJavaTypeAsTypeName()
/*     */   {
/* 301 */     switch (getType()) {
/*     */     case 2003: 
/* 303 */       return "Types.ARRAY";
/* 304 */     case -5:  return "Types.BIGINT";
/* 305 */     case -2:  return "Types.BINARY";
/* 306 */     case -7:  return "Types.BIT";
/* 307 */     case 2004:  return "Types.BLOB";
/* 308 */     case 16:  return "Types.BOOLEAN";
/* 309 */     case 1:  return "Types.CHAR";
/* 310 */     case 2005:  return "Types.CLOB";
/* 311 */     case 70:  return "Types.DATALINK";
/* 312 */     case 91:  return "Types.DATE";
/* 313 */     case 3:  return "Types.DECIMAL";
/* 314 */     case 2001:  return "Types.DISTINCT";
/* 315 */     case 8:  return "Types.DOUBLE";
/* 316 */     case 6:  return "Types.FLOAT";
/* 317 */     case 4:  return "Types.INTEGER";
/* 318 */     case 2000:  return "Types.JAVA_OBJECT";
/* 319 */     case -4:  return "Types.LONGVARBINARY";
/* 320 */     case -1:  return "Types.LONGVARCHAR";
/* 321 */     case 0:  return "Types.NULL";
/* 322 */     case 2:  return "Types.NUMERIC";
/* 323 */     case 1111:  return "Types.OTHER";
/* 324 */     case 7:  return "Types.REAL";
/* 325 */     case 2006:  return "Types.REF";
/* 326 */     case 5:  return "Types.SMALLINT";
/* 327 */     case 2002:  return "Types.STRUCT";
/* 328 */     case 92:  return "Types.TIME";
/* 329 */     case 93:  return "Types.TIMESTAMP";
/* 330 */     case -6:  return "Types.TINYINT";
/* 331 */     case -3:  return "Types.VARBINARY";
/* 332 */     case 12:  return "Types.VARCHAR"; }
/* 333 */     return "unkown SQL type " + getType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isColumnNumeric()
/*     */   {
/* 342 */     switch (getMappedType()) {
/*     */     case 1: 
/*     */     case 7: 
/*     */     case 8: 
/*     */     case 10: 
/*     */     case 11: 
/* 348 */       return true; }
/* 349 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isString()
/*     */   {
/* 358 */     return 13 == getMappedType();
/*     */   }
/*     */   
/*     */   public boolean isDate()
/*     */   {
/* 363 */     switch (getMappedType()) {
/*     */     case 6: 
/*     */     case 14: 
/*     */     case 15: 
/* 367 */       return true;
/*     */     }
/* 369 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCalendar()
/*     */   {
/* 375 */     return getMappedType() == 18;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasCompareTo()
/*     */     throws Exception
/*     */   {
/* 383 */     switch (getMappedType()) {
/*     */     case 0: 
/* 385 */       return false;
/* 386 */     case 1:  return true;
/* 387 */     case 2:  return false;
/* 388 */     case 3:  return false;
/* 389 */     case 4:  return false;
/* 390 */     case 5:  return true;
/* 391 */     case 6:  return true;
/* 392 */     case 7:  return true;
/* 393 */     case 8:  return true;
/* 394 */     case 10:  return true;
/* 395 */     case 11:  return true;
/* 396 */     case 12:  return false;
/* 397 */     case 13:  return true;
/* 398 */     case 14:  return true;
/* 399 */     case 15:  return true;
/* 400 */     case 16:  return false;
/* 401 */     case 17:  return false; }
/* 402 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean useEqualsInSetter()
/*     */     throws Exception
/*     */   {
/* 411 */     switch (getMappedType()) {
/*     */     case 2: 
/* 413 */       return true; }
/* 414 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getResultSetMethodObject(String pos)
/*     */   {
/* 424 */     return getResultSetMethodObject("rs", pos);
/*     */   }
/*     */   
/*     */   public String getResultSetMethodObject(String resultSet, String pos)
/*     */   {
/* 429 */     switch (getMappedType()) {
/*     */     case 0: 
/* 431 */       return resultSet + ".getArray(" + pos + ")";
/* 432 */     case 11:  return CodeWriter.MGR_CLASS + ".getLong(" + resultSet + ", " + pos + ")";
/* 433 */     case 3:  return resultSet + ".getBytes(" + pos + ")";
/* 434 */     case 9:  return resultSet + ".getBlob(" + pos + ")";
/* 435 */     case 2:  return CodeWriter.MGR_CLASS + ".getBoolean(" + resultSet + ", " + pos + ")";
/* 436 */     case 13:  return resultSet + ".getString(" + pos + ")";
/* 437 */     case 4:  return resultSet + ".getClob(" + pos + ")";
/* 438 */     case 16:  return resultSet + ".getURL(" + pos + ")";
/* 439 */     case 1:  return resultSet + ".getBigDecimal(" + pos + ")";
/* 440 */     case 7:  return CodeWriter.MGR_CLASS + ".getDouble(" + resultSet + ", " + pos + ")";
/* 441 */     case 8:  return CodeWriter.MGR_CLASS + ".getFloat(" + resultSet + ", " + pos + ")";
/* 442 */     case 10:  return CodeWriter.MGR_CLASS + ".getInteger(" + resultSet + ", " + pos + ")";
/*     */     case 17: 
/* 444 */       return resultSet + ".getObject(" + pos + ")";
/* 445 */     case 12:  return resultSet + ".getRef(" + pos + ")";
/* 446 */     case 5:  return resultSet + ".getDate(" + pos + ")";
/* 447 */     case 14:  return resultSet + ".getTime(" + pos + ")";
/* 448 */     case 15:  return resultSet + ".getTimestamp(" + pos + ")";
/*     */     case 6: 
/* 450 */       switch (getType()) {
/* 451 */       case 92:  return resultSet + ".getTime(" + pos + ")";
/* 452 */       case 93:  return resultSet + ".getTimestamp(" + pos + ")";
/* 453 */       case 91:  return resultSet + ".getDate(" + pos + ")"; }
/* 454 */       tuoe();
/*     */     case 18: 
/* 456 */       return CodeWriter.MGR_CLASS + ".getCalendar(" + resultSet + ", " + pos + ")"; }
/* 457 */     tuoe();
/*     */     
/* 459 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPreparedStatementMethod(String var, int pos)
/*     */   {
/* 482 */     return getPreparedStatementMethod(var, String.valueOf(pos));
/*     */   }
/*     */   
/*     */   public String getPreparedStatementMethod(String var, String pos)
/*     */   {
/* 487 */     StringBuffer sb = new StringBuffer();
/* 488 */     StringBuffer end = new StringBuffer();
/* 489 */     end.append(pos).append(", ").append(var).append(");");
/* 490 */     if ('"' != var.charAt(0)) {
/* 491 */       sb.append("if (").append(var).append(" == null) { ps.setNull(").append(pos).append(", ").append(getJavaTypeAsTypeName()).append("); } else { ");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 498 */       end.append(" }");
/*     */     }
/* 500 */     switch (getMappedType()) {
/*     */     case 0: 
/* 502 */       return "ps.setArray(" + end;
/* 503 */     case 11:  return CodeWriter.MGR_CLASS + ".setLong(ps, " + end;
/* 504 */     case 3:  return "ps.setBytes(" + end;
/* 505 */     case 9:  return "ps.setBlob(" + end;
/* 506 */     case 2:  return CodeWriter.MGR_CLASS + ".setBoolean(ps, " + end;
/* 507 */     case 13:  return "ps.setString(" + end;
/* 508 */     case 4:  return "ps.setClob(" + end;
/* 509 */     case 16:  return "ps.setURL(" + end;
/* 510 */     case 1:  return "ps.setBigDecimal(" + end;
/* 511 */     case 7:  return CodeWriter.MGR_CLASS + ".setDouble(ps, " + end;
/* 512 */     case 10:  return CodeWriter.MGR_CLASS + ".setInteger(ps, " + end;
/* 513 */     case 17:  return "ps.setObject(" + end;
/* 514 */     case 8:  return CodeWriter.MGR_CLASS + ".setFloat(ps, " + end;
/* 515 */     case 5:  return "ps.setDate(" + end;
/* 516 */     case 14:  return "ps.setTime(" + end;
/* 517 */     case 15:  return "ps.setTimestamp(" + end;
/*     */     case 6: 
/* 519 */       switch (getType()) {
/* 520 */       case 93:  return "ps.setTimestamp(" + pos + ", new java.sql.Timestamp(" + var + ".getTime())); }";
/* 521 */       case 91:  return "ps.setDate(" + pos + ", new java.sql.Date(" + var + ".getTime())); }";
/* 522 */       case 92:  return "ps.setTime(" + pos + ", new java.sql.Time(" + var + ".getTime())); }"; }
/* 523 */       return null;
/*     */     case 18: 
/* 525 */       return CodeWriter.MGR_CLASS + ".setCalendar(ps, " + end;
/* 526 */     case 12:  sb.setLength(0);sb.append("ps.setRef(").append(end);sb.setLength(sb.length() - 2);return sb.toString(); }
/* 527 */     sb.setLength(0);sb.append("ps.setObject(").append(end);sb.setLength(sb.length() - 2);return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStringConvertionMethod()
/*     */   {
/* 536 */     switch (getMappedType()) {
/*     */     case 1: 
/* 538 */       return "new java.math.BigDecimal";
/* 539 */     case 2:  return "new Boolean";
/* 540 */     case 5:  return "new java.sql.Date";
/* 541 */     case 7:  return "new Double";
/* 542 */     case 8:  return "new Float";
/* 543 */     case 10:  return "new Integer";
/* 544 */     case 11:  return "new Long";
/* 545 */     case 13:  return "";
/*     */     case 6: 
/*     */     case 14: 
/*     */     case 15: 
/* 549 */       if ("java.util.GregorianCalendar".equals(CodeWriter.dateClassName)) {
/* 550 */         return "GregorianDate";
/*     */       }
/* 552 */       return CodeWriter.MGR_CLASS + ".getDateFromString";
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 561 */     System.err.println(" unknown mapped type " + getMappedType() + " (" + getType() + ") for " + getFullName());
/* 562 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDefaultWidget()
/*     */   {
/* 571 */     if (isForeignKey()) {
/* 572 */       return "SelectWidget";
/*     */     }
/* 574 */     if ((isString()) && ((getSize() > 200) || (getSize() == -1))) {
/* 575 */       return "TextAreaWidget";
/*     */     }
/*     */     
/* 578 */     switch (getMappedType()) {
/*     */     case 2: 
/* 580 */       return "BooleanWidget";
/*     */     case 5: case 6: 
/*     */     case 14: 
/*     */     case 15: 
/* 584 */       return "DateWidget";
/*     */     case 1: case 7: 
/*     */     case 8: 
/*     */     case 10: 
/*     */     case 11: 
/* 589 */       return "NumericWidget";
/*     */     case 0: case 3: 
/*     */     case 4: 
/*     */     case 12: 
/*     */     case 13: 
/*     */     case 16: 
/*     */     case 17: 
/* 596 */       return "InputWidget";
/*     */     }
/* 598 */     System.err.println("type unknown for " + getFullName());
/* 599 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isVersion()
/*     */   {
/* 605 */     if (!CodeWriter.optimisticLockType.equalsIgnoreCase("timestamp")) {
/* 606 */       return false;
/*     */     }
/* 608 */     if (!getName().equalsIgnoreCase(CodeWriter.optimisticLockColumn)) {
/* 609 */       return false;
/*     */     }
/* 611 */     if ((getMappedType() == 11) || (getMappedType() == 13)) {
/* 612 */       return true;
/*     */     }
/* 614 */     return false;
/*     */   }
/*     */   
/*     */   public Table getTable()
/*     */   {
/* 619 */     return this.db.getTable(getTableName());
/*     */   }
/*     */   
/*     */   public void addForeignKey(Column col)
/*     */   {
/* 624 */     this.foreignKeys.add(col);
/* 625 */     getTable().addForeignKey(this);
/*     */   }
/*     */   
/*     */   public List getForeignKeys()
/*     */   {
/* 630 */     return this.foreignKeys;
/*     */   }
/*     */   
/*     */   public void addImportedKey(Column col)
/*     */   {
/* 635 */     this.importedKeys.add(col);
/* 636 */     getTable().addImportedKey(col);
/*     */   }
/*     */   
/*     */   public List getImportedKeys()
/*     */   {
/* 641 */     return this.importedKeys;
/*     */   }
/*     */   
/*     */   public int countImportedKeys()
/*     */   {
/* 646 */     return this.importedKeys.size();
/*     */   }
/*     */   
/*     */   public boolean isImportedKey()
/*     */   {
/* 651 */     if (countImportedKeys() > 0) {
/* 652 */       return true;
/*     */     }
/* 654 */     return false;
/*     */   }
/*     */   
/*     */   public Column getForeignColumn()
/*     */   {
/* 659 */     return (Column)this.foreignKeys.get(0);
/*     */   }
/*     */   
/*     */   public int countForeignKeys()
/*     */   {
/* 664 */     return this.foreignKeys.size();
/*     */   }
/*     */   
/*     */   public boolean isForeignKey()
/*     */   {
/* 669 */     if (countForeignKeys() > 0) {
/* 670 */       return true;
/*     */     }
/* 672 */     return false;
/*     */   }
/*     */   
/*     */   public String getPropertyTag()
/*     */   {
/* 677 */     return (getTableName() + "." + getName()).toLowerCase();
/*     */   }
/*     */   
/*     */   public String getDefaultRules()
/*     */   {
/* 682 */     String rule = "";
/* 683 */     if ((getNullable() == 0) && (!isPrimaryKey())) {
/* 684 */       rule = rule + " nullnotallowed";
/*     */     } else {
/* 686 */       rule = rule + " nullallowed";
/*     */     }
/* 688 */     if ((getType() == 91) || (getType() == 93)) {
/* 689 */       rule = rule + " dateformat";
/*     */     }
/* 691 */     return rule;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean columnFor(String webElement)
/*     */   {
/* 700 */     String includeProperty = ConfigHelper.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "include");
/* 701 */     String excludeProperty = ConfigHelper.getXPathProperty("//table[@name='" + getTableName() + "']/frontend/" + webElement, "exclude");
/* 702 */     String[] exclude = CodeWriter.getExplodedString(excludeProperty);
/* 703 */     String[] include = CodeWriter.getExplodedString(includeProperty);
/* 704 */     if ((exclude.length == 0) && (include.length == 0)) {
/* 705 */       return getDefaultIncludeFor(webElement);
/*     */     }
/* 707 */     if (Main.isInArray(include, getName())) {
/* 708 */       return true;
/*     */     }
/* 710 */     if (Main.isInArray(exclude, getName())) {
/* 711 */       return false;
/*     */     }
/* 713 */     if (include.length == 0) {
/* 714 */       return true;
/*     */     }
/* 716 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getDefaultIncludeFor(String webElement)
/*     */   {
/* 725 */     return true;
/*     */   }
/*     */   
/*     */   public String getDefaultValue()
/*     */   {
/* 730 */     if (Boolean.valueOf(CodeWriter.getProperty("codewriter.generate.defaultvalue", "false")).booleanValue()) { return "";
/*     */     }
/*     */     
/* 733 */     String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "defaultValue");
/* 734 */     if ((xmlDefaultValue != null) && (!"".equals(xmlDefaultValue))) {
/* 735 */       return xmlDefaultValue;
/*     */     }
/* 737 */     if (null != this.defaultValue) {
/* 738 */       if (isColumnNumeric())
/*     */         try {
/* 740 */           double value = Double.parseDouble(this.defaultValue);
/* 741 */           switch (getMappedType()) {
/*     */           case 1: 
/*     */           case 10: 
/*     */           case 11: 
/* 745 */             return generateNewAssignation(getJavaType(), this.defaultValue, String.valueOf(value));
/*     */           case 7: 
/*     */           case 8: 
/* 748 */             return generateNewAssignation(getJavaType(), String.valueOf(value), this.defaultValue);
/*     */           }
/* 750 */           return "";
/*     */         }
/*     */         catch (NumberFormatException nfe) {
/* 753 */           return "";
/*     */         }
/* 755 */       if (isDate())
/*     */         try {
/* 757 */           java.util.Date date = java.text.SimpleDateFormat.getInstance().parse(this.defaultValue);
/* 758 */           return "= SimpleDateFormat.getInstance().parse(" + this.defaultValue + "); // '" + java.text.SimpleDateFormat.getInstance().format(date) + "'";
/*     */         }
/*     */         catch (java.text.ParseException pe) {
/* 761 */           return "= null; // '" + this.defaultValue + "'";
/*     */         }
/* 763 */       if (isString())
/* 764 */         return "= \"" + this.defaultValue + '"';
/* 765 */       if (2 == getMappedType()) {
/* 766 */         return "= Boolean.valueOf(\"" + ("1".equals(this.defaultValue) ? "true" : this.defaultValue) + "\").booleanValue(); // '" + this.defaultValue + "'";
/*     */       }
/*     */     }
/*     */     
/* 770 */     return "= " + this.defaultValue;
/*     */   }
/*     */   
/*     */   private String generateNewAssignation(String type, String parameter, String comment) {
/* 774 */     StringBuffer sb = new StringBuffer(70);
/* 775 */     sb.append("= new ").append(type);
/* 776 */     sb.append('(').append(parameter).append("); // '").append(comment).append("'");
/*     */     
/* 778 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRemarks()
/*     */   {
/* 784 */     String xmlDefaultValue = ConfigHelper.getColumnProperty(getTableName(), getName(), "description");
/* 785 */     if ((xmlDefaultValue != null) && (!"".equals(xmlDefaultValue))) {
/* 786 */       return xmlDefaultValue;
/*     */     }
/* 788 */     return this.remarks == null ? "" : this.remarks;
/*     */   }
/*     */   
/*     */   public String getJavaName()
/*     */   {
/* 793 */     return convertName(getName());
/*     */   }
/*     */   
/*     */ 
/* 797 */   private static Random rand = new Random();
/*     */   
/*     */   public String getSampleData() {
/* 800 */     if ((getNullable() > 1) && (rand.nextInt(20) == 10)) {
/* 801 */       return "";
/*     */     }
/* 803 */     if (isColumnNumeric()) {
/* 804 */       return "" + rand.nextInt(100);
/*     */     }
/* 806 */     if (isDate())
/*     */     {
/* 808 */       java.util.Calendar rightNow = java.util.Calendar.getInstance();
/* 809 */       rightNow.set(2000 + rand.nextInt(20), 1 + rand.nextInt(12), 1 + rand.nextInt(28), rand.nextInt(23), rand.nextInt(60), rand.nextInt(60));
/* 810 */       java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/* 811 */       return dateFormat.format(rightNow.getTime());
/*     */     }
/*     */     
/* 814 */     return StringUtilities.getSampleString(getSize());
/*     */   }
/*     */   
/*     */   private String escape(String s) {
/* 818 */     return StringUtilities.escape(s);
/*     */   }
/*     */   
/*     */   private String escape() {
/* 822 */     return escape(getName());
/*     */   }
/*     */   
/*     */   public String convertName(String columnName) {
/* 826 */     return StringUtilities.convertName(columnName, true);
/*     */   }
/*     */   
/*     */   public String convertName(Column col) {
/* 830 */     return convertName(col.getName());
/*     */   }
/*     */   
/*     */   public String convertName() {
/* 834 */     return convertName(this.name);
/*     */   }
/*     */   
/*     */   public String getImportedKeyVarName() {
/* 838 */     return convertName(escape() + "_collection");
/*     */   }
/*     */   
/*     */   public String getGetMethod() {
/* 842 */     return convertName("get_" + escape());
/*     */   }
/*     */   
/*     */   public String getSetMethod() {
/* 846 */     return convertName("set_" + escape());
/*     */   }
/*     */   
/*     */   public String getModifiedMethod() {
/* 850 */     return convertName("is_" + escape() + "_modified");
/*     */   }
/*     */   
/*     */   public String getInitializedMethod() {
/* 854 */     return convertName("is_" + escape() + "_initialized");
/*     */   }
/*     */   
/*     */   public String getWidgetMethod() {
/* 858 */     return convertName("get_" + escape() + "_widget");
/*     */   }
/*     */   
/*     */   public String getVarName() {
/* 862 */     return convertName(escape());
/*     */   }
/*     */   
/*     */   public String getModifiedVarName() {
/* 866 */     return convertName(escape() + "_is_modified");
/*     */   }
/*     */   
/*     */   public String getInitializedVarName() {
/* 870 */     return convertName(escape() + "_is_initialized");
/*     */   }
/*     */   
/*     */   public String getImportedKeyModifiedVarName() {
/* 874 */     return convertName(escape() + "_collection_is_modified");
/*     */   }
/*     */   
/*     */   public String getImportedKeyInitializedVarName() {
/* 878 */     return convertName(escape() + "_collection_is_initialized");
/*     */   }
/*     */   
/*     */   public String getImportedKeyInitializedMethod() {
/* 882 */     return convertName("is_" + escape() + "_collection_initialized");
/*     */   }
/*     */   
/*     */   public String getImportedKeyGetMethod() {
/* 886 */     return convertName("get_" + escape() + "_collection");
/*     */   }
/*     */   
/*     */   public String getImportedKeyAddMethod() {
/* 890 */     return convertName("add_" + escape() + "");
/*     */   }
/*     */   
/*     */   public String getImportedKeySetMethod() {
/* 894 */     return convertName("set_" + escape() + "_collection");
/*     */   }
/*     */   
/*     */   public String getImportedKeyModifiedMethod() {
/* 898 */     return convertName("is_" + escape() + "_collection_modified");
/*     */   }
/*     */   
/*     */   public String getForeignKeyVarName() {
/* 902 */     return convertName(escape() + "_object");
/*     */   }
/*     */   
/*     */   public String getForeignKeyModifiedVarName() {
/* 906 */     return convertName(escape() + "_object_is_modified");
/*     */   }
/*     */   
/*     */   public String getForeignKeyInitializedVarName() {
/* 910 */     return convertName(escape() + "_object_is_initialized");
/*     */   }
/*     */   
/*     */   public String getForeignKeyInitializedMethod() {
/* 914 */     return convertName("is_" + escape() + "_object_initialized");
/*     */   }
/*     */   
/*     */   public String getForeignKeyGetMethod(String col) {
/* 918 */     return convertName("get_" + escape() + "_object");
/*     */   }
/*     */   
/*     */   public String getForeignKeySetMethod(String col) {
/* 922 */     return convertName("set_" + escape() + "_object");
/*     */   }
/*     */   
/*     */   public String getForeignKeyModifiedMethod(String col) {
/* 926 */     return convertName("is_" + escape() + "_object_modified");
/*     */   }
/*     */   
/*     */   public String getTypeName()
/*     */   {
/* 931 */     return this.typeName;
/*     */   }
/*     */   
/*     */   public void setTypeName(String typeName) {
/* 935 */     this.typeName = typeName;
/*     */   }
/*     */   
/*     */   public int compareTo(Object obj) {
/* 939 */     return ((Column)obj).ordinal - this.ordinal;
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Column.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
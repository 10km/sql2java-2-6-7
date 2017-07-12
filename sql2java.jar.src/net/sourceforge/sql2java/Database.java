/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Database
/*     */ {
/*     */   private String[] tableTypes;
/*     */   private java.sql.Connection pConnection;
/*     */   private DatabaseMetaData meta;
/*     */   private Vector tables;
/*     */   private java.util.Hashtable tableHash;
/*     */   private String engine;
/*     */   private String driver;
/*     */   private String url;
/*     */   private String username;
/*     */   private String password;
/*     */   private String catalog;
/*     */   private String schema;
/*     */   private String tablenamepattern;
/*  25 */   private boolean retrieveRemarks = true;
/*     */   private String activeConnections;
/*     */   
/*  28 */   public void setOracleRetrieveRemarks(boolean retrieveRemarks) { this.retrieveRemarks = retrieveRemarks; }
/*  29 */   public void setDriver(String driver) { this.driver = driver; }
/*  30 */   public void setUrl(String url) { this.url = url; }
/*  31 */   public void setUsername(String username) { this.username = username; }
/*  32 */   public void setPassword(String password) { this.password = password; }
/*  33 */   public void setCatalog(String catalog) { this.catalog = catalog; }
/*  34 */   public void setTableNamePattern(String tablenamepattern) { this.tablenamepattern = tablenamepattern; }
/*  35 */   public void setTableTypes(String[] tt) { this.tableTypes = tt; }
/*  36 */   public void setActiveConnections(String activeConnections) { this.activeConnections = activeConnections; }
/*  37 */   public void setIdleConnections(String idleConnections) { this.idleConnections = idleConnections; }
/*  38 */   public void setMaxWait(String maxWait) { this.maxWait = maxWait; }
/*     */   
/*  40 */   public boolean getOracleRetrieveRemarks() { return this.retrieveRemarks; }
/*  41 */   public String getEngine() { return this.engine; }
/*  42 */   public String getDriver() { return this.driver; }
/*  43 */   public String getUrl() { return this.url; }
/*  44 */   public String getUsername() { return this.username; }
/*  45 */   public String getPassword() { return this.password; }
/*  46 */   public String getCatalog() { return this.catalog; }
/*  47 */   public String getSchema() { return this.schema; }
/*  48 */   public String getTableNamePattern() { return this.tablenamepattern; }
/*  49 */   public String[] getTableTypes() { return this.tableTypes; }
/*  50 */   public String getActiveConnections() { return this.activeConnections; }
/*  51 */   public String getIdleConnections() { return this.idleConnections; }
/*  52 */   public String getMaxWait() { return this.maxWait; }
/*     */   
/*     */   public void setSchema(String schema)
/*     */   {
/*  56 */     if ("null".equalsIgnoreCase(schema)) {
/*  57 */       this.schema = null;
/*     */     } else {
/*  59 */       this.schema = schema;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Table[] getRelationTable(Table table)
/*     */   {
/*  69 */     Vector vector = new Vector();
/*     */     
/*  71 */     for (int iIndex = 0; iIndex < this.tables.size(); iIndex++)
/*     */     {
/*  73 */       Table tempTable = (Table)this.tables.get(iIndex);
/*     */       
/*     */ 
/*  76 */       if (!table.equals(tempTable))
/*     */       {
/*     */ 
/*     */ 
/*  80 */         if (tempTable.isRelationTable())
/*     */         {
/*  82 */           if (tempTable.relationConnectsTo(table))
/*     */           {
/*  84 */             if (!vector.contains(tempTable))
/*  85 */               vector.add(tempTable); }
/*     */         }
/*     */       }
/*     */     }
/*  89 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */   public void load()
/*     */     throws SQLException, ClassNotFoundException
/*     */   {
/*  95 */     Class.forName(this.driver);
/*     */     
/*     */ 
/*  98 */     System.out.println("Connecting to " + this.username + " on " + this.url + " ...");
/*  99 */     this.pConnection = java.sql.DriverManager.getConnection(this.url, this.username, this.password);
/* 100 */     System.out.println("    Connected.");
/*     */     try
/*     */     {
/* 103 */       if ((this.pConnection instanceof oracle.jdbc.driver.OracleConnection)) {
/* 104 */         ((oracle.jdbc.driver.OracleConnection)this.pConnection).setRemarksReporting(getOracleRetrieveRemarks());
/*     */       }
/*     */     }
/*     */     catch (NoClassDefFoundError ncdfe) {}catch (Exception e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 115 */     this.meta = this.pConnection.getMetaData();
/* 116 */     this.engine = this.meta.getDatabaseProductName();
/* 117 */     System.out.println("    Database server :" + this.engine + ".");
/* 118 */     this.engine = new java.util.StringTokenizer(this.engine).nextToken();
/* 119 */     this.tables = new Vector();
/* 120 */     this.tableHash = new java.util.Hashtable();
/*     */     
/* 122 */     loadTables();
/* 123 */     loadColumns();
/* 124 */     loadPrimaryKeys();
/* 125 */     loadImportedKeys();
/* 126 */     loadIndexes();
/* 127 */     loadProcedures();
/*     */     
/* 129 */     this.pConnection.close();
/*     */   }
/*     */   
/*     */   public Table[] getTables()
/*     */   {
/* 134 */     return (Table[])this.tables.toArray(new Table[this.tables.size()]);
/*     */   }
/*     */   
/*     */   private void addTable(Table t) {
/* 138 */     this.tables.addElement(t);
/* 139 */     this.tableHash.put(t.getName(), t);
/*     */   }
/*     */   
/*     */   public Table getTable(String name) {
/* 143 */     return (Table)this.tableHash.get(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void loadTables()
/*     */     throws SQLException
/*     */   {
/* 151 */     System.out.println("Loading table list according to pattern " + this.tablenamepattern + " ...");
/*     */     
/*     */ 
/* 154 */     java.util.StringTokenizer st = new java.util.StringTokenizer(this.tablenamepattern, ",; \t");
/* 155 */     while (st.hasMoreTokens()) {
/* 156 */       String pattern = st.nextToken().trim();
/* 157 */       String tableSchema = this.schema;
/* 158 */       int index = pattern.indexOf('.');
/* 159 */       if (index > 0) {
/* 160 */         tableSchema = pattern.substring(0, index);
/* 161 */         pattern = pattern.substring(index + 1);
/*     */       }
/* 163 */       ResultSet resultSet = this.meta.getTables(this.catalog, tableSchema, pattern, this.tableTypes);
/* 164 */       while (resultSet.next())
/*     */       {
/* 166 */         Table table = new Table();
/* 167 */         table.setCatalog(resultSet.getString("TABLE_CAT"));
/* 168 */         table.setSchema(resultSet.getString("TABLE_SCHEM"));
/* 169 */         table.setName(resultSet.getString("TABLE_NAME"));
/* 170 */         table.setType(resultSet.getString("TABLE_TYPE"));
/* 171 */         table.setRemarks(resultSet.getString("REMARKS"));
/* 172 */         if (CodeWriter.authorizeProcess(table.getName(), "tables.include", "tables.exclude")) {
/* 173 */           addTable(table);
/* 174 */           System.out.println("    table " + table.getName() + " found");
/*     */         }
/*     */       }
/* 177 */       resultSet.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String idleConnections;
/*     */   
/*     */   private String maxWait;
/*     */   
/*     */   private void loadColumns()
/*     */     throws SQLException
/*     */   {
/* 189 */     System.out.println("Loading columns ...");
/* 190 */     for (Iterator it = this.tables.iterator(); it.hasNext();) {
/* 191 */       Table table = (Table)it.next();
/* 192 */       Column c = null;
/*     */       
/* 194 */       ResultSet resultSet = this.meta.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%");
/* 195 */       while (resultSet.next())
/*     */       {
/* 197 */         c = new Column();
/* 198 */         c.setDatabase(this);
/* 199 */         c.setCatalog(resultSet.getString("TABLE_CAT"));
/* 200 */         c.setSchema(resultSet.getString("TABLE_SCHEM"));
/* 201 */         c.setTableName(resultSet.getString("TABLE_NAME"));
/* 202 */         c.setName(resultSet.getString("COLUMN_NAME"));
/* 203 */         c.setType(resultSet.getShort("DATA_TYPE"));
/* 204 */         c.setTypeName(resultSet.getString("TYPE_NAME"));
/* 205 */         c.setSize(resultSet.getInt("COLUMN_SIZE"));
/* 206 */         c.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
/* 207 */         c.setRadix(resultSet.getInt("NUM_PREC_RADIX"));
/* 208 */         c.setNullable(resultSet.getInt("NULLABLE"));
/* 209 */         c.setRemarks(resultSet.getString("REMARKS"));
/* 210 */         c.setDefaultValue(resultSet.getString("COLUMN_DEF"));
/* 211 */         c.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
/* 212 */         table.addColumn(c);
/*     */       }
/* 214 */       resultSet.close();
/*     */       
/* 216 */       System.out.println("    " + table.getName() + " found " + table.countColumns() + " columns");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void loadPrimaryKeys()
/*     */     throws SQLException
/*     */   {
/* 224 */     System.out.println("Database::loadPrimaryKeys");
/*     */     
/* 226 */     for (Iterator it = this.tables.iterator(); it.hasNext();) {
/* 227 */       Table table = (Table)it.next();
/* 228 */       java.util.SortedMap map = new java.util.TreeMap();
/*     */       
/* 230 */       ResultSet pResultSet = this.meta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
/* 231 */       while (pResultSet.next()) {
/* 232 */         String colName = pResultSet.getString("COLUMN_NAME");
/* 233 */         int seq = pResultSet.getShort("KEY_SEQ");
/* 234 */         System.out.println("Found primary key (seq,name) (" + seq + "," + colName + ") for table '" + table.getName() + "'");
/*     */         
/*     */ 
/* 237 */         Column col = table.getColumn(colName);
/* 238 */         if (col != null) {
/* 239 */           map.put(String.valueOf(seq), col);
/*     */         }
/*     */       }
/* 242 */       pResultSet.close();
/*     */       
/* 244 */       int size = map.size();
/* 245 */       for (int k = 1; k <= size; k++) {
/* 246 */         Column col = (Column)map.get(String.valueOf(k));
/* 247 */         table.addPrimaryKey(col);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadImportedKeys()
/*     */     throws SQLException
/*     */   {
/* 259 */     System.out.println("Loading imported keys ...");
/*     */     
/* 261 */     for (Iterator it = this.tables.iterator(); it.hasNext();) {
/* 262 */       Table table = (Table)it.next();
/*     */       ResultSet resultSet;
/*     */       try {
/* 265 */         resultSet = this.meta.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
/*     */       } catch (SQLException sqle) {
/* 267 */         System.out.println("    Error while loading imported keys for table " + table.getName()); }
/* 268 */       continue;
/*     */       
/* 270 */       while (resultSet.next())
/*     */       {
/* 272 */         String tabName = resultSet.getString("FKTABLE_NAME");
/* 273 */         String colName = resultSet.getString("FKCOLUMN_NAME");
/*     */         
/* 275 */         String foreignTabName = resultSet.getString("PKTABLE_NAME");
/* 276 */         String foreignColName = resultSet.getString("PKCOLUMN_NAME");
/*     */         
/* 278 */         Column col = getTable(tabName).getColumn(colName);
/* 279 */         Table foreignTable = getTable(foreignTabName);
/* 280 */         if (null != foreignTable) {
/* 281 */           Column foreignCol = foreignTable.getColumn(foreignColName);
/*     */           
/* 283 */           col.addForeignKey(foreignCol);
/* 284 */           foreignCol.addImportedKey(col);
/*     */           
/*     */ 
/* 287 */           System.out.println("    " + col.getFullName() + " -> " + foreignCol.getFullName() + " found ");
/*     */         }
/*     */       }
/*     */       
/* 291 */       resultSet.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadIndexes()
/*     */     throws SQLException
/*     */   {
/* 301 */     System.out.println("Loading indexes ...");
/*     */     
/* 303 */     for (Iterator it = this.tables.iterator(); it.hasNext();) {
/* 304 */       Table table = (Table)it.next();
/* 305 */       ResultSet resultSet = null;
/*     */       try {
/* 307 */         resultSet = this.meta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, true);
/*     */ 
/*     */       }
/*     */       catch (SQLException sqle)
/*     */       {
/*     */ 
/* 313 */         System.out.println("    Error while loading indexes for table " + table.getName()); }
/* 314 */       continue;
/*     */       
/* 316 */       String currentName = "";
/* 317 */       Index index = null;
/* 318 */       while (resultSet.next())
/*     */       {
/* 320 */         String colName = resultSet.getString("COLUMN_NAME");
/* 321 */         String indName = resultSet.getString("INDEX_NAME");
/*     */         
/* 323 */         if ((null != indName) && (null != colName))
/*     */         {
/*     */ 
/*     */ 
/* 327 */           Column col = table.getColumn(colName);
/* 328 */           if (!col.isPrimaryKey())
/*     */           {
/*     */ 
/* 331 */             System.out.println("  Found interesting index " + indName + " on " + colName + " for table " + table.getName());
/*     */             
/*     */ 
/*     */ 
/* 335 */             if (!currentName.equals(indName)) {
/* 336 */               index = new Index(indName, table);
/* 337 */               index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
/* 338 */               currentName = indName;
/*     */             }
/* 340 */             IndexColumn column = new IndexColumn();
/* 341 */             column.setName(resultSet.getString("COLUMN_NAME"));
/* 342 */             column.setOrdinalPosition(resultSet.getShort("ORDINAL_POSITION"));
/* 343 */             column.setSortSequence(resultSet.getString("ASC_OR_DESC"));
/* 344 */             column.setFilterCondition(resultSet.getString("FILTER_CONDITION"));
/* 345 */             column.setType(col.getType());
/* 346 */             index.addIndexColumn(column);
/*     */           }
/*     */         } }
/* 349 */       resultSet.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private void loadProcedures() throws SQLException
/*     */   {
/* 355 */     System.out.println("Loading procedures ...");
/*     */     
/* 357 */     for (Iterator it = this.tables.iterator(); it.hasNext();) {
/* 358 */       Table table = (Table)it.next();
/* 359 */       String procedurePattern = table.getTableProperty("procedures");
/* 360 */       if ((null == procedurePattern) || ("".equals(procedurePattern))) {
/* 361 */         procedurePattern = "%" + table.getName() + "%";
/*     */       }
/* 363 */       ResultSet resultSet = null;
/*     */       try {
/* 365 */         resultSet = this.meta.getProcedures(table.getCatalog(), table.getSchema(), procedurePattern);
/*     */       }
/*     */       catch (SQLException sqle)
/*     */       {
/* 369 */         System.out.println("    Error while loading procedures for table " + table.getName()); }
/* 370 */       continue;
/*     */       
/* 372 */       while (resultSet.next())
/*     */       {
/* 374 */         String spName = resultSet.getString("PROCEDURE_NAME");
/* 375 */         String spRemarks = resultSet.getString("REMARKS");
/*     */         
/*     */ 
/* 378 */         Procedure procedure = new Procedure();
/* 379 */         procedure.setName(spName);
/* 380 */         procedure.setRemarks(spRemarks);
/* 381 */         procedure.setReturnType("void");
/* 382 */         table.addProcedure(procedure);
/*     */         
/* 384 */         System.out.println("    Found procedure " + spName + " for table " + table.getName());
/*     */         
/* 386 */         ResultSet rs = this.meta.getProcedureColumns(this.catalog, this.schema, spName, null);
/* 387 */         while (rs.next()) {
/* 388 */           String colName = rs.getString("COLUMN_NAME");
/* 389 */           short columnType = rs.getShort("COLUMN_TYPE");
/* 390 */           if (0 == columnType) {
/* 391 */             System.err.println("    Column " + colName + " of unknown type in procedure " + spName);
/*     */           }
/*     */           else {
/* 394 */             Column c = new Column();
/* 395 */             c.setType(rs.getShort("DATA_TYPE"));
/* 396 */             if (5 == columnType) {
/* 397 */               procedure.setReturnType(c.getJavaType());
/*     */             }
/*     */             else {
/* 400 */               c.setDatabase(this);
/* 401 */               c.setCatalog(rs.getString("PROCEDURE_CAT"));
/* 402 */               c.setSchema(rs.getString("PROCEDURE_SCHEM"));
/* 403 */               c.setTableName(rs.getString("PROCEDURE_NAME"));
/* 404 */               c.setName(colName);
/* 405 */               c.setSize(rs.getInt("LENGTH"));
/* 406 */               c.setDecimalDigits(rs.getInt("SCALE"));
/* 407 */               c.setRadix(rs.getInt("RADIX"));
/* 408 */               c.setNullable(rs.getInt("NULLABLE"));
/* 409 */               c.setRemarks(rs.getString("REMARKS"));
/* 410 */               switch (columnType) {
/*     */               case 1: 
/* 412 */                 procedure.addInColumn(c);
/* 413 */                 break;
/*     */               case 2: 
/* 415 */                 procedure.addInOutColumn(c);
/* 416 */                 break;
/*     */               case 4: 
/* 418 */                 procedure.addOutColumn(c);
/* 419 */                 break;
/*     */               case 3: 
/*     */               default: 
/* 422 */                 procedure.setReturnType("List"); }
/*     */             }
/*     */           } }
/* 425 */         rs.close();
/*     */       }
/*     */       
/* 428 */       resultSet.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getAllPackages()
/*     */   {
/* 434 */     Vector vector = new Vector();
/* 435 */     for (int iIndex = 0; iIndex < this.tables.size(); iIndex++)
/*     */     {
/* 437 */       Table table = (Table)this.tables.get(iIndex);
/* 438 */       if (!vector.contains(table.getPackage()))
/*     */       {
/* 440 */         vector.add(table.getPackage());
/*     */       }
/*     */     }
/* 443 */     return (String[])vector.toArray(new String[vector.size()]);
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Database.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
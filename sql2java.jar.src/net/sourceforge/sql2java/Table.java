/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Table
/*     */ {
/*  17 */   private Hashtable colHash = new Hashtable();
/*  18 */   private Vector cols = new Vector();
/*  19 */   private Hashtable indHash = new Hashtable();
/*  20 */   private Vector indices = new Vector();
/*  21 */   private Hashtable indUniqueHash = new Hashtable();
/*  22 */   private Vector uniqueIndices = new Vector();
/*  23 */   private Hashtable indNonUniHash = new Hashtable();
/*  24 */   private Vector nonUniqueIndices = new Vector();
/*  25 */   private Vector priKey = new Vector();
/*     */   private String catalog;
/*     */   private String schema;
/*  28 */   private String name; private String type; private String remarks; private Vector foreignKeys = new Vector();
/*  29 */   private Vector importedKeys = new Vector();
/*     */   
/*     */   public boolean isRelationTable()
/*     */   {
/*  33 */     if ("false".equalsIgnoreCase(getTableProperty("nntable")))
/*  34 */       return false;
/*  35 */     return this.foreignKeys.size() == 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean relationConnectsTo(Table otherTable)
/*     */   {
/*  44 */     if (equals(otherTable))
/*     */     {
/*  46 */       return false;
/*     */     }
/*  48 */     int nbImported = this.importedKeys.size();
/*  49 */     for (int i = 0; i < nbImported; i++)
/*     */     {
/*  51 */       Column c = (Column)this.importedKeys.get(i);
/*  52 */       if (c.getTableName().equals(otherTable.getName()))
/*     */       {
/*  54 */         return true;
/*     */       }
/*     */     }
/*  57 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Table[] linkedTables(Database pDatabase, Table pTable)
/*     */   {
/*  65 */     Vector vector = new Vector();
/*     */     
/*  67 */     int nbImported = this.importedKeys.size();
/*  68 */     for (int iIndex = 0; iIndex < nbImported; iIndex++)
/*     */     {
/*  70 */       Column pColumn = (Column)this.importedKeys.get(iIndex);
/*  71 */       if (!pColumn.getTableName().equals(pTable.getName()))
/*     */       {
/*  73 */         Table pTableToAdd = pDatabase.getTable(pColumn.getTableName());
/*  74 */         if (!vector.contains(pTableToAdd)) {
/*  75 */           vector.add(pTableToAdd);
/*     */         }
/*     */       }
/*     */     }
/*  79 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Column getForeignKeyFor(Table pTable)
/*     */   {
/*  87 */     int nbImported = this.importedKeys.size();
/*  88 */     for (int iIndex = 0; iIndex < nbImported; iIndex++)
/*     */     {
/*  90 */       Column pColumn = (Column)this.importedKeys.get(iIndex);
/*  91 */       if (pColumn.getTableName().equals(pTable.getName())) {
/*  92 */         return pColumn;
/*     */       }
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */   
/*     */   public void setCatalog(String catalog)
/*     */   {
/* 100 */     this.catalog = catalog;
/*     */   }
/*     */   
/*     */   public void setSchema(String schema) {
/* 104 */     this.schema = schema;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/* 108 */     this.name = name;
/*     */   }
/*     */   
/*     */   public void setType(String type) {
/* 112 */     this.type = type;
/*     */   }
/*     */   
/*     */   public void setRemarks(String remarks) {
/* 116 */     if (remarks != null) {
/* 117 */       this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCatalog()
/*     */   {
/* 124 */     return this.catalog;
/*     */   }
/*     */   
/*     */   public String getSchema() {
/* 128 */     return this.schema;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 132 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getType() {
/* 136 */     return this.type;
/*     */   }
/*     */   
/*     */   public Column[] getColumns()
/*     */   {
/* 141 */     Collections.sort(this.cols);
/* 142 */     return (Column[])this.cols.toArray(new Column[this.cols.size()]);
/*     */   }
/*     */   
/*     */   public Column getColumn(String columnName)
/*     */   {
/* 147 */     return (Column)this.colHash.get(columnName.toLowerCase());
/*     */   }
/*     */   
/*     */   public void addColumn(Column column)
/*     */   {
/* 152 */     this.colHash.put(column.getName().toLowerCase(), column);
/* 153 */     this.cols.addElement(column);
/*     */   }
/*     */   
/*     */   public void removeColumn(Column column)
/*     */   {
/* 158 */     this.cols.removeElement(column);
/* 159 */     this.colHash.remove(column.getName().toLowerCase());
/*     */   }
/*     */   
/*     */   public Index[] getUniqueIndices() {
/* 163 */     return (Index[])this.uniqueIndices.toArray(new Index[this.uniqueIndices.size()]);
/*     */   }
/*     */   
/*     */   public Index[] getNonUniqueIndices() {
/* 167 */     return (Index[])this.nonUniqueIndices.toArray(new Index[this.nonUniqueIndices.size()]);
/*     */   }
/*     */   
/*     */   public int countIndices() {
/* 171 */     return this.indices.size();
/*     */   }
/*     */   
/*     */   public Index[] getIndices() {
/* 175 */     return (Index[])this.indices.toArray(new Index[this.indices.size()]);
/*     */   }
/*     */   
/*     */   public Index getIndex(String indName)
/*     */   {
/* 180 */     return (Index)this.indHash.get(indName.toLowerCase());
/*     */   }
/*     */   
/*     */   public void addIndex(Index index) {
/* 184 */     this.indHash.put(index.getName().toLowerCase(), index);
/* 185 */     this.indices.add(index);
/* 186 */     if (index.isUnique()) {
/* 187 */       this.indUniqueHash.put(index.getName().toLowerCase(), index);
/* 188 */       this.uniqueIndices.add(index);
/*     */     } else {
/* 190 */       this.indNonUniHash.put(index.getName().toLowerCase(), index);
/* 191 */       this.nonUniqueIndices.add(index);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeIndex(Index index) {
/* 196 */     this.indices.remove(index);
/* 197 */     this.indHash.remove(index.getName().toLowerCase());
/* 198 */     if (index.isUnique()) {
/* 199 */       this.uniqueIndices.remove(index);
/* 200 */       this.indUniqueHash.remove(index.getName().toLowerCase());
/*     */     } else {
/* 202 */       this.nonUniqueIndices.remove(index);
/* 203 */       this.indNonUniHash.remove(index.getName().toLowerCase());
/*     */     }
/*     */   }
/*     */   
/*     */   public Column[] getPrimaryKeys()
/*     */   {
/* 209 */     return (Column[])this.priKey.toArray(new Column[this.priKey.size()]);
/*     */   }
/*     */   
/*     */   public boolean hasCompositeKey()
/*     */   {
/* 214 */     if (this.priKey.size() > 1) {
/* 215 */       return true;
/*     */     }
/* 217 */     return false;
/*     */   }
/*     */   
/*     */   public Column getPrimaryKey() throws RuntimeException
/*     */   {
/* 222 */     if (this.priKey.size() != 1) {
/* 223 */       throw new RuntimeException("Table " + getName() + " has a composite key, not a unique primary key");
/*     */     }
/* 225 */     return (Column)this.priKey.get(0);
/*     */   }
/*     */   
/*     */   public void addPrimaryKey(Column column)
/*     */   {
/* 230 */     this.priKey.addElement(column);
/* 231 */     column.isPrimaryKey(true);
/*     */   }
/*     */   
/*     */   public Column[] getImportedKeys()
/*     */   {
/* 236 */     return (Column[])this.importedKeys.toArray(new Column[this.importedKeys.size()]);
/*     */   }
/*     */   
/*     */   public void addImportedKey(Column column)
/*     */   {
/* 241 */     if (!this.importedKeys.contains(column)) {
/* 242 */       this.importedKeys.addElement(column);
/*     */     }
/*     */   }
/*     */   
/*     */   public int countColumns()
/*     */   {
/* 248 */     return this.cols.size();
/*     */   }
/*     */   
/*     */   public int countPrimaryKeys()
/*     */   {
/* 253 */     return this.priKey.size();
/*     */   }
/*     */   
/*     */   public boolean hasPrimaryKey()
/*     */   {
/* 258 */     return countPrimaryKeys() > 0;
/*     */   }
/*     */   
/*     */   public int countImportedKeys()
/*     */   {
/* 263 */     return this.importedKeys.size();
/*     */   }
/*     */   
/*     */   public boolean hasImportedKeys()
/*     */   {
/* 268 */     return countImportedKeys() > 0;
/*     */   }
/*     */   
/*     */   public int countForeignKeys()
/*     */   {
/* 273 */     return this.foreignKeys.size();
/*     */   }
/*     */   
/*     */   public boolean hasForeignKeys()
/*     */   {
/* 278 */     return countForeignKeys() > 0;
/*     */   }
/*     */   
/*     */   public void addForeignKey(Column col)
/*     */   {
/* 283 */     if (!this.foreignKeys.contains(col)) {
/* 284 */       this.foreignKeys.add(col);
/*     */     }
/*     */   }
/*     */   
/*     */   public Column[] getForeignKeys()
/*     */   {
/* 290 */     return (Column[])this.foreignKeys.toArray(new Column[this.foreignKeys.size()]);
/*     */   }
/*     */   
/*     */   public boolean isForeignKey(Column col)
/*     */   {
/* 295 */     return this.foreignKeys.contains(col);
/*     */   }
/*     */   
/*     */   public int countManyToManyTables()
/*     */   {
/* 300 */     return getManyToManyTables().length;
/*     */   }
/*     */   
/*     */   public boolean hasManyToManyTables()
/*     */   {
/* 305 */     return countManyToManyTables() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public Table[] getManyToManyTables()
/*     */   {
/* 311 */     Vector vector = new Vector();
/*     */     
/* 313 */     Table[] linkedTables = getImportedTables();
/* 314 */     System.out.println(getName() + "  getManyToManyTables, linked tables = " + linkedTables.length);
/*     */     
/* 316 */     for (int iIndex = 0; iIndex < linkedTables.length; iIndex++)
/*     */     {
/* 318 */       System.out.println(getName() + "    " + linkedTables[iIndex].getName() + " relation table ?");
/* 319 */       if (linkedTables[iIndex].isRelationTable())
/*     */       {
/* 321 */         Table[] relationLinkedTable = linkedTables[iIndex].getForeignTables();
/* 322 */         System.out.println(getName() + "      " + linkedTables[iIndex].getName() + " has " + relationLinkedTable.length + " foreign table");
/* 323 */         for (int i = 0; i < relationLinkedTable.length; i++)
/*     */         {
/* 325 */           System.out.println(getName() + "          " + i + " " + relationLinkedTable[i].getName() + " is relation table");
/*     */           
/* 327 */           if (!relationLinkedTable[i].equals(this))
/*     */           {
/* 329 */             if (!vector.contains(relationLinkedTable[i])) {
/* 330 */               vector.add(relationLinkedTable[i]);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 336 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */   public int countLinkedTables()
/*     */   {
/* 341 */     return getLinkedTables().length;
/*     */   }
/*     */   
/*     */   public boolean hasLinkedTables()
/*     */   {
/* 346 */     return countLinkedTables() > 0;
/*     */   }
/*     */   
/*     */   public Table[] getLinkedTables()
/*     */   {
/* 351 */     Vector vector = new Vector();
/*     */     
/* 353 */     int nbImported = this.importedKeys.size();
/* 354 */     for (int iIndex = 0; iIndex < nbImported; iIndex++)
/*     */     {
/* 356 */       Column column = (Column)this.importedKeys.get(iIndex);
/* 357 */       if (!column.getTableName().equals(getName()))
/*     */       {
/* 359 */         Table pTableToAdd = column.getTable();
/* 360 */         if (!vector.contains(pTableToAdd)) {
/* 361 */           vector.add(pTableToAdd);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 366 */     int nbForeign = this.foreignKeys.size();
/* 367 */     for (int iIndex = 0; iIndex < nbForeign; iIndex++)
/*     */     {
/* 369 */       Column column = (Column)this.foreignKeys.get(iIndex);
/* 370 */       column = column.getForeignColumn();
/* 371 */       if (!column.getTableName().equals(getName()))
/*     */       {
/* 373 */         Table pTableToAdd = column.getTable();
/* 374 */         if (!vector.contains(pTableToAdd)) {
/* 375 */           vector.add(pTableToAdd);
/*     */         }
/*     */       }
/*     */     }
/* 379 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */   public int countImportedTables()
/*     */   {
/* 384 */     return getImportedTables().length;
/*     */   }
/*     */   
/*     */   public boolean hasImportedTables()
/*     */   {
/* 389 */     return countImportedTables() > 0;
/*     */   }
/*     */   
/*     */   public Table[] getImportedTables()
/*     */   {
/* 394 */     Vector vector = new Vector();
/* 395 */     int nbImported = this.importedKeys.size();
/* 396 */     for (int iIndex = 0; iIndex < nbImported; iIndex++)
/*     */     {
/* 398 */       Column column = (Column)this.importedKeys.get(iIndex);
/* 399 */       if (!column.getTableName().equals(getName()))
/*     */       {
/* 401 */         Table pTableToAdd = column.getTable();
/* 402 */         if (!vector.contains(pTableToAdd)) {
/* 403 */           vector.add(pTableToAdd);
/*     */         }
/*     */       }
/*     */     }
/* 407 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */   public int countForeignTables()
/*     */   {
/* 412 */     return getForeignTables().length;
/*     */   }
/*     */   
/*     */   public boolean hasForeignTables()
/*     */   {
/* 417 */     return countForeignTables() > 0;
/*     */   }
/*     */   
/*     */   public Table[] getForeignTables()
/*     */   {
/* 422 */     Vector vector = new Vector();
/* 423 */     int nbForeign = this.foreignKeys.size();
/* 424 */     for (int iIndex = 0; iIndex < nbForeign; iIndex++)
/*     */     {
/* 426 */       Column column = ((Column)this.foreignKeys.get(iIndex)).getForeignColumn();
/* 427 */       if (!column.getTableName().equals(getName()))
/*     */       {
/* 429 */         Table pTableToAdd = column.getTable();
/* 430 */         if (!vector.contains(pTableToAdd)) {
/* 431 */           vector.add(pTableToAdd);
/*     */         }
/*     */       }
/*     */     }
/* 435 */     return (Table[])vector.toArray(new Table[vector.size()]);
/*     */   }
/*     */   
/*     */   public Table getRelationTable(Table targetTable)
/*     */   {
/* 440 */     System.out.println("getRelationTable " + getName() + "<->" + targetTable.getName() + ")");
/* 441 */     Table[] importedTables = getImportedTables();
/* 442 */     for (int iIndex = 0; iIndex < importedTables.length; iIndex++)
/*     */     {
/*     */ 
/* 445 */       Table[] foreignTables = importedTables[iIndex].getForeignTables();
/* 446 */       for (int iIndex2 = 0; iIndex2 < foreignTables.length; iIndex2++)
/*     */       {
/*     */ 
/* 449 */         if (foreignTables[iIndex2].getName().equalsIgnoreCase(getName())) {
/* 450 */           return importedTables[iIndex];
/*     */         }
/*     */       }
/*     */     }
/* 454 */     return targetTable;
/*     */   }
/*     */   
/* 457 */   private List procedures = new ArrayList();
/* 458 */   private HashMap procHash = new HashMap();
/*     */   
/* 460 */   public int countProcedures() { return this.procedures.size(); }
/*     */   
/*     */   public boolean hasProcedures()
/*     */   {
/* 464 */     return countProcedures() > 0;
/*     */   }
/*     */   
/*     */   public Procedure[] getProcedures() {
/* 468 */     return (Procedure[])this.procedures.toArray(new Procedure[this.procedures.size()]);
/*     */   }
/*     */   
/*     */   public void addProcedure(Procedure procedure) {
/* 472 */     if (null == this.procHash.get(procedure.getName())) {
/* 473 */       this.procedures.add(procedure);
/* 474 */       this.procHash.put(procedure.getName(), procedure);
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getLinkedPackages()
/*     */   {
/* 480 */     Vector vector = new Vector();
/* 481 */     Table[] linkedTables = getLinkedTables();
/* 482 */     for (int iIndex = 0; iIndex < linkedTables.length; iIndex++)
/*     */     {
/* 484 */       if (!vector.contains(linkedTables[iIndex].getPackage())) {
/* 485 */         vector.add(linkedTables[iIndex].getPackage());
/*     */       }
/*     */     }
/* 488 */     return (String[])vector.toArray(new String[vector.size()]);
/*     */   }
/*     */   
/*     */   public String getPackage()
/*     */   {
/* 493 */     String basePackage = CodeWriter.getProperty("codewriter.package");
/* 494 */     String xmlSubpackage = getTableProperty("subpackage");
/*     */     
/* 496 */     if (null != xmlSubpackage) {
/* 497 */       return basePackage + "." + xmlSubpackage;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 502 */     int iterating = 1;
/*     */     for (;;)
/*     */     {
/* 505 */       String tablesProperty = "subpackage." + iterating + ".tables";
/* 506 */       String packageNameProperty = "subpackage." + iterating + ".name";
/* 507 */       String[] tables = CodeWriter.getPropertyExploded(tablesProperty);
/*     */       
/*     */ 
/* 510 */       if (tables.length == 0) {
/*     */         break;
/*     */       }
/* 513 */       for (int i = 0; i < tables.length; i++)
/*     */       {
/* 515 */         if (getName().equalsIgnoreCase(tables[i]))
/*     */         {
/* 517 */           String packageName = CodeWriter.getProperty(packageNameProperty);
/* 518 */           if (packageName == null) {
/* 519 */             return basePackage;
/*     */           }
/* 521 */           return basePackage + "." + packageName;
/*     */         }
/*     */       }
/* 524 */       iterating++;
/*     */     }
/* 526 */     return basePackage;
/*     */   }
/*     */   
/*     */   public String getPackagePath()
/*     */   {
/* 531 */     return getPackage().replace('.', '/') + "/";
/*     */   }
/*     */   
/*     */   public Column[] getColumnsFor(String webElement)
/*     */   {
/* 536 */     Vector vector = new Vector();
/* 537 */     int nbCols = this.cols.size();
/* 538 */     for (int i = 0; i < nbCols; i++)
/*     */     {
/* 540 */       Column c = (Column)this.cols.get(i);
/* 541 */       if (c.columnFor(webElement)) {
/* 542 */         vector.add(c);
/*     */       }
/*     */     }
/* 545 */     return (Column[])vector.toArray(new Column[vector.size()]);
/*     */   }
/*     */   
/*     */   public Column getFirstColumn()
/*     */   {
/* 550 */     return (Column)this.cols.get(0);
/*     */   }
/*     */   
/*     */   public String getTableProperty(String property)
/*     */   {
/* 555 */     return ConfigHelper.getTableProperty(this.name, property);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRemarks()
/*     */   {
/* 561 */     String xmlDefaultValue = getTableProperty("description");
/* 562 */     if ((xmlDefaultValue != null) && (!"".equals(xmlDefaultValue))) {
/* 563 */       return xmlDefaultValue;
/*     */     }
/* 565 */     return this.remarks == null ? "" : this.remarks;
/*     */   }
/*     */   
/*     */   public String getJavaName()
/*     */   {
/* 570 */     return convertName("");
/*     */   }
/*     */   
/*     */   public String convertName(String value)
/*     */   {
/* 575 */     String basename = "";
/* 576 */     if ("".equals(CodeWriter.getClassPrefix())) {
/* 577 */       basename = getName();
/*     */     } else {
/* 579 */       basename = CodeWriter.getClassPrefix() + "_" + getName();
/*     */     }
/* 581 */     if ("".equals(value)) {
/* 582 */       return StringUtilities.convertName(basename, false);
/*     */     }
/* 584 */     return StringUtilities.convertName(basename + "_" + value, false);
/*     */   }
/*     */   
/*     */   public String asClass(String suffix)
/*     */   {
/* 589 */     return convertName(suffix);
/*     */   }
/*     */   
/*     */   public String asCoreClass() {
/* 593 */     return convertName("");
/*     */   }
/*     */   
/*     */   public String asBeanClass() {
/* 597 */     return convertName("Bean");
/*     */   }
/*     */   
/*     */   public String asCacheClass() {
/* 601 */     return convertName("Cache");
/*     */   }
/*     */   
/*     */   public String asRelationnalBeanClass() {
/* 605 */     return convertName("Relationnal_Bean");
/*     */   }
/*     */   
/*     */   public String asHibernateManagerClass() {
/* 609 */     return convertName("Hibernate_Manager");
/*     */   }
/*     */   
/*     */   public String asIteratorClass() {
/* 613 */     return convertName("Iterator");
/*     */   }
/*     */   
/*     */   public String asFactoryClass() {
/* 617 */     return convertName("Factory");
/*     */   }
/*     */   
/*     */   public String asHttpFactoryClass() {
/* 621 */     return convertName("Http_Factory");
/*     */   }
/*     */   
/*     */   public String asComparatorClass() {
/* 625 */     return convertName("Comparator");
/*     */   }
/*     */   
/*     */   public String asListenerClass() {
/* 629 */     return convertName("Listener");
/*     */   }
/*     */   
/*     */   public String asRendererClass() {
/* 633 */     return convertName("Renderer");
/*     */   }
/*     */   
/*     */   public String asExceptionClass() {
/* 637 */     return convertName("Exception");
/*     */   }
/*     */   
/*     */   public String asWidgetClass() {
/* 641 */     return convertName("Widget");
/*     */   }
/*     */   
/*     */   public String asWidgetFactoryClass() {
/* 645 */     return convertName("Widget_Factory");
/*     */   }
/*     */   
/*     */   public String asActionClass() {
/* 649 */     return convertName("Action");
/*     */   }
/*     */   
/*     */   public String asActionTestClass() {
/* 653 */     return convertName("Action_Test");
/*     */   }
/*     */   
/*     */   public String asControllerClass() {
/* 657 */     return convertName("Controller");
/*     */   }
/*     */   
/*     */   public String asControllerTestClass() {
/* 661 */     return convertName("Controller_Test");
/*     */   }
/*     */   
/*     */   public String asFormControllerClass() {
/* 665 */     return convertName("Form_Controller");
/*     */   }
/*     */   
/*     */   public String asFormControllerTestClass() {
/* 669 */     return convertName("Form_Controller_Test");
/*     */   }
/*     */   
/*     */   public String asDAOClass()
/*     */   {
/* 674 */     return convertName("D_A_O");
/*     */   }
/*     */   
/*     */   public String asDAOTestClass() {
/* 678 */     return convertName("D_A_O_Test");
/*     */   }
/*     */   
/*     */   public String asDAOHibernateClass() {
/* 682 */     return convertName("D_A_O_Hibernate");
/*     */   }
/*     */   
/*     */   public String asManagerClass() {
/* 686 */     return convertName("Manager");
/*     */   }
/*     */   
/*     */   public String asManagerImplClass() {
/* 690 */     return convertName("Manager_Impl");
/*     */   }
/*     */   
/*     */   public String asManagerTestClass() {
/* 694 */     return convertName("Manager_Test");
/*     */   }
/*     */   
/*     */   public String asModelClass() {
/* 698 */     return convertName("Model");
/*     */   }
/*     */   
/*     */   public String asPKClass() {
/* 702 */     return convertName("P_K");
/*     */   }
/*     */   
/*     */   public String asTblClass() {
/* 706 */     return convertName("Tbl");
/*     */   }
/*     */   
/*     */   public Column getVersionColumn()
/*     */   {
/* 711 */     int nbCols = this.cols.size();
/* 712 */     for (int i = 0; i < nbCols; i++)
/*     */     {
/* 714 */       Column c = (Column)this.cols.get(i);
/* 715 */       if (c.isVersion()) {
/* 716 */         return c;
/*     */       }
/*     */     }
/* 719 */     throw new IllegalArgumentException("No version column for table " + getName());
/*     */   }
/*     */   
/*     */   public boolean hasVersionColumn()
/*     */   {
/*     */     try
/*     */     {
/* 726 */       getVersionColumn();
/* 727 */       return true;
/*     */     }
/*     */     catch (IllegalArgumentException e) {}
/*     */     
/* 731 */     return false;
/*     */   }
/*     */   
/*     */ 
/* 735 */   private Random aleatorio = new Random(new Date().getTime());
/*     */   
/* 737 */   public long getSerialVersionUID() { return this.aleatorio.nextLong(); }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Table.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
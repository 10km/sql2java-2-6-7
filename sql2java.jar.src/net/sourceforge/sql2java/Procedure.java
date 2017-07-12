/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Procedure
/*     */ {
/*     */   private static final String IN_COLUMN = "IN";
/*     */   private static final String IN_OUT_COLUMN = "INOUT";
/*     */   private static final String OUT_COLUMN = "OUT";
/*     */   private String name;
/*     */   private String sqlName;
/*     */   private String remarks;
/*     */   private String returnType;
/*     */   private List columns;
/*     */   private List inColumns;
/*     */   private List outColumns;
/*     */   private HashMap colsHash;
/*     */   private int position;
/*     */   
/*     */   public Procedure()
/*     */   {
/*  23 */     this.columns = new java.util.ArrayList();
/*  24 */     this.inColumns = new java.util.ArrayList();
/*  25 */     this.outColumns = new java.util.ArrayList();
/*  26 */     this.colsHash = new HashMap();
/*  27 */     this.position = 1;
/*     */   }
/*     */   
/*     */   private int nextPosition() {
/*  31 */     return this.position++;
/*     */   }
/*     */   
/*     */   private boolean addColumn(Column column) {
/*  35 */     if (null == this.colsHash.get(column.getName())) {
/*  36 */       column.setOrdinalPosition(nextPosition());
/*  37 */       this.columns.add(column);
/*  38 */       this.colsHash.put(column.getName(), column);
/*  39 */       return true;
/*     */     }
/*  41 */     return false;
/*     */   }
/*     */   
/*     */   public void addInColumn(Column column) {
/*  45 */     column.setDefaultValue("IN");
/*  46 */     if (addColumn(column)) {
/*  47 */       this.inColumns.add(column);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInOutColumn(Column column) {
/*  52 */     column.setDefaultValue("INOUT");
/*  53 */     if (addColumn(column)) {
/*  54 */       this.inColumns.add(column);
/*  55 */       this.outColumns.add(column);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addOutColumn(Column column) {
/*  60 */     column.setDefaultValue("OUT");
/*  61 */     if (addColumn(column)) {
/*  62 */       this.outColumns.add(column);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getColumnsCount() {
/*  67 */     return this.columns.size();
/*     */   }
/*     */   
/*     */   public int getOutColumnsCount() {
/*  71 */     return this.outColumns.size();
/*     */   }
/*     */   
/*     */   public Column[] getColumns() {
/*  75 */     return (Column[])this.columns.toArray(new Column[this.columns.size()]);
/*     */   }
/*     */   
/*     */   public Column[] getInColumns() {
/*  79 */     return (Column[])this.inColumns.toArray(new Column[this.inColumns.size()]);
/*     */   }
/*     */   
/*     */   public Column[] getOutColumns() {
/*  83 */     return (Column[])this.outColumns.toArray(new Column[this.outColumns.size()]);
/*     */   }
/*     */   
/*     */   public String getName() {
/*  87 */     return this.name;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  91 */     this.sqlName = name;
/*  92 */     this.name = StringUtilities.convertName(name, true);
/*     */   }
/*     */   
/*     */   public String getSqlName() {
/*  96 */     return this.sqlName;
/*     */   }
/*     */   
/*     */   public String getRemarks() {
/* 100 */     return this.remarks;
/*     */   }
/*     */   
/*     */   public void setRemarks(String remarks) {
/* 104 */     this.remarks = remarks;
/*     */   }
/*     */   
/*     */   public String getReturnType() {
/* 108 */     return this.returnType;
/*     */   }
/*     */   
/*     */   public void setReturnType(String returnType) {
/* 112 */     this.returnType = returnType;
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Procedure.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
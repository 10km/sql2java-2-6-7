/*    */ package net.sourceforge.sql2java;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ public class Index
/*    */ {
/*    */   private Table table;
/*    */   private String name;
/*    */   private boolean unique;
/*    */   private Map columns;
/*    */   
/*    */   public Index()
/*    */   {
/* 19 */     this("");
/*    */   }
/*    */   
/*    */   public Index(String name) {
/* 23 */     this(name, null);
/*    */   }
/*    */   
/*    */   public Index(String name, Table table) {
/* 27 */     this(name, table, new HashMap());
/*    */   }
/*    */   
/*    */   public Index(String name, Table table, Map columns) {
/* 31 */     this.name = name;
/* 32 */     this.table = table;
/* 33 */     this.columns = columns;
/* 34 */     if (null != this.table) {
/* 35 */       this.table.addIndex(this);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void addIndexColumn(IndexColumn column)
/*    */   {
/* 42 */     if (null != column) {
/* 43 */       this.columns.put(column.getName().toLowerCase(), column);
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeIndexColumn(Column column) {
/* 48 */     if (null != column) {
/* 49 */       this.columns.remove(column.getName().toLowerCase());
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public Table getTable()
/*    */   {
/* 56 */     return this.table;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 60 */     return this.name;
/*    */   }
/*    */   
/*    */   public boolean isUnique() {
/* 64 */     return this.unique;
/*    */   }
/*    */   
/*    */   public Map getIndexColumns() {
/* 68 */     return this.columns;
/*    */   }
/*    */   
/*    */   public List getIndexColumnsList() {
/* 72 */     List list = new ArrayList();
/* 73 */     list.addAll(this.columns.values());
/* 74 */     Collections.sort(list);
/* 75 */     return list;
/*    */   }
/*    */   
/*    */ 
/*    */   public void setName(String name)
/*    */   {
/* 81 */     this.name = name;
/*    */   }
/*    */   
/*    */   public void setUnique(boolean unique) {
/* 85 */     this.unique = unique;
/*    */   }
/*    */   
/*    */   public void setTable(Table table) {
/* 89 */     this.table = table;
/*    */   }
/*    */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Index.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
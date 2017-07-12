/*    */ package net.sourceforge.sql2java;
/*    */ 
/*    */ 
/*    */ public class IndexColumn
/*    */   extends Column
/*    */ {
/*    */   private String sortSequence;
/*    */   
/*    */   private String filterCondition;
/*    */   
/*    */   public String getSortSequence()
/*    */   {
/* 13 */     return this.sortSequence;
/*    */   }
/*    */   
/*    */   public void setSortSequence(String sortSequence) {
/* 17 */     this.sortSequence = sortSequence;
/*    */   }
/*    */   
/*    */   public String getFilterCondition() {
/* 21 */     return this.filterCondition;
/*    */   }
/*    */   
/*    */   public void setFilterCondition(String condition) {
/* 25 */     this.filterCondition = condition;
/*    */   }
/*    */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\IndexColumn.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
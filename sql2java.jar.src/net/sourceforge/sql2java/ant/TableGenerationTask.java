/*    */ package net.sourceforge.sql2java.ant;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import net.sourceforge.sql2java.Main;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.Task;
/*    */ 
/*    */ public class TableGenerationTask
/*    */   extends Task
/*    */ {
/*    */   private String propertyFile;
/*    */   private String tables;
/*    */   
/*    */   public void execute()
/*    */     throws BuildException
/*    */   {
/* 19 */     System.out.println("TableGenerationTask: " + this.propertyFile);
/* 20 */     String[] args = { this.propertyFile };
/* 21 */     Map map = new HashMap();
/* 22 */     map.put("codewriter.include", this.tables);
/* 23 */     Main.main(args, map);
/*    */   }
/*    */   
/*    */   public void setPropertyFile(String msg)
/*    */   {
/* 28 */     this.propertyFile = msg;
/*    */   }
/*    */   
/*    */   public void setTables(String msg)
/*    */   {
/* 33 */     this.tables = msg;
/*    */   }
/*    */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\ant\TableGenerationTask.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package net.sourceforge.sql2java.ant;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import net.sourceforge.sql2java.Main;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.Task;
/*    */ 
/*    */ public class UtilsGenerationTask
/*    */   extends Task
/*    */ {
/*    */   private String propertyFile;
/*    */   
/*    */   public void execute()
/*    */     throws BuildException
/*    */   {
/* 18 */     System.out.println("UtilsGenerationTask: " + this.propertyFile);
/* 19 */     String[] args = { this.propertyFile };
/* 20 */     Map map = new HashMap();
/* 21 */     map.put("check.database", "false");
/* 22 */     map.put("write.only.per.schema.templates", "true");
/* 23 */     Main.main(args, map);
/*    */   }
/*    */   
/*    */   public void setPropertyFile(String msg)
/*    */   {
/* 28 */     this.propertyFile = msg;
/*    */   }
/*    */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\ant\UtilsGenerationTask.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
/*    */ package net.sourceforge.sql2java.ant;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import net.sourceforge.sql2java.Main;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.Task;
/*    */ 
/*    */ public class GenerationTask
/*    */   extends Task
/*    */ {
/*    */   private String propertyFile;
/*    */   
/*    */   public void execute() throws BuildException
/*    */   {
/* 15 */     System.out.println("GenerationTask: " + this.propertyFile);
/* 16 */     String[] args = { this.propertyFile };
/* 17 */     Main.main(args);
/*    */   }
/*    */   
/*    */   public void setPropertyFile(String msg)
/*    */   {
/* 22 */     this.propertyFile = msg;
/*    */   }
/*    */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\ant\GenerationTask.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
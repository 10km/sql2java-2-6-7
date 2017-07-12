/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jdom.Attribute;
/*     */ import org.jdom.Document;
/*     */ import org.jdom.Element;
/*     */ import org.jdom.input.SAXBuilder;
/*     */ import org.jdom.xpath.XPath;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigHelper
/*     */ {
/*  17 */   private static Document doc = null;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  24 */       String filename = CodeWriter.getProperty("sql2java.xml", "sql2java.xml");
/*  25 */       if (!new File(filename).isFile())
/*     */       {
/*  27 */         filename = "src/sql2java.xml";
/*  28 */         if (!new File(filename).isFile())
/*     */         {
/*  30 */           filename = "src/config/sql2java.xml";
/*     */         }
/*     */       }
/*  33 */       SAXBuilder builder = new SAXBuilder();
/*  34 */       doc = builder.build(new File(filename));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  38 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getTableProperty(String table, String propertyName)
/*     */   {
/*  44 */     return getXPathProperty("//table[@name='" + table.toLowerCase() + "']", propertyName.toLowerCase());
/*     */   }
/*     */   
/*     */   public static String getColumnProperty(String table, String column, String propertyName)
/*     */   {
/*  49 */     return getXPathProperty("//table[@name='" + table.toLowerCase() + "']/column[@name='" + column.toLowerCase() + "']", propertyName);
/*     */   }
/*     */   
/*     */   public static String getProperty(String propertyName)
/*     */   {
/*  54 */     return getXPathProperty("//sql2java", propertyName.toLowerCase());
/*     */   }
/*     */   
/*     */   public static String getGlobalProperty(String propertyName)
/*     */   {
/*  59 */     return getProperty(propertyName);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getXPathProperty(String xPathQuery, String propertyName)
/*     */   {
/*  65 */     if (doc == null) {
/*  66 */       return null;
/*     */     }
/*  68 */     String result = null;
/*     */     try
/*     */     {
/*  71 */       XPath servletPath = XPath.newInstance(xPathQuery);
/*  72 */       List nodes = servletPath.selectNodes(doc);
/*  73 */       if (nodes == null) {
/*  74 */         String str1 = null;return str1; }
/*  75 */       Iterator i = nodes.iterator();
/*  76 */       if (i.hasNext())
/*     */       {
/*  78 */         Element item = (Element)i.next();
/*     */         
/*     */ 
/*  81 */         if (item.getAttribute(propertyName) != null)
/*     */         {
/*  83 */           result = item.getAttribute(propertyName).getValue();
/*  84 */           if (result != null) {
/*  85 */             str2 = result;return str2;
/*     */           }
/*     */         }
/*     */         
/*  89 */         if (item.getChild(propertyName) == null)
/*     */         {
/*  91 */           str2 = null;return str2;
/*     */         }
/*     */         
/*     */ 
/*  95 */         result = item.getChild(propertyName).getTextTrim();
/*  96 */         String str2 = result;return str2;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 100 */       e = 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 108 */         e;e.printStackTrace();
/*     */     }
/*     */     finally {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 109 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\ConfigHelper.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Main
/*     */ {
/*     */   private static Properties prop;
/*     */   
/*     */   public static void main(String[] argv)
/*     */   {
/*  23 */     main(argv, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] argv, Map overideFileProperties)
/*     */   {
/*  32 */     if ((argv == null) || (argv.length < 1)) {
/*  33 */       System.err.println("Usage: java net.sourceforge.sql2java.Main <properties filename>");
/*  34 */       System.exit(1);
/*     */     }
/*     */     
/*     */ 
/*  38 */     prop = new Properties();
/*     */     try
/*     */     {
/*  41 */       prop.load(new FileInputStream(argv[0]));
/*  42 */       CodeWriter cw = new CodeWriter(null, prop);
/*  43 */       cw.log("database properties initialization");
/*     */       
/*  45 */       Database db = new Database();
/*  46 */       db.setDriver(getProperty("jdbc.driver"));
/*  47 */       db.setUrl(getProperty("jdbc.url"));
/*  48 */       db.setUsername(getProperty("jdbc.username"));
/*  49 */       db.setPassword(getProperty("jdbc.password"));
/*  50 */       db.setCatalog(getProperty("jdbc.catalog"));
/*  51 */       db.setSchema(getProperty("jdbc.schema"));
/*  52 */       db.setTableNamePattern(getProperty("jdbc.tablenamepattern"));
/*  53 */       db.setActiveConnections(getProperty("jdbc.active", "10"));
/*  54 */       db.setIdleConnections(getProperty("jdbc.idle", "5"));
/*  55 */       db.setMaxWait(getProperty("jdbc.maxwait", "120000"));
/*     */       
/*  57 */       CodeWriter writer = new CodeWriter(db, prop);
/*     */       
/*  59 */       if (overideFileProperties != null) {
/*  60 */         prop.putAll(overideFileProperties);
/*     */       }
/*  62 */       if ("false".equalsIgnoreCase(getProperty("jdbc.oracle.retrieve.remarks"))) {
/*  63 */         db.setOracleRetrieveRemarks(false);
/*     */       } else {
/*  65 */         db.setOracleRetrieveRemarks(true);
/*     */       }
/*  67 */       String tt = getProperty("jdbc.tabletypes", "TABLE");
/*  68 */       StringTokenizer st = new StringTokenizer(tt, ",; \t");
/*  69 */       ArrayList al = new ArrayList();
/*     */       
/*  71 */       while (st.hasMoreTokens()) {
/*  72 */         al.add(st.nextToken().trim());
/*     */       }
/*     */       
/*  75 */       db.setTableTypes((String[])al.toArray(new String[al.size()]));
/*     */       
/*  77 */       db.load();
/*     */       
/*     */ 
/*  80 */       if (argv.length > 1)
/*  81 */         writer.setDestinationFolder(argv[1]);
/*  82 */       writer.process();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  86 */       e.printStackTrace();
/*  87 */       System.exit(1);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getProperty(String property)
/*     */   {
/*  93 */     String res = ConfigHelper.getGlobalProperty(property);
/*  94 */     if (res != null)
/*  95 */       return res.trim();
/*  96 */     String s = prop.getProperty(property);
/*  97 */     return s != null ? s.trim() : s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getProperty(String property, String default_val)
/*     */   {
/* 105 */     String s = getProperty(property);
/* 106 */     if (s == null)
/* 107 */       return default_val;
/* 108 */     return s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isInArray(String[] ar, String code)
/*     */   {
/* 116 */     if (ar == null)
/* 117 */       return false;
/* 118 */     for (int i = 0; i < ar.length; i++)
/*     */     {
/* 120 */       if (code.equalsIgnoreCase(ar[i]))
/* 121 */         return true;
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\Main.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
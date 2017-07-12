/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.apache.velocity.VelocityContext;
/*     */ import org.apache.velocity.app.FieldMethodizer;
/*     */ import org.apache.velocity.app.Velocity;
/*     */ import org.apache.velocity.exception.ParseErrorException;
/*     */ import org.apache.velocity.exception.ResourceNotFoundException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CodeWriter
/*     */ {
/*     */   protected static Properties props;
/*  34 */   public static String MGR_CLASS = "Manager";
/*     */   
/*     */   protected static String dateClassName;
/*     */   
/*     */   protected static String timeClassName;
/*     */   protected static String timestampClassName;
/*     */   protected static Database db;
/*     */   protected static Hashtable includeHash;
/*     */   protected static Hashtable excludeHash;
/*     */   protected static String basePackage;
/*     */   protected static String destDir;
/*     */   protected static String optimisticLockType;
/*     */   protected static String optimisticLockColumn;
/*  47 */   public static String classPrefix = "";
/*     */   
/*     */ 
/*     */   protected VelocityContext vc;
/*     */   
/*     */ 
/*     */   public Table table;
/*     */   
/*     */ 
/*     */   protected VelocityContext current_vc;
/*     */   
/*     */ 
/*     */ 
/*     */   public CodeWriter(Database db, Properties props)
/*     */   {
/*     */     try
/*     */     {
/*  64 */       db = db;
/*  65 */       props = props;
/*     */       
/*  67 */       dateClassName = props.getProperty("jdbc2java.date", "java.sql.Date");
/*  68 */       timeClassName = props.getProperty("jdbc2java.time", "java.sql.Time");
/*  69 */       timestampClassName = props.getProperty("jdbc2java.timestamp", "java.sql.Timestamp");
/*     */       
/*     */ 
/*  72 */       basePackage = props.getProperty("codewriter.package");
/*  73 */       if (basePackage == null)
/*     */       {
/*  75 */         throw new Exception("Missing property: codewriter.package");
/*     */       }
/*     */       
/*  78 */       classPrefix = props.getProperty("codewriter.classprefix");
/*  79 */       setDestinationFolder(props.getProperty("codewriter.destdir"));
/*     */       
/*  81 */       excludeHash = setHash(props.getProperty("tables.exclude"));
/*  82 */       if (excludeHash.size() != 0)
/*  83 */         System.out.println("Excluding the following tables: " + props.getProperty("tables.exclude"));
/*  84 */       includeHash = setHash(props.getProperty("tables.include"));
/*  85 */       if (includeHash.size() != 0) {
/*  86 */         System.out.println("Including only the following tables: " + props.getProperty("tables.include"));
/*     */       }
/*  88 */       optimisticLockType = props.getProperty("optimisticlock.type", "none");
/*  89 */       optimisticLockColumn = props.getProperty("optimisticlock.column");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  93 */       System.err.println("Threw an exception in the CodeWriter constructor:" + e.getMessage());
/*  94 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDestinationFolder(String destDir)
/*     */     throws Exception
/*     */   {
/* 101 */     destDir = destDir;
/* 102 */     if (destDir == null)
/*     */     {
/* 104 */       throw new Exception("Missing property: codewriter.destdir");
/*     */     }
/*     */     
/* 107 */     File dir = new File(destDir);
/*     */     try {
/* 109 */       dir.mkdirs();
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/* 114 */     if ((!dir.isDirectory()) || (!dir.canWrite()))
/*     */     {
/* 116 */       throw new Exception("Cannot write to: " + destDir);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private Hashtable setHash(String str)
/*     */   {
/* 123 */     if ((str == null) || (str.trim().equals(""))) {
/* 124 */       return new Hashtable();
/*     */     }
/*     */     
/* 127 */     Hashtable hash = new Hashtable();
/* 128 */     StringTokenizer st = new StringTokenizer(str);
/* 129 */     while (st.hasMoreTokens())
/*     */     {
/* 131 */       String val = st.nextToken().toLowerCase();
/* 132 */       hash.put(val, val);
/*     */     }
/*     */     
/* 135 */     return hash;
/*     */   }
/*     */   
/*     */   public boolean checkTable(Table newTable)
/*     */     throws Exception
/*     */   {
/* 141 */     System.out.println("    checking table " + newTable.getName() + " ...");
/* 142 */     boolean error = false;
/* 143 */     Column[] primaryKeys = newTable.getPrimaryKeys();
/* 144 */     if (newTable.getColumns().length == 0)
/*     */     {
/* 146 */       System.err.println("        WARN : no column found !");
/* 147 */       error = false;
/*     */     }
/* 149 */     if (primaryKeys.length == 0)
/*     */     {
/* 151 */       System.err.println("        WARN : No primary key is defined on table " + newTable.getName());
/* 152 */       System.err.println("            Tables without primary key are not fully supported");
/* 153 */       error = false;
/*     */ 
/*     */ 
/*     */     }
/* 157 */     else if (primaryKeys.length > 1)
/*     */     {
/* 159 */       System.err.print("        WARN : Composite primary key ");
/* 160 */       for (int ii = 0; ii < primaryKeys.length; ii++)
/* 161 */         System.err.print(primaryKeys[ii].getFullName() + ", ");
/* 162 */       System.err.println();
/* 163 */       System.err.println("            Tables with composite primary key are not fully supported");
/*     */     }
/*     */     else
/*     */     {
/* 167 */       Column pk = primaryKeys[0];
/* 168 */       String pkName = pk.getName();
/* 169 */       String normalKey = newTable.getName() + "_id";
/* 170 */       if (!pkName.equalsIgnoreCase(normalKey))
/*     */       {
/* 172 */         System.err.println("          WARN : primary key should be of form <TABLE>_ID");
/* 173 */         System.err.println("              found " + pkName + " expected " + normalKey);
/*     */       }
/* 175 */       if (!pk.isColumnNumeric())
/*     */       {
/* 177 */         System.err.println("          WARN : primary key should be a number ");
/* 178 */         System.err.println("              found " + pk.getJavaType());
/*     */       }
/*     */     }
/*     */     
/* 182 */     return error;
/*     */   }
/*     */   
/*     */   public void checkDatabase() throws Exception
/*     */   {
/* 187 */     System.out.println("Checking database tables");
/* 188 */     boolean error = false;
/* 189 */     Table[] tables = db.getTables();
/* 190 */     for (int i = 0; i < tables.length; i++)
/*     */     {
/* 192 */       if (authorizeProcess(tables[i].getName(), "tables.include", "tables.exclude"))
/*     */       {
/* 194 */         boolean b = checkTable(tables[i]);
/* 195 */         if (b == true)
/* 196 */           error = true;
/*     */       }
/*     */     }
/* 199 */     if (error == true)
/*     */     {
/* 201 */       System.err.println("    Failed : at least one of the mandatory rule for sql2java is followed by your schema.");
/* 202 */       System.err.println("    Please check the documentation for more information");
/* 203 */       System.exit(-1);
/*     */     }
/* 205 */     System.out.println("    Passed.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void process()
/*     */     throws Exception
/*     */   {
/* 215 */     if ("true".equalsIgnoreCase(Main.getProperty("check.database"))) {
/* 216 */       checkDatabase();
/*     */     }
/* 218 */     if ("true".equalsIgnoreCase(Main.getProperty("check.only.database"))) {
/* 219 */       return;
/*     */     }
/*     */     
/* 222 */     Properties vprops = new Properties();
/* 223 */     vprops.put("file.resource.loader.path", getLoadingPath());
/* 224 */     vprops.put("velocimacro.library", "macros.include.vm");
/*     */     
/* 226 */     Velocity.init(vprops);
/* 227 */     this.vc = new VelocityContext();
/* 228 */     this.vc.put("CodeWriter", new FieldMethodizer(this));
/* 229 */     this.vc.put("codewriter", this);
/* 230 */     this.vc.put("pkg", basePackage);
/* 231 */     this.vc.put("pkgPath", basePackage.replace('.', '/'));
/* 232 */     this.vc.put("strUtil", StringUtilities.getInstance());
/* 233 */     this.vc.put("fecha", new Date());
/* 234 */     this.current_vc = new VelocityContext(this.vc);
/*     */     
/*     */ 
/* 237 */     String[] schema_templates = getSchemaTemplates("velocity.templates");
/* 238 */     for (int i = 0; i < schema_templates.length; i++) {
/* 239 */       writeComponent(schema_templates[i]);
/*     */     }
/* 241 */     if ("true".equalsIgnoreCase(Main.getProperty("write.only.per.schema.templates"))) {
/* 242 */       return;
/*     */     }
/*     */     
/* 245 */     Table[] tables = db.getTables();
/* 246 */     for (int i = 0; i < tables.length; i++) {
/* 247 */       if (authorizeProcess(tables[i].getName(), "tables.include", "tables.exclude")) {
/* 248 */         writeTable(tables[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeTable(Table currentTable) throws Exception {
/* 254 */     if (currentTable.getColumns().length == 0) {
/* 255 */       return;
/*     */     }
/* 257 */     this.current_vc = new VelocityContext(this.vc);
/* 258 */     this.table = currentTable;
/* 259 */     this.current_vc.put("table", currentTable);
/*     */     
/* 261 */     String[] table_templates = getTableTemplates("velocity.templates");
/* 262 */     for (int i = 0; i < table_templates.length; i++) {
/* 263 */       writeComponent(table_templates[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeComponent(String templateName)
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 279 */       System.out.println("Generating template " + templateName);
/* 280 */       Velocity.getTemplate(templateName);
/*     */     }
/*     */     catch (ResourceNotFoundException rnfe) {
/* 283 */       System.err.println("Aborted writing component:" + templateName + (this.table != null ? " for table:" + this.table.getName() : "") + " because Velocity could not find the resource.");
/*     */       
/*     */ 
/* 286 */       return;
/*     */     }
/*     */     catch (ParseErrorException pee) {
/* 289 */       System.err.println("Aborted writing component:" + templateName + (this.table != null ? " for table:" + this.table.getName() : "") + " because there was a parse error in the resource.\n" + pee.getLocalizedMessage());
/*     */       
/*     */ 
/* 292 */       return;
/*     */     }
/*     */     catch (Exception e) {
/* 295 */       System.err.println("Aborted writing component:" + templateName + (this.table != null ? " for table:" + this.table.getName() : "") + " there was an error initializing the template.\n" + e.getLocalizedMessage());
/*     */       
/*     */ 
/* 298 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 303 */     StringWriter sw = new StringWriter();
/* 304 */     Velocity.mergeTemplate(templateName, "ISO-8859-1", this.current_vc, sw);
/*     */     
/* 306 */     System.out.println(" .... writing to " + this.current_fullfilename);
/*     */     
/*     */ 
/* 309 */     File file = new File(this.current_fullfilename);
/* 310 */     new File(file.getParent()).mkdirs();
/* 311 */     PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.current_fullfilename)));
/* 312 */     writer.write(sw.toString());
/* 313 */     writer.flush();
/* 314 */     writer.close();
/* 315 */     System.out.println("    " + this.current_filename + " done.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 322 */   String current_fullfilename = "";
/* 323 */   String current_filename = "";
/*     */   
/*     */   public void setCurrentFilename(String relpath_or_package, String fn) throws Exception {
/* 326 */     this.current_filename = (relpath_or_package.replace('.', File.separatorChar) + File.separatorChar + fn);
/* 327 */     this.current_fullfilename = (destDir + File.separatorChar + relpath_or_package.replace('.', File.separatorChar) + File.separatorChar + fn);
/*     */     
/* 329 */     UserCodeParser uc = new UserCodeParser(this.current_fullfilename);
/* 330 */     this.current_vc.put("userCode", uc);
/*     */   }
/*     */   
/*     */   public void setCurrentJavaFilename(String relpath_or_package, String fn) throws Exception {
/* 334 */     setCurrentFilename("java" + File.separatorChar + relpath_or_package, fn);
/*     */   }
/*     */   
/*     */ 
/*     */   public void log(String logStr)
/*     */   {
/* 340 */     System.out.println("        " + logStr);
/*     */   }
/*     */   
/*     */   public static String getClassPrefix() {
/* 344 */     return classPrefix;
/*     */   }
/*     */   
/*     */   public Database getDb()
/*     */   {
/* 349 */     return db;
/*     */   }
/*     */   
/*     */   public List getTables()
/*     */   {
/* 354 */     Table[] tabs = db.getTables();
/* 355 */     List tables = new ArrayList(tabs.length);
/* 356 */     for (int i = 0; i < tabs.length; i++) tables.add(tabs[i]);
/* 357 */     return tables;
/*     */   }
/*     */   
/*     */   public Table getTable(String tableName)
/*     */   {
/* 362 */     return db.getTable(tableName);
/*     */   }
/*     */   
/*     */   public List getRelationTables()
/*     */   {
/* 367 */     Table[] tabs = db.getTables();
/* 368 */     List tables = new ArrayList(tabs.length);
/* 369 */     for (int i = 0; i < tabs.length; i++) {
/* 370 */       if (tabs[i].isRelationTable()) {
/* 371 */         tables.add(tabs[i]);
/*     */       }
/*     */     }
/* 374 */     return tables;
/*     */   }
/*     */   
/*     */   public String tableName()
/*     */   {
/* 379 */     if (this.table == null) return "";
/* 380 */     return this.table.getName();
/*     */   }
/*     */   
/*     */   public Table getTable()
/*     */   {
/* 385 */     return this.table;
/*     */   }
/*     */   
/*     */   public boolean listContainsString(List list, String string)
/*     */   {
/* 390 */     Object obj = null;
/* 391 */     Iterator iter = list.iterator();
/* 392 */     for (; iter.hasNext(); 
/* 393 */         obj = iter.next()) {
/* 394 */       if (string.equals(obj)) return true;
/*     */     }
/* 396 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getProperty(String key)
/*     */   {
/* 410 */     String s = props.getProperty(key);
/* 411 */     return s != null ? s.trim() : s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getProperty(String key, String default_val)
/*     */   {
/* 421 */     String s = props.getProperty(key, default_val);
/* 422 */     return s != null ? s.trim() : s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] getPropertyExploded(String key)
/*     */   {
/* 431 */     return getPropertyExploded(key, "");
/*     */   }
/*     */   
/*     */   public static String[] getPropertyExploded(String mkey, String defaultValue)
/*     */   {
/* 436 */     String v = getProperty(mkey);
/* 437 */     if (v == null) {
/* 438 */       v = defaultValue;
/*     */     }
/* 440 */     return getExplodedString(v);
/*     */   }
/*     */   
/*     */   public static String[] getExplodedString(String value)
/*     */   {
/* 445 */     ArrayList al = new ArrayList();
/* 446 */     if (value == null) {
/* 447 */       return new String[0];
/*     */     }
/* 449 */     StringTokenizer st = new StringTokenizer(value, " ,;\t");
/* 450 */     while (st.hasMoreTokens()) {
/* 451 */       al.add(st.nextToken().trim());
/*     */     }
/*     */     
/* 454 */     return (String[])al.toArray(new String[al.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLoadingPath()
/*     */   {
/* 462 */     String ret = "";
/* 463 */     String[] paths = getPropertyExploded("velocity.templates.loadingpath", ".");
/* 464 */     for (int i = 0; i < paths.length; i++) {
/* 465 */       ret = ret + paths[i] + ",";
/*     */     }
/* 467 */     System.out.println("getLoadingPath = " + ret);
/* 468 */     return ret;
/*     */   }
/*     */   
/*     */   public String[] getSchemaTemplates(String property)
/*     */   {
/* 473 */     return getTemplates(property, true);
/*     */   }
/*     */   
/*     */   public String[] getTableTemplates(String property)
/*     */   {
/* 478 */     return getTemplates(property, false);
/*     */   }
/*     */   
/*     */   public String[] getTemplates(String property, boolean perShema)
/*     */   {
/* 483 */     Vector files = new Vector();
/* 484 */     recurseTemplate(files, Main.getProperty(property), perShema);
/* 485 */     return (String[])files.toArray(new String[files.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector recurseTemplate(Vector files, String folder, boolean perSchema)
/*     */   {
/* 493 */     String schemaOrTable = "perschema";
/* 494 */     if (!perSchema)
/* 495 */       schemaOrTable = "pertable";
/* 496 */     FileFilter filter = new FileFilter()
/*     */     {
/*     */       public boolean accept(File filename)
/*     */       {
/* 500 */         if (filename.isDirectory())
/* 501 */           return true;
/* 502 */         if (!filename.getName().endsWith(".vm"))
/* 503 */           return false;
/* 504 */         return CodeWriter.authorizeProcess(filename.getName(), "template.file.include", "template.file.exclude");
/*     */       }
/*     */       
/* 507 */     };
/* 508 */     File[] dirEntries = new File(folder).listFiles(filter);
/* 509 */     if (dirEntries == null)
/* 510 */       return files;
/* 511 */     for (int i = 0; i < dirEntries.length; i++)
/*     */     {
/*     */ 
/* 514 */       if (dirEntries[i].isFile())
/*     */       {
/*     */ 
/* 517 */         if (authorizeFile(folder, schemaOrTable))
/*     */         {
/* 519 */           files.add(folder + "/" + dirEntries[i].getName());
/*     */         }
/*     */       }
/* 522 */       else if (dirEntries[i].isDirectory())
/* 523 */         recurseTemplate(files, folder + "/" + dirEntries[i].getName(), perSchema);
/*     */     }
/* 525 */     return files;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean authorizeProcess(String autorizePattern, String includeProperty, String excludeProperty)
/*     */   {
/* 533 */     boolean accept = true;
/* 534 */     String[] include = getPropertyExploded(includeProperty);
/* 535 */     String[] exclude = getPropertyExploded(excludeProperty);
/* 536 */     if (include.length != 0)
/*     */     {
/* 538 */       if (Main.isInArray(include, autorizePattern))
/*     */       {
/* 540 */         System.out.println("Processing " + autorizePattern + " (specified in " + includeProperty + ")");
/* 541 */         return true;
/*     */       }
/* 543 */       accept = false;
/*     */     }
/* 545 */     if (exclude.length != 0)
/*     */     {
/* 547 */       if (Main.isInArray(exclude, autorizePattern))
/*     */       {
/* 549 */         System.out.println("Skipping " + autorizePattern + " (specified in " + excludeProperty + ")");
/* 550 */         return false;
/*     */       }
/*     */     }
/* 553 */     return accept;
/*     */   }
/*     */   
/*     */   public static boolean folderContainsPattern(String folder, String[] patterns)
/*     */   {
/* 558 */     if ((patterns == null) || (folder == null))
/* 559 */       return false;
/* 560 */     for (int i = 0; i < patterns.length; i++)
/*     */     {
/* 562 */       String pattern = "/" + patterns[i].toLowerCase() + "/";
/* 563 */       if (folder.toLowerCase().indexOf(pattern) != -1)
/*     */       {
/* 565 */         return true;
/*     */       }
/*     */     }
/* 568 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean authorizeFile(String folder, String schemaOrTable)
/*     */   {
/* 576 */     if (folder.toLowerCase().indexOf(schemaOrTable.toLowerCase()) == -1)
/* 577 */       return false;
/* 578 */     String[] include = getPropertyExploded("template.folder.include");
/* 579 */     String[] exclude = getPropertyExploded("template.folder.exclude");
/* 580 */     if (include.length != 0)
/*     */     {
/* 582 */       if (folderContainsPattern(folder, include) == true)
/* 583 */         return true;
/* 584 */       return false;
/*     */     }
/* 586 */     if (exclude.length != 0)
/*     */     {
/* 588 */       if (folderContainsPattern(folder, exclude) == true) {
/* 589 */         return false;
/*     */       }
/* 591 */       return true;
/*     */     }
/* 593 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\CodeWriter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
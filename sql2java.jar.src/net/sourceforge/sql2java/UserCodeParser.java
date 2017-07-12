/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UserCodeParser
/*     */ {
/*     */   private static final String START = "// ";
/*     */   private static final String BLOCK_BEGIN = "+";
/*     */   private static final String BLOCK_END = "-";
/*  27 */   private static final String LINE_SEP = System.getProperty("line.separator");
/*     */   
/*     */ 
/*     */ 
/*     */   private Hashtable codeHash;
/*     */   
/*     */ 
/*     */ 
/*     */   private String filename;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isNew;
/*     */   
/*     */ 
/*     */ 
/*     */   public UserCodeParser(String filename)
/*     */     throws Exception
/*     */   {
/*  46 */     parse(filename);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFilename()
/*     */   {
/*  53 */     return this.filename;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isNew()
/*     */   {
/*  60 */     return this.isNew;
/*     */   }
/*     */   
/*     */ 
/*     */   public void parse(String parsedFileName)
/*     */     throws Exception
/*     */   {
/*  67 */     this.codeHash = new Hashtable();
/*  68 */     boolean inBlock = false;
/*  69 */     String blockName = null;
/*  70 */     StringBuffer code = new StringBuffer();
/*  71 */     this.isNew = true;
/*     */     
/*  73 */     File file = new File(parsedFileName);
/*  74 */     if (file.exists()) {
/*  75 */       this.filename = parsedFileName;
/*  76 */       this.isNew = false;
/*  77 */       BufferedReader reader = new BufferedReader(new FileReader(file));
/*  78 */       String line = reader.readLine();
/*  79 */       while (line != null) {
/*  80 */         if (inBlock) {
/*  81 */           code.append(line).append(LINE_SEP);
/*     */         }
/*     */         
/*  84 */         if (line.indexOf("// ") != -1) {
/*  85 */           if (inBlock) {
/*  86 */             if (line.equals("// " + blockName + "-"))
/*     */             {
/*  88 */               this.codeHash.put(blockName, code.toString());
/*  89 */               inBlock = false;
/*     */             }
/*     */           } else {
/*  92 */             blockName = parseName(line);
/*  93 */             if (!"".equals(blockName)) {
/*  94 */               inBlock = true;
/*  95 */               code.setLength(0);
/*  96 */               code.append(line).append(LINE_SEP);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 101 */         line = reader.readLine();
/*     */       }
/* 103 */       reader.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private String parseName(String line) {
/* 108 */     int startPos = line.indexOf("// ");
/* 109 */     if (startPos == -1) {
/* 110 */       return "";
/*     */     }
/* 112 */     startPos += "// ".length();
/*     */     
/* 114 */     if (startPos >= line.length() + 1) {
/* 115 */       return "";
/*     */     }
/*     */     
/* 118 */     int endPos = line.lastIndexOf("+", startPos);
/* 119 */     if (endPos != line.length() - "+".length()) {
/* 120 */       return "";
/*     */     }
/* 122 */     String name = line.substring(startPos, endPos);
/* 123 */     return name.trim();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasBlock(String name)
/*     */   {
/* 135 */     return this.codeHash.get(name) != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getBlock(String name)
/*     */   {
/* 146 */     String code = null;
/* 147 */     if (name != null) {
/* 148 */       code = (String)this.codeHash.get(name);
/*     */     }
/* 150 */     if (code == null) {
/* 151 */       code = generateNewBlock(name);
/* 152 */       this.codeHash.put(name, code);
/*     */     }
/* 154 */     return code;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getBlockNames()
/*     */   {
/* 162 */     String[] list = new String[this.codeHash.size()];
/* 163 */     int i = 0;
/* 164 */     for (Enumeration e = this.codeHash.keys(); e.hasMoreElements();) {
/* 165 */       list[(i++)] = ((String)e.nextElement());
/*     */     }
/* 167 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String generateNewBlock(String name)
/*     */   {
/* 174 */     StringBuffer str = new StringBuffer(512);
/* 175 */     str.append("// ");
/* 176 */     str.append(name);
/* 177 */     str.append("+");
/* 178 */     str.append(LINE_SEP).append(LINE_SEP);
/* 179 */     str.append("// ");
/* 180 */     str.append(name);
/* 181 */     str.append("-");
/*     */     
/* 183 */     return str.toString();
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\UserCodeParser.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
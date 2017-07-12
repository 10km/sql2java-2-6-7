/*     */ package net.sourceforge.sql2java;
/*     */ 
/*     */ import java.util.Random;
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
/*     */ public final class StringUtilities
/*     */ {
/*  17 */   private static StringUtilities singleton = new StringUtilities();
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String PREFIX = "";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized StringUtilities getInstance()
/*     */   {
/*  28 */     return singleton;
/*     */   }
/*     */   
/*     */   public String getPackageAsPath(String pkg) {
/*  32 */     if (pkg == null)
/*  33 */       return "";
/*  34 */     return pkg.replace('.', '/');
/*     */   }
/*     */   
/*     */ 
/*     */   public static String convertClass(String table, String type)
/*     */   {
/*  40 */     String suffix = "";
/*  41 */     String postfix = "";
/*  42 */     if (!"".equalsIgnoreCase(""))
/*  43 */       suffix = suffix + "_";
/*  44 */     if (!"".equalsIgnoreCase(type))
/*  45 */       postfix = "_" + type;
/*  46 */     return convertName(suffix + table + postfix, false);
/*     */   }
/*     */   
/*     */   public static String convertClass(Table table, String type)
/*     */   {
/*  51 */     return convertClass(table.getName(), type);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String convertName(String name, boolean wimpyCaps)
/*     */   {
/*  68 */     StringBuffer buffer = new StringBuffer(name.length());
/*  69 */     char[] list = name.toLowerCase().toCharArray();
/*  70 */     for (int i = 0; i < list.length; i++) {
/*  71 */       if ((i == 0) && (!wimpyCaps)) {
/*  72 */         buffer.append(Character.toUpperCase(list[i]));
/*  73 */       } else if ((list[i] == '_') && (i + 1 < list.length) && (i != 0))
/*  74 */         buffer.append(Character.toUpperCase(list[(++i)])); else
/*  75 */         buffer.append(list[i]);
/*     */     }
/*  77 */     return buffer.toString();
/*     */   }
/*     */   
/*     */   public static String escape(String s) {
/*  81 */     return isReserved(s) ? "my_" + s : s;
/*     */   }
/*     */   
/*     */   public static String escape(Column s) {
/*  85 */     return isReserved(s.getName()) ? "my_" + s.getName() : s.getName();
/*     */   }
/*     */   
/*     */   public static boolean isReserved(String s) {
/*  89 */     for (int i = 0; i < reserved_words.length; i++) {
/*  90 */       if (s.compareToIgnoreCase(reserved_words[i]) == 0) {
/*  91 */         return true;
/*     */       }
/*     */     }
/*  94 */     return false;
/*     */   }
/*     */   
/*  97 */   static String[] reserved_words = { "null", "true", "false", "abstract", "double", "int", "strictfp", "boolean", "else", "interface", "super", "break", "extends", "long", "switch", "byte", "final", "native", "synchronized", "case", "finally", "new", "this", "catch", "float", "package", "throw", "char", "for", "private", "throws", "class", "goto", "protected", "transient", "const", "if", "public", "try", "continue", "implements", "return", "void", "default", "import", "short", "volatile", "do", "instanceof", "static", "while", "assert", "enum" };
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
/* 153 */   static String[] latin = { "Ac", "Aethiopia", "Africae", "Anazarbus", "Argonautarum", "Ciliciam", "Cydno", "Danaes,", "Etenim", "Iovis", "Mopsi,", "Mopsuestia", "Perseus", "Quibus", "Sandan", "Tarsus", "a", "abstractum", "ad", "alacriter", "alia", "amni", "anceps", "artes,", "auctoris", "aureo", "aut", "bene", "certamen", "certe", "cespite", "cognatione", "commune", "concitat", "condidisse", "conmilitio", "consumpsit,", "consurgentem", "continentur.", "cum", "cunctorum.", "dediti", "delatumque", "dicendi", "dici", "direpto", "disciplina,", "distarent,", "dolorem", "dolorum", "domicilium", "ductores", "eius", "eo", "errore", "et", "eum", "ex", "explicatis", "exultat,", "facultas", "feriens", "filius", "forte", "fuimus.", "gestu", "habent", "habitus", "haec", "hanc", "hastisque", "haut", "heroici", "hoc", "huic", "humanitatem", "iam", "illius", "in", "ingeni,", "intempestivum", "inter", "iram", "ita", "litus", "locari", "longe", "manes", "medentur", "memoratur,", "miles", "miretur,", "mors", "muri", "ne", "neque", "nobilis", "nobilitat,", "nobis", "nomine", "nos", "occurrere", "omnes", "opulentus", "ordinibus", "parans", "penitus", "perspicabilis", "pertinax", "pertinent,", "plerumque", "poterat", "profectus", "proximos", "pugnantium", "punico", "quadam", "quae", "quaedam", "quasi", "quem", "qui", "quidam", "quidem", "quis", "quod", "quoddam", "quorum", "rati", "ratio", "redirent,", "referens,", "repentina", "revocavere", "scuta", "se", "securitas", "sed", "sit", "solido", "sospitales.", "studio", "subire", "tecti", "terrebat", "tutela", "umquam", "uni", "urbs", "varietati", "vatis", "vel", "vellere", "vero,", "vinculum,", "vir", "vocabulum" };
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 313 */   private static Random rand = new Random();
/*     */   
/*     */   public static String getSampleString(int size) {
/* 316 */     StringBuffer s = new StringBuffer(size);
/* 317 */     while (s.length() < size - 5)
/*     */     {
/* 319 */       s.append(latin[rand.nextInt(latin.length)]).append(" ");
/*     */     }
/* 321 */     return s.toString();
/*     */   }
/*     */ }


/* Location:              D:\sql2java-2-6-7\lib\sql2java.jar!\net\sourceforge\sql2java\StringUtilities.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
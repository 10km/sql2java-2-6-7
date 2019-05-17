/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.util.Random;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import net.sourceforge.sql2java.Column;
import net.sourceforge.sql2java.Table;

public final class StringUtilities {
	private static StringUtilities singleton = new StringUtilities();
	public static final String PREFIX = "";
	static String[] reserved_words = new String[]{"null", "true", "false", "abstract", "double", "int", "strictfp",
			"boolean", "else", "interface", "super", "break", "extends", "long", "switch", "byte", "final", "native",
			"synchronized", "case", "finally", "new", "this", "catch", "float", "package", "throw", "char", "for",
			"private", "throws", "class", "goto", "protected", "transient", "const", "if", "public", "try", "continue",
			"implements", "return", "void", "default", "import", "short", "volatile", "do", "instanceof", "static",
			"while", "assert", "enum"};
	static String[] latin = new String[]{"Ac", "Aethiopia", "Africae", "Anazarbus", "Argonautarum", "Ciliciam", "Cydno",
			"Danaes,", "Etenim", "Iovis", "Mopsi,", "Mopsuestia", "Perseus", "Quibus", "Sandan", "Tarsus", "a",
			"abstractum", "ad", "alacriter", "alia", "amni", "anceps", "artes,", "auctoris", "aureo", "aut", "bene",
			"certamen", "certe", "cespite", "cognatione", "commune", "concitat", "condidisse", "conmilitio",
			"consumpsit,", "consurgentem", "continentur.", "cum", "cunctorum.", "dediti", "delatumque", "dicendi",
			"dici", "direpto", "disciplina,", "distarent,", "dolorem", "dolorum", "domicilium", "ductores", "eius",
			"eo", "errore", "et", "eum", "ex", "explicatis", "exultat,", "facultas", "feriens", "filius", "forte",
			"fuimus.", "gestu", "habent", "habitus", "haec", "hanc", "hastisque", "haut", "heroici", "hoc", "huic",
			"humanitatem", "iam", "illius", "in", "ingeni,", "intempestivum", "inter", "iram", "ita", "litus", "locari",
			"longe", "manes", "medentur", "memoratur,", "miles", "miretur,", "mors", "muri", "ne", "neque", "nobilis",
			"nobilitat,", "nobis", "nomine", "nos", "occurrere", "omnes", "opulentus", "ordinibus", "parans", "penitus",
			"perspicabilis", "pertinax", "pertinent,", "plerumque", "poterat", "profectus", "proximos", "pugnantium",
			"punico", "quadam", "quae", "quaedam", "quasi", "quem", "qui", "quidam", "quidem", "quis", "quod",
			"quoddam", "quorum", "rati", "ratio", "redirent,", "referens,", "repentina", "revocavere", "scuta", "se",
			"securitas", "sed", "sit", "solido", "sospitales.", "studio", "subire", "tecti", "terrebat", "tutela",
			"umquam", "uni", "urbs", "varietati", "vatis", "vel", "vellere", "vero,", "vinculum,", "vir", "vocabulum"};
	private static Random rand = new Random();

	private StringUtilities() {
	}

	public static synchronized StringUtilities getInstance() {
		return singleton;
	}

	public String getPackageAsPath(String pkg) {
		if (pkg == null) {
			return "";
		}
		return pkg.replace('.', '/');
	}

	public static String convertClass(String table, String type) {
		String suffix = "";
		String postfix = "";
		if (!"".equalsIgnoreCase("")) {
			suffix = suffix + "_";
		}
		if (!"".equalsIgnoreCase(type)) {
			postfix = "_" + type;
		}
		return StringUtilities.convertName(suffix + table + postfix, false);
	}

	public static String convertClass(Table table, String type) {
		return StringUtilities.convertClass(table.getName(), type);
	}

	public static String convertName(String name, boolean wimpyCaps) {
		StringBuffer buffer = new StringBuffer(name.length());
		char[] list = name.toLowerCase().toCharArray();
		for (int i = 0; i < list.length; ++i) {
			if (i == 0 && !wimpyCaps) {
				buffer.append(Character.toUpperCase(list[i]));
				continue;
			}
			if (list[i] == '_' && i + 1 < list.length && i != 0) {
				buffer.append(Character.toUpperCase(list[++i]));
				continue;
			}
			buffer.append(list[i]);
		}
		return buffer.toString();
	}

	/**
	 * 字符串首字母大写
	 * @param name
	 * @return
	 */
	public static String firstUpperCase(String name){
		if(Strings.isNullOrEmpty(name))return name;
		char[] list = name.toCharArray();
		list[0] = Character.toUpperCase(list[0]);
		return new String(list);
	}
	public static String escape(String s) {
		return StringUtilities.isReserved(s) ? "my_" + s : s;
	}

	public static String escape(Column s) {
		return StringUtilities.isReserved(s.getName()) ? "my_" + s.getName() : s.getName();
	}

	public static boolean isReserved(String s) {
		for (int i = 0; i < reserved_words.length; ++i) {
			if (s.compareToIgnoreCase(reserved_words[i]) != 0)
				continue;
			return true;
		}
		return false;
	}

	public static String getSampleString(int size) {
		StringBuffer s = new StringBuffer(size);
		while (s.length() < size - 5) {
			s.append(latin[rand.nextInt(latin.length)]).append(" ");
		}
		return s.toString();
	}
	public static final String asJavaString(String input){
		String[] list = MoreObjects.firstNonNull(input, "").split("\\r?\\n");
		return "\"" + Joiner.on("\"\n+\"").join(list) + "\"";	
	}
}
/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.sql2java.Column;
import net.sourceforge.sql2java.Database;
import net.sourceforge.sql2java.Main;
import net.sourceforge.sql2java.StringUtilities;
import net.sourceforge.sql2java.Table;
import net.sourceforge.sql2java.UserCodeParser;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.EscapeTool;

public class CodeWriter {
	protected static final String DEFAULT_BINARY_TYPE = "byte[]";
	protected static Properties props;
	public static String MGR_CLASS;
	protected static String dateClassName;
	protected static String timeClassName;
	protected static String timestampClassName;
	/** type for byte array,default 'byte[]',defined in properties */
	public static String binaryClassName;
	protected static Database db;
	protected static Hashtable<String, String> includeHash;
	protected static Hashtable<String, String> excludeHash;
	protected static String generalPackage;
	protected static String basePackage;
	protected static String destDir;
	protected static String optimisticLockType;
	protected static String optimisticLockColumn;
	public static String classPrefix;
	protected VelocityContext vc;
	public Table table;
	protected VelocityContext current_vc;
	String current_fullfilename = "";
	String current_filename = "";
	/** 是否保存当前文件标志,模板可以通过改写此标志,跳过模板生成 */
	boolean save_current_file = true;
	public CodeWriter(Database db, Properties props) {
		try {
			CodeWriter.db = db;
			CodeWriter.props = props;
			dateClassName = props.getProperty("jdbc2java.date", "java.sql.Date");
			timeClassName = props.getProperty("jdbc2java.time", "java.sql.Time");
			timestampClassName = props.getProperty("jdbc2java.timestamp", "java.sql.Timestamp");
			binaryClassName = props.getProperty("binary.type", DEFAULT_BINARY_TYPE);
			generalPackage = props.getProperty("general.package"); 
			basePackage = props.getProperty("codewriter.package");
			if (basePackage == null) {
				throw new Exception("Missing property: codewriter.package");
			}
			classPrefix = props.getProperty("codewriter.classprefix");
			this.setDestinationFolder(props.getProperty("codewriter.destdir"));
			excludeHash = this.setHash(props.getProperty("tables.exclude"));
			if (excludeHash.size() != 0) {
				System.out.println("Excluding the following tables: " + props.getProperty("tables.exclude"));
			}
			if ((CodeWriter.includeHash = this.setHash(props.getProperty("tables.include"))).size() != 0) {
				System.out.println("Including only the following tables: " + props.getProperty("tables.include"));
			}
			optimisticLockType = props.getProperty("optimisticlock.type", "none");
			optimisticLockColumn = props.getProperty("optimisticlock.column");
		} catch (Exception e) {
			System.err.println("Threw an exception in the CodeWriter constructor:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setDestinationFolder(String destDir) throws Exception {
		CodeWriter.destDir = destDir;
		if (destDir == null) {
			throw new Exception("Missing property: codewriter.destdir");
		}
		File dir = new File(destDir);
		try {
			dir.mkdirs();
		} catch (Exception e) {
			// empty catch block
		}
		if (!dir.isDirectory() || !dir.canWrite()) {
			throw new Exception("Cannot write to: " + destDir);
		}
	}

	private Hashtable<String, String> setHash(String str) {
		if (str == null || str.trim().equals("")) {
			return new Hashtable<String, String>();
		}
		Hashtable<String, String> hash = new Hashtable<String, String>();
		StringTokenizer st = new StringTokenizer(str);
		while (st.hasMoreTokens()) {
			String val = st.nextToken().toLowerCase();
			hash.put(val, val);
		}
		return hash;
	}

	public boolean checkTable(Table newTable) throws Exception {
		System.out.println("    checking table " + newTable.getName() + " ...");
		boolean error = false;
		Column[] primaryKeys = newTable.getPrimaryKeys();
		if (newTable.getColumns().length == 0) {
			System.err.println("        WARN : no column found !");
			error = false;
		}
		if (primaryKeys.length == 0) {
			System.err.println("        WARN : No primary key is defined on table " + newTable.getName());
			System.err.println("            Tables without primary key are not fully supported");
			error = false;
		} else if (primaryKeys.length > 1) {
			System.err.print("        WARN : Composite primary key ");
			for (int ii = 0; ii < primaryKeys.length; ++ii) {
				System.err.print(primaryKeys[ii].getFullName() + ", ");
			}
			System.err.println();
			System.err.println("            Tables with composite primary key are not fully supported");
		} else {
			String normalKey;
			Column pk = primaryKeys[0];
			String pkName = pk.getName();
			if (!pkName.equalsIgnoreCase(normalKey = newTable.getName() + "_id")) {
				System.err.println("          WARN : primary key should be of form <TABLE>_ID");
				System.err.println("              found " + pkName + " expected " + normalKey);
			}
			if (!pk.isColumnNumeric()) {
				System.err.println("          WARN : primary key should be a number ");
				System.err.println("              found " + pk.getJavaType());
			}
		}
		return error;
	}

	public void checkDatabase() throws Exception {
		System.out.println("Checking database tables");
		boolean error = false;
		Table[] tables = db.getTables();
		for (int i = 0; i < tables.length; ++i) {
			boolean b;
			if (!CodeWriter.authorizeProcess(tables[i].getName(), "tables.include", "tables.exclude")
					|| !(b = this.checkTable(tables[i])))
				continue;
			error = true;
		}
		if (error) {
			System.err.println(
					"    Failed : at least one of the mandatory rule for sql2java is followed by your schema.");
			System.err.println("    Please check the documentation for more information");
			System.exit(-1);
		}
		System.out.println("    Passed.");
	}

	public synchronized void process() throws Exception {
		if ("true".equalsIgnoreCase(Main.getProperty((String) "check.database"))) {
			this.checkDatabase();
		}
		if ("true".equalsIgnoreCase(Main.getProperty((String) "check.only.database"))) {
			return;
		}
		Properties vprops = new Properties();
		vprops.put("file.resource.loader.path", this.getLoadingPath());
		vprops.put("velocimacro.library", "macros.include.vm");
		Velocity.init((Properties) vprops);
		this.vc = new VelocityContext();
		this.vc.put("CodeWriter", (Object) new FieldMethodizer((Object) this));
		this.vc.put("codewriter", (Object) this);
		this.vc.put("esc", new EscapeTool());
		this.vc.put("pkg", (Object) basePackage);
		this.vc.put("gpkg", (Object) generalPackage);
		this.vc.put("schemaPkg", isGeneral()?generalPackage : basePackage);
		this.vc.put("isGeneral", isGeneral());
		this.vc.put("pkgPath", (Object) basePackage.replace('.', '/'));
		this.vc.put("strUtil", (Object) StringUtilities.getInstance());
		this.vc.put("fecha", (Object) new Date());
		this.current_vc = new VelocityContext((Context) this.vc);
		String[] schema_templates = this.getSchemaTemplates("velocity.templates");
		for (int i = 0; i < schema_templates.length; ++i) {
			this.writeComponent(schema_templates[i]);
		}
		if ("true".equalsIgnoreCase(Main.getProperty((String) "write.only.per.schema.templates"))) {
			return;
		}
		Table[] tables = db.getTables();
		for (int i2 = 0; i2 < tables.length; ++i2) {
			if (!CodeWriter.authorizeProcess(tables[i2].getName(), "tables.include", "tables.exclude"))
				continue;
			this.writeTable(tables[i2]);
		}
	}

	private void writeTable(Table currentTable) throws Exception {
		if (currentTable.getColumns().length == 0) {
			return;
		}
		this.current_vc = new VelocityContext((Context) this.vc);
		this.table = currentTable;
		this.current_vc.put("table", (Object) currentTable);
		String[] table_templates = this.getTableTemplates("velocity.templates");
		for (int i = 0; i < table_templates.length; ++i) {
			this.writeComponent(table_templates[i]);
		}
	}

	public void writeComponent(String templateName) throws Exception {
		try {
			System.out.println("Generating template " + templateName);
			Velocity.getTemplate((String) templateName);
		} catch (ResourceNotFoundException rnfe) {
			System.err.println("Aborted writing component:" + templateName
					+ (this.table != null
							? new StringBuffer().append(" for table:").append(this.table.getName()).toString()
							: "")
					+ " because Velocity could not find the resource.");
			return;
		} catch (ParseErrorException pee) {
			System.err.println("Aborted writing component:" + templateName
					+ (this.table != null
							? new StringBuffer().append(" for table:").append(this.table.getName()).toString()
							: "")
					+ " because there was a parse error in the resource.\n" + pee.getLocalizedMessage());
			return;
		} catch (Exception e) {
			System.err.println("Aborted writing component:" + templateName
					+ (this.table != null
							? new StringBuffer().append(" for table:").append(this.table.getName()).toString()
							: "")
					+ " there was an error initializing the template.\n" + e.getLocalizedMessage());
			return;
		}
		StringWriter sw = new StringWriter();
		// reset
		save_current_file = true;
		Velocity.mergeTemplate((String) templateName, (String) "UTF-8", (Context) this.current_vc, (Writer) sw);
		if(save_current_file){
			System.out.println(" .... writing to " + this.current_fullfilename);
			File file = new File(this.current_fullfilename);
			new File(file.getParent()).mkdirs();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.current_fullfilename),"UTF-8"));
			writer.write(sw.toString());
			writer.flush();
			writer.close();
			System.out.println("    " + this.current_filename + " done.");
		}else
			System.out.println("    " + this.current_filename + " skip.");
	}

	public void setCurrentFilename(String relpath_or_package, String fn) throws Exception {
		this.current_filename = relpath_or_package.replace('.', File.separatorChar) + File.separatorChar + fn;
		this.current_fullfilename = destDir + File.separatorChar + relpath_or_package.replace('.', File.separatorChar)
				+ File.separatorChar + fn;
		UserCodeParser uc = new UserCodeParser(this.current_fullfilename);
		this.current_vc.put("userCode", (Object) uc);
	}

	public void setCurrentJavaFilename(String relpath_or_package, String fn) throws Exception {
		this.setCurrentFilename("java" + File.separatorChar + relpath_or_package, fn);
	}

	public void log(String logStr) {
		System.out.println("        " + logStr);
	}

	public static String getClassPrefix() {
		return classPrefix;
	}

	public Database getDb() {
		return db;
	}

	public List<Table> getTables() {
		Table[] tabs = db.getTables();
		ArrayList<Table> tables = new ArrayList<Table>(tabs.length);
		for (int i = 0; i < tabs.length; ++i) {
			tables.add(tabs[i]);
		}
		return tables;
	}

	public Table getTable(String tableName) {
		return db.getTable(tableName);
	}

	public List<Table> getRelationTables() {
		Table[] tabs = db.getTables();
		ArrayList<Table> tables = new ArrayList<Table>(tabs.length);
		for (int i = 0; i < tabs.length; ++i) {
			if (!tabs[i].isRelationTable())
				continue;
			tables.add(tabs[i]);
		}
		return tables;
	}

	public String tableName() {
		if (this.table == null) {
			return "";
		}
		return this.table.getName();
	}

	public Table getTable() {
		return this.table;
	}

	public static String getProperty(String key) {
		String s = props.getProperty(key);
		return s != null ? s.trim() : s;
	}

	public static String getProperty(String key, String default_val) {
		String s = props.getProperty(key, default_val);
		return s != null ? s.trim() : s;
	}

	public static String[] getPropertyExploded(String key) {
		return CodeWriter.getPropertyExploded(key, "");
	}

	public static List<String> getPropertyExplodedAsList(String key) {
		return CodeWriter.getPropertyExplodedAsList(key, "");
	}
	
	public static List<String> getPropertyExplodedAsList(String mkey, String defaultValue) {
		String v = CodeWriter.getProperty(mkey);
		if (v == null) {
			v = defaultValue;
		}
		return CodeWriter.getExplodedStringAsList(v);
	}
	
	public static String[] getPropertyExploded(String mkey, String defaultValue) {
		return getPropertyExplodedAsList(mkey,defaultValue).toArray(new String[0]);
	}
	
	public static List<String> getExplodedStringAsList(String value) {
		ArrayList<String> al = new ArrayList<String>();
		if (value == null) {
			return al;
		}
		StringTokenizer st = new StringTokenizer(value, " ,;\t");
		while (st.hasMoreTokens()) {
			al.add(st.nextToken().trim());
		}
		return al;
	}
	
	public static String[] getExplodedString(String value) {
		return getExplodedStringAsList(value).toArray(new String[0]);
	}
	
	public static boolean getPropertyBoolean(String value){
		try{
			Pattern p = Pattern.compile("yes|true|on",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(getProperty(value));
			return m.matches();
		}catch(Exception e){
			return false;
		}
	}
	
	public String getLoadingPath() {
		String ret = "";
		String[] paths = CodeWriter.getPropertyExploded("velocity.templates.loadingpath", ".");
		for (int i = 0; i < paths.length; ++i) {
			ret = ret + paths[i] + ",";
		}
		System.out.println("getLoadingPath = " + ret);
		return ret;
	}

	public String[] getSchemaTemplates(String property) {
		return this.getTemplates(property, true);
	}

	public String[] getTableTemplates(String property) {
		return this.getTemplates(property, false);
	}

	public String[] getTemplates(String property, boolean perShema) {
		Vector<String> files = new Vector<String>();
		this.recurseTemplate(files, Main.getProperty((String) property), perShema);
		return files.toArray(new String[files.size()]);
	}

	public Vector<String> recurseTemplate(Vector<String> files, String folder, boolean perSchema) {
		FileFilter filter;
		File[] dirEntries;
		String schemaOrTable = "perschema";
		if (!perSchema) {
			schemaOrTable = "pertable";
		}
		if ((dirEntries = new File(folder).listFiles(filter = new FileFilter() {

			public boolean accept(File filename) {
				if (filename.isDirectory()) {
					return true;
				}
				if (!filename.getName().endsWith(".vm")) {
					return false;
				}
				return CodeWriter.authorizeProcess(filename.getName(), "template.file.include",
						"template.file.exclude");
			}
		})) == null) {
			return files;
		}
		for (int i = 0; i < dirEntries.length; ++i) {
			if (dirEntries[i].isFile()) {
				if (!CodeWriter.authorizeFile(folder, schemaOrTable))
					continue;
				files.add(folder + "/" + dirEntries[i].getName());
				continue;
			}
			if (!dirEntries[i].isDirectory())
				continue;
			this.recurseTemplate(files, folder + "/" + dirEntries[i].getName(), perSchema);
		}
		return files;
	}

	public static boolean authorizeProcess(String autorizePattern, String includeProperty, String excludeProperty) {
		boolean accept = true;
		String[] include = CodeWriter.getPropertyExploded(includeProperty);
		String[] exclude = CodeWriter.getPropertyExploded(excludeProperty);
		if (include.length != 0) {
			if (Main.isInArray((String[]) include, (String) autorizePattern)) {
				System.out.println("Processing " + autorizePattern + " (specified in " + includeProperty + ")");
				return true;
			}
			accept = false;
		}
		if (exclude.length != 0 && Main.isInArray((String[]) exclude, (String) autorizePattern)) {
			System.out.println("Skipping " + autorizePattern + " (specified in " + excludeProperty + ")");
			return false;
		}
		return accept;
	}

	public static boolean folderContainsPattern(String folder, String[] patterns) {
		if (patterns == null || folder == null) {
			return false;
		}
		for (int i = 0; i < patterns.length; ++i) {
			String pattern = "/" + patterns[i].toLowerCase() + "/";
			if (folder.toLowerCase().indexOf(pattern) == -1)
				continue;
			return true;
		}
		return false;
	}

	public static boolean authorizeFile(String folder, String schemaOrTable) {
		if (folder.toLowerCase().indexOf(schemaOrTable.toLowerCase()) == -1) {
			return false;
		}
		String[] include = CodeWriter.getPropertyExploded("template.folder.include");
		String[] exclude = CodeWriter.getPropertyExploded("template.folder.exclude");
		if (include.length != 0) {
			if (CodeWriter.folderContainsPattern(folder, include)) {
				return true;
			}
			return false;
		}
		if (exclude.length != 0) {
			if (CodeWriter.folderContainsPattern(folder, exclude)) {
				return false;
			}
			return true;
		}
		return true;
	}

	static {
		MGR_CLASS = "Manager";
		classPrefix = "";
	}

	public static String getBinaryClassName() {
		return binaryClassName;
	}
	public static boolean binaryIsByteBuffer() {
		return !DEFAULT_BINARY_TYPE.equals(binaryClassName);
	}
	public static boolean isGeneral(){
		return null != generalPackage && !generalPackage.isEmpty();
	}
	public static String getSchemaPkg(){
		return isGeneral()?generalPackage : basePackage;
	}
	public  String getImplPackageOfSchema(){
		String pkg = getSchemaPkg();
		if(isGeneral())
			pkg += "." + "" + getDb().engineAsSubPackage();
		return pkg;
	}

	public void setSaveCurrentFile(boolean save_current_fullfile) {
		this.save_current_file = save_current_fullfile;
	}
}
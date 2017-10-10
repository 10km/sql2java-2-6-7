/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import net.sourceforge.sql2java.CodeWriter;
import net.sourceforge.sql2java.Column;
import net.sourceforge.sql2java.ConfigHelper;
import net.sourceforge.sql2java.Database;
import net.sourceforge.sql2java.Index;
import net.sourceforge.sql2java.Procedure;
import net.sourceforge.sql2java.StringUtilities;

public class Table {
	private Hashtable<String,Column> colHash = new Hashtable<String,Column>();
	private Vector<Column> cols = new Vector<Column>();
	private Hashtable<String,Index> indHash = new Hashtable<String,Index>();
	private Vector<Index> indices = new Vector<Index>();
	private Hashtable<String,Index> indUniqueHash = new Hashtable<String,Index>();
	private Vector<Index> uniqueIndices = new Vector<Index>();
	private Hashtable<String,Index> indNonUniHash = new Hashtable<String,Index>();
	private Vector<Index> nonUniqueIndices = new Vector<Index>();
	private Vector<Column> priKey = new Vector<Column>();
	private Column autoincrement;
	private String catalog;
	private String schema;
	private String name;
	private String type;
	private String remarks;
	private Database database;
	private Vector<Column> foreignKeys = new Vector<Column>();
	private Vector<Column> importedKeys = new Vector<Column>();
	/* FK_NAME 为索引保存所有 foreign keys */
	private Map<String,Vector<Column>> fkNameMap = new HashMap<String,Vector<Column>>();
	private List<Procedure> procedures = new ArrayList<Procedure>();
	private HashMap<String,Procedure> procHash = new HashMap<String,Procedure>();
	private Random aleatorio = new Random(new Date().getTime());

	public boolean isRelationTable() {
		if ("false".equalsIgnoreCase(this.getTableProperty("nntable"))) {
			return false;
		}
		return this.foreignKeys.size() == 2;
	}

	public boolean relationConnectsTo(Table otherTable) {
		if (this.equals(otherTable)) {
			return false;
		}
		int nbImported = this.importedKeys.size();
		for (int i = 0; i < nbImported; ++i) {
			Column c = (Column) this.importedKeys.get(i);
			if (!c.getTableName().equals(otherTable.getName()))
				continue;
			return true;
		}
		return false;
	}

	public Table[] linkedTables(Database pDatabase, Table pTable) {
		Vector<Table> vector = new Vector<Table>();
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Table pTableToAdd;
			Column pColumn = (Column) this.importedKeys.get(iIndex);
			if (pColumn.getTableName().equals(pTable.getName())
					|| vector.contains(pTableToAdd = pDatabase.getTable(pColumn.getTableName())))
				continue;
			vector.add(pTableToAdd);
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public Column getForeignKeyFor(Table pTable) {
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Column pColumn = (Column) this.importedKeys.get(iIndex);
			if (!pColumn.getTableName().equals(pTable.getName()))
				continue;
			return pColumn;
		}
		return null;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRemarks(String remarks) {
		if (remarks != null) {
			this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
		}
	}

	public String getCatalog() {
		return this.catalog;
	}

	public String getSchema() {
		return this.schema;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public Column[] getColumns() {
		Collections.sort(this.cols);
		return this.cols.toArray(new Column[this.cols.size()]);
	}
	public Column[] getColumnsExceptPrimary() {
		if(!this.hasPrimaryKey())
			return getColumns();
		Vector<Column> columns = new Vector<Column>(this.cols);
		for(Iterator<Column> itor = columns.iterator();itor.hasNext();){
			Column column = itor.next();
			if(column.isPrimaryKey())itor.remove();
		}
		Collections.sort(columns);
		return columns.toArray(new Column[columns.size()]);
	}
	public Column getColumn(String columnName) {
		return (Column) this.colHash.get(columnName.toLowerCase());
	}

	public void addColumn(Column column) {
		this.colHash.put(column.getName().toLowerCase(), column);
		this.cols.addElement(column);
	}

	public void removeColumn(Column column) {
		this.cols.removeElement((Object) column);
		this.colHash.remove(column.getName().toLowerCase());
	}

	public Index[] getUniqueIndices() {
		return this.uniqueIndices.toArray(new Index[this.uniqueIndices.size()]);
	}

	public Index[] getNonUniqueIndices() {
		return this.nonUniqueIndices.toArray( new Index[this.nonUniqueIndices.size()]);
	}

	public int countIndices() {
		return this.indices.size();
	}

	public Index[] getIndices() {
		return this.indices.toArray( new Index[this.indices.size()]);
	}

	public Index getIndex(String indName) {
		return (Index) this.indHash.get(indName.toLowerCase());
	}

	public void addIndex(Index index) {
		this.indHash.put(index.getName().toLowerCase(), index);
		this.indices.add(index);
		if (index.isUnique()) {
			this.indUniqueHash.put(index.getName().toLowerCase(), index);
			this.uniqueIndices.add(index);
		} else {
			this.indNonUniHash.put(index.getName().toLowerCase(), index);
			this.nonUniqueIndices.add(index);
		}
	}

	public void removeIndex(Index index) {
		this.indices.remove((Object) index);
		this.indHash.remove(index.getName().toLowerCase());
		if (index.isUnique()) {
			this.uniqueIndices.remove((Object) index);
			this.indUniqueHash.remove(index.getName().toLowerCase());
		} else {
			this.nonUniqueIndices.remove((Object) index);
			this.indNonUniHash.remove(index.getName().toLowerCase());
		}
	}

	public Column[] getPrimaryKeys() {
		return this.priKey.toArray( new Column[this.priKey.size()]);
	}

	public boolean hasCompositeKey() {
		if (this.priKey.size() > 1) {
			return true;
		}
		return false;
	}

	public Column getPrimaryKey() throws RuntimeException {
		if (this.priKey.size() != 1) {
			throw new RuntimeException("Table " + this.getName() + " has a composite key, not a unique primary key");
		}
		return (Column) this.priKey.get(0);
	}

	public void addPrimaryKey(Column column) {
		this.priKey.addElement(column);
		column.isPrimaryKey(true);
	}

	public Column[] getImportedKeys() {
		return this.importedKeys.toArray( new Column[this.importedKeys.size()]);
	}

	public void addImportedKey(Column column) {
		if (!this.importedKeys.contains((Object) column)) {
			this.importedKeys.addElement(column);
		}
	}

	public int countColumns() {
		return this.cols.size();
	}

	public int countPrimaryKeys() {
		return this.priKey.size();
	}

	public boolean hasPrimaryKey() {
		return this.countPrimaryKeys() > 0;
	}

	public int countImportedKeys() {
		return this.importedKeys.size();
	}

	public boolean hasImportedKeys() {
		return this.countImportedKeys() > 0;
	}

	public int countForeignKeys() {
		return this.foreignKeys.size();
	}

	public boolean hasForeignKeys() {
		return this.countForeignKeys() > 0;
	}

	public void addForeignKey(Column col, String fkName, short keySeq) {
		if (!this.foreignKeys.contains((Object) col)) {
			this.foreignKeys.add(col);
		}
		if(null!=fkName&&!fkName.isEmpty()){
			if(keySeq<=0)
				throw new IllegalArgumentException("the argument 'keySeq' must >0");
			if(null==this.fkNameMap.get(fkName)){
				this.fkNameMap.put(fkName, new Vector<Column>());
			}
			Vector<Column> fkCols = fkNameMap.get(fkName);
			if(keySeq > fkCols.size()){
				fkCols.setSize(keySeq);
			}
			fkCols.set(keySeq-1, col);
		}
	}
	
	/**
	 * 返回所有 foreign key name ( FK_NAME )
	 * @return
	 */
	public Vector<String> getFkMapNames() {		
		Vector<String> res=new Vector<String>(this.fkNameMap.keySet());
		Collections.sort(res);
		return res;
	}

	/**
	 * 检索外键引用指定表(tableName)的所有 FK_NAME<br>
	 * 没有结果则返回空数组
	 * @param tableName
	 * @return
	 */
	public Vector<String> getFkMapNames(String tableName) {
		Vector<String> names=new Vector<String>();
		// System.out.printf("getFkMapNames of %s for %s\n", this.getName(),tableName);
		for(Entry<String, Vector<Column>> entry:this.fkNameMap.entrySet()){
			for(Column col:entry.getValue().get(0).getForeignKeys()){
				if(col.getTableName().equals(tableName)){
					// System.out.printf("    %s\n",entry.getKey());
					names.add(entry.getKey());
					break;
				}
			}
		}
		Collections.sort(names);
		
		return names;
	}

	/**
	 * 检索指定 FK_NAME 包含的所有字段<br>
	 * 没有结果则返回空数组
	 * @param fkName
	 * @return
	 */
	public Vector<Column> getForeignKeysByFkName(String fkName) {		
		Vector<Column> keys=this.fkNameMap.get(fkName);
		return null==keys?new Vector<Column>():new Vector<Column>(keys);
	}
	
	/**
	 * 判断 FK_NAME 包含的所有字段是否都允许为null
	 * @param fkName
	 * @return
	 */
	public boolean isNullable(String fkName){
		for(Column column: getForeignKeysByFkName(fkName)){
			if(column.isNotNull())return false;
		}
		return true;
	}
	/**
	 * 返回 FK_NAME 包含的所有字段中不允许为null的所有字段
	 * @param fkName
	 * @return
	 */
	public Vector<Column> noNullableColumns(String fkName){
		Vector<Column> keys = getForeignKeysByFkName(fkName);
		for(Iterator<Column> itor = keys.iterator();itor.hasNext();){
			Column column = itor.next();
			if(Column.columnNullable !=column.getNullable())continue;
			itor.remove();
		}
		return keys;
	}
	private String toUniversalFkName(String fkName) {
		Vector<Column> keys = this.fkNameMap.get(fkName);
		if(null!=keys){
			Vector<String> names=new Vector<String>();
			for(Column k:keys)names.add(k.getName());
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<keys.size();++i){
				if(i>0)
					sb.append("_");
				sb.append(keys.get(i).getName());
			}
			return sb.toString();
		}
		return "";
	}
	
	public Table getForeignTableByFkName(String fkName){
		Vector<Column> keys = this.getForeignKeysByFkName(fkName);
		return 0==keys.size()? null:keys.get(0).getForeignColumn().getTable();		
	}
	public Column[] getForeignKeys() {
		return this.foreignKeys.toArray( new Column[this.foreignKeys.size()]);
	}

	public boolean isForeignKey(Column col) {
		return this.foreignKeys.contains((Object) col);
	}

	public int countManyToManyTables() {
		return this.getManyToManyTables().length;
	}

	public boolean hasManyToManyTables() {
		return this.countManyToManyTables() > 0;
	}

	public Table[] getManyToManyTables() {
		Vector<Table> vector = new Vector<Table>();
		Table[] linkedTables = this.getImportedTables();
		System.out.println(this.getName() + "  getManyToManyTables, linked tables = " + linkedTables.length);
		for (int iIndex = 0; iIndex < linkedTables.length; ++iIndex) {
			System.out.println(this.getName() + "    " + linkedTables[iIndex].getName() + " relation table ?");
			if (!linkedTables[iIndex].isRelationTable())
				continue;
			Table[] relationLinkedTable = linkedTables[iIndex].getForeignTables();
			System.out.println(this.getName() + "      " + linkedTables[iIndex].getName() + " has "
					+ relationLinkedTable.length + " foreign table");
			for (int i = 0; i < relationLinkedTable.length; ++i) {
				System.out.println(this.getName() + "          " + i + " " + relationLinkedTable[i].getName()
						+ " is relation table");
				if (relationLinkedTable[i].equals(this) || vector.contains(relationLinkedTable[i]))
					continue;
				vector.add(relationLinkedTable[i]);
			}
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public int countLinkedTables() {
		return this.getLinkedTables().length;
	}

	public boolean hasLinkedTables() {
		return this.countLinkedTables() > 0;
	}

	public Table[] getLinkedTables() {
		Vector<Table> vector = new Vector<Table>();
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Table pTableToAdd;
			Column column = (Column) this.importedKeys.get(iIndex);
			if (column.getTableName().equals(this.getName()) || vector.contains(pTableToAdd = column.getTable()))
				continue;
			vector.add(pTableToAdd);
		}
		int nbForeign = this.foreignKeys.size();
		for (int iIndex2 = 0; iIndex2 < nbForeign; ++iIndex2) {
			Table pTableToAdd;
			Column column = (Column) this.foreignKeys.get(iIndex2);
			if ((column = column.getForeignColumn()).getTableName().equals(this.getName())
					|| vector.contains(pTableToAdd = column.getTable()))
				continue;
			vector.add(pTableToAdd);
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public int countImportedTables() {
		return this.getImportedTables().length;
	}

	public boolean hasImportedTables() {
		return this.countImportedTables() > 0;
	}

	public Table[] getImportedTables() {
		Vector<Table> vector = new Vector<Table>();
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Table pTableToAdd;
			Column column = (Column) this.importedKeys.get(iIndex);
			if (column.getTableName().equals(this.getName()) || vector.contains(pTableToAdd = column.getTable()))
				continue;
			vector.add(pTableToAdd);
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public int countForeignTables() {
		return this.getForeignTables().length;
	}

	public boolean hasForeignTables() {
		return this.countForeignTables() > 0;
	}

	public Table[] getForeignTables() {
		Vector<Table> vector = new Vector<Table>();
		int nbForeign = this.foreignKeys.size();
		for (int iIndex = 0; iIndex < nbForeign; ++iIndex) {
			Table pTableToAdd;
			Column column = ((Column) this.foreignKeys.get(iIndex)).getForeignColumn();
			if (column.getTableName().equals(this.getName()) || vector.contains(pTableToAdd = column.getTable()))
				continue;
			vector.add(pTableToAdd);
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public Table getRelationTable(Table targetTable) {
		System.out.println("getRelationTable " + this.getName() + "<->" + targetTable.getName() + ")");
		Table[] importedTables = this.getImportedTables();
		for (int iIndex = 0; iIndex < importedTables.length; ++iIndex) {
			Table[] foreignTables = importedTables[iIndex].getForeignTables();
			for (int iIndex2 = 0; iIndex2 < foreignTables.length; ++iIndex2) {
				if (!foreignTables[iIndex2].getName().equalsIgnoreCase(this.getName()))
					continue;
				return importedTables[iIndex];
			}
		}
		return targetTable;
	}

	public int countProcedures() {
		return this.procedures.size();
	}

	public boolean hasProcedures() {
		return this.countProcedures() > 0;
	}

	public Procedure[] getProcedures() {
		return this.procedures.toArray( new Procedure[this.procedures.size()]);
	}

	public void addProcedure(Procedure procedure) {
		if (null == this.procHash.get(procedure.getName())) {
			this.procedures.add(procedure);
			this.procHash.put(procedure.getName(), procedure);
		}
	}

	public String[] getLinkedPackages() {
		Vector<String> vector = new Vector<String>();
		Table[] linkedTables = this.getLinkedTables();
		for (int iIndex = 0; iIndex < linkedTables.length; ++iIndex) {
			if (vector.contains(linkedTables[iIndex].getPackage()))
				continue;
			vector.add(linkedTables[iIndex].getPackage());
		}
		return vector.toArray(new String[vector.size()]);
	}

	public String getPackage() {
		String basePackage = CodeWriter.getProperty((String) "codewriter.package");
		String xmlSubpackage = this.getTableProperty("subpackage");
		if (null != xmlSubpackage) {
			return basePackage + "." + xmlSubpackage;
		}
		int iterating = 1;
		do {
			String tablesProperty = "subpackage." + iterating + ".tables";
			String packageNameProperty = "subpackage." + iterating + ".name";
			String[] tables = CodeWriter.getPropertyExploded((String) tablesProperty);
			if (tables.length == 0)
				break;
			for (int i = 0; i < tables.length; ++i) {
				if (!this.getName().equalsIgnoreCase(tables[i]))
					continue;
				String packageName = CodeWriter.getProperty((String) packageNameProperty);
				if (packageName == null) {
					return basePackage;
				}
				return basePackage + "." + packageName;
			}
			++iterating;
		} while (true);
		return basePackage;
	}
	public String getPackage(boolean isBean){
		String generalPackage = CodeWriter.getProperty((String) "general.package");
		if(null == generalPackage || generalPackage.isEmpty())
			return getPackage();
		return isBean
				? generalPackage
				: generalPackage + "." + this.getDatabase().engineAsSubPackage();
	}
	public String getPackagePath() {
		return this.getPackage().replace('.', '/') + "/";
	}

	public Column[] getColumnsFor(String webElement) {
		Vector<Column> vector = new Vector<Column>();
		int nbCols = this.cols.size();
		for (int i = 0; i < nbCols; ++i) {
			Column c = (Column) this.cols.get(i);
			if (!c.columnFor(webElement))
				continue;
			vector.add(c);
		}
		return vector.toArray( new Column[vector.size()]);
	}

	public Column getFirstColumn() {
		return (Column) this.cols.get(0);
	}

	public String getTableProperty(String property) {
		return ConfigHelper.getTableProperty((String) this.name, (String) property);
	}

	public String getRemarks() {
		String xmlDefaultValue = this.getTableProperty("description");
		if (xmlDefaultValue != null && !"".equals(xmlDefaultValue)) {
			return xmlDefaultValue;
		}
		return this.remarks == null ? "" : this.remarks;
	}

	public boolean hasRemarks(){
		return !getRemarks().isEmpty();
	}
	public String getJavaName() {
		return this.convertName("");
	}
	
	private String getBasename(boolean nsp){
		String basename =nsp
				? this.getName().replaceFirst(this.getDatabase().getSamePrefix(), "")
				: this.getName();
		return  "".equals(CodeWriter.getClassPrefix())
				? basename
				: CodeWriter.getClassPrefix() + "_" + basename;
	}
	
	public String convertName(String value,boolean nsp) {
		String basename = getBasename(nsp);
		if ("".equals(value)) {
			return StringUtilities.convertName((String) basename, false);
		}
		return StringUtilities.convertName((String) (basename + "_" + value), false);
	}
	
	public String convertName(String value) {
		return convertName(value,false);
	}
	
	public String convertNameNSP(String value) {
		return convertName(value,true);
	}
	
	public String asClass(String suffix) {
		return this.convertName(suffix);
	}

	public String asCoreClass() {
		return this.convertName("");
	}

	public String asCoreClassNSP() {
		return this.convertNameNSP("");
	}
	
	public String asBeanClass() {
		return this.convertName("Bean");
	}
	
	public String asFullBeanClass() {
		return this.getPackage() + "." + this.asBeanClass();
	}
	
	public String asBeanClassNSP() {
		return this.convertNameNSP("Bean");	
	}
	
	public String asBeanClass(boolean nsp) {
		return convertName("Bean",nsp);
	}
	
	public String asCacheClass() {
		return this.convertName("Cache");
	}
	public String asCacheClass(boolean nsp) {
		return this.convertName("Cache",nsp);
	}
	public String asRelationnalBeanClass() {
		return this.convertName("Relationnal_Bean");
	}

	public String asHibernateManagerClass() {
		return this.convertName("Hibernate_Manager");
	}

	public String asIteratorClass() {
		return this.convertName("Iterator");
	}

	public String asFactoryClass() {
		return this.convertName("Factory");
	}

	public String asHttpFactoryClass() {
		return this.convertName("Http_Factory");
	}

	public String asComparatorClass() {
		return this.convertName("Comparator");
	}

	public String asListenerClass() {
		return this.convertName("Listener");
	}

	public String asListenerClassNSP() {
		return this.convertNameNSP("Listener");
	}
	
	public String asRendererClass() {
		return this.convertName("Renderer");
	}

	public String asExceptionClass() {
		return this.convertName("Exception");
	}

	public String asWidgetClass() {
		return this.convertName("Widget");
	}

	public String asWidgetFactoryClass() {
		return this.convertName("Widget_Factory");
	}

	public String asActionClass() {
		return this.convertName("Action");
	}

	public String asActionTestClass() {
		return this.convertName("Action_Test");
	}

	public String asControllerClass() {
		return this.convertName("Controller");
	}

	public String asControllerTestClass() {
		return this.convertName("Controller_Test");
	}

	public String asFormControllerClass() {
		return this.convertName("Form_Controller");
	}

	public String asFormControllerTestClass() {
		return this.convertName("Form_Controller_Test");
	}

	public String asDAOClass() {
		return this.convertName("D_A_O");
	}

	public String asDAOTestClass() {
		return this.convertName("D_A_O_Test");
	}

	public String asDAOHibernateClass() {
		return this.convertName("D_A_O_Hibernate");
	}

	public String asManagerClass() {
		return this.convertName("Manager");
	}
	public String asManagerClass(boolean nsp) {
		return this.convertName("Manager",nsp);
	}
	public String asManagerClassNSP() {
		return this.convertNameNSP("Manager");
	}
	public String asManagerInterfaceNSP() {
		return "I"+asManagerClassNSP();
	}
	public String asManagerImplClass() {
		return this.convertName("Manager_Impl");
	}

	public String asManagerTestClass() {
		return this.convertName("Manager_Test");
	}
	public String asCacheManagerClass() {
		return this.convertName("cache_manager");
	}
	public String asCacheManagerClassNSP() {
		return this.convertNameNSP("cache_manager");
	}
	public String asCacheManagerClass(boolean nsp) {
		return this.convertName("cache_manager",nsp);
	}
	public String asCacheVarName(){
		return StringUtilities.convertName( getBasename(true) +  "_cache",true);
	}
	public String asCacheVarSetMethod(){
		return StringUtilities.convertName("set_" +  getBasename(true) +  "_cache",true);
	}
	public String asCacheVarGetMethod(){
		return StringUtilities.convertName("get_" +  getBasename(true) +  "_cache",true);
	}
	public String asModelClass() {
		return this.convertName("Model");
	}

	public String asPKClass() {
		return this.convertName("P_K");
	}

	public String asTblClass() {
		return this.convertName("Tbl");
	}

	public Column getVersionColumn() {
		int nbCols = this.cols.size();
		for (int i = 0; i < nbCols; ++i) {
			Column c = (Column) this.cols.get(i);
			if (!c.isVersion())
				continue;
			return c;
		}
		throw new IllegalArgumentException("No version column for table " + this.getName());
	}

	public boolean hasVersionColumn() {
		try {
			this.getVersionColumn();
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public long getSerialVersionUID() {
		return this.aleatorio.nextLong();
	}
	/**
	 * 生成MD5校验码
	 * 
	 * @param source
	 * @return
	 */
	static public byte[] getMD5(byte[] source) {
		if (null==source)
			return null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(source);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 将字节数组转为long<br>
	 * 如果input为null,或offset指定的剩余数组长度不足8字节则抛出异常
	 * @param input 
	 * @param offset 起始偏移量
	 * @param littleEndian 输入数组是否小端模式
	 * @return
	 */
	public static long longFrom8Bytes(byte[] input, int offset, boolean littleEndian){
		if(offset <0 || offset+8>input.length)
			throw new IllegalArgumentException(String.format("less than 8 bytes from index %d  is insufficient for long",offset));
		long value=0;
		for(int  count=0;count<8;++count){
			int shift=(littleEndian?count:(7-count))<<3;
			value |=((long)0xff<< shift) & ((long)input[offset+count] << shift);
		}
		return value;
	}
	/**
	 * 根据输入的String返回唯一的UID(long)
	 * @param input
	 * @return
	 */
	public long getSerialVersionUID(String input){
		byte[] md5 = getMD5(input.getBytes());
		return longFrom8Bytes(md5,0, false)  ^ longFrom8Bytes(md5,8, false);
	}	

	public String asFKConst(String fkName){
		return (this.name+"_FK_" + toUniversalFkName(fkName)).toUpperCase();
	}

	public String asIKConst(String fkName){
		Table foreignTable = this.getForeignTableByFkName(fkName);
		return (null==foreignTable?"":foreignTable.getName() + "_IK_" + this.name +"_" + toUniversalFkName(fkName)).toUpperCase();
	}
	
	public String asRefArg(String fkName){
		return StringUtilities.convertName("ref_"+ this.getForeignTableByFkName(fkName).asCoreClassNSP() +"_by_" + toUniversalFkName(fkName),true);
	}
	
	public String asImpArg(String fkName){
		return StringUtilities.convertName("imp_"+ this.asCoreClassNSP() +"_by_" + toUniversalFkName(fkName),true);
	}

	public String getReferencedVarName(String fkName){
		return StringUtilities.convertName("referenced_by_" + toUniversalFkName(fkName),true);
	}
	
	public String getReferencedVarGetMethod(String fkName) {
		return StringUtilities.convertName("get_" + "referenced_by_" + toUniversalFkName(fkName),true);
	}
	
	public String getReferencedVarSetMethod(String fkName){
		return StringUtilities.convertName("set_" + "referenced_by_" + toUniversalFkName(fkName),true);
	}
	
	public String getImportedBeansGetMethod(String fkName) {		
		return "get" + this.asBeanClassNSP() + "s" + StringUtilities.convertName("By_" + toUniversalFkName(fkName),false);
	}
	public String getImportedBeansSetMethod(String fkName) {		
		return "set" + this.asBeanClassNSP() + "s" + StringUtilities.convertName("By_" + toUniversalFkName(fkName),false);
	}
	public String getImportedBeansDelMethod(String fkName) {		
		return "delete" + this.asBeanClassNSP() + "s" + StringUtilities.convertName("By_" + toUniversalFkName(fkName),false);
	}
	public String stateVarType(){
		return countColumns()>64 ? "long[]":"long";
	}	
	
	public String stateVarInitializedStatement(){
		if(countColumns()>64){
			int len = (countColumns()+63)>>6;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<len;++i){
				if(i>0)sb.append(",");
				sb.append("0L");				
			}
			return String.format("new long[]{%s}",sb.toString());
		}else{
			return "0L";
		}
	}
	public String stateVarAssignStatement(String src,String dst){
		if(countColumns()>64){
	        return "if( null != ${src} && ${dst}.length != ${src}.length )System.arraycopy(${src},0,${dst},0,${dst}.length)"
	        		.replace("${src}", src).replace("${dst}", dst);
		}else{
			return dst + " = " + src;
		}
	}
	protected Database getDatabase() {
		return database;
	}

	protected void setDatabase(Database database) {
		this.database = database;
	}

	public Column getAutoincrement() {
		return autoincrement;
	}

	public void setAutoincrement(Column autoincrement) {
		this.autoincrement = autoincrement;
	}
   
	public int countForeignKeyNames(){
		return this.getFkMapNames().size();
	}
	
	public int countImportedKeyNames(){
		int count = 0;
		for(Table table:this.getImportedTables()){
			count += table.getFkMapNames(this.getName()).size();
		}
		return count;
	}
	public String bitResetAssignExpression(Column[]columns,String varName,String indent){
		if(null == columns || 0 == columns.length)return "// columns is null or empty";
		if(null == indent)indent = "";
		if(countColumns()>64){
			StringBuffer buffer = new StringBuffer();
			for(Column column : columns){
				int pos = column.getOrdinalPosition()-1;
				buffer.append(indent).append(String.format("%s[%d] &= (~(1L << %d));\n",varName,pos>>6,pos & 0x3f));				
			}
			return buffer.toString();
		}else{
			StringBuffer buffer = new StringBuffer("(");
			for(int i=0;i<columns.length;++i){
				if(i > 0)
					buffer.append(" |\n").append(indent);
				buffer.append(columns[i].getIDMaskConstName());
			}
			buffer.append(")");
			return String.format("%s &= (~%s)", varName,buffer.toString());
		}
	}
}
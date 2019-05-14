/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

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
	/** FK_NAME 为索引保存所有 foreign keys */
	private LinkedHashMap<String, ForeignKey> fkNameMap = new LinkedHashMap<String,ForeignKey>();
	private List<Procedure> procedures = new ArrayList<Procedure>();
	private HashMap<String,Procedure> procHash = new HashMap<String,Procedure>();
	private Random aleatorio = new Random(new Date().getTime());

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("catalog",catalog)
				.append("schema", schema)
				.append("name", name)
				.append("remarks", remarks)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(catalog)
				.append(indices)
				.append(name)
				.append(schema)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj))return true;
		if(!(obj instanceof Table))return false;
		Table other = (Table)obj;
		return new EqualsBuilder()
			.append(catalog, other.catalog)
			.append(indices, other.indices)
			.append(name, other.name)
			.append(schema, other.schema)
			.append(type, other.type)
			.isEquals();
	}

	public boolean isRelationTable() {
		if ("false".equalsIgnoreCase(this.getTableProperty("nntable"))) {
			return false;
		}
		return this.foreignKeys.size() == 2;
	}
	
	/**
	 * 代替{@link #isRelationTable()},判断当前表是否为联接表
	 * @return
	 */
	public boolean isJunctionTable() {
		if(fkNameMap.size() ==2){
			HashSet<Column> fkColumns = Sets.<Column>newHashSet();
			for(ForeignKey fks:fkNameMap.values()){
				fkColumns.addAll(fks.columns);
			}
			Vector<Column> pkColumns = getPrimaryKeysAsList();
			if(pkColumns.size() == fkColumns.size()){
				fkColumns.retainAll(getPrimaryKeysAsList());
				return fkColumns.size() ==pkColumns.size();	
			}			
		}			
		return false;
	}
	/**
	 * 判断是否为联接表,且主键为两个字段
	 * @return
	 */
	public boolean isSampleJunctionTable() {
		return isJunctionTable() && 2 == countPrimaryKeys();
	}
	/**
	 * 判断外键是否为自引用
	 * @return
	 */
	public boolean isSelfRef(ForeignKey fk) {
		if(null == fk)return false;
		return fk.getForeignTable().equals(this);
	}
	/** 返回所有自引用外键 */
	public List<ForeignKey> getSelfRefKeys(){
		return ImmutableList.copyOf(Collections2.filter(this.fkNameMap.values(), new Predicate<ForeignKey>(){
			@Override
			public boolean apply(ForeignKey input) {
				return isSelfRef(input);
			}}));
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
	/**
	 * 代替 {@link #linkedTables(Database, Table)}<br>
	 * 对于联接表(junction)返回{@code pTable}联接的表<br>
	 * @param pTable
	 * @return 当前对象不是连接表或{@code pTable}不属于联接表时返回{@code null}
	 */
	public Table tableOfJunction(Table pTable) {
		if(this.isJunctionTable()){			
			for(ForeignKey fk:this.fkNameMap.values()){
				Table jtable = fk.getForeignTable();
				if(!jtable.equals(pTable))
					return jtable; 
			}
		}
		return null;
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

	/** 返回当前对象的关联表 */
	public List<Table> getJunctionTables() {
		List<Table> tabs = this.getDatabase().getJunctionTables();
		if(isJunctionTable()){
			// 当前表是关联表，则返回空
			tabs.clear();
			return tabs;
		}
		for(Iterator<Table> itor = tabs.iterator();itor.hasNext();){
			Table table = itor.next();
			if(!table.getForeignTablesAsList().contains(this)){
				itor.remove();
			}
		}
		return tabs;
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
		return this.getColumnsAsList().toArray(new Column[this.cols.size()]);
	}
	public Vector<Column> getColumnsAsList() {
		Collections.sort(this.cols);
		return this.cols;
	}
	public List<Column> getColumnsExceptPrimaryAsList() {
		return Lists.newArrayList(Collections2.filter(getColumnsAsList(), new Predicate<Column>(){
			@Override
			public boolean apply(Column input) {
				return !input.isPrimaryKey();
			}}));
	}
	public Column[] getColumnsExceptPrimary() {
		return getColumnsExceptPrimaryAsList().toArray(new Column[0]);
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
	public List<Index> getIndicesAsList(Boolean unique) {
		Iterator<Index>itor = this.indices.iterator();
		if(Boolean.TRUE == unique)
			itor = Iterators.filter(itor, new Predicate<Index>(){
				@Override
				public boolean apply(Index arg0) {
					return arg0.isUnique();
				}});
		return ImmutableList.copyOf(itor);
	}
	public List<Index> getIndicesAsList(){
		return getIndicesAsList(false);
	} 
	public List<Index> getUniqueIndicesAsList(){
		return getIndicesAsList(true);
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
	public Vector<Column> getPrimaryKeysAsList() {
		return new Vector<Column>(this.priKey);
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

	public void addForeignKey(Column col, 
			String fkName, 
			short keySeq,
			Table.ForeignKeyRule updateRule,
			Table.ForeignKeyRule deleteRule) {
		checkNotNull(col);
		checkArgument(!Strings.isNullOrEmpty(fkName));
		checkArgument(keySeq>0,"the argument 'keySeq' must >0");

		if (!this.foreignKeys.contains(col)) {
			this.foreignKeys.add(col);
		}
		if(!this.fkNameMap.containsKey(fkName))
			this.fkNameMap.put(fkName, new ForeignKey(fkName,updateRule,deleteRule,col));
		Vector<Column> fkCols = fkNameMap.get(fkName).columns;
		if(keySeq > fkCols.size()){
			fkCols.setSize(keySeq);
		}
		fkCols.set(keySeq-1, col);
	}
	
	/**
	 * 返回所有 foreign key name ( FK_NAME )
	 * @return
	 */
	public Vector<String> getFkMapNames() {		
		return new Vector<String>(this.fkNameMap.keySet());
	}

	/**
	 * 检索外键引用指定表(tableName)的所有 FK_NAME<br>
	 * 没有结果则返回空数组
	 * @param tableName
	 * @return
	 */
	public Vector<String> getFkMapNames(String tableName) {
		Vector<String> names=new Vector<String>();
		for(ForeignKey fk:this.fkNameMap.values()){
			for(Column col:fk.columns.get(0).getForeignKeys()){
				if(col.getTableName().equals(tableName)){
					names.add(fk.fkName);
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
		ForeignKey keys=this.fkNameMap.get(fkName);
		return null==keys?new Vector<Column>():new Vector<Column>(keys.columns);
	}
	
	/**
	 * 检索指定 FK_NAME 的{@link ForeignKey}对象<br>
	 * @param fkName
	 * @return
	 * @see java.util.concurrent.ConcurrentHashMap#get(java.lang.Object)
	 */
	public ForeignKey getForeignKey(String fkName) {
		return fkNameMap.get(fkName);
	}
	
	/**
	 * 返回{@code table}对应的所有{@link ForeignKey}对象
	 * @param table
	 * @return
	 */
	public List<ForeignKey> getForeignKeys(final Table table){		
		return Lists.newArrayList(Collections2.filter(this.fkNameMap.values(), new Predicate<ForeignKey>(){
			@Override
			public boolean apply(ForeignKey input) {
				return input.getForeignTable().equals(table);
			}}));
	}
	public List<ForeignKey> getImportedFoeignKeysAsList(){
		ArrayList<ForeignKey> list = new ArrayList<ForeignKey>();
		for(Table table:getImportedTablesAsList()){
			list.addAll(table.getForeignKeys(this));
		}
		return list;
	}
	public List<ForeignKey> getForeignKeysAsList(){
		return ImmutableList.copyOf(this.fkNameMap.values());
	}
	/**
	 * 返回 所有需要输出foreign key listener的 {@link ForeignKey}对象
	 * @return
	 */
	public List<ForeignKey> getForeignKeysForListener(){
		Collection<ForeignKey> c = Maps.filterEntries(fkNameMap, 
				new Predicate<Entry<String, ForeignKey>>(){
					@Override
					public boolean apply(Entry<String, ForeignKey> input) {
						ForeignKey fk = input.getValue();
						return fk.updateRule.isNoAction() 
								&& !Strings.isNullOrEmpty(fk.deleteRule.getEventOfDeleteRule());
					}}).values();
		// 排序输出
		return Ordering.natural().onResultOf(new Function<ForeignKey,String>(){
			@Override
			public String apply(ForeignKey input) {
				return input.fkName;
			}}).sortedCopy(c);
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
			if(DatabaseMetaData.columnNullable !=column.getNullable())continue;
			itor.remove();
		}
		return keys;
	}
	private String toUniversalFkName(String fkName) {
		ForeignKey key = this.fkNameMap.get(fkName);
		if(null!=key){
			return key.getUniversalName();
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
		for(Table jtable:getJunctionTables()){
			Table pTableToAdd = jtable.tableOfJunction(this);
			if(vector.contains(pTableToAdd)){
				continue;
			}
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

	public List<Table> getImportedTablesAsList() {
		return Lists.newArrayList(Lists.transform(this.importedKeys, new Function<Column,Table>(){
			@Override
			public Table apply(Column t) {
				return t.getTable();
			}}));
	}
	public Table[] getImportedTables() {
		return getImportedTablesAsList().toArray(new Table[0]);
	}
	public int countForeignTables() {
		return this.getForeignTables().length;
	}

	public boolean hasForeignTables() {
		return this.countForeignTables() > 0;
	}

	public List<Table> getForeignTablesAsList() {
		return Lists.newArrayList(Lists.transform(this.foreignKeys, new Function<Column,Table>(){
			@Override
			public Table apply(Column t) {
				return t.getForeignColumn().getTable();
			}}));
	}
	public Table[] getForeignTables() {
		return getForeignTablesAsList().toArray(new Table[0]);
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
	
	public String getBasename(Boolean nsp){
		String basename =Boolean.TRUE == nsp
				? this.getName().replaceFirst(this.getDatabase().getSamePrefix(), "")
				: this.getName();
		return  "".equals(CodeWriter.getClassPrefix())
				? basename
				: CodeWriter.getClassPrefix() + "_" + basename;
	}

	public String convertName(String value,Boolean nsp) {
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
	public String asCoreClass(Boolean nsp) {
		return this.convertName("",nsp);
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
	
	public String asBeanClass(Boolean nsp) {
		return convertName("Bean",nsp);
	}
	public String asConstClass() {
		return this.convertName("Const");
	}
	public String asConstClass(boolean nsp) {
		return this.convertName("Const",nsp);
	}
	public String asConstClassNSP() {
		return asConstClass(true);
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
	public String asManagerClass(Boolean nsp) {
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
	public String asVar(String prefix,String suffix){
		return StringUtilities.convertName( prefix + getBasename(true) +  suffix,true);
	}
	public String asVar(String prefix){
		return asVar(prefix,"");
	}
	public String asVar(){
		return asVar("","");
	}
	public String asVarBean(){
		return asVar("","_Bean");
	}
	public String asVarManager(){
		return asVar("","_Manager");
	}
	public String asConverterVar(){
		return "converter" + asBeanClassNSP() ;
	}
	public String asConverterConstVar(){		
		return  "converter_".concat(asBeanClassNSP()).toUpperCase();
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
	public String asInstanceMethod(Boolean nsp){
		return "instanceOf" + asManagerClass(nsp);
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
	public String asFkVar(String fkName){
		return StringUtilities.convertName(toUniversalFkName(fkName),true);
	}
	public String asIKVar(String fkName){
		Table foreignTable = this.getForeignTableByFkName(fkName);
		return null==foreignTable
				? ""
				: StringUtilities.convertName(toUniversalFkName(fkName) + "_of_" +getBasename(true), true);
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
	
	public String getForeignKeyListenerVar(String fkName) {		
		return "foreignKeyListener" + StringUtilities.convertName("By_" + toUniversalFkName(fkName),false);
	}
	public String getBindMethod(String fkName) {
		return StringUtilities.convertName(
				Joiner.on('_').join("bind",
						toUniversalFkName(fkName),"listener",
						"to",
						getForeignTableByFkName(fkName).getBasename(false),"Manager"),
				true);
	}
	
	public String stateVarType(){
		return countColumns()>64 ? "long[]":"long";
	}
	/** 生成全0L的modified初始值  */
	public String maskInitializeWithZero(){
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
	/** 根据字段是否有default value生成initialized字段初始值  */
	public String maskInitializeWithDefaultValue(){
		if(countColumns()>64){
			final long[] array = new long[(countColumns()+63)>>6];
			Arrays.fill(array, 0L);
			Collections2.filter(getColumnsAsList(), new Predicate<Column>(){
				@Override
				public boolean apply(Column input) {
					if(!input.getDefaultValue().isEmpty()){
						int index = input.getOrdinalPosition()-1;
						array[index>>6] |= (1L << (index & 0x3f));
					}
					return false;
				}});
			String initValue = Joiner.on(',').join(Lists.transform(Longs.asList(array),new Function<Long,String>(){
				@Override
				public String apply(Long input) {
					return Long.toBinaryString(input.longValue());
				}}));
			return String.format("new long[]{%s}",initValue);
		}else{
			Collection<Column> defCols = Collections2.filter(getColumnsAsList(), new Predicate<Column>(){
				@Override
				public boolean apply(Column input) {
					return !input.getDefaultValue().isEmpty();
				}});
			String mask = Joiner.on(" | ").join(Collections2.transform(defCols,
					new Function<Column,String>(){
						@Override
						public String apply(Column input) {
							return input.getIDMaskConstName();
						}}));
			return mask.isEmpty()?"0L":"("+mask+")";
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
	public String getLoadMethodOfJunction(){
		if(this.isJunctionTable()){
			return "load" + "Via" + this.asCoreClass(true);
		}
		return "";
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
	/**
	 * 包含foreign key信息的数据对象 
	 * @author guyadong
	 *
	 */
	public static class ForeignKey{
		/** foreign key name */
		final String fkName;
		/** columns of foreign key ,in KEY_SEQ order*/
		final Vector<Column> columns = new Vector<Column>();
		/** UPDATE_RULE */
		final Table.ForeignKeyRule updateRule;
		/**  DELETE_RULE */
		final Table.ForeignKeyRule deleteRule;
		public ForeignKey(String fkName, 
				Table.ForeignKeyRule updateRule, 
				Table.ForeignKeyRule deleteRule, 
				Column columns) {
			checkArgument(!Strings.isNullOrEmpty(fkName));
			checkNotNull(columns);
			this.fkName = fkName;
			this.columns.add(columns);
			this.updateRule = checkNotNull(updateRule);
			this.deleteRule = checkNotNull(deleteRule);
		}
		public Table getForeignTable(){
			return columns.get(0).getForeignColumn().getTable();
		}
		public Table getTable(){
			return columns.get(0).getTable();
		}
		/** 返回主键{@code primaryColumn}对应的字段 */
		public Column foreignColumnOf(Column primaryColumn){
			for(Column column:columns){
				if(column.getForeignColumn().equals(primaryColumn))return column;
			}
			return null;
		}
		@Override
		public boolean equals(Object obj) {
			if(super.equals(obj))return true;
			if(!(obj instanceof ForeignKey))return false;
			ForeignKey other = (ForeignKey)obj;
			return new EqualsBuilder()
				.append(fkName, other.fkName)
				.append(columns, other.columns)
				.append(updateRule, other.updateRule)
				.append(deleteRule, other.deleteRule)
				.isEquals();
		}
		@Override
		public String toString() {			
			return new ToStringBuilder(this)
					.append("fkName",fkName)
					.append("columns", columns)
					.append("updateRule", updateRule)
					.append("deleteRule", deleteRule)
					.toString();
		}
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(fkName)
					.append(columns)
					.append(updateRule)
					.append(deleteRule)
					.toHashCode();
		}
		public String getFkName() {
			return fkName;
		}
		public Vector<Column> getColumns() {
			return columns;
		}
		public Table.ForeignKeyRule getUpdateRule() {
			return updateRule;
		}
		public Table.ForeignKeyRule getDeleteRule() {
			return deleteRule;
		}
		public String asFkVar(){
			return StringUtilities.convertName(getUniversalName(),true);
		}
		public String asIkVar(){
			return StringUtilities.convertName(getUniversalName() + "_of_" +getTable().getBasename(true), true);
		}
		public String getUniversalName() {
			return Joiner.on('_').join(Lists.transform(
					columns, 
					new Function<Column,String>(){
						@Override
							public String apply(Column input) {
								return input.getName();
							}}));
		}
	}
	public static enum ForeignKeyRule{
		CASCADE("DELETE"),RESTRICT,SET_NULL("UPDATE"),NO_ACTION,SET_DEFAULT("UPDATE");
		private final String eventOfDeleteRule;

		private ForeignKeyRule(){
			this("");
		}
		private ForeignKeyRule(String event) {
			this.eventOfDeleteRule = event;
		}
		public boolean isNoAction(){
			return this == NO_ACTION || this == RESTRICT;
		}
		public boolean equals(String value){
			try{
				if(Strings.isNullOrEmpty(value))return false;
				return this == ForeignKeyRule.valueOf(value.toUpperCase());
			}catch(Exception e){
				return false;
			}
		}
		public String getEventOfDeleteRule() {
			return eventOfDeleteRule;
		}
	}
	public String getCyeleTestMethod(ForeignKey fk) {
		return getSelftMethod(fk,"is_cycle_on_");
	}
	public String getTopMethod(ForeignKey fk) {
		return getSelftMethod(fk,"top_of_");
	}
	public String getLevelMethod(ForeignKey fk) {
		return getSelftMethod(fk,"level_of_");
	}
	public String getListMethod(ForeignKey fk) {
		return getSelftMethod(fk,"list_of_");
	}
	public String getCheckNotCycleMethod(ForeignKey fk) {
		return getSelftMethod(fk,"check_cycle_of_");
	}
	public String getChildListMethod(ForeignKey fk) {
		return getSelftMethod(fk,"child_list_by_");
	}
	private  String getSelftMethod(ForeignKey fk,String prefix) {
		if(null == fk )return null;
		return StringUtilities.convertName(
				prefix + Joiner.on('_').join(Lists.transform(fk.columns, new Function<Column,String>(){
					@Override
					public String apply(Column input) {
						return input.getName();
					}})),
				true);
	}
	public String getGetManagerMethod(){
		return "get" +  asManagerClassNSP();		
	}
}
/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
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
	private String catalog;
	private String schema;
	private String name;
	private String type;
	private String remarks;
	private Vector<Column> foreignKeys = new Vector<Column>();
	private Vector<Column> importedKeys = new Vector<Column>();
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

	public void addForeignKey(Column col) {
		if (!this.foreignKeys.contains((Object) col)) {
			this.foreignKeys.add(col);
		}
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

	public String getJavaName() {
		return this.convertName("");
	}

	public String convertName(String value) {
		String basename = "";
		basename = "".equals(CodeWriter.getClassPrefix())
				? this.getName()
				: CodeWriter.getClassPrefix() + "_" + this.getName();
		if ("".equals(value)) {
			return StringUtilities.convertName((String) basename, (boolean) false);
		}
		return StringUtilities.convertName((String) (basename + "_" + value), (boolean) false);
	}

	public String asClass(String suffix) {
		return this.convertName(suffix);
	}

	public String asCoreClass() {
		return this.convertName("");
	}

	public String asBeanClass() {
		return this.convertName("Bean");
	}

	public String asCacheClass() {
		return this.convertName("Cache");
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

	public String asManagerImplClass() {
		return this.convertName("Manager_Impl");
	}

	public String asManagerTestClass() {
		return this.convertName("Manager_Test");
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
}
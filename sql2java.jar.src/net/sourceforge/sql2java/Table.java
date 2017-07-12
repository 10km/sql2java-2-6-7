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

public class Table {
	private Hashtable colHash;
	private Vector cols;
	private Hashtable indHash;
	private Vector indices;
	private Hashtable indUniqueHash;
	private Vector uniqueIndices;
	private Hashtable indNonUniHash;
	private Vector nonUniqueIndices;
	private Vector priKey;
	private String catalog;
	private String schema;
	private String name;
	private String type;
	private String remarks;
	private Vector foreignKeys;
	private Vector importedKeys;
	private List procedures;
	private HashMap procHash;
	private Random aleatorio;

	public Table() {
		this.colHash = new Hashtable();
		this.cols = new Vector();
		this.indHash = new Hashtable();
		this.indices = new Vector();
		this.indUniqueHash = new Hashtable();
		this.uniqueIndices = new Vector();
		this.indNonUniHash = new Hashtable();
		this.nonUniqueIndices = new Vector();
		this.priKey = new Vector();

		this.foreignKeys = new Vector();
		this.importedKeys = new Vector();

		this.procedures = new ArrayList();
		this.procHash = new HashMap();

		this.aleatorio = new Random(new Date().getTime());
	}

	public boolean isRelationTable() {
		if ("false".equalsIgnoreCase(getTableProperty("nntable")))
			return false;
		return (this.foreignKeys.size() == 2);
	}

	public boolean relationConnectsTo(Table otherTable) {
		if (super.equals(otherTable)) {
			return false;
		}
		int nbImported = this.importedKeys.size();
		for (int i = 0; i < nbImported; ++i) {
			Column c = (Column) this.importedKeys.get(i);
			if (c.getTableName().equals(otherTable.getName())) {
				return true;
			}
		}
		return false;
	}

	public Table[] linkedTables(Database pDatabase, Table pTable) {
		Vector vector = new Vector();

		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Column pColumn = (Column) this.importedKeys.get(iIndex);
			if (pColumn.getTableName().equals(pTable.getName()))
				continue;
			Table pTableToAdd = pDatabase.getTable(pColumn.getTableName());
			if (!(vector.contains(pTableToAdd))) {
				vector.add(pTableToAdd);
			}
		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public Column getForeignKeyFor(Table pTable) {
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Column pColumn = (Column) this.importedKeys.get(iIndex);
			if (pColumn.getTableName().equals(pTable.getName())) {
				return pColumn;
			}
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
		if (remarks != null)
			this.remarks = remarks.replaceAll("/\\*", "SLASH*").replaceAll("\\*/", "*SLASH");
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
		return ((Column[]) (Column[]) this.cols.toArray(new Column[this.cols.size()]));
	}

	public Column getColumn(String columnName) {
		return ((Column) this.colHash.get(columnName.toLowerCase()));
	}

	public void addColumn(Column column) {
		this.colHash.put(column.getName().toLowerCase(), column);
		this.cols.addElement(column);
	}

	public void removeColumn(Column column) {
		this.cols.removeElement(column);
		this.colHash.remove(column.getName().toLowerCase());
	}

	public Index[] getUniqueIndices() {
		return ((Index[]) (Index[]) this.uniqueIndices.toArray(new Index[this.uniqueIndices.size()]));
	}

	public Index[] getNonUniqueIndices() {
		return ((Index[]) (Index[]) this.nonUniqueIndices.toArray(new Index[this.nonUniqueIndices.size()]));
	}

	public int countIndices() {
		return this.indices.size();
	}

	public Index[] getIndices() {
		return ((Index[]) (Index[]) this.indices.toArray(new Index[this.indices.size()]));
	}

	public Index getIndex(String indName) {
		return ((Index) this.indHash.get(indName.toLowerCase()));
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
		this.indices.remove(index);
		this.indHash.remove(index.getName().toLowerCase());
		if (index.isUnique()) {
			this.uniqueIndices.remove(index);
			this.indUniqueHash.remove(index.getName().toLowerCase());
		} else {
			this.nonUniqueIndices.remove(index);
			this.indNonUniHash.remove(index.getName().toLowerCase());
		}
	}

	public Column[] getPrimaryKeys() {
		return ((Column[]) (Column[]) this.priKey.toArray(new Column[this.priKey.size()]));
	}

	public boolean hasCompositeKey() {
		return (this.priKey.size() > 1);
	}

	public Column getPrimaryKey() throws RuntimeException {
		if (this.priKey.size() != 1) {
			throw new RuntimeException("Table " + getName() + " has a composite key, not a unique primary key");
		}
		return ((Column) this.priKey.get(0));
	}

	public void addPrimaryKey(Column column) {
		this.priKey.addElement(column);
		column.isPrimaryKey(true);
	}

	public Column[] getImportedKeys() {
		return ((Column[]) (Column[]) this.importedKeys.toArray(new Column[this.importedKeys.size()]));
	}

	public void addImportedKey(Column column) {
		if (!(this.importedKeys.contains(column)))
			this.importedKeys.addElement(column);
	}

	public int countColumns() {
		return this.cols.size();
	}

	public int countPrimaryKeys() {
		return this.priKey.size();
	}

	public boolean hasPrimaryKey() {
		return (countPrimaryKeys() > 0);
	}

	public int countImportedKeys() {
		return this.importedKeys.size();
	}

	public boolean hasImportedKeys() {
		return (countImportedKeys() > 0);
	}

	public int countForeignKeys() {
		return this.foreignKeys.size();
	}

	public boolean hasForeignKeys() {
		return (countForeignKeys() > 0);
	}

	public void addForeignKey(Column col) {
		if (!(this.foreignKeys.contains(col)))
			this.foreignKeys.add(col);
	}

	public Column[] getForeignKeys() {
		return ((Column[]) (Column[]) this.foreignKeys.toArray(new Column[this.foreignKeys.size()]));
	}

	public boolean isForeignKey(Column col) {
		return this.foreignKeys.contains(col);
	}

	public int countManyToManyTables() {
		return getManyToManyTables().length;
	}

	public boolean hasManyToManyTables() {
		return (countManyToManyTables() > 0);
	}

	public Table[] getManyToManyTables() {
		Vector vector = new Vector();

		Table[] linkedTables = getImportedTables();
		System.out.println(getName() + "  getManyToManyTables, linked tables = " + linkedTables.length);

		for (int iIndex = 0; iIndex < linkedTables.length; ++iIndex) {
			System.out.println(getName() + "    " + linkedTables[iIndex].getName() + " relation table ?");
			if (!(linkedTables[iIndex].isRelationTable()))
				continue;
			Table[] relationLinkedTable = linkedTables[iIndex].getForeignTables();
			System.out.println(getName() + "      " + linkedTables[iIndex].getName() + " has "
					+ relationLinkedTable.length + " foreign table");
			for (int i = 0; i < relationLinkedTable.length; ++i) {
				System.out.println(
						getName() + "          " + i + " " + relationLinkedTable[i].getName() + " is relation table");

				if ((relationLinkedTable[i].equals(this)) || (vector.contains(relationLinkedTable[i])))
					continue;
				vector.add(relationLinkedTable[i]);
			}

		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public int countLinkedTables() {
		return getLinkedTables().length;
	}

	public boolean hasLinkedTables() {
		return (countLinkedTables() > 0);
	}

	public Table[] getLinkedTables() {
		Vector vector = new Vector();

		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Column column = (Column) this.importedKeys.get(iIndex);
			if (column.getTableName().equals(getName()))
				continue;
			Table pTableToAdd = column.getTable();
			if (!(vector.contains(pTableToAdd))) {
				vector.add(pTableToAdd);
			}

		}

		int nbForeign = this.foreignKeys.size();
		for (int iIndex = 0; iIndex < nbForeign; ++iIndex) {
			Column column = (Column) this.foreignKeys.get(iIndex);
			column = column.getForeignColumn();
			if (column.getTableName().equals(getName()))
				continue;
			Table pTableToAdd = column.getTable();
			if (!(vector.contains(pTableToAdd))) {
				vector.add(pTableToAdd);
			}
		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public int countImportedTables() {
		return getImportedTables().length;
	}

	public boolean hasImportedTables() {
		return (countImportedTables() > 0);
	}

	public Table[] getImportedTables() {
		Vector vector = new Vector();
		int nbImported = this.importedKeys.size();
		for (int iIndex = 0; iIndex < nbImported; ++iIndex) {
			Column column = (Column) this.importedKeys.get(iIndex);
			if (column.getTableName().equals(getName()))
				continue;
			Table pTableToAdd = column.getTable();
			if (!(vector.contains(pTableToAdd))) {
				vector.add(pTableToAdd);
			}
		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public int countForeignTables() {
		return getForeignTables().length;
	}

	public boolean hasForeignTables() {
		return (countForeignTables() > 0);
	}

	public Table[] getForeignTables() {
		Vector vector = new Vector();
		int nbForeign = this.foreignKeys.size();
		for (int iIndex = 0; iIndex < nbForeign; ++iIndex) {
			Column column = ((Column) this.foreignKeys.get(iIndex)).getForeignColumn();
			if (column.getTableName().equals(getName()))
				continue;
			Table pTableToAdd = column.getTable();
			if (!(vector.contains(pTableToAdd))) {
				vector.add(pTableToAdd);
			}
		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public Table getRelationTable(Table targetTable) {
		System.out.println("getRelationTable " + getName() + "<->" + targetTable.getName() + ")");
		Table[] importedTables = getImportedTables();
		for (int iIndex = 0; iIndex < importedTables.length; ++iIndex) {
			Table[] foreignTables = importedTables[iIndex].getForeignTables();
			for (int iIndex2 = 0; iIndex2 < foreignTables.length; ++iIndex2) {
				if (foreignTables[iIndex2].getName().equalsIgnoreCase(getName())) {
					return importedTables[iIndex];
				}
			}
		}
		return targetTable;
	}

	public int countProcedures() {
		return this.procedures.size();
	}

	public boolean hasProcedures() {
		return (countProcedures() > 0);
	}

	public Procedure[] getProcedures() {
		return ((Procedure[]) (Procedure[]) this.procedures.toArray(new Procedure[this.procedures.size()]));
	}

	public void addProcedure(Procedure procedure) {
		if (null == this.procHash.get(procedure.getName())) {
			this.procedures.add(procedure);
			this.procHash.put(procedure.getName(), procedure);
		}
	}

	public String[] getLinkedPackages() {
		Vector vector = new Vector();
		Table[] linkedTables = getLinkedTables();
		for (int iIndex = 0; iIndex < linkedTables.length; ++iIndex) {
			if (!(vector.contains(linkedTables[iIndex].getPackage()))) {
				vector.add(linkedTables[iIndex].getPackage());
			}
		}
		return ((String[]) (String[]) vector.toArray(new String[vector.size()]));
	}

	public String getPackage() {
		String basePackage = CodeWriter.getProperty("codewriter.package");
		String xmlSubpackage = getTableProperty("subpackage");

		if (null != xmlSubpackage) {
			return basePackage + "." + xmlSubpackage;
		}

		int iterating = 1;
		while (true) {
			String tablesProperty = "subpackage." + iterating + ".tables";
			String packageNameProperty = "subpackage." + iterating + ".name";
			String[] tables = CodeWriter.getPropertyExploded(tablesProperty);

			if (tables.length == 0) {
				break;
			}
			for (int i = 0; i < tables.length; ++i) {
				if (!(getName().equalsIgnoreCase(tables[i])))
					continue;
				String packageName = CodeWriter.getProperty(packageNameProperty);
				if (packageName == null) {
					return basePackage;
				}
				return basePackage + "." + packageName;
			}

			++iterating;
		}
		return basePackage;
	}

	public String getPackagePath() {
		return getPackage().replace('.', '/') + "/";
	}

	public Column[] getColumnsFor(String webElement) {
		Vector vector = new Vector();
		int nbCols = this.cols.size();
		for (int i = 0; i < nbCols; ++i) {
			Column c = (Column) this.cols.get(i);
			if (c.columnFor(webElement)) {
				vector.add(c);
			}
		}
		return ((Column[]) (Column[]) vector.toArray(new Column[vector.size()]));
	}

	public Column getFirstColumn() {
		return ((Column) this.cols.get(0));
	}

	public String getTableProperty(String property) {
		return ConfigHelper.getTableProperty(this.name, property);
	}

	public String getRemarks() {
		String xmlDefaultValue = getTableProperty("description");
		if ((xmlDefaultValue != null) && (!("".equals(xmlDefaultValue)))) {
			return xmlDefaultValue;
		}
		return ((this.remarks == null) ? "" : this.remarks);
	}

	public String getJavaName() {
		return convertName("");
	}

	public String convertName(String value) {
		String basename = "";
		if ("".equals(CodeWriter.getClassPrefix()))
			basename = getName();
		else {
			basename = CodeWriter.getClassPrefix() + "_" + getName();
		}
		if ("".equals(value)) {
			return StringUtilities.convertName(basename, false);
		}
		return StringUtilities.convertName(basename + "_" + value, false);
	}

	public String asClass(String suffix) {
		return convertName(suffix);
	}

	public String asCoreClass() {
		return convertName("");
	}

	public String asBeanClass() {
		return convertName("Bean");
	}

	public String asCacheClass() {
		return convertName("Cache");
	}

	public String asRelationnalBeanClass() {
		return convertName("Relationnal_Bean");
	}

	public String asHibernateManagerClass() {
		return convertName("Hibernate_Manager");
	}

	public String asIteratorClass() {
		return convertName("Iterator");
	}

	public String asFactoryClass() {
		return convertName("Factory");
	}

	public String asHttpFactoryClass() {
		return convertName("Http_Factory");
	}

	public String asComparatorClass() {
		return convertName("Comparator");
	}

	public String asListenerClass() {
		return convertName("Listener");
	}

	public String asRendererClass() {
		return convertName("Renderer");
	}

	public String asExceptionClass() {
		return convertName("Exception");
	}

	public String asWidgetClass() {
		return convertName("Widget");
	}

	public String asWidgetFactoryClass() {
		return convertName("Widget_Factory");
	}

	public String asActionClass() {
		return convertName("Action");
	}

	public String asActionTestClass() {
		return convertName("Action_Test");
	}

	public String asControllerClass() {
		return convertName("Controller");
	}

	public String asControllerTestClass() {
		return convertName("Controller_Test");
	}

	public String asFormControllerClass() {
		return convertName("Form_Controller");
	}

	public String asFormControllerTestClass() {
		return convertName("Form_Controller_Test");
	}

	public String asDAOClass() {
		return convertName("D_A_O");
	}

	public String asDAOTestClass() {
		return convertName("D_A_O_Test");
	}

	public String asDAOHibernateClass() {
		return convertName("D_A_O_Hibernate");
	}

	public String asManagerClass() {
		return convertName("Manager");
	}

	public String asManagerImplClass() {
		return convertName("Manager_Impl");
	}

	public String asManagerTestClass() {
		return convertName("Manager_Test");
	}

	public String asModelClass() {
		return convertName("Model");
	}

	public String asPKClass() {
		return convertName("P_K");
	}

	public String asTblClass() {
		return convertName("Tbl");
	}

	public Column getVersionColumn() {
		int nbCols = this.cols.size();
		for (int i = 0; i < nbCols; ++i) {
			Column c = (Column) this.cols.get(i);
			if (c.isVersion()) {
				return c;
			}
		}
		throw new IllegalArgumentException("No version column for table " + getName());
	}

	public boolean hasVersionColumn() {
		try {
			getVersionColumn();
			return true;
		} catch (IllegalArgumentException e) {
		}
		return false;
	}

	public long getSerialVersionUID() {
		return this.aleatorio.nextLong();
	}
}
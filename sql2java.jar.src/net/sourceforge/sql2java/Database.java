/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import net.sourceforge.sql2java.CodeWriter;
import net.sourceforge.sql2java.Column;
import net.sourceforge.sql2java.Index;
import net.sourceforge.sql2java.IndexColumn;
import net.sourceforge.sql2java.Procedure;
import net.sourceforge.sql2java.Table;
import oracle.jdbc.driver.OracleConnection;

public class Database {
	private String[] tableTypes;
	private Connection pConnection;
	private DatabaseMetaData meta;
	private Vector<Table> tables;
	private Hashtable<String,Table> tableHash;
	private String engine;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String catalog;
	private String schema;
	private String tablenamepattern;
	private boolean retrieveRemarks = true;
	private String activeConnections;
	private String idleConnections;
	private String maxWait;
	/**
	 * 所有表名的共同前缀
	 */
	private String samePrefix = "";
	public void setOracleRetrieveRemarks(boolean retrieveRemarks) {
		this.retrieveRemarks = retrieveRemarks;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void setTableNamePattern(String tablenamepattern) {
		this.tablenamepattern = tablenamepattern;
	}

	public void setTableTypes(String[] tt) {
		this.tableTypes = tt;
	}

	public void setActiveConnections(String activeConnections) {
		this.activeConnections = activeConnections;
	}

	public void setIdleConnections(String idleConnections) {
		this.idleConnections = idleConnections;
	}

	public void setMaxWait(String maxWait) {
		this.maxWait = maxWait;
	}

	public boolean getOracleRetrieveRemarks() {
		return this.retrieveRemarks;
	}

	public String getEngine() {
		return this.engine;
	}

	public String getDriver() {
		return this.driver;
	}

	public String getUrl() {
		return this.url;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getCatalog() {
		return this.catalog;
	}

	public String getSchema() {
		return this.schema;
	}

	public String getTableNamePattern() {
		return this.tablenamepattern;
	}

	public String[] getTableTypes() {
		return this.tableTypes;
	}

	public String getActiveConnections() {
		return this.activeConnections;
	}

	public String getIdleConnections() {
		return this.idleConnections;
	}

	public String getMaxWait() {
		return this.maxWait;
	}

	public void setSchema(String schema) {
		this.schema = "null".equalsIgnoreCase(schema) ? null : schema;
	}

	public Table[] getRelationTable(Table table) {
		Vector<Table> vector = new Vector<Table>();
		for (int iIndex = 0; iIndex < this.tables.size(); ++iIndex) {
			Table tempTable = (Table) this.tables.get(iIndex);
			if (table.equals((Object) tempTable) || !tempTable.isRelationTable() || !tempTable.relationConnectsTo(table)
					|| vector.contains((Object) tempTable))
				continue;
			vector.add(tempTable);
		}
		return vector.toArray(new Table[vector.size()]);
	}

	public void load() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);
		System.out.println("Connecting to " + this.username + " on " + this.url + " ...");
		this.pConnection = DriverManager.getConnection(this.url, this.username, this.password);
		System.out.println("    Connected.");
		try {
			if (this.pConnection instanceof OracleConnection) {
				((OracleConnection) this.pConnection).setRemarksReporting(this.getOracleRetrieveRemarks());
			}
		} catch (NoClassDefFoundError ncdfe) {
		} catch (Exception e) {
			// empty catch block
		}
		this.meta = this.pConnection.getMetaData();
		this.engine = this.meta.getDatabaseProductName();
		System.out.println("    Database server :" + this.engine + ".");
		this.engine = new StringTokenizer(this.engine).nextToken();
		this.tables = new Vector<Table>();
		this.tableHash = new Hashtable<String,Table>();
		this.loadTables();
		this.initSamePrefix();
		this.loadColumns();
		this.loadPrimaryKeys();
		this.loadImportedKeys();
		this.loadIndexes();
		this.loadProcedures();
		this.sortElements();
		this.pConnection.close();
	}

	public Table[] getTables() {
		return this.tables.toArray(new Table[this.tables.size()]);
	}

	private void addTable(Table t) {
		this.tables.addElement(t);
		this.tableHash.put(t.getName(), t);
	}

	public Table getTable(String name) {
		return (Table) this.tableHash.get(name);
	}

	private void loadTables() throws SQLException {
		System.out.println("Loading table list according to pattern " + this.tablenamepattern + " ...");
		StringTokenizer st = new StringTokenizer(this.tablenamepattern, ",; \t");
		while (st.hasMoreTokens()) {
			String pattern = st.nextToken().trim();
			String tableSchema = this.schema;
			int index = pattern.indexOf(46);
			if (index > 0) {
				tableSchema = pattern.substring(0, index);
				pattern = pattern.substring(index + 1);
			}
			ResultSet resultSet = this.meta.getTables(this.catalog, tableSchema, pattern, this.tableTypes);
			while (resultSet.next()) {
				Table table = new Table();
				table.setCatalog(resultSet.getString("TABLE_CAT"));
				table.setSchema(resultSet.getString("TABLE_SCHEM"));
				table.setName(resultSet.getString("TABLE_NAME"));
				table.setType(resultSet.getString("TABLE_TYPE"));
				table.setRemarks(resultSet.getString("REMARKS"));
				table.setDatabase(this);
				if (!CodeWriter.authorizeProcess((String) table.getName(), (String) "tables.include",
						(String) "tables.exclude"))
					continue;
				this.addTable(table);
				System.out.println("    table " + table.getName() + " found");
			}
			resultSet.close();
		}
	}

	private void loadColumns() throws SQLException {
		System.out.println("Loading columns ...");
		Iterator<Table> it = this.tables.iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			Column c = null;
			ResultSet resultSet = this.meta.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%");
			while (resultSet.next()) {
				c = new Column();
				c.setDatabase(this);
				c.setCatalog(resultSet.getString("TABLE_CAT"));
				c.setSchema(resultSet.getString("TABLE_SCHEM"));
				c.setTableName(resultSet.getString("TABLE_NAME"));
				c.setName(resultSet.getString("COLUMN_NAME"));
				c.setType(resultSet.getShort("DATA_TYPE"));
				c.setTypeName(resultSet.getString("TYPE_NAME"));
				c.setSize(resultSet.getInt("COLUMN_SIZE"));
				c.setDecimalDigits(resultSet.getInt("DECIMAL_DIGITS"));
				c.setRadix(resultSet.getInt("NUM_PREC_RADIX"));
				c.setNullable(resultSet.getInt("NULLABLE"));
				c.setRemarks(resultSet.getString("REMARKS"));
				c.setDefaultValue(resultSet.getString("COLUMN_DEF"));
				c.setOrdinalPosition(resultSet.getInt("ORDINAL_POSITION"));
				table.addColumn(c);
			}
			resultSet.close();
			System.out.println("    " + table.getName() + " found " + table.countColumns() + " columns");
		}
	}

	private void loadPrimaryKeys() throws SQLException {
		System.out.println("Database::loadPrimaryKeys");
		Iterator<Table> it = this.tables.iterator();
		while (it.hasNext()) {
			Column col;
			Table table = (Table) it.next();
			TreeMap<String, Column> map = new TreeMap<String, Column>();
			ResultSet pResultSet = this.meta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
			while (pResultSet.next()) {
				String colName = pResultSet.getString("COLUMN_NAME");
				short seq = pResultSet.getShort("KEY_SEQ");
				System.out.println("Found primary key (seq,name) (" + seq + "," + colName + ") for table '"
						+ table.getName() + "'");
				col = table.getColumn(colName);
				if (col == null)
					continue;
				map.put(String.valueOf(seq), col);
			}
			pResultSet.close();
			int size = map.size();
			for (int k = 1; k <= size; ++k) {
				col = (Column) map.get(String.valueOf(k));
				table.addPrimaryKey(col);
			}
		}
	}

	private void loadImportedKeys() throws SQLException {
		System.out.println("Loading imported keys ...");
		Iterator<Table> it = this.tables.iterator();
		while (it.hasNext()) {
			ResultSet resultSet;
			Table table = (Table) it.next();
			try {
				resultSet = this.meta.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
			} catch (SQLException sqle) {
				System.out.println("    Error while loading imported keys for table " + table.getName());
				continue;
			}
			while (resultSet.next()) {
				String tabName = resultSet.getString("FKTABLE_NAME");
				String colName = resultSet.getString("FKCOLUMN_NAME");
				String foreignTabName = resultSet.getString("PKTABLE_NAME");
				String foreignColName = resultSet.getString("PKCOLUMN_NAME");
				String foreignKeyName = resultSet.getString("FK_NAME");
				if(null==foreignKeyName|| foreignKeyName.isEmpty()){
					Column[] primaryKeys = this.getTable(tabName).getPrimaryKeys();
					if(1==primaryKeys.length){
						// make a fake name
						foreignKeyName="fk_"+ tabName + "_" +primaryKeys[0].getName();
					}else
						System.out.println("WARN: FK_NAME return empty,the generated code  may be incorrected.");
				}
					
				short seq = resultSet.getShort("KEY_SEQ");
				Column col = this.getTable(tabName).getColumn(colName);
				Table foreignTable = this.getTable(foreignTabName);
				if (null == foreignTable)
					continue;
				Column foreignCol = foreignTable.getColumn(foreignColName);
				col.addForeignKey(foreignCol, foreignKeyName, seq);
				foreignCol.addImportedKey(col);
				System.out.println("    " + col.getFullName() + " -> " + foreignCol.getFullName() + " found seq:"+ seq+" foreign key name:"+ foreignKeyName);
			}
			resultSet.close();
		}
	}

	private void loadIndexes() throws SQLException {
		System.out.println("Loading indexes ...");
		Iterator<Table> it = this.tables.iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			ResultSet resultSet = null;
			try {
				resultSet = this.meta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, true);
			} catch (SQLException sqle) {
				System.out.println("    Error while loading indexes for table " + table.getName());
				continue;
			}
			String currentName = "";
			Index index = null;
			while (resultSet.next()) {
				Column col;
				String colName = resultSet.getString("COLUMN_NAME");
				String indName = resultSet.getString("INDEX_NAME");
				if (null == indName || null == colName || (col = table.getColumn(colName)).isPrimaryKey())
					continue;
				System.out.println(
						"  Found interesting index " + indName + " on " + colName + " for table " + table.getName());
				if (!currentName.equals(indName)) {
					index = new Index(indName, table);
					index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
					currentName = indName;
				}
				IndexColumn column = new IndexColumn();
				column.setName(resultSet.getString("COLUMN_NAME"));
				column.setOrdinalPosition((int) resultSet.getShort("ORDINAL_POSITION"));
				column.setSortSequence(resultSet.getString("ASC_OR_DESC"));
				column.setFilterCondition(resultSet.getString("FILTER_CONDITION"));
				column.setType(col.getType());
				index.addIndexColumn(column);
			}
			resultSet.close();
		}
	}

	private void loadProcedures() throws SQLException {
		System.out.println("Loading procedures ...");
		Iterator<Table> it = this.tables.iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			String procedurePattern = table.getTableProperty("procedures");
			if (null == procedurePattern || "".equals(procedurePattern)) {
				procedurePattern = "%" + table.getName() + "%";
			}
			ResultSet resultSet = null;
			try {
				resultSet = this.meta.getProcedures(table.getCatalog(), table.getSchema(), procedurePattern);
			} catch (SQLException sqle) {
				System.out.println("    Error while loading procedures for table " + table.getName());
				continue;
			}
			while (resultSet.next()) {
				String spName = resultSet.getString("PROCEDURE_NAME");
				String spRemarks = resultSet.getString("REMARKS");
				Procedure procedure = new Procedure();
				procedure.setName(spName);
				procedure.setRemarks(spRemarks);
				procedure.setReturnType("void");
				table.addProcedure(procedure);
				System.out.println("    Found procedure " + spName + " for table " + table.getName());
				ResultSet rs = this.meta.getProcedureColumns(this.catalog, this.schema, spName, null);
				block9 : while (rs.next()) {
					String colName = rs.getString("COLUMN_NAME");
					short columnType = rs.getShort("COLUMN_TYPE");
					if (0 == columnType) {
						System.err.println("    Column " + colName + " of unknown type in procedure " + spName);
						continue;
					}
					Column c = new Column();
					c.setType(rs.getShort("DATA_TYPE"));
					if (5 == columnType) {
						procedure.setReturnType(c.getJavaType());
						continue;
					}
					c.setDatabase(this);
					c.setCatalog(rs.getString("PROCEDURE_CAT"));
					c.setSchema(rs.getString("PROCEDURE_SCHEM"));
					c.setTableName(rs.getString("PROCEDURE_NAME"));
					c.setName(colName);
					c.setSize(rs.getInt("LENGTH"));
					c.setDecimalDigits(rs.getInt("SCALE"));
					c.setRadix(rs.getInt("RADIX"));
					c.setNullable(rs.getInt("NULLABLE"));
					c.setRemarks(rs.getString("REMARKS"));
					switch (columnType) {
						case 1 : {
							procedure.addInColumn(c);
							continue block9;
						}
						case 2 : {
							procedure.addInOutColumn(c);
							continue block9;
						}
						case 4 : {
							procedure.addOutColumn(c);
							continue block9;
						}
					}
					procedure.setReturnType("List");
				}
				rs.close();
			}
			resultSet.close();
		}
	}

	public String[] getAllPackages() {
		Vector<String> vector = new Vector<String>();
		for (int iIndex = 0; iIndex < this.tables.size(); ++iIndex) {
			Table table = (Table) this.tables.get(iIndex);
			if (vector.contains(table.getPackage()))
				continue;
			vector.add(table.getPackage());
		}
		return vector.toArray(new String[vector.size()]);
	}
	/**
	 * sort foreign keys and Import keys of all column
	 */
	private void sortElements(){
		for(Table table:this.tables){
			for(Column column:table.getColumns()){
				Collections.sort(column.getForeignKeys());
				Collections.sort(column.getImportedKeys());
			}
		}
	}
	
	public String getSamePrefix()  {		
		return this.samePrefix;
	}	
	/**
	 * 计算所有表名的共同前缀
	 */
	private void initSamePrefix()  {
		int index=-1;
		if(0==this.tables.size())return;
		String first=this.tables.get(0).getName();
		try{
			for(int i=0;i<first.length();++i){
				for(int j=1;j<this.tables.size();++j){
					String c=this.tables.get(j).getName();
					if(c.charAt(i)!=first.charAt(i))
						throw new IndexOutOfBoundsException();
				}
				index=i;
			}
		}catch(IndexOutOfBoundsException e){			
		}
		this.samePrefix= index<0?"":first.substring(0, index+1);
		System.out.printf("samePrefix = [%s]\n", this.samePrefix);
	}
}
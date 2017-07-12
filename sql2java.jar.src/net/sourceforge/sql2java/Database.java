/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.sql.*;
import java.util.*;
import oracle.jdbc.driver.OracleConnection;

public class Database {

	private String tableTypes[];
	private Connection pConnection;
	private DatabaseMetaData meta;
	private Vector tables;
	private Hashtable tableHash;
	private String engine;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String catalog;
	private String schema;
	private String tablenamepattern;
	private boolean retrieveRemarks;
	private String activeConnections;
	private String idleConnections;
	private String maxWait;

	public Database() {
		retrieveRemarks = true;
	}

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

	public void setTableTypes(String tt[]) {
		tableTypes = tt;
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
		return retrieveRemarks;
	}

	public String getEngine() {
		return engine;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getTableNamePattern() {
		return tablenamepattern;
	}

	public String[] getTableTypes() {
		return tableTypes;
	}

	public String getActiveConnections() {
		return activeConnections;
	}

	public String getIdleConnections() {
		return idleConnections;
	}

	public String getMaxWait() {
		return maxWait;
	}

	public void setSchema(String schema) {
		if ("null".equalsIgnoreCase(schema))
			this.schema = null;
		else
			this.schema = schema;
	}

	public Table[] getRelationTable(Table table) {
		Vector vector = new Vector();
		for (int iIndex = 0; iIndex < tables.size(); iIndex++) {
			Table tempTable = (Table) tables.get(iIndex);
			if (!table.equals(tempTable) && tempTable.isRelationTable() && tempTable.relationConnectsTo(table)
					&& !vector.contains(tempTable))
				vector.add(tempTable);
		}

		return (Table[]) (Table[]) vector.toArray(new Table[vector.size()]);
	}

	public void load() throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		System.out.println("Connecting to " + username + " on " + url + " ...");
		pConnection = DriverManager.getConnection(url, username, password);
		System.out.println("    Connected.");
		try {
			if (pConnection instanceof OracleConnection)
				((OracleConnection) pConnection).setRemarksReporting(getOracleRetrieveRemarks());
		} catch (NoClassDefFoundError ncdfe) {
		} catch (Exception e) {
		}
		meta = pConnection.getMetaData();
		engine = meta.getDatabaseProductName();
		System.out.println("    Database server :" + engine + ".");
		engine = (new StringTokenizer(engine)).nextToken();
		tables = new Vector();
		tableHash = new Hashtable();
		loadTables();
		loadColumns();
		loadPrimaryKeys();
		loadImportedKeys();
		loadIndexes();
		loadProcedures();
		pConnection.close();
	}

	public Table[] getTables() {
		return (Table[]) (Table[]) tables.toArray(new Table[tables.size()]);
	}

	private void addTable(Table t) {
		tables.addElement(t);
		tableHash.put(t.getName(), t);
	}

	public Table getTable(String name) {
		return (Table) tableHash.get(name);
	}

	private void loadTables() throws SQLException {
		System.out.println("Loading table list according to pattern " + tablenamepattern + " ...");
		ResultSet resultSet;
		label0 : for (StringTokenizer st = new StringTokenizer(tablenamepattern, ",; \t"); st.hasMoreTokens(); resultSet
				.close()) {
			String pattern = st.nextToken().trim();
			String tableSchema = schema;
			int index = pattern.indexOf('.');
			if (index > 0) {
				tableSchema = pattern.substring(0, index);
				pattern = pattern.substring(index + 1);
			}
			resultSet = meta.getTables(catalog, tableSchema, pattern, tableTypes);
			do {
				if (!resultSet.next())
					continue label0;
				Table table = new Table();
				table.setCatalog(resultSet.getString("TABLE_CAT"));
				table.setSchema(resultSet.getString("TABLE_SCHEM"));
				table.setName(resultSet.getString("TABLE_NAME"));
				table.setType(resultSet.getString("TABLE_TYPE"));
				table.setRemarks(resultSet.getString("REMARKS"));
				if (CodeWriter.authorizeProcess(table.getName(), "tables.include", "tables.exclude")) {
					addTable(table);
					System.out.println("    table " + table.getName() + " found");
				}
			} while (true);
		}

	}

	private void loadColumns() throws SQLException {
		System.out.println("Loading columns ...");
		Table table;
		for (Iterator it = tables.iterator(); it.hasNext(); System.out
				.println("    " + table.getName() + " found " + table.countColumns() + " columns")) {
			table = (Table) it.next();
			Column c = null;
			ResultSet resultSet;
			for (resultSet = meta.getColumns(table.getCatalog(), table.getSchema(), table.getName(), "%"); resultSet
					.next(); table.addColumn(c)) {
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
			}

			resultSet.close();
		}

	}

	private void loadPrimaryKeys() throws SQLException {
		System.out.println("Database::loadPrimaryKeys");
		for (Iterator it = tables.iterator(); it.hasNext();) {
			Table table = (Table) it.next();
			SortedMap map = new TreeMap();
			ResultSet pResultSet = meta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
			do {
				if (!pResultSet.next())
					break;
				String colName = pResultSet.getString("COLUMN_NAME");
				int seq = pResultSet.getShort("KEY_SEQ");
				System.out.println("Found primary key (seq,name) (" + seq + "," + colName + ") for table '"
						+ table.getName() + "'");
				Column col = table.getColumn(colName);
				if (col != null)
					map.put(String.valueOf(seq), col);
			} while (true);
			pResultSet.close();
			int size = map.size();
			int k = 1;
			while (k <= size) {
				Column col = (Column) map.get(String.valueOf(k));
				table.addPrimaryKey(col);
				k++;
			}
		}

	}

	private void loadImportedKeys() throws SQLException {
		System.out.println("Loading imported keys ...");
		Iterator it = tables.iterator();
		do {
			if (!it.hasNext())
				break;
			Table table = (Table) it.next();
			ResultSet resultSet;
			try {
				resultSet = meta.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
			} catch (SQLException sqle) {
				System.out.println("    Error while loading imported keys for table " + table.getName());
				continue;
			}
			do {
				if (!resultSet.next())
					break;
				String tabName = resultSet.getString("FKTABLE_NAME");
				String colName = resultSet.getString("FKCOLUMN_NAME");
				String foreignTabName = resultSet.getString("PKTABLE_NAME");
				String foreignColName = resultSet.getString("PKCOLUMN_NAME");
				Column col = getTable(tabName).getColumn(colName);
				Table foreignTable = getTable(foreignTabName);
				if (null != foreignTable) {
					Column foreignCol = foreignTable.getColumn(foreignColName);
					col.addForeignKey(foreignCol);
					foreignCol.addImportedKey(col);
					System.out.println("    " + col.getFullName() + " -> " + foreignCol.getFullName() + " found ");
				}
			} while (true);
			resultSet.close();
		} while (true);
	}

	private void loadIndexes() throws SQLException {
		System.out.println("Loading indexes ...");
		Iterator it = tables.iterator();
		do {
			if (!it.hasNext())
				break;
			Table table = (Table) it.next();
			ResultSet resultSet = null;
			try {
				resultSet = meta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, true);
			} catch (SQLException sqle) {
				System.out.println("    Error while loading indexes for table " + table.getName());
				continue;
			}
			String currentName = "";
			Index index = null;
			do {
				if (!resultSet.next())
					break;
				String colName = resultSet.getString("COLUMN_NAME");
				String indName = resultSet.getString("INDEX_NAME");
				if (null != indName && null != colName) {
					Column col = table.getColumn(colName);
					if (!col.isPrimaryKey()) {
						System.out.println("  Found interesting index " + indName + " on " + colName + " for table "
								+ table.getName());
						if (!currentName.equals(indName)) {
							index = new Index(indName, table);
							index.setUnique(!resultSet.getBoolean("NON_UNIQUE"));
							currentName = indName;
						}
						IndexColumn column = new IndexColumn();
						column.setName(resultSet.getString("COLUMN_NAME"));
						column.setOrdinalPosition(resultSet.getShort("ORDINAL_POSITION"));
						column.setSortSequence(resultSet.getString("ASC_OR_DESC"));
						column.setFilterCondition(resultSet.getString("FILTER_CONDITION"));
						column.setType(col.getType());
						index.addIndexColumn(column);
					}
				}
			} while (true);
			resultSet.close();
		} while (true);
	}

	private void loadProcedures() throws SQLException {
		System.out.println("Loading procedures ...");
		Iterator it = tables.iterator();
		do {
			if (!it.hasNext())
				break;
			Table table = (Table) it.next();
			String procedurePattern = table.getTableProperty("procedures");
			if (null == procedurePattern || "".equals(procedurePattern))
				procedurePattern = "%" + table.getName() + "%";
			ResultSet resultSet = null;
			try {
				resultSet = meta.getProcedures(table.getCatalog(), table.getSchema(), procedurePattern);
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
				ResultSet rs = meta.getProcedureColumns(catalog, schema, spName, null);
				label0 : do {
					String colName;
					short columnType;
					Column c;
					do {
						if (!rs.next())
							break label0;
						colName = rs.getString("COLUMN_NAME");
						columnType = rs.getShort("COLUMN_TYPE");
						if (0 == columnType) {
							System.err.println("    Column " + colName + " of unknown type in procedure " + spName);
							continue;
						}
						c = new Column();
						c.setType(rs.getShort("DATA_TYPE"));
						if (5 != columnType)
							break;
						procedure.setReturnType(c.getJavaType());
					} while (true);
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
						case 1 : // '\001'
							procedure.addInColumn(c);
							break;

						case 2 : // '\002'
							procedure.addInOutColumn(c);
							break;

						case 4 : // '\004'
							procedure.addOutColumn(c);
							break;

						case 3 : // '\003'
						default :
							procedure.setReturnType("List");
							break;
					}
				} while (true);
				rs.close();
			}
			resultSet.close();
		} while (true);
	}

	public String[] getAllPackages() {
		Vector vector = new Vector();
		for (int iIndex = 0; iIndex < tables.size(); iIndex++) {
			Table table = (Table) tables.get(iIndex);
			if (!vector.contains(table.getPackage()))
				vector.add(table.getPackage());
		}

		return (String[]) (String[]) vector.toArray(new String[vector.size()]);
	}
}
/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import oracle.jdbc.driver.OracleConnection;

public class Database {
	private String[] tableTypes;
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
		this.retrieveRemarks = true;
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
		if ("null".equalsIgnoreCase(schema))
			this.schema = null;
		else
			this.schema = schema;
	}

	public Table[] getRelationTable(Table table) {
		Vector vector = new Vector();

		for (int iIndex = 0; iIndex < this.tables.size(); ++iIndex) {
			Table tempTable = (Table) this.tables.get(iIndex);

			if (table.equals(tempTable)) {
				continue;
			}

			if ((!(tempTable.isRelationTable())) || (!(tempTable.relationConnectsTo(table)))
					|| (vector.contains(tempTable)))
				continue;
			vector.add(tempTable);
		}

		return ((Table[]) (Table[]) vector.toArray(new Table[vector.size()]));
	}

	public void load() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);

		System.out.println("Connecting to " + this.username + " on " + this.url + " ...");
		this.pConnection = DriverManager.getConnection(this.url, this.username, this.password);
		System.out.println("    Connected.");
		try {
			if (this.pConnection instanceof OracleConnection) {
				((OracleConnection) this.pConnection).setRemarksReporting(getOracleRetrieveRemarks());
			}

		} catch (NoClassDefFoundError ncdfe) {
		} catch (Exception e) {
		}

		this.meta = this.pConnection.getMetaData();
		this.engine = this.meta.getDatabaseProductName();
		System.out.println("    Database server :" + this.engine + ".");
		this.engine = new StringTokenizer(this.engine).nextToken();
		this.tables = new Vector();
		this.tableHash = new Hashtable();

		loadTables();
		loadColumns();
		loadPrimaryKeys();
		loadImportedKeys();
		loadIndexes();
		loadProcedures();

		this.pConnection.close();
	}

	public Table[] getTables() {
		return ((Table[]) (Table[]) this.tables.toArray(new Table[this.tables.size()]));
	}

	private void addTable(Table t) {
		this.tables.addElement(t);
		this.tableHash.put(t.getName(), t);
	}

	public Table getTable(String name) {
		return ((Table) this.tableHash.get(name));
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
				if (CodeWriter.authorizeProcess(table.getName(), "tables.include", "tables.exclude")) {
					addTable(table);
					System.out.println("    table " + table.getName() + " found");
				}
			}
			resultSet.close();
		}
	}

	private void loadColumns() throws SQLException {
		System.out.println("Loading columns ...");
		for (Iterator it = this.tables.iterator(); it.hasNext();) {
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

		for (Iterator it = this.tables.iterator(); it.hasNext();) {
			Table table = (Table) it.next();
			SortedMap map = new TreeMap();

			ResultSet pResultSet = this.meta.getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
			while (pResultSet.next()) {
				String colName = pResultSet.getString("COLUMN_NAME");
				int seq = pResultSet.getShort("KEY_SEQ");
				System.out.println("Found primary key (seq,name) (" + seq + "," + colName + ") for table '"
						+ table.getName() + "'");

				Column col = table.getColumn(colName);
				if (col != null) {
					map.put(String.valueOf(seq), col);
				}
			}
			pResultSet.close();

			int size = map.size();
			for (int k = 1; k <= size; ++k) {
				Column col = (Column) map.get(String.valueOf(k));
				table.addPrimaryKey(col);
			}
		}
	}

	private void loadImportedKeys() throws SQLException {
		System.out.println("Loading imported keys ...");

		for (Iterator it = this.tables.iterator(); it.hasNext();) {
			Table table = (Table) it.next();
			ResultSet resultSet;
			try {
				resultSet = this.meta.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
			} catch (SQLException sqle) {
				System.out.println("    Error while loading imported keys for table " + table.getName());
			}
			continue;

			while (resultSet.next()) {
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
			}

			resultSet.close();
		}
	}

	private void loadIndexes() throws SQLException {
		System.out.println("Loading indexes ...");

		for (Iterator it = this.tables.iterator(); it.hasNext();) {
			Table table = (Table) it.next();
			ResultSet resultSet = null;
			try {
				resultSet = this.meta.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, true);
			} catch (SQLException sqle) {
				System.out.println("    Error while loading indexes for table " + table.getName());
			}
			continue;

			String currentName = "";
			Index index = null;
			while (resultSet.next()) {
				String colName = resultSet.getString("COLUMN_NAME");
				String indName = resultSet.getString("INDEX_NAME");

				if (null == indName)
					continue;
				if (null == colName) {
					continue;
				}

				Column col = table.getColumn(colName);
				if (col.isPrimaryKey()) {
					continue;
				}
				System.out.println(
						"  Found interesting index " + indName + " on " + colName + " for table " + table.getName());

				if (!(currentName.equals(indName))) {
					index = new Index(indName, table);
					index.setUnique(!(resultSet.getBoolean("NON_UNIQUE")));
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

			resultSet.close();
		}
	}

	private void loadProcedures() throws SQLException {
		System.out.println("Loading procedures ...");

		for (Iterator it = this.tables.iterator(); it.hasNext();) {
			Table table = (Table) it.next();
			String procedurePattern = table.getTableProperty("procedures");
			if ((null == procedurePattern) || ("".equals(procedurePattern))) {
				procedurePattern = "%" + table.getName() + "%";
			}
			ResultSet resultSet = null;
			try {
				resultSet = this.meta.getProcedures(table.getCatalog(), table.getSchema(), procedurePattern);
			} catch (SQLException sqle) {
				System.out.println("    Error while loading procedures for table " + table.getName());
			}
			continue;

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
				while (rs.next()) {
					String colName = rs.getString("COLUMN_NAME");
					short columnType = rs.getShort("COLUMN_TYPE");
					if (0 == columnType) {
						System.err.println("    Column " + colName + " of unknown type in procedure " + spName);
					}

					Column c = new Column();
					c.setType(rs.getShort("DATA_TYPE"));
					if (5 == columnType) {
						procedure.setReturnType(c.getJavaType());
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
						case 1 :
							procedure.addInColumn(c);
							break;
						case 2 :
							procedure.addInOutColumn(c);
							break;
						case 4 :
							procedure.addOutColumn(c);
							break;
						case 3 :
						default :
							procedure.setReturnType("List");
					}
				}
				rs.close();
			}

			resultSet.close();
		}
	}

	public String[] getAllPackages() {
		Vector vector = new Vector();
		for (int iIndex = 0; iIndex < this.tables.size(); ++iIndex) {
			Table table = (Table) this.tables.get(iIndex);
			if (vector.contains(table.getPackage()))
				continue;
			vector.add(table.getPackage());
		}

		return ((String[]) (String[]) vector.toArray(new String[vector.size()]));
	}
}
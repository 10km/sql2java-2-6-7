/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Procedure {
	private static final String IN_COLUMN = "IN";
	private static final String IN_OUT_COLUMN = "INOUT";
	private static final String OUT_COLUMN = "OUT";
	private String name;
	private String sqlName;
	private String remarks;
	private String returnType;
	private List columns;
	private List inColumns;
	private List outColumns;
	private HashMap colsHash;
	private int position;

	public Procedure() {
		this.columns = new ArrayList();
		this.inColumns = new ArrayList();
		this.outColumns = new ArrayList();
		this.colsHash = new HashMap();
		this.position = 1;
	}

	private int nextPosition() {
		return (this.position++);
	}

	private boolean addColumn(Column column) {
		if (null == this.colsHash.get(column.getName())) {
			column.setOrdinalPosition(nextPosition());
			this.columns.add(column);
			this.colsHash.put(column.getName(), column);
			return true;
		}
		return false;
	}

	public void addInColumn(Column column) {
		column.setDefaultValue("IN");
		if (addColumn(column))
			this.inColumns.add(column);
	}

	public void addInOutColumn(Column column) {
		column.setDefaultValue("INOUT");
		if (addColumn(column)) {
			this.inColumns.add(column);
			this.outColumns.add(column);
		}
	}

	public void addOutColumn(Column column) {
		column.setDefaultValue("OUT");
		if (addColumn(column))
			this.outColumns.add(column);
	}

	public int getColumnsCount() {
		return this.columns.size();
	}

	public int getOutColumnsCount() {
		return this.outColumns.size();
	}

	public Column[] getColumns() {
		return ((Column[]) (Column[]) this.columns.toArray(new Column[this.columns.size()]));
	}

	public Column[] getInColumns() {
		return ((Column[]) (Column[]) this.inColumns.toArray(new Column[this.inColumns.size()]));
	}

	public Column[] getOutColumns() {
		return ((Column[]) (Column[]) this.outColumns.toArray(new Column[this.outColumns.size()]));
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.sqlName = name;
		this.name = StringUtilities.convertName(name, true);
	}

	public String getSqlName() {
		return this.sqlName;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
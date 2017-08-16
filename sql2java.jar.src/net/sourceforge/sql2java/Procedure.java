/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package net.sourceforge.sql2java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.sql2java.Column;
import net.sourceforge.sql2java.StringUtilities;

public class Procedure {
	private static final String IN_COLUMN = "IN";
	private static final String IN_OUT_COLUMN = "INOUT";
	private static final String OUT_COLUMN = "OUT";
	private String name;
	private String sqlName;
	private String remarks;
	private String returnType;
	private List<Column> columns = new ArrayList<Column>();
	private List<Column> inColumns = new ArrayList<Column>();
	private List<Column> outColumns = new ArrayList<Column>();
	private HashMap<String,Column> colsHash = new HashMap<String,Column>();
	private int position = 1;

	private int nextPosition() {
		return this.position++;
	}

	private boolean addColumn(Column column) {
		if (null == this.colsHash.get(column.getName())) {
			column.setOrdinalPosition(this.nextPosition());
			this.columns.add(column);
			this.colsHash.put(column.getName(), column);
			return true;
		}
		return false;
	}

	public void addInColumn(Column column) {
		column.setDefaultValue("IN");
		if (this.addColumn(column)) {
			this.inColumns.add(column);
		}
	}

	public void addInOutColumn(Column column) {
		column.setDefaultValue("INOUT");
		if (this.addColumn(column)) {
			this.inColumns.add(column);
			this.outColumns.add(column);
		}
	}

	public void addOutColumn(Column column) {
		column.setDefaultValue("OUT");
		if (this.addColumn(column)) {
			this.outColumns.add(column);
		}
	}

	public int getColumnsCount() {
		return this.columns.size();
	}

	public int getOutColumnsCount() {
		return this.outColumns.size();
	}

	public Column[] getColumns() {
		return this.columns.toArray(new Column[this.columns.size()]);
	}

	public Column[] getInColumns() {
		return this.inColumns.toArray(new Column[this.inColumns.size()]);
	}

	public Column[] getOutColumns() {
		return this.outColumns.toArray(new Column[this.outColumns.size()]);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.sqlName = name;
		this.name = StringUtilities.convertName((String) name, (boolean) true);
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
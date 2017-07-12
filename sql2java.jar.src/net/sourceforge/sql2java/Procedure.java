/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java;

import java.util.*;

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
		columns = new ArrayList();
		inColumns = new ArrayList();
		outColumns = new ArrayList();
		colsHash = new HashMap();
		position = 1;
	}

	private int nextPosition() {
		return position++;
	}

	private boolean addColumn(Column column) {
		if (null == colsHash.get(column.getName())) {
			column.setOrdinalPosition(nextPosition());
			columns.add(column);
			colsHash.put(column.getName(), column);
			return true;
		} else {
			return false;
		}
	}

	public void addInColumn(Column column) {
		column.setDefaultValue("IN");
		if (addColumn(column))
			inColumns.add(column);
	}

	public void addInOutColumn(Column column) {
		column.setDefaultValue("INOUT");
		if (addColumn(column)) {
			inColumns.add(column);
			outColumns.add(column);
		}
	}

	public void addOutColumn(Column column) {
		column.setDefaultValue("OUT");
		if (addColumn(column))
			outColumns.add(column);
	}

	public int getColumnsCount() {
		return columns.size();
	}

	public int getOutColumnsCount() {
		return outColumns.size();
	}

	public Column[] getColumns() {
		return (Column[]) (Column[]) columns.toArray(new Column[columns.size()]);
	}

	public Column[] getInColumns() {
		return (Column[]) (Column[]) inColumns.toArray(new Column[inColumns.size()]);
	}

	public Column[] getOutColumns() {
		return (Column[]) (Column[]) outColumns.toArray(new Column[outColumns.size()]);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		sqlName = name;
		this.name = StringUtilities.convertName(name, true);
	}

	public String getSqlName() {
		return sqlName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
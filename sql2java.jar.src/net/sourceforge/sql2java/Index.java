/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/

package net.sourceforge.sql2java;

import java.util.*;

public class Index {

	private Table table;
	private String name;
	private boolean unique;
	private Map columns;

	public Index() {
		this("");
	}

	public Index(String name) {
		this(name, null);
	}

	public Index(String name, Table table) {
		this(name, table, ((Map) (new HashMap())));
	}

	public Index(String name, Table table, Map columns) {
		this.name = name;
		this.table = table;
		this.columns = columns;
		if (null != this.table)
			this.table.addIndex(this);
	}

	public void addIndexColumn(IndexColumn column) {
		if (null != column)
			columns.put(column.getName().toLowerCase(), column);
	}

	public void removeIndexColumn(Column column) {
		if (null != column)
			columns.remove(column.getName().toLowerCase());
	}

	public Table getTable() {
		return table;
	}

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique;
	}

	public Map getIndexColumns() {
		return columns;
	}

	public List getIndexColumnsList() {
		List list = new ArrayList();
		list.addAll(columns.values());
		Collections.sort(list);
		return list;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
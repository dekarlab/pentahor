package de.dekarlab.pentahor;

import java.util.ArrayList;
import java.util.List;

public class PRColumnVariable extends PRVariable {
	private List<Object> valueColumn;

	public PRColumnVariable(String rName, String pentahoName, int type) {
		super(rName, pentahoName, type);
	}

	/**
	 * Variable.
	 * 
	 * @param rName
	 * @param pentahoName
	 * @param type
	 * @param value
	 */
	public PRColumnVariable(String rName, String pentahoName, int type,
			List<Object> valueColumn) {
		super(rName, pentahoName, type);
		this.valueColumn = valueColumn;
	}

	/**
	 * Get column value.
	 * 
	 * @return
	 */
	public List<Object> getValueColumn() {
		return valueColumn;
	}

	/**
	 * Get column value.
	 * 
	 * @param valueColumn
	 */
	public void setValueColumn(List<Object> valueColumn) {
		this.valueColumn = valueColumn;
	}

	/**
	 * Set value.
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		if (valueColumn == null) {
			valueColumn = new ArrayList<Object>();
		}
		valueColumn.add(value);
	}

}

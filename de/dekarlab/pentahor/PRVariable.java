/*
 *   This file is part of PRCalcPlugin.
 *
 *   PRCalcPlugin is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   PRCalcPlugin is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with PPRCalcPlugin.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Copyright 2013 dekarlab.de Theory. Solutions. Online Training.
 */
package de.dekarlab.pentahor;

public class PRVariable {
	public static final int TYPE_NUMBER = 0;// Double
	public static final int TYPE_STRING = 1;// String
	public static final int TYPE_DATE = 2;// Date
	public static final int TYPE_BOOLEAN = 3;// Boolean

	private String rName;
	private String pentahoName;
	private Object value;
	private int type;

	/**
	 * Variable.
	 * 
	 * @param rName
	 * @param pentahoName
	 * @param type
	 * @param value
	 */
	public PRVariable(String rName, String pentahoName, int type, Object value) {
		this.rName = rName;
		this.pentahoName = pentahoName;
		this.value = value;
		this.type = type;
	}

	/**
	 * Variable.
	 * 
	 * @param rName
	 * @param pentahoName
	 * @param type
	 */
	public PRVariable(String rName, String pentahoName, int type) {
		this(rName, pentahoName, type, null);
	}

	/**
	 * Get value.
	 * 
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set value.
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Get r name.
	 * 
	 * @return
	 */
	public String getrName() {
		return rName;
	}

	/**
	 * Get variable name in pentaho.
	 * 
	 * @return
	 */
	public String getPentahoName() {
		return pentahoName;
	}

	/**
	 * Get type of variable.
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

}

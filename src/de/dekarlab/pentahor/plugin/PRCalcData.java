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

package de.dekarlab.pentahor.plugin;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.BaseStepData;

import de.dekarlab.pentahor.PRVariable;

public class PRCalcData extends BaseStepData implements StepDataInterface {

	/**
	 * New output row meta with new variables.
	 */
	private RowMetaInterface outputRowMeta;
	/**
	 * Initial count of columns.
	 */
	private int inputSize;

	private PRVariable[] inputVars;
	private PRVariable[] outputVars;

	public PRCalcData() {
		super();
	}

	public RowMetaInterface getOutputRowMeta() {
		return outputRowMeta;
	}

	public void setOutputRowMeta(RowMetaInterface outputRowMeta) {
		this.outputRowMeta = outputRowMeta;
	}

	public int getInputSize() {
		return inputSize;
	}

	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	public PRVariable[] getInputVars() {
		return inputVars;
	}

	public void setInputVars(PRVariable[] inputVars) {
		this.inputVars = inputVars;
	}

	public PRVariable[] getOutputVars() {
		return outputVars;
	}

	public void setOutputVars(PRVariable[] outputVars) {
		this.outputVars = outputVars;
	}

}
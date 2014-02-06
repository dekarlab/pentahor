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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import de.dekarlab.pentahor.PRColumnVariable;
import de.dekarlab.pentahor.PRPrintStream;
import de.dekarlab.pentahor.PRVariable;
import de.dekarlab.pentahor.RRunner;

/**
 *
 */
public class PRCalc extends BaseStep implements StepInterface {

	private PRCalcMeta meta;
	private PRCalcData data;

	/**
	 * Constructor.
	 * 
	 * @param stepMeta
	 * @param stepDataInterface
	 * @param copyNr
	 * @param transMeta
	 * @param trans
	 */
	public PRCalc(final StepMeta stepMeta,
			final StepDataInterface stepDataInterface, final int copyNr,
			final TransMeta transMeta, final Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/**
	 * Run R and put result to output.
	 * 
	 * @param smi
	 * @param sdi
	 * @return
	 * @throws KettleException
	 */
	protected void runR(PRVariable[] inputVars, PRVariable[] outputVars)
			throws Exception {
		// Create R code runner.
		RRunner rr = RRunner.getInstance(
				environmentSubstitute(meta.getScriptFilePath()), inputVars,
				outputVars);
		// Evaluate
		logBasic("Run R Script from " + meta.getScriptFilePath());
		rr.run();
		// Close
		rr.closeREngine();
		logBasic("R output: " + rr.getOutput());
		logBasic("R Script is evaluated. REngine is closed");
	}

	/**
	 * Initialize input and output variables for every row.
	 * 
	 * @param rowInput
	 * @param inputVars
	 * @param outputVars
	 */
	private void initInputOutputVars(Object[] rowInput, PRVariable[] inputVars,
			PRVariable[] outputVars) {
		// Create input variables with values.
		PRVariable varTemp;
		logDebug("Init Input Vars");
		for (int i = 0; i < meta.getInputVars().size(); i++) {
			varTemp = meta.getInputVars().get(i);
			if (meta.isInputTable()) {// Save all values in columns
				if (inputVars[i] == null) {// first row
					inputVars[i] = new PRColumnVariable(varTemp.getrName(),
							varTemp.getPentahoName(), varTemp.getType());
				}
			} else {
				inputVars[i] = new PRVariable(varTemp.getrName(),
						varTemp.getPentahoName(), varTemp.getType());
			}
			String[] fNames = getInputRowMeta().getFieldNames();
			for (int j = 0; j < fNames.length; j++) {
				if (fNames[j].equals(varTemp.getPentahoName())) {
					logDebug("Name: " + varTemp.getPentahoName() + " Class: "
							+ rowInput[j].getClass().getCanonicalName());
					inputVars[i].setValue(rowInput[j]);
					logDebug("Input: " + inputVars[i].getrName() + "="
							+ inputVars[i].getValue());
					break;
				}
			}
		}
		logDebug("Init Output Vars");
		// Create output variables.
		for (int i = 0; i < meta.getOutputVars().size(); i++) {
			varTemp = meta.getOutputVars().get(i);
			outputVars[i] = new PRVariable(varTemp.getrName(),
					varTemp.getPentahoName(), varTemp.getType());
		}
	}

	/**
	 * Initialize row meta.
	 * 
	 * @param smi
	 * @param sdi
	 * @param rowInput
	 * @return
	 */
	private void initOutputRowMeta() {
		System.setOut(new PRPrintStream(new ByteArrayOutputStream(), log));
		System.setErr(new PRPrintStream(new ByteArrayOutputStream(), log));
		RowMetaInterface outputRowMeta = null;
		if (meta.isInputTable()) {
			// output is only result
			outputRowMeta = new RowMeta();
		} else {
			// Add output variables as columns to result.
			outputRowMeta = getInputRowMeta().clone();
		}
		data.setInputSize(getInputRowMeta().size());
		ValueMetaInterface valueMeta;
		for (int i = 0; i < meta.getOutputVars().size(); i++) {
			valueMeta = new ValueMeta();
			valueMeta.setName(meta.getOutputVars().get(i).getPentahoName());
			valueMeta.setType(convertRTypeToPentahoType(meta.getOutputVars()
					.get(i).getType()));
			outputRowMeta.addValueMeta(valueMeta);
		}
		data.setOutputRowMeta(outputRowMeta);
	}

	/**
	 * Load JRI library.
	 * 
	 * @throws KettleException
	 */
	protected void loadJRILibrary() throws Exception {
		logBasic("Loading JRI library from: "
				+ new File(System.getProperty("java.library.path"))
						.getCanonicalPath());
		System.setProperty("jri.ignore.ule", "yes");
		logBasic("R is installed in R_HOME: " + System.getenv("R_HOME"));
		// System.load("/home/dk/R/x86_64-pc-linux-gnu-library/2.15/rJava/jri/libjri.so");
		System.loadLibrary("jri");
		logBasic("JRI library is found!");
	}

	/**
	 * Process row.
	 */
	public final boolean processRow(final StepMetaInterface smi,
			final StepDataInterface sdi) throws KettleException {
		// Get Input row
		Object[] rowInput = getRow();
		meta = (PRCalcMeta) smi;
		data = (PRCalcData) sdi;
		try {
			// load JRI library
			loadJRILibrary();
			// Initialize output row meta
			if (first) {
				first = false;
				initOutputRowMeta();
				// Initialize lists of input and output variables with values.
				data.setInputVars(new PRVariable[meta.getInputVars().size()]);
				data.setOutputVars(new PRVariable[meta.getOutputVars().size()]);
			}
			PRVariable[] inputVars = data.getInputVars();
			PRVariable[] outputVars = data.getOutputVars();
			if (rowInput == null) { // no more input to be expected...
				// this.logDebug("Input Table: " + meta.isInputTable());
				// this.logDebug("Var: " + inputVars[0]);
				if (meta.isInputTable() && inputVars[0] != null) {
					this.logDebug("Run R for Table.");
					// Run R for whole table
					runR(inputVars, outputVars);
					Object[] rowOutput = new Object[outputVars.length];
					// Add calculated outputs to output
					for (int i = 0; i < outputVars.length; i++) {
						rowOutput[i] = outputVars[i].getValue();
						logDebug("Result: " + outputVars[i].getrName() + "="
								+ outputVars[i].getValue());
					}
					putRow(data.getOutputRowMeta(), rowOutput);
				}
				this.logDebug("No More Rows.");
				setOutputDone();
				return false;
			}
			if (meta.isInputTable()) {
				// Save new values in variables
				initInputOutputVars(rowInput, inputVars, outputVars);
				return true;
			} else {
				// Return also values from input
				Object[] rowOutput = RowDataUtil.resizeArray(rowInput, data
						.getOutputRowMeta().size());
				// Initialize variables
				initInputOutputVars(rowInput, inputVars, outputVars);
				// Run R for every row.
				runR(inputVars, outputVars);
				// Add calculated outputs to output
				for (int i = 0; i < outputVars.length; i++) {
					rowOutput[data.getInputSize() + i] = outputVars[i]
							.getValue();
					logDebug("Result: " + outputVars[i].getrName() + "="
							+ outputVars[i].getValue());
				}
				putRow(data.getOutputRowMeta(), rowOutput);
				return true;
			}
		} catch (Exception e) {
			throw new KettleException("Error by executing R script: "
					+ Arrays.toString(rowInput), e);
		}
	}

	/**
	 * Convert R Type to pentaho type.
	 * 
	 * @param rtype
	 * @return
	 */
	public static int convertRTypeToPentahoType(int rtype) {
		switch (rtype) {
		case PRVariable.TYPE_BOOLEAN:
			return ValueMetaInterface.TYPE_BOOLEAN;
		case PRVariable.TYPE_NUMBER:
			return ValueMetaInterface.TYPE_NUMBER;
		case PRVariable.TYPE_STRING:
			return ValueMetaInterface.TYPE_STRING;
		}
		return ValueMetaInterface.TYPE_STRING;
	}

	/**
	 * Initialize step.
	 */
	public final boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (PRCalcMeta) smi;
		data = (PRCalcData) sdi;
		if (super.init(smi, sdi)) {
			try {
				// this.logDebug("Meta Fields: " + meta.getFields().size());
				return true;
			} catch (Exception e) {
				logError("An error occurred, processing will be stopped: "
						+ e.getMessage());
				setErrors(1);
				stopAll();
			}
		}
		return false;
	}

	/**
	 * Dispose step.
	 */
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}
}

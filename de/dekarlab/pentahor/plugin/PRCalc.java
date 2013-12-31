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
	 * Process row.
	 */
	public final boolean processRow(final StepMetaInterface smi,
			final StepDataInterface sdi) throws KettleException {
		// this also waits for a previous step to be
		// finished.
		Object[] rowInput = getRow();
		if (rowInput == null) { // no more input to be expected...
			this.logDebug("No More Rows.");
			setOutputDone();
			return false;
		}

		try {
			logBasic("Loading JRI library from: "
					+ new File(System.getProperty("java.library.path"))
							.getCanonicalPath());
			System.setProperty("jri.ignore.ule", "yes");
			logBasic("R is installed in R_HOME: " + System.getenv("R_HOME"));
			// System.load("/home/dk/R/x86_64-pc-linux-gnu-library/2.15/rJava/jri/libjri.so");
			System.loadLibrary("jri");
			logBasic("JRI library is found!");

			meta = (PRCalcMeta) smi;
			data = (PRCalcData) sdi;

			if (first) {
				first = false;
				System.setOut(new PRPrintStream(new ByteArrayOutputStream(),
						log));
				System.setErr(new PRPrintStream(new ByteArrayOutputStream(),
						log));
				// Add output variables as columns to result.
				RowMetaInterface outputRowMeta = getInputRowMeta().clone();
				data.setInputSize(outputRowMeta.size());
				ValueMetaInterface valueMeta;
				for (int i = 0; i < meta.getOutputVars().size(); i++) {
					valueMeta = new ValueMeta();
					valueMeta.setName(meta.getOutputVars().get(i)
							.getPentahoName());
					valueMeta.setType(convertRTypeToPentahoType(meta
							.getOutputVars().get(i).getType()));
					outputRowMeta.addValueMeta(valueMeta);
				}
				data.setOutputRowMeta(outputRowMeta);
			}

			Object[] rowOutput = RowDataUtil.resizeArray(rowInput, data
					.getOutputRowMeta().size());

			PRVariable[] inputVars = new PRVariable[meta.getInputVars().size()];
			PRVariable[] outputVars = new PRVariable[meta.getOutputVars()
					.size()];

			// Create input variables with values.
			PRVariable varTemp;
			logDebug("Init Input Vars");
			for (int i = 0; i < meta.getInputVars().size(); i++) {
				varTemp = meta.getInputVars().get(i);
				inputVars[i] = new PRVariable(varTemp.getrName(),
						varTemp.getPentahoName(), varTemp.getType());
				String[] fNames = getInputRowMeta().getFieldNames();
				for (int j = 0; j < fNames.length; j++) {
					if (fNames[j].equals(varTemp.getPentahoName())) {
						logDebug("Name: " + varTemp.getPentahoName()
								+ " Class: "
								+ rowOutput[j].getClass().getCanonicalName());
						inputVars[i].setValue(rowOutput[j]);
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
			// Add calculated outputs to output
			for (int i = 0; i < outputVars.length; i++) {
				rowOutput[data.getInputSize() + i] = outputVars[i].getValue();
				logDebug("Result: " + outputVars[i].getrName() + "="
						+ outputVars[i].getValue());
			}
			putRow(data.getOutputRowMeta(), rowOutput);
		} catch (Error e) {
			throw new KettleException("Error by executing R script: ", e);
		} catch (Exception e) {
			throw new KettleException("Error by executing R script: "
					+ Arrays.toString(rowInput), e);
		}
		return true;
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

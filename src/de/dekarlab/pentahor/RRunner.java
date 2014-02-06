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

import java.io.BufferedReader;
import java.io.FileReader;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 *
 * 
 * 
 */
public class RRunner {
	/**
	 * R Engine.
	 */
	private Rengine re;
	/**
	 * Path to R Script.
	 */
	private String filePath;
	/**
	 * Callback function for R engine.
	 */
	private RMainLoopCallbacks callback;
	/**
	 * Input variables.
	 */
	private PRVariable[] inputVars;
	/**
	 * Output variables.
	 */
	private PRVariable[] outputVars;
	/**
	 * Return expression.
	 */
	private REXP ret;

	private static RRunner instance;
	/**
	 * Is running?
	 */
	private boolean running = false;

	/**
	 * Constructor.
	 * 
	 * @param filePath
	 */
	private RRunner(String filePath, PRVariable[] inputVars,
			PRVariable[] outputVars, RCallback callback) {
		this.filePath = filePath;
		this.inputVars = inputVars;
		this.outputVars = outputVars;
		this.callback = callback;
	}

	/**
	 * Get instance.
	 * 
	 * @param filePath
	 * @param inputVars
	 * @param outputVars
	 * @return
	 * @throws Exception
	 */
	public static RRunner getInstance(String filePath, PRVariable[] inputVars,
			PRVariable[] outputVars) throws Exception {
		return getInstance(filePath, inputVars, outputVars, new RCallback(true));
	}

	/**
	 * Get instance.
	 * 
	 * @param filePath
	 * @param inputVars
	 * @param outputVars
	 * @param callback
	 * @return
	 * @throws Exception
	 */
	public static RRunner getInstance(String filePath, PRVariable[] inputVars,
			PRVariable[] outputVars, RCallback callback) throws Exception {
		if (instance == null) {
			instance = new RRunner(filePath, inputVars, outputVars, callback);
			String[] args = new String[] { "--vanilla" };
			instance.initREngine(args);
		} else {
			instance.inputVars = inputVars;
			instance.outputVars = outputVars;
			instance.callback = callback;
			instance.filePath = filePath;
		}
		return instance;
	}

	/**
	 * Run evaluation.
	 */
	public synchronized void run() throws Exception {
		if (running) {
			throw new Exception("It is possible to have only one R session!");
		}
		try {
			running = true;
			if (callback instanceof RCallback) {
				((RCallback) callback).clearOutput();
			}
			initVariables();
			evaluateFileByString(filePath);

			ret = re.eval("OUTPUT");
			if (ret == null) {
				throw new Exception(
						"The R Script file should contain initialization of OUTPUT list to transfer values to PDI, for example, OUTPUT<-list(\"c\"=c)");
			}
			getVariables();
		} finally {
			running = false;
		}
	}

	/**
	 * Get output.
	 * 
	 * @return
	 */
	public String getOutput() {
		if (callback instanceof RCallback) {
			return ((RCallback) callback).getOutput();
		}
		return "empty";
	}

	/**
	 * Initialize variables.
	 */
	protected void initVariables() {
		// for (PRVariable var : inputVars) {
		// switch (var.getType()) {
		// case PRVariable.TYPE_BOOLEAN:
		// re.assign(var.getrName(),
		// new boolean[] { (Boolean) var.getValue() });
		// break;
		// case PRVariable.TYPE_NUMBER:
		// re.assign(var.getrName(),
		// new double[] { (Double) var.getValue() });
		// break;
		// case PRVariable.TYPE_STRING:
		// re.assign(var.getrName(),
		// new String[] { (String) var.getValue() });
		// break;
		// default:
		// }
		// }

		for (PRVariable var : inputVars) {
			if (var instanceof PRColumnVariable) {
				initColumnVariable((PRColumnVariable) var);
			} else {
				initVariable(var);
			}
		}
	}

	protected void initVariable(PRVariable var) {
		switch (var.getType()) {
		case PRVariable.TYPE_BOOLEAN:
			re.eval(var.getrName() + "<-"
					+ ((Boolean) var.getValue() ? "TRUE" : "FALSE"));
			break;
		case PRVariable.TYPE_NUMBER:
			re.eval(var.getrName() + "<-" + (Double) var.getValue());
			break;
		case PRVariable.TYPE_STRING:
			re.eval(var.getrName() + "<-" + "\"" + var.getValue() + "\"");
			break;
		default:
		}
	}

	/**
	 * Initialize column variables.
	 * 
	 * @param var
	 */
	protected void initColumnVariable(PRColumnVariable var) {
		switch (var.getType()) {
		case PRVariable.TYPE_BOOLEAN:
			Boolean[] valueFrom = var.getValueColumn().toArray(
					new Boolean[var.getValueColumn().size()]);
			boolean[] value = new boolean[valueFrom.length];
			for (int i = 0; i < value.length; i++) {
				value[i] = valueFrom[i];
			}
			re.assign(var.getrName(), value);
			break;
		case PRVariable.TYPE_NUMBER:
			Double[] valueFromD = var.getValueColumn().toArray(
					new Double[var.getValueColumn().size()]);
			double[] valueD = new double[valueFromD.length];
			for (int i = 0; i < valueD.length; i++) {
				valueD[i] = valueFromD[i];
			}
			re.assign(var.getrName(), valueD);
			break;
		case PRVariable.TYPE_STRING:
			re.assign(
					var.getrName(),
					var.getValueColumn().toArray(
							new String[var.getValueColumn().size()]));
			break;
		default:
		}

	}

	/**
	 * Update output variables value.
	 */
	protected void getVariables() throws Exception {
		RList list = ret.asList();
		for (PRVariable var : outputVars) {
			REXP varR = list.at(var.getrName());
			if (varR == null) {
				throw new Exception("Var with name " + var.getrName()
						+ " is not found in OUTPUT.");
			}

			switch (var.getType()) {
			case PRVariable.TYPE_BOOLEAN:
				if (varR.asBool().isTRUE()) {
					var.setValue(true);
				} else {
					var.setValue(false);
				}
				break;
			case PRVariable.TYPE_NUMBER:
				var.setValue(varR.asDouble());
				break;
			case PRVariable.TYPE_STRING:
				var.setValue(varR.asString());
				break;
			default:
			}
		}
	}

	/**
	 * Initialize REngine.
	 * 
	 * @param args
	 */
	protected void initREngine(String[] args) throws Exception {
		// Rengine.DEBUG = 1;
		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			throw new Exception(
					"** Version mismatch - Java files don't match library version.");
		}
		// System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		// (that's the "false" as second argument)
		// 3) the callbacks are implemented by this class
		re = new Rengine(args, false, callback);
		// System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			throw new Exception("Cannot load R");
		}
	}

	/**
	 * Close R Engine.
	 */
	public void closeREngine() {
		re.end();
		// System.out.println("end");
		running = false;
	}

	/**
	 * Evaluate file line by line.
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public void evaluateFileByString(String fileName) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line = br.readLine();
			while (line != null) {
				re.eval(line);
				System.out.println(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

}

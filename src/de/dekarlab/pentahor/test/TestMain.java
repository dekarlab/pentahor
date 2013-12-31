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

package de.dekarlab.pentahor.test;

import de.dekarlab.pentahor.PRVariable;
import de.dekarlab.pentahor.RRunner;

public class TestMain {
	/**
	 * Run r.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			// Initialize input variables
//			PRVariable[] inputVars = new PRVariable[2];
//			inputVars[0] = new PRVariable("a", "a", PRVariable.TYPE_NUMBER,
//					0.001);
//			inputVars[1] = new PRVariable("b", "b", PRVariable.TYPE_NUMBER,
//					0.001);
//			// Initialize output variables
//			PRVariable[] outputVars = new PRVariable[1];
//			outputVars[0] = new PRVariable("c", "c", PRVariable.TYPE_NUMBER);
//			// Initialize R engine.
//			// System.out.println(new File(".").getAbsolutePath());
//			RRunner rr = RRunner.getInstance(
//					"src/de/dekarlab/pentahor/test/test1.r", inputVars,
//					outputVars);
//			// Evaluate
//			rr.run();
//			// Close
//			rr.closeREngine();
//
//			// NEw session
//			inputVars[0] = new PRVariable("a", "a", PRVariable.TYPE_NUMBER,
//					0.003);
//			inputVars[1] = new PRVariable("b", "b", PRVariable.TYPE_NUMBER,
//					0.004);
//			// Show calculated values
//			System.out.println("Value for " + outputVars[0].getrName() + ":"
//					+ outputVars[0].getValue());
//
//			rr = RRunner.getInstance("src/de/dekarlab/pentahor/test/test1.r",
//					inputVars, outputVars);
//			// Evaluate
//			rr.run();
//			// Close
//			rr.closeREngine();

			// ----------------------------------------- STEP
			PRVariable[] outputVars = new PRVariable[2];
			outputVars[0] = new PRVariable("err_gen", "err_gen",
					PRVariable.TYPE_NUMBER);
			outputVars[1] = new PRVariable("msg", "msg", PRVariable.TYPE_STRING);

			RRunner rr = RRunner.getInstance(
					"/home/dk/ShareWise/code/r/Run.R", new PRVariable[0],
					outputVars);
			// Evaluate
			rr.run();
			//System.out.println(rr.getOutput());

			// Close
			rr.closeREngine();
			// Show calculated values
			for (int i = 0; i < outputVars.length; i++) {
				System.out.println("Value for " + outputVars[i].getrName()
						+ ":" + outputVars[i].getValue());
			}
			// ----------------------------------------------------------
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

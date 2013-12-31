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

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class RCallback implements RMainLoopCallbacks {

	private StringBuffer output;
	private boolean sysout;

	public RCallback(boolean sysout) {
		output = new StringBuffer();
		this.sysout = sysout;
	}

	@Override
	public void rBusy(Rengine rEeng, int arg1) {
		output.append("rBusy").append("\n");
		if (sysout) {
			System.out.println("rBusy");
		}
	}

	@Override
	public String rChooseFile(Rengine re, int arg1) {
		output.append("rChooseFile").append("\n");
		if (sysout) {
			System.out.println("rChooseFile");
		}

		return null;
	}

	@Override
	public void rFlushConsole(Rengine re) {
		output.append("rFlushConsole").append("\n");

		if (sysout) {
			System.out.println("rFlushConsole");
		}

	}

	@Override
	public void rLoadHistory(Rengine re, String arg1) {
		output.append("rLoadHistory").append("\n");
		if (sysout) {
			System.out.println("rLoadHistory");
		}

	}

	@Override
	public String rReadConsole(Rengine re, String arg1, int arg2) {
		output.append("rReadConsole").append("\n");
		if (sysout) {
			System.out.println("rReadConsole");
		}

		return null;
	}

	@Override
	public void rSaveHistory(Rengine re, String arg1) {
		output.append("rSaveHistory").append("\n");
		if (sysout) {
			System.out.println("rSaveHistory");
		}

	}

	@Override
	public void rShowMessage(Rengine re, String arg1) {
		output.append("rShowMessage" + arg1).append("\n");
		if (sysout) {
			System.out.println("rShowMessage");
		}

	}

	@Override
	public void rWriteConsole(Rengine re, String arg1, int arg2) {
		output.append(arg1).append("\n");
		if (sysout) {
			System.out.println(arg1);
		}

	}

	public void clearOutput() {
		output = new StringBuffer();
	}

	public String getOutput() {
		return output.toString();
	}

}

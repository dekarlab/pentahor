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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import de.dekarlab.pentahor.PRVariable;

/**
 * Calc R step.
 * 
 */
public class PRCalcMeta extends BaseStepMeta implements StepMetaInterface {

	private String scriptFilePath;
	private List<PRVariable> inputVars;
	private List<PRVariable> outputVars;
	private boolean inputTable;

	/**
	 * Constructor.
	 */
	public PRCalcMeta() {
		super();
	}

	/**
	 * Load XML.
	 */
	public final void loadXML(final Node stepnode,
			final List<DatabaseMeta> databases,
			final Map<String, Counter> counters) throws KettleXMLException {
		readData(stepnode, databases);
	}

	/**
	 * Clone MetaData.
	 * 
	 * @return
	 */
	public final Object clone() {
		PRCalcMeta retval = (PRCalcMeta) super.clone();
		return retval;
	}

	/**
	 * Read meta data from XML.
	 * 
	 * @param stepnode
	 * @param databases
	 * @throws KettleXMLException
	 */
	private void readData(final Node stepnode,
			final List<? extends SharedObjectInterface> databases)
			throws KettleXMLException {
		try {
			scriptFilePath = XMLHandler.getTagValue(stepnode, "scriptfilepath");
			inputTable = false;
			String value = XMLHandler.getTagValue(stepnode, "inputTable");
			if (value != null && value.equals("true")) {
				inputTable = true;
			}
			this.inputVars = new ArrayList<PRVariable>();
			this.outputVars = new ArrayList<PRVariable>();

			Node infields = XMLHandler.getSubNode(stepnode, "inputvars");
			int nrInFields = XMLHandler.countNodes(infields, "inputvar");
			for (int i = 0; i < nrInFields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(infields, "inputvar", i);
				String rName = XMLHandler.getTagValue(fnode, "rname");
				String pentahoName = XMLHandler.getTagValue(fnode,
						"pentahoname");
				int type = Integer.parseInt(XMLHandler.getTagValue(fnode,
						"type"));
				inputVars.add(new PRVariable(rName, pentahoName, type));
			}

			Node outfields = XMLHandler.getSubNode(stepnode, "outputvars");
			int nrOutFields = XMLHandler.countNodes(outfields, "outputvar");
			for (int i = 0; i < nrOutFields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(outfields, "outputvar",
						i);
				String rName = XMLHandler.getTagValue(fnode, "rname");
				String pentahoName = XMLHandler.getTagValue(fnode,
						"pentahoname");
				int type = Integer.parseInt(XMLHandler.getTagValue(fnode,
						"type"));
				outputVars.add(new PRVariable(rName, pentahoName, type));
			}
		} catch (Exception e) {
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	/**
	 * Get XML for this step.
	 * 
	 * @return
	 */
	public final String getXML() {
		StringBuffer retval = new StringBuffer();
		retval.append("    ").append(
				XMLHandler.addTagValue("scriptfilepath", scriptFilePath));
		retval.append("    ").append(
				XMLHandler.addTagValue("inputTable", (inputTable ? "true"
						: "false")));
		retval.append("    <inputvars>").append(Const.CR);
		for (PRVariable field : inputVars) {
			retval.append("      <inputvar>").append(Const.CR);
			retval.append("        ").append(
					XMLHandler.addTagValue("rname", field.getrName()));
			retval.append("        ").append(
					XMLHandler.addTagValue("pentahoname",
							field.getPentahoName()));
			retval.append("        ").append(
					XMLHandler.addTagValue("type", field.getType()));
			retval.append("      </inputvar>").append(Const.CR);
		}
		retval.append("    </inputvars>").append(Const.CR);

		retval.append("    <outputvars>").append(Const.CR);
		for (PRVariable field : outputVars) {
			retval.append("      <outputvar>").append(Const.CR);
			retval.append("        ").append(
					XMLHandler.addTagValue("rname", field.getrName()));
			retval.append("        ").append(
					XMLHandler.addTagValue("pentahoname",
							field.getPentahoName()));
			retval.append("        ").append(
					XMLHandler.addTagValue("type", field.getType()));
			retval.append("      </outputvar>").append(Const.CR);
		}
		retval.append("    </outputvars>").append(Const.CR);

		return retval.toString();
	}

	/**
	 * Read step from repository.
	 * 
	 * @param rep
	 * @param idStep
	 * @param databases
	 * @param counters
	 * @throws KettleException
	 */
	public void readRep(Repository rep, ObjectId idStep,
			List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		try {
			this.scriptFilePath = rep.getStepAttributeString(idStep,
					"scriptfilepath");

			inputTable = false;
			String value = rep.getStepAttributeString(idStep, "inputTable");
			if (value != null && value.equals("true")) {
				inputTable = true;
			}

			int nrInFields = rep
					.countNrStepAttributes(idStep, "inputvar_rname");
			for (int i = 0; i < nrInFields; i++) {
				String rName = rep.getStepAttributeString(idStep, i,
						"inputvar_rname");
				String pentahoName = rep.getStepAttributeString(idStep, i,
						"inputvar_pentahoname");
				int type = Integer.parseInt(rep.getStepAttributeString(idStep,
						i, "inputvar_type"));
				inputVars.add(new PRVariable(rName, pentahoName, type));
			}
			int nrOutFields = rep.countNrStepAttributes(idStep,
					"outputvar_rname");
			for (int i = 0; i < nrOutFields; i++) {
				String rName = rep.getStepAttributeString(idStep, i,
						"outputvar_rname");
				String pentahoName = rep.getStepAttributeString(idStep, i,
						"outputvar_pentahoname");
				int type = Integer.parseInt(rep.getStepAttributeString(idStep,
						i, "outputvar_type"));
				outputVars.add(new PRVariable(rName, pentahoName, type));
			}

		} catch (Exception e) {
			throw new KettleException(
					"Unexpected error reading step information from the repository",
					e);
		}
	}

	/**
	 * Save object to Repository.
	 * 
	 * @param rep
	 * @param idTransformation
	 * @param idStep
	 * @throws KettleException
	 */
	public void saveRep(Repository rep, ObjectId idTransformation,
			ObjectId idStep) throws KettleException {
		try {
			rep.saveStepAttribute(idTransformation, idStep, "scriptfilepath",
					scriptFilePath);
			rep.saveStepAttribute(idTransformation, idStep, "inputTable",
					(inputTable ? "true" : "false"));

			for (int i = 0; i < inputVars.size(); i++) {
				rep.saveStepAttribute(idTransformation, idStep, i,
						"inputvar_rname", inputVars.get(i).getrName());
				rep.saveStepAttribute(idTransformation, idStep, i,
						"inputvar_pentahoname", inputVars.get(i)
								.getPentahoName());
				rep.saveStepAttribute(idTransformation, idStep, i,
						"inputvar_type", inputVars.get(i).getType());
			}

			for (int i = 0; i < outputVars.size(); i++) {
				rep.saveStepAttribute(idTransformation, idStep, i,
						"outputvar_rname", outputVars.get(i).getrName());
				rep.saveStepAttribute(idTransformation, idStep, i,
						"outputvar_pentahoname", outputVars.get(i)
								.getPentahoName());
				rep.saveStepAttribute(idTransformation, idStep, i,
						"outputvar_type", outputVars.get(i).getType());
			}

		} catch (Exception e) {
			throw new KettleException(
					"Unable to save step information to the repository for idStep="
							+ idStep, e);
		}
	}

	/**
	 * Get fields for next step.
	 * 
	 * @param row
	 * @param origin
	 * @param info
	 * @param nextStep
	 * @param space
	 * @throws KettleStepException
	 */
	public final void getFields(final RowMetaInterface row,
			final String origin, final RowMetaInterface[] info,
			final StepMeta nextStep, final VariableSpace space)
			throws KettleStepException {
		// append the outputFields to the output
		for (int i = 0; i < outputVars.size(); i++) {
			ValueMetaInterface v = new ValueMeta(outputVars.get(i)
					.getPentahoName(),
					PRCalc.convertRTypeToPentahoType(outputVars.get(i)
							.getType()));
			v.setOrigin(origin);
			row.addValueMeta(v);
		}
	}

	/**
	 * Check the step MetaData.
	 * 
	 * @param remarks
	 * @param transMeta
	 * @param stepMeta
	 * @param prev
	 * @param input
	 * @param output
	 * @param info
	 */
	public final void check(final List<CheckResultInterface> remarks,
			final TransMeta transMeta, final StepMeta stepMeta,
			final RowMetaInterface prev, final String input[],
			final String output[], final RowMetaInterface info) {
		CheckResult cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK,
				"OK", stepMeta);
		remarks.add(cr);
	}

	/**
	 * Get step.
	 * 
	 * @param stepMeta
	 * @param stepDataInterface
	 * @param cnr
	 * @param transMeta
	 * @param trans
	 * @return
	 */
	public final StepInterface getStep(final StepMeta stepMeta,
			final StepDataInterface stepDataInterface, final int cnr,
			final TransMeta transMeta, final Trans trans) {
		return new PRCalc(stepMeta, stepDataInterface, cnr, transMeta, trans);
	}

	/**
	 * Get dialog.
	 * 
	 * @param shell
	 * @param meta
	 * @param transMeta
	 * @param name
	 * @return
	 */
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta,
			TransMeta transMeta, String name) {
		return new PRCalcDialog(shell, meta, transMeta, name);
	}

	/**
	 * Get step data.
	 * 
	 * @return
	 */
	public final StepDataInterface getStepData() {
		try {
			return new PRCalcData();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get used DB connections.
	 */
	public final DatabaseMeta[] getUsedDatabaseConnections() {
		return super.getUsedDatabaseConnections();
	}

	/**
	 * Set default values.
	 */
	@Override
	public void setDefault() {
		scriptFilePath = "file.r";
		inputVars = new ArrayList<PRVariable>();
		outputVars = new ArrayList<PRVariable>();
	}

	/**
	 * Get script file path.
	 * 
	 * @return
	 */
	public String getScriptFilePath() {
		return scriptFilePath;
	}

	/**
	 * Set script file path.
	 * 
	 * @param scriptFilePath
	 */
	public void setScriptFilePath(String scriptFilePath) {
		this.scriptFilePath = scriptFilePath;
	}

	/**
	 * Get input vars.
	 * 
	 * @return
	 */
	public List<PRVariable> getInputVars() {
		return inputVars;
	}

	/**
	 * Set input vars.
	 * 
	 * @param inputVars
	 */
	public void setInputVars(List<PRVariable> inputVars) {
		this.inputVars = inputVars;
	}

	/**
	 * Get output vars.
	 * 
	 * @return
	 */
	public List<PRVariable> getOutputVars() {
		return outputVars;
	}

	/**
	 * Set output vars.
	 * 
	 * @param outputVars
	 */
	public void setOutputVars(List<PRVariable> outputVars) {
		this.outputVars = outputVars;
	}

	public boolean isInputTable() {
		return inputTable;
	}

	public void setInputTable(boolean inputTable) {
		this.inputTable = inputTable;
	}

}

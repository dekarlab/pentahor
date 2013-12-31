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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import de.dekarlab.pentahor.PRVariable;

/**
 * GUI for calculator.
 */
public class PRCalcDialog extends BaseStepDialog implements StepDialogInterface {
	/**
	 * Package name.
	 */
	private static Class<?> PKG = PRCalcMeta.class; // for i18n purposes

	/**
	 * Label.
	 */
	private Label wlScriptFilePath;
	/**
	 * Path to script file.
	 */
	private TextVar wScriptFilePath;
	/**
	 * Input variables.
	 */
	private Label wlInputVars;

	/**
	 * Input variables.
	 */
	private TableView wInputVars;
	/**
	 * Output variables.
	 */
	private TableView wOutputVars;

	/**
	 * Output variables.
	 */
	private Label wlOutputVars;

	/**
	 * Meta Data for Step.
	 */
	private PRCalcMeta meta;

	/**
	 * All fields from the previous steps, used for dropdown selection.
	 */
	private RowMetaInterface prevFields = null;

	/**
	 * Field column.
	 */
	private ColumnInfo fieldColumnInputVars = null;

	/**
	 * Input variables.
	 */
	private Link wlDevelopedBy;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 * @param baseStepMeta
	 * @param transMeta
	 * @param stepname
	 */
	public PRCalcDialog(Shell parent, Object baseStepMeta, TransMeta transMeta,
			String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		meta = (PRCalcMeta) baseStepMeta;
	}

	/**
	 * Open dialog.
	 */
	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
				| SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};
		backupChanged = meta.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "PRCalcDialog.Title") + " "
				+ BaseMessages.getString(PKG, "PRCalcDialog.Version"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		/*************************************************
		 * STEP NAME ENTRY
		 *************************************************/

		// Step Name line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname
				.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		/*************************************************
		 * Properties group.
		 *************************************************/
		Group gProps = new Group(shell, SWT.SHADOW_ETCHED_IN);
		gProps.setText(BaseMessages.getString(PKG, "PRCalcDialog.Group"));

		FormLayout gLayout = new FormLayout();
		gLayout.marginWidth = 5;
		gLayout.marginHeight = 5;
		gProps.setLayout(gLayout);
		props.setLook(gProps);

		FormData fdgProps = new FormData();
		fdgProps.top = new FormAttachment(wStepname, margin);
		fdgProps.right = new FormAttachment(100, 0);
		fdgProps.left = new FormAttachment(0, 0);
		fdgProps.bottom = new FormAttachment(90, -margin);

		gProps.setLayoutData(fdgProps);

		// /*************************************************
		// * Path To Script Path
		// *************************************************/
		wlScriptFilePath = new Label(gProps, SWT.RIGHT);
		wlScriptFilePath.setText(BaseMessages.getString(PKG,
				"PRCalcDialog.FileName"));
		props.setLook(wlScriptFilePath);

		FormData fdlScriptFilePath = new FormData();
		fdlScriptFilePath.top = new FormAttachment(0, margin);
		fdlScriptFilePath.left = new FormAttachment(0, 0);
		fdlScriptFilePath.right = new FormAttachment(middle, -margin);
		wlScriptFilePath.setLayoutData(fdlScriptFilePath);

		wScriptFilePath = new TextVar(transMeta, gProps, SWT.SINGLE | SWT.LEFT
				| SWT.BORDER);
		wScriptFilePath.addModifyListener(lsMod);
		wScriptFilePath.setToolTipText(BaseMessages.getString(PKG,
				"PRCalcDialog.FileName.Tooltip"));
		props.setLook(wScriptFilePath);

		FormData fdScriptFilePath = new FormData();
		fdScriptFilePath.top = new FormAttachment(0, margin);
		fdScriptFilePath.left = new FormAttachment(middle, 0);
		fdScriptFilePath.right = new FormAttachment(100, 0);
		wScriptFilePath.setLayoutData(fdScriptFilePath);

		/*************************************************
		 * Input variables table
		 *************************************************/

		wlInputVars = new Label(gProps, SWT.NONE);
		wlInputVars.setText(BaseMessages.getString(PKG,
				"PRCalcDialog.InputVars"));
		props.setLook(wlInputVars);
		FormData fdlInputVars = new FormData();
		fdlInputVars.left = new FormAttachment(0, 0);
		fdlInputVars.top = new FormAttachment(wScriptFilePath, margin);
		wlInputVars.setLayoutData(fdlInputVars);

		int inputVarWidgetCols = 3;
		int inputVarWidgetRows = (meta.getInputVars() != null ? meta
				.getInputVars().size() : 1);

		ColumnInfo[] ciInputVars = new ColumnInfo[inputVarWidgetCols];
		ciInputVars[0] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.InputVar.Column.PName"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] {}, false);
		ciInputVars[1] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.InputVar.Column.RName"),
				ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciInputVars[2] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.InputVar.Column.RType"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, false);
		ciInputVars[2].setComboValues(getTypeComboValues());

		fieldColumnInputVars = ciInputVars[0];

		wInputVars = new TableView(transMeta, gProps, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
				ciInputVars, inputVarWidgetRows, lsMod, props);
		props.setLook(wInputVars);
		
		FormData fdInputVars = new FormData();
		fdInputVars.left = new FormAttachment(0, margin);
		fdInputVars.top = new FormAttachment(wlInputVars, margin);
		fdInputVars.right = new FormAttachment(100, -margin);
		fdInputVars.bottom = new FormAttachment(50, -margin);

		wInputVars.setLayoutData(fdInputVars);

		/*************************************************
		 * Output variables table
		 *************************************************/

		wlOutputVars = new Label(gProps, SWT.NONE);
		wlOutputVars.setText(BaseMessages.getString(PKG,
				"PRCalcDialog.OutputVars"));
		props.setLook(wlOutputVars);
		FormData fdlOutputVars = new FormData();
		fdlOutputVars.top = new FormAttachment(wInputVars, margin);
		fdlOutputVars.left = new FormAttachment(0, margin);

		wlOutputVars.setLayoutData(fdlOutputVars);

		int outputVarWidgetCols = 3;
		int outputVarWidgetRows = (meta.getOutputVars() != null ? meta
				.getOutputVars().size() : 1);

		ColumnInfo[] ciOutputVars = new ColumnInfo[outputVarWidgetCols];
		ciOutputVars[0] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.OutputVar.Column.RName"),
				ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciOutputVars[1] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.OutputVar.Column.PName"),
				ColumnInfo.COLUMN_TYPE_TEXT, false);
		ciOutputVars[2] = new ColumnInfo(BaseMessages.getString(PKG,
				"PRCalcDialog.OutputVar.Column.RType"),
				ColumnInfo.COLUMN_TYPE_CCOMBO, false);
		ciOutputVars[2].setComboValues(getTypeComboValues());

		wOutputVars = new TableView(transMeta, gProps, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL,
				ciOutputVars, outputVarWidgetRows, lsMod, props);
		props.setLook(wOutputVars);

		FormData fdOutputVars = new FormData();
		fdOutputVars.top = new FormAttachment(wlOutputVars, margin);
		fdOutputVars.left = new FormAttachment(0, margin);
		fdOutputVars.right = new FormAttachment(100, -margin);
		fdOutputVars.bottom = new FormAttachment(100, -margin);
		wOutputVars.setLayoutData(fdOutputVars);

		/*************************************************
		 * // OK AND CANCEL BUTTONS
		 *************************************************/

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		Button wHelp = new Button(shell, SWT.PUSH);
		wHelp.setText(BaseMessages.getString(PKG, "PRCalcDialog.Button.Help"));

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK,
				wCancel, wHelp }, margin, gProps);

		// Add listeners
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};
		// Add listeners
		Listener lsHelp = new Listener() {
			public void handleEvent(Event e) {
				showHelp();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		wHelp.addListener(SWT.Selection, lsHelp);

		// /*************************************************
		// * Installation notes.
		// *************************************************/
		// wlInstallNote = new Label(shell, SWT.LEFT | SWT.BORDER | SWT.FILL);
		// props.setLook(wlInstallNote);
		// FormData fdInstallNote = new FormData();
		// fdInstallNote.top = new FormAttachment(wCancel, 5);
		// fdInstallNote.left = new FormAttachment(0, 5);
		// fdInstallNote.right = new FormAttachment(100, -5);
		// wlInstallNote.setLayoutData(fdInstallNote);

		/*************************************************
		 * Developed By
		 *************************************************/
		wlDevelopedBy = new Link(shell, SWT.RIGHT | SWT.FILL);
		wlDevelopedBy.setText(BaseMessages.getString(PKG,
				"PRCalcDialog.DevelopedBy"));
		props.setLook(wlDevelopedBy);
		FormData fdDevelopedBy = new FormData();
		fdDevelopedBy.top = new FormAttachment(wHelp, 5);
		fdDevelopedBy.right = new FormAttachment(100, -5);
		wlDevelopedBy.setLayoutData(fdDevelopedBy);

		/*************************************************
		 * // DEFAULT ACTION LISTENERS
		 *************************************************/

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		// wScriptFilePath.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set the shell size, based upon previous time...
		setSize();

		/*************************************************
		 * // POPULATE AND OPEN DIALOG
		 *************************************************/

		loadGuiFromModel();
		setPossibleInputFields();

		meta.setChanged(backupChanged);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	/**
	 * Update notes in label. Show help.
	 */
	protected void showHelp() {
		String notes = "";
		// 1. Install rJava package in R by executing:
		// install.packages("rJava")
		notes = BaseMessages.getString(PKG, "PRCalcDialog.InstallNote.Step1")
				+ "\n";
		// 2. Specify path to rJava/jri/jri.dll or rJava/jri/libjri.so:
		try {
			notes += BaseMessages.getString(PKG,
					"PRCalcDialog.InstallNote.Step2")
					+ "\n"
					+ new File(System.getProperty("java.library.path"))
							.getCanonicalPath() + "\n";
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 3. Specify location of R using R_HOME environment variable.
		notes += BaseMessages.getString(PKG, "PRCalcDialog.InstallNote.Step3")
				+ "\n";
		// The current value of R_HOME is:
		notes += BaseMessages
				.getString(PKG, "PRCalcDialog.InstallNote.Step3.1")
				+ " "
				+ System.getenv("R_HOME") + "\n";
		// 4. To get the values from R session it is needed to use variable
		notes += BaseMessages.getString(PKG, "PRCalcDialog.InstallNote.Step4")
				+ "\n";
		// with the name OUTPUT in the script, for example: OUTPUT =
		// list("c"=c).
		notes += BaseMessages
				.getString(PKG, "PRCalcDialog.InstallNote.Step4.1") + "\n";
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION
				| SWT.OK);
		messageBox.setText("Help");
		messageBox.setMessage(notes);
		messageBox.open();
	}

	/**
	 * Get type combo values.
	 * 
	 * @return
	 */
	protected static String[] getTypeComboValues() {
		String[] values = new String[] { "String", "Number", "Boolean" };
		return values;
	}

	/**
	 * Load GUI from model.
	 */
	public void loadGuiFromModel() {
		wStepname.selectAll();
		if (meta.getScriptFilePath() != null) {
			wScriptFilePath.setText(meta.getScriptFilePath());
		}

		// Input variables
		if (meta.getInputVars() != null) {
			for (int i = 0; i < meta.getInputVars().size(); i++) {
				TableItem item = wInputVars.table.getItem(i);
				if (meta.getInputVars().get(i).getPentahoName() != null) {
					item.setText(1, meta.getInputVars().get(i).getPentahoName());
				}
				if (meta.getInputVars().get(i).getrName() != null) {
					item.setText(2, meta.getInputVars().get(i).getrName());
				}
				item.setText(3, varTypeToStr(meta.getInputVars().get(i)
						.getType()));
			}
		}
		wInputVars.setRowNums();
		wInputVars.optWidth(true);

		// Output variables
		if (meta.getOutputVars() != null) {
			for (int i = 0; i < meta.getOutputVars().size(); i++) {
				TableItem item = wOutputVars.table.getItem(i);
				if (meta.getOutputVars().get(i).getrName() != null) {
					item.setText(1, meta.getOutputVars().get(i).getrName());
				}
				if (meta.getOutputVars().get(i).getPentahoName() != null) {
					item.setText(2, meta.getOutputVars().get(i)
							.getPentahoName());
				}
				item.setText(3, varTypeToStr(meta.getOutputVars().get(i)
						.getType()));
			}
		}
		wOutputVars.setRowNums();
		wOutputVars.optWidth(true);
	}

	/**
	 * Set values for fields in ComboBox.
	 */
	private void setPossibleInputFields() {
		Runnable fieldLoader = new Runnable() {
			public void run() {
				try {
					prevFields = transMeta.getPrevStepFields(stepname);
				} catch (KettleException e) {
					prevFields = new RowMeta();
					String msg = BaseMessages.getString(PKG,
							"PRCalcDialog.Err.UnableToFindFields");
					logError(msg);
				}
				if (prevFields != null) {
					String[] prevStepFieldNames = prevFields.getFieldNames();
					Arrays.sort(prevStepFieldNames);
					fieldColumnInputVars.setComboValues(prevStepFieldNames);
				}
			}
		};
		new Thread(fieldLoader).start();
	}

	/**
	 * Variable type to number.
	 * 
	 * @param type
	 * @return
	 */
	protected String varTypeToStr(int type) {
		switch (type) {
		case PRVariable.TYPE_BOOLEAN:
			return "Boolean";
		case PRVariable.TYPE_STRING:
			return "String";
		case PRVariable.TYPE_NUMBER:
			return "Number";
		}
		return "String";
	}

	/**
	 * Variable type to string.
	 * 
	 * @param type
	 * @return
	 */
	protected int varTypeToInt(String type) {
		if (type.equals("Number")) {
			return PRVariable.TYPE_NUMBER;
		} else if (type.equals("String")) {
			return PRVariable.TYPE_STRING;
		} else if (type.equals("Boolean")) {
			return PRVariable.TYPE_BOOLEAN;
		}
		return PRVariable.TYPE_STRING;
	}

	/**
	 * Cancel button action.
	 */
	private void cancel() {
		stepname = null;
		meta.setChanged(backupChanged);
		dispose();
	}

	/**
	 * OK button action.
	 */
	private void ok() {
		stepname = wStepname.getText();
		meta.setScriptFilePath(wScriptFilePath.getText());
		// Input variables.
		int nrInputVars = wInputVars.nrNonEmpty();
		List<PRVariable> inputVars = new ArrayList<PRVariable>();
		PRVariable var;
		for (int i = 0; i < nrInputVars; i++) {
			TableItem item = wInputVars.getNonEmpty(i);
			var = new PRVariable(item.getText(2), item.getText(1),
					varTypeToInt(item.getText(3)));
			inputVars.add(var);
		}
		meta.setInputVars(inputVars);

		// Output variables.
		int nrOutputVars = wOutputVars.nrNonEmpty();
		List<PRVariable> outputVars = new ArrayList<PRVariable>();
		for (int i = 0; i < nrOutputVars; i++) {
			TableItem item = wOutputVars.getNonEmpty(i);
			var = new PRVariable(item.getText(1), item.getText(2),
					varTypeToInt(item.getText(3)));
			outputVars.add(var);
		}
		meta.setOutputVars(outputVars);

		dispose();
	}
}

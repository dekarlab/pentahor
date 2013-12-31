package de.dekarlab.pentahor.test;

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;

import de.dekarlab.pentahor.plugin.PRCalcDialog;
import de.dekarlab.pentahor.plugin.PRCalcMeta;

public class TestDlg {

	public static void main(String[] args) {
		try {
			KettleEnvironment.init(false);
			EnvUtil.environmentInit();
			Display d = new Display();
			PropsUI.init(d, Props.TYPE_PROPERTIES_SPOON);
			Shell shell = new Shell(d);
			FormLayout layout = new FormLayout();
			layout.marginWidth = 3;
			layout.marginHeight = 3;
			shell.setLayout(layout);
			TransMeta transMeta = new TransMeta();
			PRCalcDialog dlg = new PRCalcDialog(shell, new PRCalcMeta(),
					transMeta, "");
			dlg.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

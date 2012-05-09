package jcf.gen.eclipse.core.preference;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfGenPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public JcfGenPreferencePage() {
		super(GRID);
		
		setPreferenceStore(JcfGeneratorPlugIn.getDefault().getPreferenceStore());
		setDescription(MessageUtil.getMessage("preference.title.description"));
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		
//		parent.setLayout(new GridLayout(1, true));
//		parent.setLayoutData(new GridData());
//		
//		Group dbGroup = new Group(parent, SWT.NONE);
//		
//		dbGroup.setText("DataBase Info");
//		dbGroup.setLayout(new GridLayout(1, false));
//		dbGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		FileFieldEditor dbPropery = new FileFieldEditor(Constants.DB_PROPERTY_FILE, "Db property file", parent);
		addField(dbPropery);
		
		DirectoryFieldEditor srcDir = new DirectoryFieldEditor(Constants.SOURCE_DIRECTORY, "Source path", parent);
		addField(srcDir);
		
		DirectoryFieldEditor vmDir = new DirectoryFieldEditor(Constants.VM_DIRECTORY, "Template path", parent);
		addField(vmDir);
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
}

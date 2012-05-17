package jcf.gen.eclipse.core.preference;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class JcfConnectionPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public JcfConnectionPreferencePage() {
		super(FLAT);
		
		setPreferenceStore(JcfGeneratorPlugIn.getDefault().getPreferenceStore());
		setDescription(MessageUtil.getMessage("preference.conn.description"));
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		Group dbCategoryGroup = new Group(main, SWT.NONE);
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbCategoryGroup);
		
		String[][] dbCategory = {{Constants.DB_ORACLE, Constants.DB_ORACLE}};
		
		RadioGroupFieldEditor dbCategoryEditor = new RadioGroupFieldEditor(Constants.DB_CATEGORY_RADIO, 
				MessageUtil.getMessage("preference.db.category.desc"),
				dbCategory.length, 
				dbCategory, 
				dbCategoryGroup);
		
		addField(dbCategoryEditor);
		
		updateMargin(dbCategoryGroup);
		
		Group dbGroup = new Group(main, SWT.NONE);
		dbGroup.setText(MessageUtil.getMessage("preference.group.db"));
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbGroup);
		
		StringFieldEditor driverClassNameEditor = new StringFieldEditor(Constants.DB_DRIVER_CLASS, 
				MessageUtil.getMessage("preference.db.driver.name"), dbGroup);
		driverClassNameEditor.getLabelControl(dbGroup).setToolTipText(MessageUtil.getMessage("preference.db.driver.name.tooltip"));
		addField(driverClassNameEditor);
		
		StringFieldEditor driverUrlEditor = new StringFieldEditor(Constants.DB_URL, 
				MessageUtil.getMessage("preference.db.driver.url"), dbGroup);
		driverUrlEditor.getLabelControl(dbGroup).setToolTipText(MessageUtil.getMessage("preference.db.driver.url.tooltip"));
		addField(driverUrlEditor);
		
		StringFieldEditor driverUsernameEditor = new StringFieldEditor(Constants.DB_USERNAME, 
				MessageUtil.getMessage("preference.db.driver.username"), dbGroup);
		addField(driverUsernameEditor);
		
		StringFieldEditor driverPasswordEditor = new StringFieldEditor(Constants.DB_PASSWORD, 
				MessageUtil.getMessage("preference.db.driver.password"), dbGroup);
		driverPasswordEditor.getTextControl(dbGroup).setEchoChar('*');
		addField(driverPasswordEditor);
		
		updateMargin(dbGroup);
	}
	
	private void updateMargin(Group group) {
		GridLayout layout = (GridLayout) group.getLayout();
		
		layout.marginWidth = 5;
		layout.marginHeight = 5;
	}
}

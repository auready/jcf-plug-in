package jcf.gen.eclipse.core.preference;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class JcfLicensePreferencePage extends FieldEditorPreferencePage implements 
		IWorkbenchPreferencePage {

	public JcfLicensePreferencePage() {
		super(FLAT);
		
		setPreferenceStore(JcfGeneratorPlugIn.getDefault().getPreferenceStore());
	}
	
	public void init(IWorkbench workbench) {
	}
	
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		Group licenseGroup = new Group(main, SWT.COLOR_DARK_GRAY);
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(licenseGroup);
		
		Label licenseLabel = new Label(licenseGroup, SWT.NONE);	
		licenseLabel.setText(MessageUtil.getMessage("preference.license.description"));
		
		updateMargin(licenseGroup);
	}
	
	private void updateMargin(Composite group) {
		GridLayout layout = new GridLayout();
		
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = 5;
	    layout.verticalSpacing = 5;
		
		group.setLayout(layout);
	}
}

package jcf.gen.eclipse.core.preference.page;

import jcf.gen.eclipse.core.preference.AbstractJcfPreferencePage;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class LicensePreferencePage extends AbstractJcfPreferencePage {

	public LicensePreferencePage() {
	}
	
	@Override
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		Group licenseGroup = new Group(main, SWT.COLOR_DARK_GRAY);
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(licenseGroup);
		
		Label licenseLabel = new Label(licenseGroup, SWT.NONE);	
		licenseLabel.setText(MessageUtil.getMessage("preference.license.description"));
		
		updateMarginGroup(licenseGroup);
	}
	
	protected void updateMarginGroup(Composite group) {
		GridLayout layout = new GridLayout();
		
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = 5;
	    layout.verticalSpacing = 5;
		
		group.setLayout(layout);
	}
}

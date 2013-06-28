package jcf.gen.eclipse.core.preference.page;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.preference.AbstractJcfPreferencePage;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfGenPreferencePage extends AbstractJcfPreferencePage {

	public JcfGenPreferencePage() {
		super(GRID);
		
		setDescription(MessageUtil.getMessage("preference.title.description"));
	}
	
	@Override
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
//		Group dbGroup = new Group(main, SWT.NONE);
//		
//		dbGroup.setText(MessageUtil.getMessage("preference.group.db"));
//		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbGroup);
//		
//		FileFieldEditor dbFileEditor = 
//				new FileFieldEditor(Constants.DB_PROPERTY_FILE, 
//						MessageUtil.getMessage("preference.db.file"), dbGroup);
//		
//		addField(dbFileEditor);
//		
//		updateMargin(dbGroup);
		
		Group templateGroup = new Group(main, SWT.COLOR_DARK_GRAY);
		
		templateGroup.setText(MessageUtil.getMessage("preference.group.template"));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(templateGroup);
		
		DirectoryFieldEditor templateEditor = 
				new DirectoryFieldEditor(Constants.TEMPLATE_DIRECTORY, 
						MessageUtil.getMessage("preference.template.path"), templateGroup) {
			@Override
			protected boolean doCheckState() {
				String fileName = getTextControl().getText().trim();
				
				if ((fileName.length() == 0) && isEmptyStringAllowed()) {
					return true;
				}
				
				File file = new File(fileName);
				
				return (!file.exists()) || (file.isDirectory());
			}
			
			@Override
			protected void createControl(Composite parent) {
				super.setValidateStrategy(VALIDATE_ON_KEY_STROKE);
				super.createControl(parent);
				
			}
		};
		
		updateMargin(templateGroup);
		
		templateEditor.setEmptyStringAllowed(false);
		templateEditor.getLabelControl(templateGroup).setToolTipText(MessageUtil.getMessage("preference.template.path.tooltip"));
		
		addField(templateEditor);
		
		Group srcGroup = new Group(main, SWT.COLOR_DARK_GRAY);
		
		srcGroup.setText(MessageUtil.getMessage("preference.group.source"));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(srcGroup);
		
		DirectoryFieldEditor srcEditor = 
				new DirectoryFieldEditor(Constants.SOURCE_DIRECTORY, 
						MessageUtil.getMessage("preference.source.path"), srcGroup) {
			@Override
			protected void createControl(Composite parent) {
				super.setValidateStrategy(VALIDATE_ON_KEY_STROKE);
				super.createControl(parent);
			}
		};
		
		updateMargin(srcGroup);
		
		srcEditor.setEmptyStringAllowed(true);
		srcEditor.getLabelControl(srcGroup).setToolTipText(MessageUtil.getMessage("preference.source.path.tooltip"));
		
		addField(srcEditor);
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
}

package jcf.gen.eclipse.core.preference;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
	
	public void init(IWorkbench workbench) {
	}

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
		
		Group vmFileGroup = new Group(main, SWT.COLOR_DARK_GRAY);
		
		vmFileGroup.setText(MessageUtil.getMessage("preference.group.velocity"));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(vmFileGroup);
		
		addField(new BooleanFieldEditor(Constants.CONTROLLER_FILE, 
				MessageUtil.getMessage("preference.velocity.controller"), vmFileGroup));
		
		addField(new BooleanFieldEditor(Constants.SERVICE_FILE, 
				MessageUtil.getMessage("preference.velocity.service"), vmFileGroup));
		
		addField(new BooleanFieldEditor(Constants.MODEL_FILE, 
				MessageUtil.getMessage("preference.velocity.model"), vmFileGroup));
		
		addField(new BooleanFieldEditor(Constants.SQLMAP_FILE, 
				MessageUtil.getMessage("preference.velocity.sqlmap"), vmFileGroup));
		
		updateMargin(vmFileGroup);
	}
	
	private void updateMargin(Group group) {
		GridLayout layout = (GridLayout) group.getLayout();
		
		layout.marginWidth = 5;
		layout.marginHeight = 5;
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
}

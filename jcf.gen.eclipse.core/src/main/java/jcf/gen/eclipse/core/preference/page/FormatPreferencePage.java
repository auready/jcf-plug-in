package jcf.gen.eclipse.core.preference.page;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.preference.AbstractJcfPreferencePage;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class FormatPreferencePage extends AbstractJcfPreferencePage {

	public FormatPreferencePage() {
		setDescription(MessageUtil.getMessage("preference.format.description"));
	}
	
	@Override
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		// file choose
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
		
		// author
		Group authorGroup = new Group(main, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(authorGroup);
		
		addField(getStringFieldEditor(Constants.AUTHOR, MessageUtil.getMessage("preference.format.author"), "", authorGroup));
		
		updateMargin(authorGroup);
		
		// tab
		TabFolder tabFolder = new TabFolder(main, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		addPkgNameTab(tabFolder);
		
		addFileNameTab(tabFolder);
	}
	
	private void addPkgNameTab(TabFolder tabFolder) {
		Composite page = new Composite(tabFolder, SWT.NONE);
		
		page.setLayout(new GridLayout(1, false));
		page.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		
		tabItem.setText(MessageUtil.getMessage("preference.group.package"));
		tabItem.setControl(page);
		
		Group pkgNameGroup = new Group(page, SWT.COLOR_DARK_GRAY);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(pkgNameGroup);
		
		addField(getStringFieldEditor(Constants.CONTROLLER_PKG_NAME, MessageUtil.getMessage("preference.format.package.controller"), "", pkgNameGroup));
		addField(getStringFieldEditor(Constants.SERVICE_PKG_NAME, MessageUtil.getMessage("preference.format.package.service"), "", pkgNameGroup));
		addField(getStringFieldEditor(Constants.MODEL_PKG_NAME, MessageUtil.getMessage("preference.format.package.model"), "", pkgNameGroup));
		addField(getStringFieldEditor(Constants.SQLMAP_PKG_NAME, MessageUtil.getMessage("preference.format.package.sqlmap"), "", pkgNameGroup));
		
		updateMargin(pkgNameGroup);
	}
	
	private void addFileNameTab(TabFolder tabFolder) {
		Composite page = new Composite(tabFolder, SWT.NONE);
		
		page.setLayout(new GridLayout(1, false));
		page.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		
		tabItem.setText(MessageUtil.getMessage("preference.group.filename"));
		tabItem.setControl(page);
		
		Group fileNameGroup = new Group(page, SWT.COLOR_DARK_GRAY);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(fileNameGroup);
		
		addField(getStringFieldEditor(Constants.CONTROLLER_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.controller"), MessageUtil.getMessage("preference.format.file.controller.tooltip"), fileNameGroup));
		
		addField(getStringFieldEditor(Constants.ISERVICE_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.iservice"), MessageUtil.getMessage("preference.format.file.iservice.tooltip"), fileNameGroup));
		
		addField(getStringFieldEditor(Constants.SERVICE_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.service"), MessageUtil.getMessage("preference.format.file.service.tooltip"), fileNameGroup));
		
		addField(getStringFieldEditor(Constants.MODEL_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.model"), MessageUtil.getMessage("preference.format.file.model.tooltip"), fileNameGroup));
		
		addField(getStringFieldEditor(Constants.SQLMAP_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.sqlmap"), MessageUtil.getMessage("preference.format.file.sqlmap.tooltip"), fileNameGroup));
		
		updateMargin(fileNameGroup);
	}
	
	private StringFieldEditor getStringFieldEditor(String name, String label, String toolTip, Composite parent) {
		StringFieldEditor fieldEditor = new StringFieldEditor(name, label, parent);
		
		if (StringUtils.isNotEmpty(toolTip)) {
			fieldEditor.getLabelControl(parent).setToolTipText(toolTip);
		}
		
		return fieldEditor;
	}
}
package jcf.gen.eclipse.core.preference;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

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
		
		StringFieldEditor author = new StringFieldEditor(Constants.AUTHOR, 
				MessageUtil.getMessage("preference.format.author"), authorGroup);
		addField(author);
		
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
		
		Group pkgNameGroup = new Group(page, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(pkgNameGroup);
		
		StringFieldEditor controllerPkgName = new StringFieldEditor(Constants.CONTROLLER_PKG_NAME, 
				MessageUtil.getMessage("preference.format.package.controller"), pkgNameGroup);
		addField(controllerPkgName);
		
		StringFieldEditor servicePkgName = new StringFieldEditor(Constants.SERVICE_PKG_NAME, 
				MessageUtil.getMessage("preference.format.package.service"), pkgNameGroup);
		addField(servicePkgName);
		
		StringFieldEditor modelPkgName = new StringFieldEditor(Constants.MODEL_PKG_NAME, 
				MessageUtil.getMessage("preference.format.package.model"), pkgNameGroup);
		addField(modelPkgName);
		
		StringFieldEditor sqlmapPkgName = new StringFieldEditor(Constants.SQLMAP_PKG_NAME, 
				MessageUtil.getMessage("preference.format.package.sqlmap"), pkgNameGroup);
		addField(sqlmapPkgName);
		
		updateMargin(pkgNameGroup);
	}
	
	private void addFileNameTab(TabFolder tabFolder) {
		Composite page = new Composite(tabFolder, SWT.NONE);
		
		page.setLayout(new GridLayout(1, false));
		page.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		
		tabItem.setText(MessageUtil.getMessage("preference.group.filename"));
		tabItem.setControl(page);
		
		Group fileNameGroup = new Group(page, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(fileNameGroup);
		
		StringFieldEditor controllerFileName = new StringFieldEditor(Constants.CONTROLLER_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.controller"), fileNameGroup);
		controllerFileName.getLabelControl(fileNameGroup).setToolTipText(MessageUtil.getMessage("preference.format.file.controller.tooltip"));
		addField(controllerFileName);
		
		StringFieldEditor iServiceFileName = new StringFieldEditor(Constants.ISERVICE_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.iservice"), fileNameGroup);
		iServiceFileName.getLabelControl(fileNameGroup).setToolTipText(MessageUtil.getMessage("preference.format.file.iservice.tooltip"));
		addField(iServiceFileName);
		
		StringFieldEditor serviceFileName = new StringFieldEditor(Constants.SERVICE_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.service"), fileNameGroup);
		serviceFileName.getLabelControl(fileNameGroup).setToolTipText(MessageUtil.getMessage("preference.format.file.service.tooltip"));
		addField(serviceFileName);
		
		StringFieldEditor modelFileName = new StringFieldEditor(Constants.MODEL_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.model"), fileNameGroup);
		modelFileName.getLabelControl(fileNameGroup).setToolTipText(MessageUtil.getMessage("preference.format.file.model.tooltip"));
		addField(modelFileName);
		
		StringFieldEditor sqlmapFileName = new StringFieldEditor(Constants.SQLMAP_FILE_NAME, 
				MessageUtil.getMessage("preference.format.file.sqlmap"), fileNameGroup);
		sqlmapFileName.getLabelControl(fileNameGroup).setToolTipText(MessageUtil.getMessage("preference.format.file.sqlmap.tooltip"));
		addField(sqlmapFileName);
		
		updateMargin(fileNameGroup);
	}
}
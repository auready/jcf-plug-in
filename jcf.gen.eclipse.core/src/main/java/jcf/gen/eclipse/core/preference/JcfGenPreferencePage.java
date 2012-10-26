package jcf.gen.eclipse.core.preference;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfGenPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	private TableViewer viewer;
	private Table table;
	private Button addButton;
	private Button removeButton;
	
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
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		Group dbGroup = new Group(main, SWT.NONE);
		
		dbGroup.setText(MessageUtil.getMessage("preference.group.db"));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbGroup);
		
		FileFieldEditor schemaFileEditor = 
				new FileFieldEditor(Constants.SCHEMA_PROPERTY_FILE, 
						MessageUtil.getMessage("preference.db.schema"), dbGroup);
		
		addField(schemaFileEditor);
		
		FileFieldEditor dbFileEditor = 
				new FileFieldEditor(Constants.DB_PROPERTY_FILE, 
						MessageUtil.getMessage("preference.db.file"), dbGroup);
		
		addField(dbFileEditor);
		
		updateMargin(dbGroup);
		
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
		
		this.createVmTemplateGroup(main);
		
		performDefaults();
	}
	/*
	@Override
	protected void performDefaults() {
		table.removeAll();
		
		super.performDefaults();
	}
	
	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();

		StringBuffer id = new StringBuffer();
		StringBuffer path = new StringBuffer();
		
		TableItem[] items = table.getItems();
		
		for (int i = 0; i < items.length; i++) {
			id.append(items[i].getText(0)).append("\n");
			path.append(items[i].getText(1)).append("\n");
		}
		
		store.setValue(Constants.DB_ID, id.toString());
		store.setValue(Constants.DB_PATH, path.toString());
		
		return super.performOk();
	}
	*/
	private void createDbFileList(Composite parent) {
		GridLayout layout = new GridLayout();
	    
		layout.marginWidth = 0;
	    layout.marginHeight = 0;
	    layout.numColumns = 2;
		
	    parent.setLayout(layout);
	    parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
		viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

		this.table = viewer.getTable();
		
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.table.setEnabled(true);
		
		this.table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean enable = false;
				TableItem[] items = JcfGenPreferencePage.this.table.getItems();
				
				if (items.length > 0) {
					enable = true;
				}

				JcfGenPreferencePage.this.removeButton.setEnabled(enable);
			}
		});
				
		TableColumn propNameCol = new TableColumn(table, SWT.LEFT);
		
		propNameCol.setText("Name");
		propNameCol.setWidth(150);
		
		TableColumn propPathCol = new TableColumn(table, SWT.LEFT);
		
		propPathCol.setText("Path");
		propPathCol.setWidth(300);
		
		this.createButtons(parent);
	}
	
	private void createButtons(Composite parent) {
		GridLayout layout = new GridLayout();
	    
	    layout.marginHeight = 0;
	    layout.marginWidth = 0;
		
		Composite buttons = new Composite(parent, 0);
		
	    buttons.setLayoutData(new GridData(2));
	    buttons.setLayout(layout);
		
		addButton = new Button(buttons, SWT.PUSH);
		
		addButton.setText("Add..");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DbPathDialog dialog = new DbPathDialog(JcfGenPreferencePage.this.getShell());
				
				if (dialog.open() == 0) {
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(new String[] {dialog.getId(), dialog.getPath()});
				}
			}
		});
		
		removeButton = new Button(buttons, SWT.PUSH);
		
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int[] indices = table.getSelectionIndices();
				table.remove(indices);
			}
		});
	}
	
	private void createVmTemplateGroup(Composite parent) {
		Group vmFileGroup = new Group(parent, SWT.COLOR_DARK_GRAY);
		
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
		
		addField(new BooleanFieldEditor(Constants.GROOVY_FILE, 
				MessageUtil.getMessage("preference.velocity.groovy"), vmFileGroup));
		
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
	
	private class DbPathDialog extends Dialog {
		private Text idText;
		private Text pathText;
		
		private String id = "";
		private String path = "";
		
		public DbPathDialog(Shell parent) {
			super(parent);
			setShellStyle(SWT.TITLE | SWT.CLOSE);
		}
		
		public DbPathDialog(Shell parent, String id, String path) {
			super(parent);
			setShellStyle(SWT.TITLE | SWT.CLOSE);
			
			this.id = id;
			this.path = path;
		}
		
		protected void constrainShellSize() {
			Shell shell = DbPathDialog.this.getShell();
			shell.pack();
			shell.setSize(400, shell.getSize().y);
		}
		
		protected Control createDialogArea(Composite parent) {
			DbPathDialog.this.getShell().setText("Db Property File");
			
			Composite container = new Composite(parent, SWT.NONE);
		    
			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			container.setLayout(new GridLayout(3, false));
			
			Label label = new Label(container, SWT.NONE);
			label.setText("DB ID:");
			
			this.idText = new Text(container, SWT.BORDER);
			this.idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.idText.setText(this.id);
			
			label = new Label(container, SWT.NONE);
			
			label = new Label(container, SWT.NONE);
			label.setText("PATH:");
			
			this.pathText = new Text(container, SWT.BORDER);
			this.pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.pathText.setText(this.path);
			
			Button filePathButton = new Button(container, SWT.PUSH);
			filePathButton.setText("...");
			filePathButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					FileDialog openDialog = new FileDialog(DbPathDialog.this.getShell(), SWT.OPEN);
					
					String openFile = openDialog.open();
					if (openFile != null) {
						pathText.setText(openFile);
					}
				}
			});
			
			return container;
		}
		
		protected void okPressed() {
			if (StringUtils.isEmpty(this.idText.getText())) {
				openAlertDialog("DB ID");
				return;
			}
			
			if (StringUtils.isEmpty(this.pathText.getText())) {
				openAlertDialog("DB FILE PATH");
				return;
			}
			
			this.id = this.idText.getText();
			this.path = this.pathText.getText();
			
			super.okPressed();
		}
		
		public String getId() {
			return this.id;
		}
		
		public String getPath() {
			return this.path;
		}
		
		private void openAlertDialog(String message) {
			MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			
			box.setMessage(message);
			box.setText("Warning");
			box.open();
		}
	}
}

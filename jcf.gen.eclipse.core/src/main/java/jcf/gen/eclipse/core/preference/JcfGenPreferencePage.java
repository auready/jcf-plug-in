package jcf.gen.eclipse.core.preference;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
		
//		FileFieldEditor dbFileEditor = 
//				new FileFieldEditor(Constants.DB_PROPERTY_FILE, 
//						MessageUtil.getMessage("preference.db.file"), dbGroup);
//		
//		addField(dbFileEditor);

		this.createDbFileList(dbGroup);
		
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
		
		addField(new BooleanFieldEditor(Constants.GROOVY_FILE, 
				MessageUtil.getMessage("preference.velocity.groovy"), vmFileGroup));
		
		updateMargin(vmFileGroup);
	}
	
	@Override
	protected void performDefaults() {
		
	}
	
	@Override
	public boolean performOk() {
		
		return true;
	}
	
	private void createDbFileList(Composite parent) {
		GridLayout layout = new GridLayout();
	    
		layout.marginWidth = 0;
	    layout.marginHeight = 0;
	    layout.numColumns = 2;
		
	    parent.setLayout(layout);
	    parent.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
		viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		
		table = viewer.getTable();
		
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean enable = false;
				TableItem[] items = table.getItems();
				
				if (items.length > 0) {
					enable = true;
				}

				removeButton.setEnabled(enable);
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
				
			}
		});
		
		removeButton = new Button(buttons, SWT.PUSH);
		
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				
				
			}
		});
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
		
		private String id;
		private String path;
		
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
		
		
		
		
		
		public String getId() {
			return this.id;
		}
		
		public String getPath() {
			return this.path;
		}
	}
}

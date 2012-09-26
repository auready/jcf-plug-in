package jcf.gen.eclipse.core.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.jdbc.model.TableColumns;
import jcf.gen.eclipse.core.jdbc.DatabaseService;
import jcf.gen.eclipse.core.luncher.DefaultLuncher;
import jcf.gen.eclipse.core.utils.ColumnNameCamelCaseMap;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfCodeGenTitleDialog extends TitleAreaDialog {
	private Text txtSrcFolder = null;
	private Text txtPackageName = null;
	private Text txtUserCase = null;
	private Text txtSql = null;
	
	private DatabaseService databaseService;
	
	private Map<String, Object> argument = new HashMap<String, Object>();
	private Set<String> delArgument = new HashSet<String>();
	private Map<String, Boolean> templateArg = new HashMap<String, Boolean>();
	
	private TabFolder tabFolder;
	private TableViewer tableViewer;
	
	private String srcPath = "";
	private String packageName = "";
	private String userCaseName = "";
	private String query = "";
	
	private String tabName = MessageUtil.getMessage("tab.table.title");
	
	public JcfCodeGenTitleDialog(Shell parent) {
		super(parent);
		setShellStyle(SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		
		shell.setText(MessageUtil.getMessage("dialog.shell.title"));
//		shell.setImage(JcfGeneratorPlugIn.getImageDescriptor("icons/sample.gif").createImage());
	}
	
	@Override
	public void create() {
		super.create();
		
		setTitle(MessageUtil.getMessage("dialog.area.title"));
		setMessage(MessageUtil.getMessage("dialog.area.message"));
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 600);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.databaseService = new DatabaseService();
		
		this.createTabContents(container);
		
		this.createCodeGroup(container);
		
		//Separator
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return area;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button generateButton = createButton(parent, OK, MessageUtil.getMessage("button.default.generator"), false);
		
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				generateSourceCode();
			}
		});
		
		Button cancelButton = createButton(parent, CANCEL, MessageUtil.getMessage("button.default.cancel"), false);
		
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	
	protected void createTabContents(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);
		
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Table Base Tab
		TabItem tableTabItem = new TabItem(tabFolder, SWT.NONE);
		Composite pageOne = new Composite(tabFolder, SWT.NONE);
		
		pageOne.setLayout(new GridLayout(1, false));
		pageOne.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableTabItem.setText(MessageUtil.getMessage("tab.table.title"));
		tableTabItem.setControl(pageOne);
		
		this.createDbGroup(pageOne);
		this.createTemplateGroup(pageOne);
		
		// Query Base Tab
		TabItem sqlTabItem = new TabItem(tabFolder, SWT.NONE);
		Composite pageTwo = new Composite(tabFolder, SWT.NONE);
		
		pageTwo.setLayout(new GridLayout(1, false));
		pageTwo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sqlTabItem.setText(MessageUtil.getMessage("tab.query.title"));
		sqlTabItem.setControl(pageTwo);
		
		this.createSqlGroup(pageTwo);
		
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				tabName = tabFolder.getSelection()[0].getText();
			}
		});
	}
	
	protected void createDbGroup(Composite parent) {
		//Database Properties
		Group groupDbInfo = new Group(parent, SWT.NONE);
		
		groupDbInfo.setText(MessageUtil.getMessage("group.db.setting.title"));
		groupDbInfo.setLayout(new GridLayout(2, false));
		groupDbInfo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//DB Table
		Label labelDbTable = new Label(groupDbInfo, SWT.NONE);
		
		labelDbTable.setLayoutData(this.getLabelLayout());
		labelDbTable.setText(MessageUtil.getMessage("label.db.table"));
		
		//ComboViewer
		final Combo comboTabName = new Combo(groupDbInfo, SWT.READ_ONLY);
		
		comboTabName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboTabName.setEnabled(false);
		
		String dbFilePath = this.getDbPropertyFilePath();
		
		if (StringUtils.isNotEmpty(dbFilePath)) {
			comboTabName.setEnabled(true);
						
			String[] dbTableNames = databaseService.getTableNames();
			
			comboTabName.setItems(dbTableNames);
		}
			
		comboTabName.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnNameCamelCaseMap camelCaseStr = new ColumnNameCamelCaseMap();
				String tableName = comboTabName.getItem(comboTabName.getSelectionIndex());
				
				String camelCaseTableName = camelCaseStr.tableNameConvert(tableName);
				
				txtUserCase.setText(camelCaseTableName); 
				userCaseName = camelCaseTableName;
				
				//Table Contents Change
				List<TableColumns> list = databaseService.getColumnList(tableName);
				
				tableViewer.getTable().setEnabled(true);
				tableViewer.setInput(list);
				
				((CheckboxTableViewer) tableViewer).setAllChecked(true);
				
				//Argument
				if (list.size() > 0) {
					argument.clear();
					
					argument.put(Constants.TABLENAME, tableName);
					argument.put(Constants.COLUMNS, list);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		//Column
		Label labelTabCol = new Label(groupDbInfo, SWT.NONE);
		
		labelTabCol.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		labelTabCol.setText("");
				
		TableColumnViewer tableColumnViewer = new TableColumnViewer(groupDbInfo);
		
		tableViewer = tableColumnViewer.createTableViewer();
		
		tableViewer.getTable().setEnabled(false);
		
		tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = event.gc.getFontMetrics().getHeight() * 2;
			}
		});
		
		tableViewer.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					String tempName = event.item.toString();
					String colName = tempName.substring(tempName.indexOf("{") + 1, tempName.indexOf("}"));
					
					if (delArgument.contains(colName)) {
						delArgument.remove(colName);
					} else {
						delArgument.add(colName);
					}
				}
			}
		});
		
	}
	
	protected void createSqlGroup(Composite parent) {
		Group groupSqlInfo = new Group(parent, SWT.NONE);
		
		groupSqlInfo.setText(MessageUtil.getMessage("group.sql.setting.title"));
		groupSqlInfo.setLayout(new GridLayout(1, false));
		groupSqlInfo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label labelDbTable = new Label(groupSqlInfo, SWT.NONE);
		
		labelDbTable.setLayoutData(this.getLabelLayout());
		labelDbTable.setText(MessageUtil.getMessage("label.sql.text"));
		
		txtSql = new Text(groupSqlInfo, SWT.BORDER | SWT.MULTI);
		
		txtSql.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtSql.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtSql.getText().length() > 0) {
					query = txtSql.getText().trim();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	protected void createTemplateGroup(Composite parent) {
		final Group groupTemplate = new Group(parent, SWT.NONE);
		
		groupTemplate.setText("Template");
		groupTemplate.setLayout(new GridLayout(5, false));
		groupTemplate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		templateArg.put(Constants.CONTROLLER_FILE, isCheckedTemplate(Constants.CONTROLLER_FILE));
		templateArg.put(Constants.SERVICE_FILE, isCheckedTemplate(Constants.SERVICE_FILE));
		templateArg.put(Constants.MODEL_FILE, isCheckedTemplate(Constants.MODEL_FILE));
		templateArg.put(Constants.SQLMAP_FILE, isCheckedTemplate(Constants.SQLMAP_FILE));
		templateArg.put(Constants.GROOVY_FILE, isCheckedTemplate(Constants.GROOVY_FILE));
		
		final Button[] checkTemplate = new Button[5];
		
		checkTemplate[0] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[0].setText("Controller");
		checkTemplate[0].setSelection(isCheckedTemplate(Constants.CONTROLLER_FILE));
		checkTemplate[0].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				templateArg.put(Constants.CONTROLLER_FILE, checkTemplate[0].getSelection());
			}
		});
		
		checkTemplate[1] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[1].setText("Service");
		checkTemplate[1].setSelection(isCheckedTemplate(Constants.SERVICE_FILE));
		checkTemplate[1].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				templateArg.put(Constants.SERVICE_FILE, checkTemplate[1].getSelection());
			}
		});
		
		checkTemplate[2] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[2].setText("Model");
		checkTemplate[2].setSelection(isCheckedTemplate(Constants.MODEL_FILE));
		checkTemplate[2].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				templateArg.put(Constants.MODEL_FILE, checkTemplate[2].getSelection());
			}
		});
		
		checkTemplate[3] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[3].setText("Sql-Map");
		checkTemplate[3].setSelection(isCheckedTemplate(Constants.SQLMAP_FILE));
		checkTemplate[3].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				templateArg.put(Constants.SQLMAP_FILE, checkTemplate[3].getSelection());
			}
		});
		
		checkTemplate[4] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[4].setText("Groovy");
		checkTemplate[4].setSelection(isCheckedTemplate(Constants.GROOVY_FILE));
		checkTemplate[4].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				templateArg.put(Constants.GROOVY_FILE, checkTemplate[4].getSelection());
			}
		});
	}
	
	private boolean isCheckedTemplate(String category) {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore().getBoolean(category);
	}
	
	protected void createCodeGroup(Composite parent) {
		//Code Properties
		final Group groupInfo = new Group(parent, SWT.NONE);
		
		groupInfo.setText(MessageUtil.getMessage("group.code.setting.title"));
		groupInfo.setLayout(new GridLayout(3, false));
		groupInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//Source Folder
		Label labelSrcFolder = new Label(groupInfo, SWT.NONE);
		
		labelSrcFolder.setLayoutData(this.getLabelLayout());
		labelSrcFolder.setText(MessageUtil.getMessage("label.code.src.folder"));
		
		txtSrcFolder = new Text(groupInfo, SWT.BORDER);
		
		txtSrcFolder.setLayoutData(this.getTextLayout2());
		txtSrcFolder.setText("");
		srcPath = "";
		
		Button btnSelectDir = new Button(groupInfo, SWT.PUSH);
		
		btnSelectDir.setText(MessageUtil.getMessage("button.directory.dialog"));
		btnSelectDir.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSelectDir.setEnabled(true);
		
		btnSelectDir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(groupInfo.getShell());
				
				directoryDialog.setMessage(MessageUtil.getMessage("dialog.directory.message"));
				
				String dir = directoryDialog.open();
				
				if (dir != null) {
					txtSrcFolder.setText(dir);
					srcPath = dir;
				}
			}
		});
		
		String sourceDir = this.getSourceDiretory();
		
		if (StringUtils.isNotEmpty(sourceDir)) {
			txtSrcFolder.setText(sourceDir);
			
			srcPath = sourceDir;
		}
		
		//Package Name
		Label labelPackage = new Label(groupInfo, SWT.NONE);
		
		labelPackage.setLayoutData(this.getLabelLayout());
		labelPackage.setText(MessageUtil.getMessage("label.code.src.package"));
				
		txtPackageName = new Text(groupInfo, SWT.BORDER);
		txtPackageName.setLayoutData(this.getTextLayout());
		
		txtPackageName.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtPackageName.getText().length() > 0) {
					packageName = txtPackageName.getText();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		//UserCase Name
		Label labelUserCase = new Label(groupInfo, SWT.NONE);
		
		labelUserCase.setLayoutData(this.getLabelLayout());
		labelUserCase.setText(MessageUtil.getMessage("label.code.src.usercase"));
		
		txtUserCase = new Text(groupInfo, SWT.BORDER);
		txtUserCase.setLayoutData(this.getTextLayout());
		
		txtUserCase.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtUserCase.getText().length() > 0) {
					String first = txtUserCase.getText().substring(0, 1);
					
					if (first.toUpperCase() != first) {
						setMessage("Usercase Name start with an uppercase letter");
					} 
					
					userCaseName = txtUserCase.getText();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	private GridData getLabelLayout() {
		GridData gridData = new GridData();
		
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		
		return gridData;
	}
	
	private GridData getTextLayout() {
		GridData gridData = new GridData();
		
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 2;
		gridData.verticalSpan = 1;
		
		return gridData;
	}
	
	private GridData getTextLayout2() {
		GridData gridData = new GridData();
		
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		
		return gridData;
	}
	
	private String getDbPropertyFilePath() {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore().getString(Constants.DB_PROPERTY_FILE);
	}
	
	private String getSourceDiretory() {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore().getString(Constants.SOURCE_DIRECTORY);
	}
	
	private void generateSourceCode() {
		DefaultLuncher luncher = new DefaultLuncher();
		
		if (tabName.equals(MessageUtil.getMessage("tab.table.title"))) {
			argument.put(Constants.TEMPLATE_CHECK, templateArg);
			
			luncher.execute(srcPath, packageName, userCaseName, argument, delArgument);
			
		} else {
			templateArg.put(Constants.CONTROLLER_FILE, false);
			templateArg.put(Constants.SERVICE_FILE, false);
			templateArg.put(Constants.MODEL_FILE, true);
			templateArg.put(Constants.SQLMAP_FILE, false);
			templateArg.put(Constants.GROOVY_FILE, false);
			
			argument.put(Constants.TEMPLATE_CHECK, templateArg);
			
			List<TableColumns> list = databaseService.getQueryMetaData(query.toUpperCase());
			
			argument.put(Constants.COLUMNS, list);
			
			luncher.execute(srcPath, packageName, userCaseName, argument);
		}
	}
}

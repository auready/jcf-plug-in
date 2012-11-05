package jcf.gen.eclipse.core.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Display;
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
import jcf.gen.eclipse.core.utils.FileUtils;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfCodeGenTitleDialog extends TitleAreaDialog {
	private Map<String, Object> argument = new HashMap<String, Object>();
	private Set<String> delArgument = new HashSet<String>();
	private Map<String, Boolean> templateArg = new HashMap<String, Boolean>();
	
	private DatabaseService databaseService;
	
	private TabFolder tabFolder;
	private TableViewer tableViewer;
	private Combo comboTabName;
	private Combo comboTopCategory;
	private Text txtSql = null;
	private Text txtSrcFolder = null;
	private Text txtMidCategory;
	private Text txtSmallCategory;
	private Text txtShortCategory;
	private Text txtBizNameCategory;
	private Text txtAuthorCategory;
	private Text txtMethodCategory;
	
	private Text txtActionClass = null;
	private Text txtServiceClass = null;
	private Text txtModelClass = null;
	private Text txtGroovyClass = null;
	
	private String schema = "";
	private String srcPath = "";
	private String query = "";
	private String author = "";
	private String bizName = "";
	private String bizAbbr = "";
	private String methodName = "";
	private String camelCaseTableName = "";
	
	private String actionPkgName = "";
	private String servicePkgName = "";
	private String modelPkgName = "";
	private String groovyPkgName = "";
	
	private String[] TOP_CATEGORY = {MessageUtil.getMessage("ko.code.top.category.com"), 
									 MessageUtil.getMessage("ko.code.top.category.haksa"),
									 MessageUtil.getMessage("ko.code.top.category.huas"),
									 MessageUtil.getMessage("ko.code.top.category.research"),
									 MessageUtil.getMessage("ko.code.top.category.aff")};
	
	private String tabName = MessageUtil.getMessage("tab.table.title");
	
	public JcfCodeGenTitleDialog(Shell parent) {
		super(parent);
		setShellStyle(SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(MessageUtil.getMessage("dialog.shell.title"));
	}
	
	@Override
	public void create() {
		super.create();
		
		setTitle(MessageUtil.getMessage("dialog.area.title"));
		setMessage(MessageUtil.getMessage("dialog.area.message"));
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 750);
	}
	
	@Override
	protected Point getInitialLocation(Point initPoint) {
		Display display = Display.getCurrent();
		
		int x = (display.getClientArea().width - getInitialSize().x) / 2;
		int y = (display.getClientArea().height - getInitialSize().y) / 2;
		
		return new Point(x, y);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(JcfGeneratorPlugIn.getImageDescriptor("icons/logo.png").createImage());
		
		Composite area = (Composite) super.createDialogArea(parent);		
		Composite container = new Composite(area, SWT.NONE);
		
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.createSchemaGroup(container);
		
		this.createTabContents(container);
		
		this.createTemplateGroup(container);
		
		this.createCodeGroup(container);
		
		this.createBizGroup(container);
		
		this.createClassGroup(container);
		
		//Separator
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return area;
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		Button previewButton = createButton(parent, IDialogConstants.PROCEED_ID, 
				MessageUtil.getMessage("button.default.preview"), false);
		
		previewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				makeCodePreviewDialog(parent);
			}
		});
		
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
	
	protected void makeCodePreviewDialog(final Composite parent) {
		this.addArgInfo();
		
		CodePreviewDialog preview = new CodePreviewDialog(parent.getShell());
		
		preview.open(argument, delArgument);
	}
	
	protected void createSchemaGroup(Composite parent) {
		Group groupSchema = new Group(parent, SWT.NONE);
		
		groupSchema.setLayout(new GridLayout(2, false));
		groupSchema.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label labelDbTable = new Label(groupSchema, SWT.NONE);
		
		labelDbTable.setLayoutData(this.getLabelLayout());
		labelDbTable.setText(MessageUtil.getMessage("label.db.schema"));
		
		final Combo comboSchmea = new Combo(groupSchema, SWT.READ_ONLY);
		
		comboSchmea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		if (StringUtils.isNotEmpty(getPreferenceStore(Constants.DB_PROPERTY_FILE))) {
			databaseService = new DatabaseService();
			
			comboSchmea.setEnabled(true);
			comboSchmea.setItems(databaseService.getSchmeaList());
		} else {
			comboSchmea.setEnabled(false);
		}
		
		comboSchmea.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				schema = comboSchmea.getItem(comboSchmea.getSelectionIndex());
				
				comboTabName.setEnabled(true);
				comboTabName.setItems(databaseService.getTableNames(schema));
				
				tableViewer.setInput(null);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
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
		comboTabName = new Combo(groupDbInfo, SWT.READ_ONLY);
		
		comboTabName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboTabName.setEnabled(false);
			
		comboTabName.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnNameCamelCaseMap camelCaseStr = new ColumnNameCamelCaseMap();
				String tableName = comboTabName.getItem(comboTabName.getSelectionIndex());
				
				camelCaseTableName = camelCaseStr.tableNameConvert(tableName);
				
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
		groupTemplate.setLayout(new GridLayout(4, false));
		groupTemplate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		templateArg.put(Constants.CONTROLLER_FILE, isCheckedTemplate(Constants.CONTROLLER_FILE));
		templateArg.put(Constants.SERVICE_FILE, isCheckedTemplate(Constants.SERVICE_FILE));
		templateArg.put(Constants.MODEL_FILE, isCheckedTemplate(Constants.MODEL_FILE));
		templateArg.put(Constants.GROOVY_FILE, isCheckedTemplate(Constants.GROOVY_FILE));
		
		final Button[] checkTemplate = new Button[4];
		
		checkTemplate[0] = new Button(groupTemplate, SWT.CHECK);
		checkTemplate[0].setText("Action");
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
		checkTemplate[3].setText("Groovy");
		checkTemplate[3].setSelection(isCheckedTemplate(Constants.GROOVY_FILE));
		checkTemplate[3].addSelectionListener(new SelectionAdapter() {
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
		
		groupInfo.setText(MessageUtil.getMessage("group.category.setting.title"));
		groupInfo.setLayout(new GridLayout(6, false));
		groupInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//Source Folder
		Label labelSrcFolder = new Label(groupInfo, SWT.NONE);
		
		labelSrcFolder.setLayoutData(this.getLabelLayout());
		labelSrcFolder.setText(MessageUtil.getMessage("label.code.src.folder"));
		
		txtSrcFolder = new Text(groupInfo, SWT.BORDER);
		
		txtSrcFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
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
		
		String sourceDir = this.getPreferenceStore(Constants.SOURCE_DIRECTORY);
		
		if (StringUtils.isNotEmpty(sourceDir)) {
			txtSrcFolder.setText(sourceDir);
			srcPath = sourceDir;
		}
	}
	
	protected void createBizGroup(Composite parent) {
		final Group groupInfo = new Group(parent, SWT.NONE);
		
		groupInfo.setLayout(new GridLayout(6, false));
		groupInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//Large Category
		Label labelTopCategory = new Label(groupInfo, SWT.NONE);
		
		labelTopCategory.setLayoutData(this.getLabelLayout());
		labelTopCategory.setText(MessageUtil.getMessage("ko.title.top.category"));
		
		comboTopCategory = new Combo(groupInfo, SWT.READ_ONLY);
		
		comboTopCategory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboTopCategory.setEnabled(true);
		comboTopCategory.setItems(this.TOP_CATEGORY);
		comboTopCategory.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String category = comboTopCategory.getItem(comboTopCategory.getSelectionIndex());
				
				if (category.contains("COM")) {
					txtMidCategory.setText("comm");
				} else {
					txtMidCategory.setText("");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
			
		// Mid Category
		Label labelMidCategory = new Label(groupInfo, SWT.NONE);
		
		labelMidCategory.setLayoutData(this.getLabelLayout());
		labelMidCategory.setText(MessageUtil.getMessage("ko.title.middle.category"));
		
		txtMidCategory = new Text(groupInfo, SWT.BORDER);
		txtMidCategory.setLayoutData(this.getOneSpanTextLayout());
		
		// Small Category
		Label labelSmallCategory = new Label(groupInfo, SWT.NONE);
		
		labelSmallCategory.setLayoutData(this.getLabelLayout());
		labelSmallCategory.setText(MessageUtil.getMessage("ko.title.small.category"));
		
		txtSmallCategory = new Text(groupInfo, SWT.BORDER);
		txtSmallCategory.setLayoutData(this.getOneSpanTextLayout());
		
		// Short Category
		Label labelShortCategory = new Label(groupInfo, SWT.NONE);
		
		labelShortCategory.setLayoutData(this.getLabelLayout());
		labelShortCategory.setText(MessageUtil.getMessage("ko.title.bizabbr.category"));
		
		txtShortCategory = new Text(groupInfo, SWT.BORDER);
		txtShortCategory.setLayoutData(this.getOneSpanTextLayout());
		txtShortCategory.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				bizAbbr = txtShortCategory.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Biz Name Category
		Label labelBizNameCategory = new Label(groupInfo, SWT.NONE);
		
		labelBizNameCategory.setLayoutData(this.getLabelLayout());
		labelBizNameCategory.setText(MessageUtil.getMessage("ko.title.bizname.category"));
		
		txtBizNameCategory = new Text(groupInfo, SWT.BORDER);
		txtBizNameCategory.setLayoutData(this.getOneSpanTextLayout());
		txtBizNameCategory.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				bizName = txtBizNameCategory.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Author
		Label labelAuthorCategory = new Label(groupInfo, SWT.NONE);
		
		labelAuthorCategory.setLayoutData(this.getLabelLayout());
		labelAuthorCategory.setText(MessageUtil.getMessage("ko.title.author.category"));
		
		txtAuthorCategory = new Text(groupInfo, SWT.BORDER);
		txtAuthorCategory.setLayoutData(this.getOneSpanTextLayout());
		txtAuthorCategory.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				author = txtAuthorCategory.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Method
		Label labelMethodCategory = new Label(groupInfo, SWT.NONE);
		
		labelMethodCategory.setLayoutData(this.getLabelLayout());
		labelMethodCategory.setText(MessageUtil.getMessage("ko.title.method.category"));
		
		txtMethodCategory = new Text(groupInfo, SWT.BORDER);
		txtMethodCategory.setLayoutData(this.getOneSpanTextLayout());
		txtMethodCategory.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				methodName = txtMethodCategory.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		Label tmp = new Label(groupInfo, SWT.NONE);
		tmp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		// Button
		Button btnInitClassName = new Button(groupInfo, SWT.PUSH);
		
		btnInitClassName.setText(MessageUtil.getMessage("ko.btn.class.init"));
		btnInitClassName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		btnInitClassName.setEnabled(true);
		
		btnInitClassName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				comboTopCategory.setItems(TOP_CATEGORY);
				
				txtMidCategory.setText("");
				txtSmallCategory.setText("");
				txtShortCategory.setText("");
				txtBizNameCategory.setText("");
				txtAuthorCategory.setText("");
				txtMethodCategory.setText("");
				
				txtActionClass.setText("");
				txtServiceClass.setText("");
				txtModelClass.setText("");
				txtGroovyClass.setText("");
			}
		});
		
		Button btnCreateClassName = new Button(groupInfo, SWT.PUSH);
		
		btnCreateClassName.setText(MessageUtil.getMessage("ko.btn.class.create"));
		btnCreateClassName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnCreateClassName.setEnabled(true);
		
		btnCreateClassName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String topCategory = comboTopCategory.getText();
				String midCategory = txtMidCategory.getText();
				String smallCategory = txtSmallCategory.getText();
				String shortCategory = txtShortCategory.getText();
				
				topCategory = topCategory.substring(topCategory.indexOf("[") + 1, topCategory.length() - 1).toLowerCase();
				midCategory = StringUtils.isNotEmpty(midCategory) ? "." + midCategory.toLowerCase() : "";
				smallCategory = StringUtils.isNotEmpty(smallCategory) ? "." + smallCategory.toLowerCase() : "";
				shortCategory = StringUtils.isNotEmpty(shortCategory) ? "." + StringUtils.capitalize(shortCategory.toLowerCase()) : "";
				
				actionPkgName = topCategory + midCategory + ".action" + shortCategory + "Act";
				servicePkgName = topCategory + midCategory + smallCategory + shortCategory + "Svc";
				groovyPkgName = topCategory + midCategory + smallCategory + ".qry" + shortCategory + "Qry";
				
				if (tabName.equals(MessageUtil.getMessage("tab.table.title"))) {
					modelPkgName = topCategory + midCategory + smallCategory + ".model." + camelCaseTableName + "M01";
				} else {
					modelPkgName = topCategory + midCategory + smallCategory + ".model." + shortCategory + "0000M01";
				}
				
				txtActionClass.setText(actionPkgName);
				txtServiceClass.setText(servicePkgName);
				txtGroovyClass.setText(groovyPkgName);
				txtModelClass.setText(modelPkgName);
			}
		});
	}
	
	protected void createClassGroup(Composite parent) {
		final Group groupInfo = new Group(parent, SWT.NONE);
		
		groupInfo.setLayout(new GridLayout(4, false));
		groupInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Action Class
		Label labelActionClass = new Label(groupInfo, SWT.NONE);
		
		labelActionClass.setLayoutData(this.getLabelLayout());
		labelActionClass.setText("Action");
		
		txtActionClass = new Text(groupInfo, SWT.BORDER);
		txtActionClass.setLayoutData(this.getOneSpanTextLayout());
		txtActionClass.setEditable(false);
		txtActionClass.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				actionPkgName = txtActionClass.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Service Class
		Label labelServiceClass = new Label(groupInfo, SWT.NONE);
		
		labelServiceClass.setLayoutData(this.getLabelLayout());
		labelServiceClass.setText("Service");
		
		txtServiceClass = new Text(groupInfo, SWT.BORDER);
		txtServiceClass.setLayoutData(this.getOneSpanTextLayout());
		txtServiceClass.setEditable(false);
		txtServiceClass.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				servicePkgName = txtServiceClass.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Model Class
		Label labelModelClass = new Label(groupInfo, SWT.NONE);
		
		labelModelClass.setLayoutData(this.getLabelLayout());
		labelModelClass.setText("Model");
		
		txtModelClass = new Text(groupInfo, SWT.BORDER);
		txtModelClass.setLayoutData(this.getOneSpanTextLayout());
		txtModelClass.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				modelPkgName = txtModelClass.getText();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Groovy Class
		Label labelGroovyClass = new Label(groupInfo, SWT.NONE);
		
		labelGroovyClass.setLayoutData(this.getLabelLayout());
		labelGroovyClass.setText("Groovy");
		
		txtGroovyClass = new Text(groupInfo, SWT.BORDER);
		txtGroovyClass.setLayoutData(this.getOneSpanTextLayout());
		txtGroovyClass.setEditable(false);
		txtGroovyClass.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				groovyPkgName = txtGroovyClass.getText();
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
	
	private GridData getOneSpanTextLayout() {
		GridData gridData = new GridData();
		
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		
		return gridData;
	}
	
	private String getPreferenceStore(String id) {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore().getString(id);
	}
	
	private void addArgInfo() {
		if (tabName.equals(MessageUtil.getMessage("tab.table.title"))) {
			argument.put(Constants.TEMPLATE_CHECK, templateArg);
			
		} else {
			templateArg.put(Constants.CONTROLLER_FILE, false);
			templateArg.put(Constants.SERVICE_FILE, false);
			templateArg.put(Constants.MODEL_FILE, true);
			templateArg.put(Constants.GROOVY_FILE, false);
			
			argument.put(Constants.TEMPLATE_CHECK, templateArg);
			
			List<TableColumns> list = databaseService.getQueryMetaData(query.toUpperCase());
			
			argument.put(Constants.COLUMNS, list);
			delArgument = null;
		}
		
		argument.put(Constants.SCHEMA, schema);
		argument.put(Constants.BIZ_ABBR, bizAbbr.toUpperCase());
		argument.put(Constants.BIZ_NAME, bizName);
		argument.put(Constants.AUTHOR, author);
		argument.put(Constants.METHOD_NAME, methodName);

		argument.put(Constants.ACTION_PKG_NAME, actionPkgName);
		argument.put(Constants.SERVICE_PKG_NAME, servicePkgName);
		argument.put(Constants.MODEL_PKG_NAME, modelPkgName);
		argument.put(Constants.GROOVY_PKG_NAME, groovyPkgName);
	}
	
	private void generateSourceCode() {
		this.addArgInfo();
		
		DefaultLuncher luncher = new DefaultLuncher();

		luncher.execute(srcPath, argument, delArgument);
	}
}

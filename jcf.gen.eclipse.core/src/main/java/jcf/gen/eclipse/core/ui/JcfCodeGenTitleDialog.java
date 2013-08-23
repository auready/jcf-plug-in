package jcf.gen.eclipse.core.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
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
import org.eclipse.swt.widgets.Text;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.jdbc.DatabaseAccessor;
import jcf.gen.eclipse.core.jdbc.TableColumns;
import jcf.gen.eclipse.core.luncher.DefaultLuncher;
import jcf.gen.eclipse.core.utils.DbUtils;
import jcf.gen.eclipse.core.utils.PreferenceUtil;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.MessageUtil;

public class JcfCodeGenTitleDialog extends TitleAreaDialog {
	private Text txtSrcFolder = null;
	private Text txtPackageName = null;
	private Text txtUserCase = null;
	
	private DatabaseAccessor dao = null;
	
	private Map<String, Object> argument = new HashMap<String, Object>();
	private Set<String> excludeCols = new HashSet<String>();
	private Map<String, Boolean> template = new HashMap<String, Boolean>();
	
	private TableViewer tableViewer;
	private String[] objItems;
	
	private String srcPath = "";
	private String packageName = "";
	private String userCaseName = "";
	
	private String serviePath = "";
	
	private boolean isDbConn = false;
	
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
		return new Point(500, 600);
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
		setTitleImage(JcfGeneratorPlugIn.getImageDescriptor("icons/keis.png").createImage());
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (DbUtils.isDbConn()) {
			isDbConn = true;
			dao = new DatabaseAccessor();
		}
		
		createDbGroup(container);
		
		createTemplateGroup(container);
		
		createCodeGroup(container);
		
		//Separator
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return area;
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		Button previewButton = createButton(parent, IDialogConstants.PROCEED_ID, MessageUtil.getMessage("button.default.preview"), false);
		
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
		final Combo comboTabName = new Combo(groupDbInfo, SWT.DROP_DOWN);
		
		comboTabName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboTabName.setEnabled(false);
		
		if (isDbConn) {
			comboTabName.setEnabled(true);
			
			String[] temp = dao.getTableNames("");
			List<String> tmpList = new ArrayList<String>();
			
			for (int i = 0; i < temp.length; i++) {
				String tableName = temp[i];
				
				if (StringUtils.isBlank(tableName)) continue;
				tmpList.add(tableName);
			}
			
			objItems = tmpList.toArray(new String[tmpList.size()]);
			comboTabName.setItems(objItems);
		}
			
		comboTabName.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setTableCombo(comboTabName.getSelectionIndex());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		comboTabName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				String tempObjName = comboTabName.getText().toUpperCase();
				String[] objNames = dao.getTableNames(tempObjName);
				
				try {
					SimpleContentProposalProvider scp = new SimpleContentProposalProvider(objNames);
					
					scp.setProposals(objNames);
				 	scp.setFiltering(true);
				 	
				 	KeyStroke ks = KeyStroke.getInstance("Ctrl+Space");
				 	
				 	ContentProposalAdapter adapter = 
				 			new ContentProposalAdapter(comboTabName, new ComboContentAdapter(), scp, ks, null);
				 	
				 	adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		});
		
		comboTabName.addListener(SWT.CHANGED, new Listener() {
			public void handleEvent(Event event) {
				String selectedObjName = comboTabName.getText().toUpperCase();
				
				int index = 0;
				
				if ((index = findTableName(selectedObjName)) >= 0) {
					setTableCombo(index);
				}
			}
		});
		
		//Column
		Label labelTabCol = new Label(groupDbInfo, SWT.NONE);
		
		labelTabCol.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		labelTabCol.setText("");
				
		TableColumnViewer tableColumnViewer = new TableColumnViewer(groupDbInfo);
		
		tableViewer = tableColumnViewer.createTableViewer();
		
		tableViewer.getTable().setEnabled(false);
		tableViewer.getTable().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					String tempName = event.item.toString();
					String colName = tempName.substring(tempName.indexOf("{") + 1, tempName.indexOf("}"));
					
					if (excludeCols.contains(colName)) {
						excludeCols.remove(colName);
					} else {
						excludeCols.add(colName);
					}
				}
			}
		});
	}
	
	private void setTableCombo(int index) {
		String tempTableName = objItems[index];
		String tableName = tempTableName.substring(0, tempTableName.indexOf(" ["));
		
//		ColumnNameCamelCaseMap camelCaseStr = new ColumnNameCamelCaseMap();
//		String camelCaseTableName = camelCaseStr.tableNameConvert(tableName);		
//		txtUserCase.setText(camelCaseTableName);
//		userCaseName = camelCaseTableName;
		
		//Table Contents Change
		List<TableColumns> list = dao.getColumnList(tableName);
		
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
	
	private int findTableName(String tableName) {
		for (int i = 0; i < objItems.length; i++) {
			if (objItems[i].equals(tableName)) return i;
		}
		
		return -1;
	}
	
	private String[] category = {Constants.CONTROLLER_FILE, Constants.SERVICE_FILE, Constants.MODEL_FILE, Constants.SQLMAP_FILE};
	
	protected void createTemplateGroup(Composite parent) {
		final Group groupInfo = new Group(parent, SWT.NONE);
		
		groupInfo.setText(MessageUtil.getMessage("group.template.setting.title"));
		groupInfo.setLayout(new GridLayout(5, false));
		groupInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Initialize
		for (int i = 0, len = category.length; i < len; i++) {
			template.put(category[i], PreferenceUtil.isBoolen(category[i]));
		}
		
		final Button[] btnTemplate = new Button[4];
		
		for (int i = 0; i < category.length; i++) {
			final int idx = i;
			
			btnTemplate[idx] = new Button(groupInfo, SWT.CHECK);
			btnTemplate[idx].setText(category[idx].substring(0, category[idx].indexOf("_")).toLowerCase());
			btnTemplate[idx].setSelection(PreferenceUtil.isBoolen(category[idx]));
			btnTemplate[idx].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					template.put(category[idx], btnTemplate[idx].getSelection());
				}
			});
		}
		
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
		
		String sourceDir = PreferenceUtil.getStringValue(Constants.SOURCE_DIRECTORY);
		
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
			public void keyReleased(KeyEvent e) {
				if (txtPackageName.getText().length() > 0) {
					serviePath = txtPackageName.getText();
				}
			}
			
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
			public void keyReleased(KeyEvent e) {
				if (txtUserCase.getText().length() > 0) userCaseName = txtUserCase.getText();
			}
			
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
	
	private void setDataArgument() {
		packageName = PreferenceUtil.getStringValue(Constants.PACKAGE_PATH) + "." + serviePath;
		
		argument.put(Constants.EXCLUDE_COLUMNS, excludeCols);
		argument.put(Constants.TEMPLATE, template);
		argument.put(Constants.SERVICE_MAPPING, serviePath);
	}

	protected void makeCodePreviewDialog(final Composite parent) {
		setDataArgument();
		
		if (!StringUtils.isEmpty((String) argument.get(Constants.TABLENAME))) {
			CodePreviewDialog preview = new CodePreviewDialog(parent.getShell());
			preview.open(packageName, userCaseName, argument);			
		}
	}
	
	private void generateSourceCode() {
		setDataArgument();
		
		if (!StringUtils.isEmpty((String) argument.get(Constants.TABLENAME))) {
			DefaultLuncher luncher = new DefaultLuncher();
			luncher.execute(srcPath, packageName, userCaseName, argument);
		}
	}
}

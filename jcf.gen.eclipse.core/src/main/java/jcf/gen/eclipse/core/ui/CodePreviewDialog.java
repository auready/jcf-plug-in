package jcf.gen.eclipse.core.ui;

import java.util.Map;
import java.util.Set;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.luncher.DefaultLuncher;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class CodePreviewDialog extends Dialog {

	private TabFolder tabFolder;
	
	private String packageName;
	private String usercaseName;
	private Map<String, Object> argument;
	private Set<String> delArgument;
	
	private Map<String, String> retMap;
	
	public CodePreviewDialog(Shell parent) {
		super(parent);
		
		setShellStyle(SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		
		shell.setText(MessageUtil.getMessage("dialog.preview.title"));
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(600, 600);
	}
	
	@Override
	protected Point getInitialLocation(Point initPoint) {
		Display display = Display.getCurrent();
		
		int x = (display.getClientArea().width - getInitialSize().x) / 2;
		int y = (display.getClientArea().height - getInitialSize().y) / 2;
		
		return new Point(x, y);
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		Button closeButton = createButton(parent, IDialogConstants.CLOSE_ID, 
				MessageUtil.getMessage("button.default.preview.close"), false);
		
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.generateSourceCode();
		
		this.createPreviewTabContents(container);
		
		return area;
	}
	
	public void open(String packageName, String userCaseName, Map<String, Object> arg, Set<String> delArg) {
		this.packageName = packageName;
		this.usercaseName = userCaseName;
		this.argument = arg;
		this.delArgument = delArg;

		super.open();
	}
	
	protected void createPreviewTabContents(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.addTabItem(Constants.MODEL);
		this.addTabItem(Constants.SERVICE);
		this.addTabItem(Constants.CONTROLLER);
		this.addTabItem(Constants.SQLMAP);
		this.addTabItem(Constants.TDO);
	}
	
	private void addTabItem(String tabName) {
		Composite page = new Composite(tabFolder, SWT.NONE);
		
		page.setLayout(new GridLayout(1, false));
		page.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		
		tabItem.setText(tabName);
		tabItem.setControl(page);
		
		Text content = new Text(page, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		content.setText(retMap.get(tabName));
	}
	
	private void generateSourceCode() {
		DefaultLuncher luncher = new DefaultLuncher();
		
		retMap = luncher.execute(packageName, usercaseName, argument, delArgument);
	}
}

package jcf.gen.eclipse.core.preference.page;

import java.sql.Connection;
import java.sql.DriverManager;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.preference.AbstractJcfPreferencePage;
import jcf.gen.eclipse.core.utils.MessageUtil;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

public class ConnectionPreferencePage extends AbstractJcfPreferencePage {
	
	private StringFieldEditor driverClassNameEditor;
	private StringFieldEditor driverUrlEditor;
	private StringFieldEditor driverUsernameEditor;
	private StringFieldEditor driverPasswordEditor;
	
	public ConnectionPreferencePage() {
		setDescription(MessageUtil.getMessage("preference.conn.description"));
	}
	
	@Override
	protected void createFieldEditors() {
		Composite main = getFieldEditorParent();
		
		GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(main);
		
		Group dbCategoryGroup = new Group(main, SWT.NONE);
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbCategoryGroup);
		
		String[][] dbCategory = {{Constants.DB_ORACLE, Constants.DB_ORACLE}, {Constants.DB_MYSQL, Constants.DB_MYSQL}};
		
		RadioGroupFieldEditor dbCategoryEditor = new RadioGroupFieldEditor(Constants.DB_CATEGORY_RADIO, 
				MessageUtil.getMessage("preference.db.category.desc"),
				dbCategory.length, 
				dbCategory, 
				dbCategoryGroup);
		
		addField(dbCategoryEditor);
		
		((GridLayout) dbCategoryGroup.getLayout()).marginWidth = 5;
		((GridLayout) dbCategoryGroup.getLayout()).marginBottom = 5;
		
		Group dbGroup = new Group(main, SWT.NONE);
		dbGroup.setText(MessageUtil.getMessage("preference.group.db"));
		
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).applyTo(dbGroup);
		
		driverClassNameEditor = new StringFieldEditor(Constants.DB_DRIVER_CLASS, 
				MessageUtil.getMessage("preference.db.driver.name"), dbGroup);
		driverClassNameEditor.getLabelControl(dbGroup).setToolTipText(MessageUtil.getMessage("preference.db.driver.name.tooltip"));
		addField(driverClassNameEditor);
		
		driverUrlEditor = new StringFieldEditor(Constants.DB_URL, 
				MessageUtil.getMessage("preference.db.driver.url"), dbGroup);
		driverUrlEditor.getLabelControl(dbGroup).setToolTipText(MessageUtil.getMessage("preference.db.driver.url.tooltip"));
		addField(driverUrlEditor);
		
		driverUsernameEditor = new StringFieldEditor(Constants.DB_USERNAME, 
				MessageUtil.getMessage("preference.db.driver.username"), dbGroup);
		addField(driverUsernameEditor);
		
		driverPasswordEditor = new StringFieldEditor(Constants.DB_PASSWORD, 
				MessageUtil.getMessage("preference.db.driver.password"), dbGroup);
		driverPasswordEditor.getTextControl(dbGroup).setEchoChar('*');
		addField(driverPasswordEditor);
		
		updateMargin(dbGroup);
		
		Composite buttonPanel = new Composite(main, SWT.NONE);
		
		buttonPanel.setLayout(new GridLayout(1, false));
		
		GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);
		
		Button dbConnTestBtn = new Button(buttonPanel, SWT.PUSH);
		
		GridDataFactory.fillDefaults().applyTo(dbConnTestBtn);
		
		dbConnTestBtn.setText("Database Connection Test");
		dbConnTestBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean connTest = dbConnetionTest();
				String msg = connTest ? "Connection OK" : "Connection FAIL";
				
				connMsgBox(msg, connTest);
			}
		});
	}
	
	public void connMsgBox(String message, boolean isConn) {
		int style = isConn ? SWT.ICON_INFORMATION | SWT.OK : SWT.ICON_ERROR | SWT.ERROR;
		
		MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), style);
		
		msg.setText("Database Connection");
		msg.setMessage(message);
		msg.open();
	}
	
	public boolean dbConnetionTest() {
		boolean result = true;
		
		String className = driverClassNameEditor.getStringValue();
		String url = driverUrlEditor.getStringValue();
		String username = driverUsernameEditor.getStringValue();
		String password = driverPasswordEditor.getStringValue();
		
		Connection conn = null;
		
		try {		
			Class.forName(className);
	
			conn = DriverManager.getConnection(url, username, password);
			conn.close();
			
		} catch (Exception e) {
			result = false;
		} 
		
		return result;
	}
}

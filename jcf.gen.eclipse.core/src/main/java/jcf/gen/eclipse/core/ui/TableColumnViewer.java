package jcf.gen.eclipse.core.ui;

import java.util.List;

import jcf.gen.eclipse.core.jdbc.TableColumns;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnViewer {
	private Composite composite;
	
	public TableColumnViewer(Composite composite) {
		setComposite(composite);
	}
	
	public void setComposite(Composite composite) {
		this.composite = composite;
	}
	
	public TableViewer createTableViewer() {
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		CheckboxTableViewer chkTableViewer = new CheckboxTableViewer(table);
		
		chkTableViewer.setContentProvider(new TableColContentsProvider());
		chkTableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		String[] titles = {"COLUMN_NAME", "DATA_TYPE", "NULL", "COMMENT"};
		int[] widths = {150, 90, 50, 156};
		
		for (int i = 0, n = titles.length; i < n; i++) {
			this.createTableViewerCol(chkTableViewer, titles[i], widths[i], i);
		}
		
		return chkTableViewer;
	}
	
	public void createTableViewerCol(TableViewer viewer, String title, int width, final int idx) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.SELECTED);
		
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableColumns col = (TableColumns) element;
				String text = "";
				
				switch (idx) {
					case 0:
						text = col.getColumnName();
						break;
						
					case 1:
						text = col.getDataType();
						break;
						
					case 2:
						text = col.getNullable().equals("N") ? "" : "Yes";
						break;
						
					case 3:
						text = col.getColumnCommnet();
						break;
						
					default:
						break;
				}
				
				return text;
			}
		});
		
		TableColumn tableColumn = tableViewerColumn.getColumn();
		
		tableColumn.setText(title);
		tableColumn.setWidth(width);
		tableColumn.setAlignment(SWT.LEFT);
	}
	
	class TableColContentsProvider implements IStructuredContentProvider {
		
		@Override
		public Object[] getElements(Object arg0) {
			return ((List<TableColumns>) arg0).toArray();
		}
		
		@Override
		public void dispose() {
		}
		
		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}
}

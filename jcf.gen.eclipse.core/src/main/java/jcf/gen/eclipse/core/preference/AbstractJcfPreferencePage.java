package jcf.gen.eclipse.core.preference;

import jcf.gen.eclipse.core.utils.PreferenceUtil;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class AbstractJcfPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AbstractJcfPreferencePage() {
		this(FLAT);
	}
	
	public AbstractJcfPreferencePage(int style) {
		super(style);
		
		setPreferenceStore(PreferenceUtil.getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	protected void updateMargin(Group group) {
		GridLayout layout = (GridLayout) group.getLayout();
		
		layout.marginWidth = 5;
		layout.marginHeight = 5;
	}
}

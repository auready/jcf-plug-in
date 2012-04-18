package jcf.gen.eclipse.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import jcf.gen.eclipse.core.ui.JcfCodeGenTitleDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class JcfGenHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public JcfGenHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		MessageDialog.openInformation(
//				window.getShell(),
//				"JCF-DEV",
//				"Hello");
		
		JcfCodeGenTitleDialog dialog = new JcfCodeGenTitleDialog(window.getShell());
		dialog.open();
		
		return null;
	}
}

package jcf.gen.eclipse.core.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.Constants;

public class JcfGenPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JcfGeneratorPlugIn.getDefault().getPreferenceStore();

		store.setDefault(Constants.DB_PROPERTY_FILE, "");
		store.setDefault(Constants.DB_DRIVER_CLASS, "");
		store.setDefault(Constants.DB_URL, "");
		store.setDefault(Constants.DB_USERNAME, "");
		store.setDefault(Constants.DB_PASSWORD, "");
		
		store.setDefault(Constants.TEMPLATE_DIRECTORY, System.getProperty("user.home"));
		store.setDefault(Constants.SOURCE_DIRECTORY, "");
		
		store.setDefault(Constants.CONTROLLER_FILE, true);
		store.setDefault(Constants.SERVICE_FILE, true);
		store.setDefault(Constants.MODEL_FILE, true);
		store.setDefault(Constants.SQLMAP_FILE, true);
//		store.setDefault(Constants.GROOVY_FILE, true);
	}

}

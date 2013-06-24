package jcf.gen.eclipse.core.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.PreferenceUtil;

public class JcfGenPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferenceUtil.getPreferenceStore();

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
		
		store.setDefault(Constants.CONTROLLER_PKG_NAME, "controller");
		store.setDefault(Constants.SERVICE_PKG_NAME, "service");
		store.setDefault(Constants.MODEL_PKG_NAME, "model");
		store.setDefault(Constants.SQLMAP_PKG_NAME, "sqlmap");
		
		store.setDefault(Constants.CONTROLLER_FILE_NAME, "Controller");
		store.setDefault(Constants.ISERVICE_FILE_NAME, "Service");
		store.setDefault(Constants.SERVICE_FILE_NAME, "ServiceImpl");
		store.setDefault(Constants.MODEL_FILE_NAME, "Model");
		store.setDefault(Constants.SQLMAP_FILE_NAME, "-sqlMap");
		
		store.setDefault(Constants.AUTHOR, "");
	}

}

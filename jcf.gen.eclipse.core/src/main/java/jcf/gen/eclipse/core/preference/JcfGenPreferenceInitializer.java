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
		store.setDefault(Constants.SOURCE_DIRECTORY, "");
	}

}

package jcf.gen.eclipse.core.utils;

import org.eclipse.jface.preference.IPreferenceStore;

import jcf.gen.eclipse.core.JcfGeneratorPlugIn;

public class PreferenceUtil {

	public static IPreferenceStore getPreferenceStore() {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore();
	}
	
	public static String getStringValue(String name) {
		return getPreferenceStore().getString(name);
	}
	
	public static boolean isBoolen(String name) {
		return getPreferenceStore().getBoolean(name);
	}
	
}

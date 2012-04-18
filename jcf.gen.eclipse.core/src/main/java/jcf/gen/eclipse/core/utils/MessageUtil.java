package jcf.gen.eclipse.core.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

public class MessageUtil {
	
	protected MessageUtil() {
		throw new UnsupportedOperationException();
	}
	
	public static String getMessage(String key) {
		String message = "";
		
		try {
			message = ResourceBundle.getBundle("messages").getString(key);
			
			if (StringUtils.isEmpty(message)) {
				return key;
			}
			
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		
		return message;
	}
}

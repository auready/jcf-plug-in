package jcf.gen.eclipse.core.generator.controller;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ControlGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.UC_NAME)).
				append(getPreferenceString(Constants.CONTROLLER_FILE_NAME)).append(".java").toString());
	}

	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator()).
				append(getPreferenceString(Constants.CONTROLLER_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "controlTemplate.vm";
	}
}

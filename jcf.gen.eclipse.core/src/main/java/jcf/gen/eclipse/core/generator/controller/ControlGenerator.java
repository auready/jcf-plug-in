package jcf.gen.eclipse.core.generator.controller;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ControlGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.ACTION_FILE_NAME)).append(".java").toString());
	}

	public String getPackagePath(Map<String,Object> infoMap) {
		return new StringBuilder((String) infoMap.get(Constants.ACTION_PKG_NAME)).toString();
	}

	@Override
	public String getVmFileName() {
		return "actionTemplate.vm";
	}
}

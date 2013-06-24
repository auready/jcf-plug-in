package jcf.gen.eclipse.core.generator.service;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class IServiceGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String, Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.UC_NAME)).
				append(getPreferenceString(Constants.ISERVICE_FILE_NAME)).append(".java").toString());
	}

	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator()).
				append(getPreferenceString(Constants.SERVICE_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "iServiceTemplate.vm";
	}
}

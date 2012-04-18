package jcf.gen.eclipse.core.generator.service;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ServiceGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String, Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.UC_NAME)).append("Service.java").toString());
	}

	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator())
		.append("service").toString();
	}

	public String getVmFileName() {
		return "serviceTemplate.vm";
	}
}

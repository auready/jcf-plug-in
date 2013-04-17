package jcf.gen.eclipse.core.generator.service;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class IServiceGenerator extends AbstractSourceGenerator {

	@Override
	public String getFileName(Map<String, Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.UC_NAME)).append("Service.java").toString());
	}

	@Override
	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator())
				.append("service").toString();
	}

	@Override
	public String getVmFileName() {
		return "serviceInterfaceTemplate.vm";
	}
}

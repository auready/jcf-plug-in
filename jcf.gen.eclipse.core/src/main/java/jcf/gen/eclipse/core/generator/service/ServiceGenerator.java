package jcf.gen.eclipse.core.generator.service;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ServiceGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String, Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.SERVICE_FILE_NAME)).toString());
	}

	public String getPackagePath(Map<String,Object> infoMap) {
		return new StringBuilder((String) infoMap.get(Constants.SERVICE_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "serviceTemplate.vm";
	}
}

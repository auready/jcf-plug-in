package jcf.gen.eclipse.core.generator.action;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ActionGenerator extends AbstractSourceGenerator {
	
	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.UC_NAME)).append("Action.java").toString());
	}

	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator()).append("action").toString();
	}
	
	@Override
	public String getVmFileName() {
		return "actionTemplate.vm";
	}

}

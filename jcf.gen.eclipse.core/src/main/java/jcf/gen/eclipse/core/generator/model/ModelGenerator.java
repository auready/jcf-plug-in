package jcf.gen.eclipse.core.generator.model;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class ModelGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.MODEL_FILE_NAME)).append(".java").toString());
	}

	public String getPackagePath(Map<String,Object> infoMap) {
		return new StringBuilder((String) infoMap.get(Constants.MODEL_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "modelTemplate.vm";
	}
}

package jcf.gen.eclipse.core.generator.dao;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class GroovyGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.GROOVY_FILE_NAME)).append(".groovy").toString());
	}

	public String getPackagePath(Map<String,Object> infoMap) {
		return new StringBuilder((String) infoMap.get(Constants.GROOVY_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "groovyTemplate.vm";
	}
}

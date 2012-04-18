package jcf.gen.eclipse.core.generator.dao;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class GroovyGenerator extends AbstractSourceGenerator {

	@Override
	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.TABLE_NAME_PASCAL)).append("Query.groovy").toString());
	}

	@Override
	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator())
		.append("dao").toString();
	}

	@Override
	public String getVmFileName() {
		return "groovyTemplate.vm";
	}
}

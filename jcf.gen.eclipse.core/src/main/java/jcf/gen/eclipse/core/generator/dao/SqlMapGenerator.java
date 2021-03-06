package jcf.gen.eclipse.core.generator.dao;

import java.util.Map;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.generator.AbstractSourceGenerator;
import jcf.gen.eclipse.core.utils.StrUtils;

public class SqlMapGenerator extends AbstractSourceGenerator {

	public String getFileName(Map<String,Object> infoMap) {
		return StrUtils.nts(new StringBuilder((String)infoMap.get(Constants.TABLE_NAME_PASCAL)).
				append(getPreferenceString(Constants.SQLMAP_FILE_NAME)).append(".xml").toString());
	}

	public String getPackagePath(String basePackPath) {
		return new StringBuilder(basePackPath).append(getSeperator())
				.append(getPreferenceString(Constants.SQLMAP_PKG_NAME)).toString();
	}

	public String getVmFileName() {
		return "sqlMapTemplate.vm";
	}
}

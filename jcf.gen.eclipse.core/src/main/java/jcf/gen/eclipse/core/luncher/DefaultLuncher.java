package jcf.gen.eclipse.core.luncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcf.gen.eclipse.core.generator.controller.ControlGenerator;
import jcf.gen.eclipse.core.generator.dao.GroovyGenerator;
import jcf.gen.eclipse.core.generator.dao.SqlMapGenerator;
import jcf.gen.eclipse.core.generator.service.ServiceGenerator;
import jcf.gen.eclipse.core.generator.model.ModelGenerator;
import jcf.gen.eclipse.core.jdbc.model.TableColumns;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.utils.ColumnNameCamelCaseMap;
import jcf.gen.eclipse.core.utils.DbUtils;

public class DefaultLuncher {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultLuncher.class);

	public DefaultLuncher() {
	}
	
	private boolean hasPrimaryKeyInList(List<Map<String, Object>> list) {
		for (Map<String, Object> column : list) {
			if (column.get(Constants.PK_COL) != null) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void execute(String srcPath, String packageName, String userCaseName, Map<String, Object> arg, Set<String> delArg) {
		ColumnNameCamelCaseMap columnNameCamelCase = new ColumnNameCamelCaseMap();
		
		String tableName = (String) arg.get(Constants.TABLENAME);
		List<TableColumns> columnList = (List<TableColumns>) arg.get(Constants.COLUMNS);
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		boolean hasNumberType = false;
		
		for (TableColumns col : columnList) {
			String colName = col.getColumnName();
			
			if (!delArg.contains(colName)) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put(Constants.COL_TABLE_NAME, col.getTableName());
				map.put(Constants.COL_TABLE_COMMENT, col.getTableComment());
				map.put(Constants.COL_COLUMN_NAME, col.getColumnName());
				map.put(Constants.COL_COLUMN_COMMENT, col.getColumnCommnet());
				map.put(Constants.COL_PK, col.getPk());
				map.put(Constants.COLUMN_DATA_TYPE, DbUtils.convertToDataType(col.getDataType()));
				map.put(Constants.COL_DATA_LENGTH, col.getDataLength());
				map.put(Constants.COL_CHAR_LENGTH, col.getCharLength());
				map.put(Constants.COL_DATA_PRECISION, col.getDataPrecision());
				map.put(Constants.COL_DATA_SCALE, col.getDataScale());
				map.put(Constants.COL_NULLABLE, col.getNullable());
				map.put(Constants.COL_COLUMN_ID, col.getColumnId());
				map.put(Constants.COL_DEFAULT_LENGTH, col.getDefaultLength());
				map.put(Constants.COL_DATA_DEFAULT, col.getDataDefault());
				map.put(Constants.COLUMN_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(col.getColumnName()));
				map.put(Constants.COLUMN_NAME_PASCAL, columnNameCamelCase.pascalCaseConverter(col.getColumnName()));
				
				if (!hasNumberType) {
					if (DbUtils.hasNumberType(col.getDataType())) hasNumberType = true;
				}
				
				list.add(map);
			}
		}
		 
		Map<String, Object> model = new HashMap<String, Object>();
		
		model.put(Constants.TABLENAME, tableName);
		model.put(Constants.TABLE_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(tableName));
		model.put(Constants.TABLE_NAME_PASCAL, columnNameCamelCase.pascalCaseConverter(tableName));
		model.put(Constants.COLUMNS, list);
		model.put(Constants.SHARP, "#");
		model.put(Constants.DOLLOR, "$");
		model.put(Constants.TABLE_COMMENT, (list.get(0)).get(Constants.COL_TABLE_COMMENT));
		model.put(Constants.IMPORT_MATH_CLASS, (hasNumberType ? Constants.IMPORT_BIG_DECIMAL : Constants.IMPORT_NULL));
		
		String isPkExist = hasPrimaryKeyInList(list) ? Constants.IS_PK_EXIST_Y : Constants.IS_PK_EXIST_N;
		model.put(Constants.IS_PK_EXIST, isPkExist);
		
		template = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE);
		
		this.run(srcPath, packageName, userCaseName, model);
		
		MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
		
		msg.setText("JCF");
		msg.setMessage("JCF Source Generate");
		msg.open();
	}
	
	private Map<String, Boolean> template;
	
	private boolean createTemplateFile(String category) {
		return template.get(category);
	}
	
	private void run(String srcPath, String packageName, String userCaseName, Map<String, Object> model) {
		if (createTemplateFile(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();		
			controlGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (createTemplateFile(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			serviceGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		
		if (createTemplateFile(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			modelGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (createTemplateFile(Constants.SQLMAP_FILE)) {
			SqlMapGenerator sqlMapGenerator = new SqlMapGenerator();
			sqlMapGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (createTemplateFile(Constants.GROOVY_FILE)) {
			GroovyGenerator groovyGenerator = new GroovyGenerator();
			groovyGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
	}
}

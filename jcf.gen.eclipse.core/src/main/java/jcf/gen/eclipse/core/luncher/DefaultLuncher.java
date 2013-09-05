package jcf.gen.eclipse.core.luncher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import jcf.gen.eclipse.core.generator.controller.ControlGenerator;
import jcf.gen.eclipse.core.generator.dao.SqlMapGenerator;
import jcf.gen.eclipse.core.generator.service.ServiceGenerator;
import jcf.gen.eclipse.core.generator.model.ModelGenerator;
import jcf.gen.eclipse.core.jdbc.TableColumns;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.ColumnNameCamelCaseMap;
import jcf.gen.eclipse.core.utils.DbUtils;
import jcf.gen.eclipse.core.utils.PreferenceUtil;
import jcf.gen.eclipse.core.utils.StrUtils;

public class DefaultLuncher {
	
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
	private Map<String, Object> makeMapData(Map<String, Object> arg) {
		ColumnNameCamelCaseMap columnNameCamelCase = new ColumnNameCamelCaseMap();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<TableColumns> columnList = (List<TableColumns>) arg.get(Constants.COLUMNS);
		Set<String> excludeCols = (Set<String>) arg.get(Constants.EXCLUDE_COLUMNS);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		boolean hasNumberType = false;
		
		for (TableColumns col : columnList) {
			String colName = col.getColumnName();
			
			if (!excludeCols.contains(colName)) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put(Constants.COL_TABLE_NAME, col.getTableName());
				map.put(Constants.COL_TABLE_COMMENT, StrUtils.nvl(col.getTableComment()));
				map.put(Constants.COL_COLUMN_NAME, col.getColumnName());
				map.put(Constants.COL_COLUMN_COMMENT, StrUtils.makeColumnComment(col.getColumnCommnet()));
				map.put(Constants.COL_PK, col.getPk());
				map.put(Constants.COL_DATA_TYPE, DbUtils.convertToDataType(col.getDataType()));
				map.put(Constants.COL_DATA_LENGTH, col.getDataLength());
				map.put(Constants.COL_CHAR_LENGTH, col.getCharLength());
				map.put(Constants.COL_DATA_PRECISION, col.getDataPrecision());
				map.put(Constants.COL_DATA_SCALE, col.getDataScale());
				map.put(Constants.COL_NULLABLE, col.getNullable());
				map.put(Constants.COL_COLUMN_ID, col.getColumnId());
				map.put(Constants.COL_DEFAULT_LENGTH, col.getDefaultLength());
				map.put(Constants.COL_DATA_DEFAULT, col.getDataDefault());
				map.put(Constants.COLUMN_NAME_CAMEL, StrUtils.nvl(columnNameCamelCase.camelCaseConverter(col.getColumnName())));
				map.put(Constants.COLUMN_NAME_PASCAL, StrUtils.nvl(columnNameCamelCase.pascalCaseConverter(col.getColumnName())));
				
				if (!hasNumberType) {
					if (DbUtils.hasNumberType(col.getDataType())) hasNumberType = true;
				}
				
				list.add(map);
			}
		}
		
		String tableName = (String) arg.get(Constants.TABLENAME);
		
		model.put(Constants.TABLENAME, tableName);
		model.put(Constants.TABLE_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(tableName));
		model.put(Constants.TABLE_NAME_PASCAL, columnNameCamelCase.tableNameConvert(tableName));
		model.put(Constants.COLUMNS, list);
		model.put(Constants.SHARP, "#");
		model.put(Constants.DOLLOR, "$");
		model.put(Constants.TABLE_COMMENT, StrUtils.nvl(String.valueOf((list.get(0)).get(Constants.COL_TABLE_COMMENT))));
//		model.put(Constants.IMPORT_MATH_CLASS, (hasNumberType ? Constants.IMPORT_BIG_DECIMAL : Constants.IMPORT_NULL));
//		model.put(Constants.PACKAGE_PATH, PreferenceUtil.getStringValue(Constants.PACKAGE_PATH));
		model.put(Constants.SERVICE_MAPPING, StrUtils.getServicePath((String) arg.get(Constants.SERVICE_MAPPING)));
		model.put(Constants.AUTHOR, PreferenceUtil.getStringValue(Constants.AUTHOR));
		model.put(Constants.CREATE_DATE, new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
		model.put(Constants.SCHEMA, PreferenceUtil.getStringValue(Constants.DB_USERNAME).toUpperCase());
		
		model.put(Constants.TAB, "\t");
		
		String isPkExist = hasPrimaryKeyInList(list) ? Constants.IS_PK_EXIST_Y : Constants.IS_PK_EXIST_N;
		model.put(Constants.IS_PK_EXIST, isPkExist);
		
		template = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE);
		
		return model;
	}
	
	public void execute(String srcPath, String packageName, String userCaseName, Map<String, Object> arg) {
		Map<String, Object> model = this.makeMapData(arg);
		
		run(srcPath, packageName, userCaseName, model);
		
		MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
		
		msg.setText("KEIS");
		msg.setMessage("KEIS Source Generate");
		msg.open();
	}
	
	public Map<String, String> execute(String packageName, String userCaseName, Map<String, Object> arg) {
		Map<String, Object> model = this.makeMapData(arg);
		
		return preview(packageName, userCaseName, model);
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
		
	}
	
	private Map<String, String> preview(String packageName, String userCaseName, Map<String, Object> model) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if (createTemplateFile(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();
			map.put(Constants.CONTROLLER, controlGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (createTemplateFile(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			map.put(Constants.SERVICE, serviceGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (createTemplateFile(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			map.put(Constants.MODEL, modelGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (createTemplateFile(Constants.SQLMAP_FILE)) {
			SqlMapGenerator sqlMapGenerator = new SqlMapGenerator();
			map.put(Constants.SQLMAP, sqlMapGenerator.generatorText(packageName, userCaseName, model));
		}
		
		return map;
	}
	
}

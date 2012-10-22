package jcf.gen.eclipse.core.luncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
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
import jcf.gen.eclipse.core.utils.ColumnNameCamelCaseMap;

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
		Map<String, Object> model = this.makeMapData(arg, delArg);
		Map<String, Boolean> templateArg = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE_CHECK);
		
		if (delArg == null) {
			model.put(Constants.TABLE_NAME_PASCAL, userCaseName);
		}
		
		this.run(srcPath, packageName, userCaseName, model, templateArg);
		
		MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
		
		msg.setText("JCF");
		msg.setMessage("JCF Source Generate");
		msg.open();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> execute(String packageName, String userCaseName, Map<String, Object> arg, Set<String> delArg) {
		Map<String, Object> model = this.makeMapData(arg, delArg);
		Map<String, Boolean> templateArg = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE_CHECK);
		
		if (delArg == null) {
			model.put(Constants.TABLE_NAME_PASCAL, userCaseName);
		}
		
		return this.preview(packageName, userCaseName, model, templateArg);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> makeMapData(Map<String, Object> arg, Set<String> delArg) {
		ColumnNameCamelCaseMap columnNameCamelCase = new ColumnNameCamelCaseMap();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<TableColumns> columnList = (List<TableColumns>) arg.get(Constants.COLUMNS);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		if (delArg != null) {
			for (TableColumns col : columnList) {
				String colName = col.getColumnName();
				
				if (!delArg.contains(colName)) {
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put(Constants.COL_TABLE_NAME, col.getTableName());
					map.put(Constants.COL_TABLE_COMMENT, col.getTableComment());
					map.put(Constants.COL_COLUMN_NAME, col.getColumnName());
					map.put(Constants.COL_COLUMN_COMMENT, col.getColumnCommnet());
					map.put(Constants.COL_PK, col.getPk());
					map.put(Constants.COL_DATA_TYPE, col.getDataType());
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
					
					list.add(map);
				}
			}
			
			String tableName = (String) arg.get(Constants.TABLENAME);
			
			model.put(Constants.TABLENAME, tableName);
			model.put(Constants.TABLE_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(tableName));
			model.put(Constants.TABLE_NAME_PASCAL, columnNameCamelCase.pascalCaseConverter(tableName));
			model.put(Constants.COLUMNS, list);
			model.put(Constants.SHARP, "#");
			model.put(Constants.DOLLOR, "$");
			model.put(Constants.TABLE_COMMENT, (list.get(0)).get(Constants.COL_TABLE_COMMENT));
			
			String isPkExist = hasPrimaryKeyInList(list) ? Constants.IS_PK_EXIST_Y : Constants.IS_PK_EXIST_N;
			model.put(Constants.IS_PK_EXIST, isPkExist);
			
		} else {
			for (TableColumns col : columnList) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put(Constants.COL_COLUMN_NAME, col.getColumnName());
				map.put(Constants.COL_DATA_TYPE, col.getDataType());
				map.put(Constants.COLUMN_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(col.getColumnName()));
				map.put(Constants.COLUMN_NAME_PASCAL, columnNameCamelCase.modelCaseConverter(col.getColumnName()));
				
				list.add(map);
			}
			
			model.put(Constants.COLUMNS, list);
		}
		
		return model;
	}
	
	private void run(String srcPath, String packageName, String userCaseName, Map<String, Object> model, Map<String, Boolean> templateArg) {
		if (templateArg.get(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();		
			controlGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (templateArg.get(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			serviceGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (templateArg.get(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			modelGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (templateArg.get(Constants.SQLMAP_FILE)) {
			SqlMapGenerator sqlMapGenerator = new SqlMapGenerator();
			sqlMapGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
		
		if (templateArg.get(Constants.GROOVY_FILE)) {
			GroovyGenerator groovyGenerator = new GroovyGenerator();
			groovyGenerator.generatorFile(srcPath, packageName, userCaseName, model);
		}
	}
	
	private Map<String, String> preview(String packageName, String userCaseName, Map<String, Object> model, Map<String, Boolean> templateArg) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(Constants.CONTROLLER, "");
		map.put(Constants.SERVICE, "");
		map.put(Constants.MODEL, "");
		map.put(Constants.SQLMAP, "");
		map.put(Constants.GROOVY, "");
		
		if (templateArg.get(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();
			map.put(Constants.CONTROLLER, controlGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (templateArg.get(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			map.put(Constants.SERVICE, serviceGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (templateArg.get(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			map.put(Constants.MODEL, modelGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (templateArg.get(Constants.SQLMAP_FILE)) {
			SqlMapGenerator sqlMapGenerator = new SqlMapGenerator();
			map.put(Constants.SQLMAP, sqlMapGenerator.generatorText(packageName, userCaseName, model));
		}
		
		if (templateArg.get(Constants.GROOVY_FILE)) {
			GroovyGenerator groovyGenerator = new GroovyGenerator();
			map.put(Constants.GROOVY, groovyGenerator.generatorText(packageName, userCaseName, model));
		}
		
		return map;
	}
	
}

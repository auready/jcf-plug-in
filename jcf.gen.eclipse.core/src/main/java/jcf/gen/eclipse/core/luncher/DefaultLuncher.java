package jcf.gen.eclipse.core.luncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import jcf.gen.eclipse.core.generator.controller.ControlGenerator;
import jcf.gen.eclipse.core.generator.dao.GroovyGenerator;
import jcf.gen.eclipse.core.generator.service.ServiceGenerator;
import jcf.gen.eclipse.core.generator.model.ModelGenerator;
import jcf.gen.eclipse.core.jdbc.model.TableColumns;
import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.ColumnNameCamelCaseMap;
import jcf.gen.eclipse.core.utils.DbUtils;
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
	public void execute(String srcPath, Map<String, Object> arg, Set<String> delArg) {
		Map<String, Object> model = this.makeMapData(arg, delArg);
		Map<String, Boolean> templateArg = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE_CHECK);
		
		this.run(srcPath, model, templateArg);
		
		MessageBox msg = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
		
		msg.setText("JCF");
		msg.setMessage("JCF Source Generate");
		msg.open();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> execute(Map<String, Object> arg, Set<String> delArg) {
		Map<String, Object> model = this.makeMapData(arg, delArg);
		Map<String, Boolean> templateArg = (HashMap<String, Boolean>) arg.get(Constants.TEMPLATE_CHECK);
		
		return this.preview(model, templateArg);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> makeMapData(Map<String, Object> arg, Set<String> delArg) {
		ColumnNameCamelCaseMap columnNameCamelCase = new ColumnNameCamelCaseMap();
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<TableColumns> columnList = (List<TableColumns>) arg.get(Constants.COLUMNS);
		
		Map<String, Object> model = new HashMap<String, Object>();
		boolean hasNumberType = false;
		
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
				map.put(Constants.COLUMN_DATA_TYPE, DbUtils.convertToDataType(col.getDataType()));
				map.put(Constants.COLUMN_NAME_CAMEL, columnNameCamelCase.camelCaseConverter(col.getColumnName()));
				map.put(Constants.COLUMN_NAME_PASCAL, columnNameCamelCase.modelCaseConverter(col.getColumnName()));
				
				if (!hasNumberType) {
					if (DbUtils.hasNumberType(col.getDataType())) hasNumberType = true;
				}
				
				list.add(map);
			}
			
			model.put(Constants.COLUMNS, list);
		}
		
		model.put(Constants.IMPORT_MATH_CLASS, (hasNumberType ? Constants.IMPORT_BIG_DECIMAL : Constants.IMPORT_NULL));
		model.put(Constants.BIZ_NAME, (String) arg.get(Constants.BIZ_NAME));
		model.put(Constants.AUTHOR, (String) arg.get(Constants.AUTHOR));
		
		String actionFileName = (String) arg.get(Constants.ACTION_PKG_NAME);
		String serviceFileName = (String) arg.get(Constants.SERVICE_PKG_NAME);
		String modelFileName = (String) arg.get(Constants.MODEL_PKG_NAME);
		String groovyFileName = (String) arg.get(Constants.GROOVY_PKG_NAME);
		
		int idx = StrUtils.seperateFileFromPkg(actionFileName);
		model.put(Constants.ACTION_PKG_NAME, actionFileName.substring(0, idx));
		model.put(Constants.ACTION_FILE_NAME, actionFileName.substring(idx + 1));
		
		idx = StrUtils.seperateFileFromPkg(serviceFileName);
		model.put(Constants.SERVICE_PKG_NAME, serviceFileName.substring(0, idx));
		model.put(Constants.SERVICE_FILE_NAME, serviceFileName.substring(idx + 1));
		
		idx = StrUtils.seperateFileFromPkg(modelFileName);
		model.put(Constants.MODEL_PKG_NAME, modelFileName.substring(0, idx));
		model.put(Constants.MODEL_FILE_NAME, modelFileName.substring(idx + 1));
		
		idx = StrUtils.seperateFileFromPkg(groovyFileName);
		model.put(Constants.GROOVY_PKG_NAME, groovyFileName.substring(0, idx));
		model.put(Constants.GROOVY_FILE_NAME, groovyFileName.substring(idx + 1));
		
		return model;
	}
	
	private void run(String srcPath, Map<String, Object> model, Map<String, Boolean> templateArg) {
		if (templateArg.get(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();		
			controlGenerator.generatorFile(srcPath, model);
		}
		
		if (templateArg.get(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			serviceGenerator.generatorFile(srcPath, model);
		}
		
		if (templateArg.get(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			modelGenerator.generatorFile(srcPath, model);
		}
		
		if (templateArg.get(Constants.GROOVY_FILE)) {
			GroovyGenerator groovyGenerator = new GroovyGenerator();
			groovyGenerator.generatorFile(srcPath, model);
		}
	}
	
	private Map<String, String> preview(Map<String, Object> model, Map<String, Boolean> templateArg) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put(Constants.CONTROLLER, "");
		map.put(Constants.SERVICE, "");
		map.put(Constants.MODEL, "");
		map.put(Constants.GROOVY, "");
		
		if (templateArg.get(Constants.CONTROLLER_FILE)) {
			ControlGenerator controlGenerator = new ControlGenerator();
			map.put(Constants.CONTROLLER, controlGenerator.generatorText(model));
		}
		
		if (templateArg.get(Constants.SERVICE_FILE)) {
			ServiceGenerator serviceGenerator = new ServiceGenerator();
			map.put(Constants.SERVICE, serviceGenerator.generatorText(model));
		}
		
		if (templateArg.get(Constants.MODEL_FILE)) {
			ModelGenerator modelGenerator = new ModelGenerator();
			map.put(Constants.MODEL, modelGenerator.generatorText(model));
		}
		
		if (templateArg.get(Constants.GROOVY_FILE)) {
			GroovyGenerator groovyGenerator = new GroovyGenerator();
			map.put(Constants.GROOVY, groovyGenerator.generatorText(model));
		}
		
		return map;
	}
	
}

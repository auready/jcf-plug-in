package jcf.gen.eclipse.core.generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.Assert;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.utils.FileUtils;
import jcf.gen.eclipse.core.utils.StrUtils;

public abstract class AbstractSourceGenerator implements SourceGenerator {
	
	public AbstractSourceGenerator() {
		init();
	}
	
	public abstract String getVmFileName();
	
	public abstract String getFileName(Map<String,Object> infoModel);

	public abstract String getPackagePath(String basePackPath);
	
	protected void init() {
		try {
			VelocityEngineFactory vef = new VelocityEngineFactory();

			Properties prop = new Properties();
			
			prop.put("input.encoding", "utf-8");
			prop.put("output.encoding", "utf-8");
			
			vef.setResourceLoaderPath("file:" + getPreferenceString(Constants.TEMPLATE_DIRECTORY));
			vef.setVelocityProperties(prop);
			
			setVelocityEngine(vef.createVelocityEngine());
			
		} catch (VelocityException ve) {
			throw new VelocityException(ve.toString());
		} catch (IOException ioe) {
			throw new VelocityException(ioe.toString());
		}
	}
	
	protected String getSeperator() {
		Properties properties = System.getProperties();
		String seperator = StrUtils.nts((String) properties.get("file.separator"));
		return seperator;
	}
	
	private String getBasePath(String srcPath, String packageName) {
		String seperator = getSeperator();
		String packagePath = StrUtils.replaceAll(packageName, ".", seperator);
		
		return new StringBuilder(srcPath).append(seperator).append(packagePath).toString();
	}
	
	private Map<String, Object> initMapData(String packageName, String userCaseName, Map<String, Object> model) {
		Assert.hasText(packageName, "Package name must not be null or empty");
		Assert.hasText(userCaseName, "Vo name must not be null or empty");
		
		model.put(Constants.PACKAGE, packageName);
//		model.put(Constants.SERVICE_MAPPING, StrUtils.getServicePath(packageName));
		model.put(Constants.UC_NAME, StringUtils.capitalize(userCaseName));
		model.put(Constants.UC_NAME_CAMEL, StringUtils.uncapitalize(userCaseName));
		
		return model;
	}
	
	private IPreferenceStore getPreferenceStore() {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore();
	}
	
	protected String getPreferenceString(String name) {
		return getPreferenceStore().getString(name);
	}
	
	private VelocityEngine velocityEngine;

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	
	public void generatorFile(String srcPath, String packageName, String userCaseName, Map<String, Object> model) {
		model = initMapData(packageName, userCaseName, model);
		
		String fullPackagePath = getPackagePath(getBasePath(srcPath, packageName));
		
		FileUtils.makeDirectories(fullPackagePath);
		
		String fileWithFullPath = new StringBuilder(fullPackagePath).append(getSeperator()).append(getFileName(model)).toString();
		
		try {
			FileOutputStream fos = new FileOutputStream(fileWithFullPath);
			Writer writer = new OutputStreamWriter(fos, "UTF-8");
			
			VelocityEngineUtils.mergeTemplate(velocityEngine, getVmFileName(), model, writer);
			
			writer.close();
			
		} catch (FileNotFoundException fnfe) {
			throw new VelocityException(fnfe.toString());
		} catch (IOException ioe) {
			throw new VelocityException(ioe.toString());
		}	
	}
	
	public String generatorText(String packageName, String userCaseName, Map<String, Object> model) {
		model = initMapData(packageName, userCaseName, model);
		
		String result = "";
		
		try {
			StringWriter writer = new StringWriter();
			
			VelocityEngineUtils.mergeTemplate(velocityEngine, getVmFileName(), model, writer);
			
			result = writer.toString();
			
			writer.close();
			
			return result;
			
		} catch (IOException ioe) {
			throw new VelocityException(ioe.toString());
		}
	}
}
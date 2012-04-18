package jcf.gen.eclipse.core.generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ui.velocity.VelocityEngineUtils;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.FileUtils;
import jcf.gen.eclipse.core.utils.MessageUtil;
import jcf.gen.eclipse.core.utils.StrUtils;

public abstract class AbstractSourceGenerator implements SourceGenerator {
	
	public AbstractSourceGenerator() {
		ClassPathXmlApplicationContext classPathXmlApplicationContext 
				= new ClassPathXmlApplicationContext("/config/applicationContext-generator.xml", AbstractSourceGenerator.class);
		classPathXmlApplicationContext.getAutowireCapableBeanFactory()
				.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
	}
	
	public abstract String getVmFileName();
	
	public abstract String getFileName(Map<String,Object> infoModel);

	public abstract String getPackagePath(String basePackPath);
	
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
	
	private VelocityEngine velocityEngine;

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	
	public void generatorFile(String srcPath, String packageName, String userCaseName, Map<String, Object> model) {		
		if (StringUtils.isNotEmpty(userCaseName)) {
			model.put(Constants.UC_NAME, userCaseName);
			model.put(Constants.UC_NAME_CAMEL, StringUtils.uncapitalize(userCaseName));
		} else {
			throw new RuntimeException(MessageUtil.getMessage("exception.runtime.usercase"));
		}
		
		if (StringUtils.isNotEmpty(packageName)) {
			model.put(Constants.PACKAGE, packageName);
		} else {
			throw new RuntimeException(MessageUtil.getMessage("exception.runtime.package"));
		}
		
		String fullPackagePath = getPackagePath(getBasePath(srcPath, packageName));
		
//		FileUtils.removeDirectories(fullPackagePath, true);
		FileUtils.makeDirectories(fullPackagePath);
		
		String fileWithFullPath = new StringBuilder(fullPackagePath).append(getSeperator()).append(getFileName(model)).toString();
		
		try {
			FileOutputStream fos = new FileOutputStream(fileWithFullPath);
			Writer writer = new OutputStreamWriter(fos);
			
			VelocityEngineUtils.mergeTemplate(velocityEngine, getVmFileName(), model, writer);
			
			writer.close();
			
		} catch (FileNotFoundException fnfe) {
			throw new VelocityException(fnfe.toString());
		} catch (IOException ioe) {
			throw new VelocityException(ioe.toString());
		}
		
	}
}
package jcf.gen.eclipse.core.generator;

import java.util.Map;

public interface SourceGenerator {
	public void generatorFile(String srcPath, Map<String, Object> model);
}

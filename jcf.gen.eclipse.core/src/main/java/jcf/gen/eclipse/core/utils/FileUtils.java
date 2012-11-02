package jcf.gen.eclipse.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public static final int BUFFER_SIZE = 4096;
	
	public static boolean makeDirectories(String path) {        
        File f = new File(path);

        if (!f.exists() && !f.isDirectory()) {
        	return f.mkdirs();
        } else {
        	logger.debug("Path already existing on the file system. Exiting...");
        }

        return false;
    }
	
	public static boolean removeDirectories(String path, boolean flag) {
    	if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);

            if(f.isDirectory() && f != null) {
            	if (f.exists() && f.isDirectory() && (f.listFiles().length < 1)) {
            		return f.delete();
            	} else {
            		if(flag) {
            			for(File file : f.listFiles()) {
            				file.delete();
            			}
            			
            			f.delete();
            		}
            		return f.delete();
            	}
            } else return true;
        }

        return false;
    }
	
	public static HashMap<String, String> readPropertyFile(String filePath) {
		HashMap<String, String> resMap = new HashMap<String, String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			String str = null;
			
			while ((str = br.readLine()) != null) {
				if (!str.startsWith("#")) {
					int idx = str.indexOf("=");
					
					if (idx > 0) resMap.put(str.substring(0, idx), str.substring(idx + 1));
				}
			}
			
			br.close();
			
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
		
		return resMap;
	}
	
	public static String[] readPropertyFiles(String path) {
		String data = "";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			String temp = br.readLine();
			data = temp.substring(temp.indexOf("=") + 1);
			
			br.close();
			
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage());
		}
		
		return data.split(",");
	}
	
	public static byte[] getFileToByteArray(String fullFilePath) {
		FileInputStream fis;
		
		byte[] out = null;
		
		try {
			fis = new FileInputStream(fullFilePath);
			out = new byte[fis.available()];

			fis.read(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
	}
}

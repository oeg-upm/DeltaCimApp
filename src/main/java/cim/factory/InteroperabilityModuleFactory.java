package cim.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cim.ConfigTokens;
import helio.framework.objects.Tuple;

public class InteroperabilityModuleFactory {
	
	private static Logger log = Logger.getLogger(InteroperabilityModuleFactory.class.getName());

	private InteroperabilityModuleFactory() {
		// empty
	}
	
	public static Tuple<String, String> readInteroperabilityModule(String moduelFile) {
		Tuple<String, String> mappingsContent = new Tuple<>();
		try {
			ZipFile zipFile = new ZipFile(moduelFile);
	
		    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	
		    while(entries.hasMoreElements()){
		        ZipEntry entry = entries.nextElement();
		        String fileName = ConfigTokens.MODULES_BASE_DIR + entry.getName();
				if (isReadingFile(fileName)) {
					String rawMappping = readZippedFile(zipFile, entry);
					mappingsContent.setFirstElement(rawMappping);
				} else if (isWrittingFile(fileName)) {
					mappingsContent.setSecondElement(readZippedFile(zipFile, entry));
				}
		    }
		    zipFile.close();
		    
		} catch (IOException e) {
			log.severe(e.toString());
		} 
		
		return mappingsContent;
	}
	
	private static boolean isReadingFile(String fileDir) {
		return fileDir.contains(ConfigTokens.MODULES_BASE_DIR_READING) && !fileDir.contains(".DS_Store") && !fileDir.endsWith(ConfigTokens.MODULES_BASE_DIR_READING);
	}
	
	private static boolean isWrittingFile(String fileDir) {
		return fileDir.contains(ConfigTokens.MODULES_BASE_DIR_WRITTING) && !fileDir.contains(".DS_Store") && !fileDir.endsWith(ConfigTokens.MODULES_BASE_DIR_WRITTING);
	}
	
	private static String readZippedFile(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
		StringBuilder fileContent = new StringBuilder();
         BufferedReader zipReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
         while (zipReader.ready()) {
        	 	fileContent.append(zipReader.readLine()).append("\n");
         }
         zipReader.close();
         String data = null;
         if(!fileContent.toString().isEmpty())
        	 	data = fileContent.toString();
         return data;
	}
	

}

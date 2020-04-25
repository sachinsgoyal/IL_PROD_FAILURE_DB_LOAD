package com.github.sachin;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Map;

public class ReadCommonError {	
	public static void readCommonError(Map<String, Boolean> commonErrormap) {
		try {
			String FileToRead="C:\\Users\\sgoyal\\Documents\\JAVA_LEARN\\Java_Learning_workspace\\IL_PROD_FAILURE_DB_LOAD\\src\\CommonErrors.txt";
			File file = new File(FileToRead);
			FileReader fr = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fr);
			String line = "";
			String[] val;
			boolean flag=false;
			while ((line = lnr.readLine()) != null) {
				val = line.split("=");
				flag = Boolean.parseBoolean(val[1]);
				SimpleCsv2DbInserter.logger.info(line+" Error FLAG"+flag);
				commonErrormap.put(val[0], flag);
				flag=false;
			}
			lnr.close();fr.close();
			file=null;fr=null;lnr=null;
		}catch(Exception e) {		
			SimpleCsv2DbInserter.logger.error("Error"+e.getMessage(), e);
			e.printStackTrace();
		}
	}
}

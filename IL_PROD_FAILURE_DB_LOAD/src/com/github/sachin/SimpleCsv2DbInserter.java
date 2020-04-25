package com.github.sachin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleCsv2DbInserter {
	
	public static final Logger logger = LoggerFactory.getLogger(SimpleCsv2DbInserter.class);
	static Set<DataObject> dbData = new HashSet<DataObject>();	
	public static Map<String, Boolean> commonErrormap = new HashMap<String, Boolean>();
	
	public static void main(String[] args)  {
		try {			
		logger.info("Execution Started ");
		
		logger.info("Read Start Common Error");
		ReadCommonError.readCommonError(commonErrormap);
		logger.info("Read END Common Error");
				
		logger.info("Read All Files Start");
		readallfiles();		
		logger.info("Read All Files END");
		
		logger.info("DB INSERTION START");
		DBWriter.dbWrite(dbData);
		logger.info("DB INSERTION END");
		
		logger.info("Execution END ");
		}catch(Exception e){
			logger.error("Error"+e.getMessage(), e);
		}
	}	
	
	public static void readallfiles() throws IOException {	
		Files.walk(Paths.get("C:\\Users\\sgoyal\\Documents\\FailureReport\\")).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				readFile(filePath);
			}
		});
		
	}
	public static void readFile(Path filePath){
		try {
		BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
		FileTime flcreatetime =attributes.creationTime();
		Instant filecreatetime=flcreatetime.toInstant();		
		Timestamp filetimestamp = Timestamp.from(filecreatetime);
		
		logger.debug("File Path--"+filePath);
		File datafile =filePath.toFile();		
		FileReader fr = new FileReader(datafile);
		LineNumberReader lnr = new LineNumberReader(fr);
			String line = "",shorterror="";
			String[] val;
			int failcount,requestid;
			boolean commonerr=false; 
			DataObject dao;
			while ((line = lnr.readLine()) != null ) {			
				line=line.trim().replaceAll(" +", " ");
				
				if(line.isEmpty() ||line.contains("COUNT(*)") || line.contains("----------") || line.contains("SP2-0734: unknown command") ) {
					logger.trace("Ignored Lines="+line);
					continue;		
				}
				
				
				val = line.split(",");
				failcount=Integer.parseInt(val[0]);
				requestid=Integer.parseInt(val[val.length-1].trim());
				
				line = line.replaceAll(", "+requestid, "");
				line = line.replaceAll(failcount+",", "");		
				
				if(line.contains("(") && line.contains(")"))
					shorterror = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
				else
					shorterror = line;
				
				for(String cmerr:commonErrormap.keySet()) {
					if(line.contains(cmerr)) {						
						shorterror=cmerr;
						commonerr=commonErrormap.get(cmerr);
						logger.trace(shorterror+"--Error FLAG--"+commonerr);
					}
					//else
						//logger.info(line+"--Error FLAG--"+commonerr+"--checked with--"+cmerr);
				}
				
				logger.trace(filetimestamp+"#"+failcount+"#"+requestid+"#"+shorterror+"#"+commonerr+"#"+line);
				dao=new DataObject(filetimestamp,failcount,requestid,line,commonerr,shorterror);
				dbData.add(dao);
				shorterror ="";commonerr=false;
			}
			lnr.close();fr.close();fr=null;lnr=null;
			//FileOutputStream outStream = new FileOutputStream();
			//outStream.
			//String outfilename="C:\\Users\\sgoyal\\Documents\\Processed_Files\\Proc_"+filetimestamp;
			//boolean test=datafile.renameTo(new File(outfilename));
			//System.out.println(test);
			//datafile.delete(); //To Delete the file after process
			
			datafile=null;
		}catch(IOException e) {
			logger.error(" File Reade Error"+e.getMessage(), e);
		}catch(Exception e) {
			logger.error("Error Inside readFile method"+e.getMessage(), e);
		}
	}	
}
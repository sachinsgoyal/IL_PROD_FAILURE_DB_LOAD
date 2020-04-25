package com.github.sachin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefineExistingDB {	
	
	public static Map<String, Boolean> commonErrormap = new HashMap<String, Boolean>();
	static Set<DataObject> dbData = new HashSet<DataObject>();	
	
	public static void main(String[] args) {
		System.out.println("START");
		ReadCommonError.readCommonError(commonErrormap);
		System.out.println("END ERROR COUNT"+commonErrormap.size());
		refineDB();
		System.out.println("END DAO COUNT"+dbData.size());
		DBWriter.dbWrite(dbData);
	}	

	public static void refineDB() {
		try {
		String FileToRead="C:\\Output\\dupe_il_fail_report_202004241331.csv";
		File file = new File(FileToRead);
		FileReader fr = new FileReader(file);
		LineNumberReader lnr = new LineNumberReader(fr);
		String line = "",shorterror="",fullerror="";
		String[] val;
		boolean commonerr=false;
		DataObject dao;
		Integer failcount=0,requestid=0;
		Timestamp filetimestamp;
		while ((line = lnr.readLine()) != null) {
			if(line.isEmpty() ||line.contains("\"failcount\"#\"requestid\"#\"commonerr\"#\"shortdesc\"#\"description\"")) {
				continue;		
			}
			
			val = line.split("#");		
			filetimestamp = Timestamp.valueOf(val[0]);
			failcount=Integer.parseInt(val[1]);
			requestid=Integer.parseInt(val[2]);
			commonerr = Boolean.parseBoolean(val[3]);
			shorterror=val[4];
			fullerror=val[5];	
			
			for(String cmerr:commonErrormap.keySet()) {
				if(fullerror.contains(cmerr)) {						
					shorterror=cmerr;
					commonerr=commonErrormap.get(cmerr);
				}
			}
			
			dao=new DataObject(filetimestamp,failcount,requestid,fullerror,commonerr,shorterror);
			dbData.add(dao);
		}
		lnr.close();fr.close();
		file=null;fr=null;lnr=null;
	}

	catch(Exception e) {
		e.printStackTrace();
	}
}
}

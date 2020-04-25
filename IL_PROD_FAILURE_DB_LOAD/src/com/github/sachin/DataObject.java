package com.github.sachin;

import java.sql.Timestamp;

public class DataObject {
	private Timestamp filetimestamp;
	private int failcount;
	private int requestid;
	private String description;
	private boolean commonerr;
	private String shortdesc;
	
	public String getshortdesc() {
		return shortdesc;
	}

	public Timestamp getFiletimestamp() {
		return filetimestamp;
	}

	public int getFailcount() {
		return failcount;
	}
	
	public int getRequestid() {
		return requestid;
	}
	
	public String getDescription() {
		return description;
	}	

	public boolean isCommonerr() {
		return commonerr;
	}

	DataObject(Timestamp infiletimestamp, int infailcount, int inrequestid, String indescription, boolean incommonerr,String inshortdesc) {
		filetimestamp = infiletimestamp;
		failcount = infailcount;
		requestid = inrequestid;
		description = indescription;
		commonerr = incommonerr;
		shortdesc=inshortdesc;
	}
}

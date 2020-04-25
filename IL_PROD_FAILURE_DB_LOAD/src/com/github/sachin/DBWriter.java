package com.github.sachin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class DBWriter {
	
	 public static void dbWrite(Set<DataObject> dbData) {
	    	String jdbcURL = "jdbc:postgresql://sachin-redvm2:5432/sachin";
	        String username = "sachin";
	        String password = "sachin";
	        
	        Connection connection = null;
	         try {
	 
	            connection = DriverManager.getConnection(jdbcURL, username, password);
	            connection.setAutoCommit(false);
	 
	            String sql = "INSERT INTO il_fail_report (filedate, failcount, requestid, commonerr, shortdesc, description) VALUES (?, ?, ?, ?, ?, ?)";
	            PreparedStatement statement = connection.prepareStatement(sql);
	 
	            for(DataObject dao:dbData ) {
	                statement.setTimestamp(1,dao.getFiletimestamp());
	                statement.setInt(2,dao.getFailcount());
	                statement.setInt(3,dao.getRequestid());
	                statement.setBoolean(4, dao.isCommonerr());
	                statement.setString(5, dao.getshortdesc());
	                statement.setString(6, dao.getDescription());
	                statement.addBatch();
					
	            } 
	            // execute the remaining queries
	            statement.executeBatch();
	 
	            connection.commit();
	            connection.close();
	 
	        }  catch (SQLException ex) {
	        	SimpleCsv2DbInserter.logger.error("Error"+ex.getMessage(), ex);
	 
	            try {
	                connection.rollback();
	            } catch (SQLException e) {
	            	SimpleCsv2DbInserter.logger.error("Rollback Error"+e.getMessage(), e);
	            }
	        }
	         catch (Exception ex) {
	        	 SimpleCsv2DbInserter.logger.error("Error"+ex.getMessage(), ex);
	         }         
	    }
	
	
}

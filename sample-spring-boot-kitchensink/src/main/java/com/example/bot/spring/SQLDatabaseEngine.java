package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		Connection connection=null;
		String result=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			connection=this.getConnection();
			stmt = connection.prepareStatement("SELECT weight, measure FROM nutrient_table where description like concat('%', ? , '%')");
			stmt.setString(1,text); //the input
			rs = stmt.executeQuery();	
			if(rs.next()){
				weight=rs.getString(1);
				measure=rs.getString(2);
				// int fre=rs.getInt(2);
				// fre++;
				// stmt = connection.prepareStatement("UPDATE resH SET frequency = ? WHERE response = ?");
				// stmt.setInt(1,fre);
				// stmt.setString(2, result);
				// stmt.executeUpdate();
				result =  "The weight is " + weight + "\n The measure is "+measure; 
			}
			
    	} catch (Exception e) {
    		log.info("SQLException while searching: {}", e.toString());
    	}finally{
			try{
				if(rs.next())
					rs.close();
				if(stmt!=null)
					stmt.close();
				if(connection!=null)
					connection.close();
			}catch (Exception ex) {
    			log.info("SQLException while closing: {}", ex.toString());
    		}

		}

    	if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort()  +dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}

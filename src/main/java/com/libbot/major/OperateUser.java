package com.libbot.major;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.ResponseBean;
import beans.UserBean;

@Path("user")
public class OperateUser {
	
	Connection con;
    Statement s;
    ResultSet rs;
    URI dbUri;
    String username;
    String password;
    String dbUrl;
    Properties props;
	public OperateUser() {
		super();
		try
		 {
			 dbUri = new URI("postgres://tzczhqfnqssqyk:9ca0c628a0453a959e1a98f26365cbfe9f8a67290eab40ec41e78580c5df5ec9@ec2-54-163-234-88.compute-1.amazonaws.com:5432/d5agkiio1i15o9");

			 Class.forName("org.postgresql.Driver");
			  username = dbUri.getUserInfo().split(":")[0];
	          password = dbUri.getUserInfo().split(":")[1];
	          dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
	          props = new Properties();
	         props.setProperty("user",username);
	         props.setProperty("password",password);
	         props.setProperty("ssl","true");
	         props.setProperty("sslfactory","org.postgresql.ssl.NonValidatingFactory");
	         
	      }
		 catch(Exception e)
		 {}
	}
	
	@GET
	@Path("get/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserBean getUser(@PathParam("username") String username)
	{
		UserBean user=null;
		try
		{
			con = DriverManager.getConnection(dbUrl, props);
	        s = con.createStatement(); 
	        String query = "select * from users where username ='" + username + "';";
	        
	        rs = s.executeQuery(query);

            if (rs.next()) {
            	user=new UserBean();
            	user.setEmail(rs.getString(3));
            	user.setUsername(rs.getString(1));
            	user.setType(rs.getString(5));
            	user.setPassword(rs.getString(2));
            	user.setName(rs.getString(4));
            }
            con.close();
		}
		catch(Exception e)
		{}
		return user;
	}
	
	 @PUT
	 @Path("add")
	 @Consumes(MediaType.APPLICATION_JSON)
	 @Produces(MediaType.APPLICATION_JSON)
	    public ResponseBean addUser(UserBean user)
	    {
	    	ResponseBean res=new ResponseBean();
	    	try {
	    		con = DriverManager.getConnection(dbUrl, props);
	    		s = con.createStatement(); 
	    		String query="insert into users values('" + user.getUsername()+ "','" + user.getPassword()
	    		+ "','"+user.getEmail() + "','"+user.getName() + "','" + user.getType() + "');";
	    	
	    		int a=s.executeUpdate(query);
	    	}
	    	catch(Exception e) {
	    		res.setStatus(400);
	    		res.setMessage("Error Saving Data");
	    		return res;
	    	}
	    	
	    	res.setStatus(200);
			res.setMessage("Data Saved Successfully");
	    	return res;
	    }
	

}

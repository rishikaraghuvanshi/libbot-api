package com.libbot.major;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import beans.BookBean;
import beans.UserBean;

@Path("books")
public class OperateBooks {
	
	Connection con;
    Statement s;
    ResultSet rs;
    URI dbUri;
    String username;
    String password;
    String dbUrl;
    Properties props;
    
	public OperateBooks() {
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
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookBean> getBooks()
	{
		ArrayList<BookBean> books=new ArrayList<>();
		BookBean book=null;
		try
		{
			con = DriverManager.getConnection(dbUrl, props);
	        s = con.createStatement(); 
	        String query = "select * from books;";
	        
	        rs = s.executeQuery(query);

            while (rs.next()) {
            	book=new BookBean();
            	book.setBook_id(rs.getString(1));
            	book.setBook_name(rs.getString(2));
            	book.setAuthor(rs.getString(3));
            	book.setGenre(rs.getString(4));
            	book.setCopies(rs.getInt(5));
            	books.add(book);
            }
            con.close();
		}
		catch(Exception e)
		{}
		return books;
	}

}

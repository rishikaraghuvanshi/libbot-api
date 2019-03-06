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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import beans.BookBean;
import beans.ResponseBean;
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
	
	@GET
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookBean> searchBooks(@QueryParam("bookName") String bookName, @QueryParam("author") String author)
	{
		ArrayList<BookBean> books=new ArrayList<>();
		BookBean book=null;
		try
		{
			con = DriverManager.getConnection(dbUrl, props);
	        s = con.createStatement(); 
	        String query = "select * from books where name='"+bookName+"' and author='"+author+"';";
	        rs = s.executeQuery(query);
	        while(rs.next())
	        {
	        	book=new BookBean();
	        	book.setAuthor(author);
	        	book.setBook_name(bookName);
	        	book.setBook_id(rs.getString(1));
	        	book.setGenre(rs.getString(4));
            	book.setCopies(rs.getInt(5));
            	books.add(book);
	        }
		}
		catch(Exception e)
		{}
		return books;
	}
	@GET
	@Path("issue")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseBean issueBook(@QueryParam("id") String id, @QueryParam("username") String username)
	{
		ResponseBean res=new ResponseBean();
		try
		{
			con = DriverManager.getConnection(dbUrl, props);
			con.setAutoCommit(false);
	        s = con.createStatement(); 
	        String query = "select * from books where book_id='"+id+"';";
	        rs = s.executeQuery(query);
	        if(rs.next())
	        {
	        	if(rs.getInt(5)==0)
	        	{
	        		res.setMessage("Ooops..! Book unavailabe. Please try later");
	        		res.setStatus(400);
	        		con.close();
	        		return res;
	        	}
	        	query="update books set copies="+ String.valueOf(rs.getInt(5)-1)+" where book_id='"+id+"';";
	        	int a=s.executeUpdate(query);
	        	if(a==0 || a==-1)
	        	{
	        		res.setMessage("Incorrect input");
	        		res.setStatus(400);
	        		con.close();
	        		return res;
	        	}
	        	res.setMessage("Book issued. Please checkout at LRC gate");
	        	res.setStatus(200);
	        	con.commit();
	        	con.close();
	        	return res;
	        }
	        else
	        {
	        	res.setMessage("Book not found");
	        	res.setStatus(400);
	        	con.commit();
	    		con.close();
	        }
		}
		catch(Exception e)
		{}
		
		return res;
		
	}

}

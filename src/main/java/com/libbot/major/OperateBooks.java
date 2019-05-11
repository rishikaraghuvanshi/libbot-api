package com.libbot.major;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import beans.BookBean;
import beans.ResponseBean;
import beans.ReturnBean;
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
            	con.close();
	        }
		}
		catch(Exception e)
		{
			return null;
		}
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
	        	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
	        	dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
	    		Date date = new Date();
	    		Calendar to = Calendar.getInstance();
	    		Calendar from = Calendar.getInstance();
	        	to.setTime(date);
	        	to.add(Calendar.DATE, 15);
	        	query="insert into issue_books values(default,'"+dateFormat.format(from.getTime())+"','"
	        			+dateFormat.format(to.getTime())+ "',"+String.valueOf(id)+",'"+username+"',0,0);";
	        	a=s.executeUpdate(query);
	        	if(a==0 || a==-1)
	        	{
	        		res.setMessage("Error issuing book");
	        		res.setStatus(400);
	        		con.close();
	        		return res;
	        	}
	        	res.setMessage("Book issued with details- Issue date:"+dateFormat.format(from.getTime())+" Return date:"+dateFormat.format(to.getTime()) +" Please checkout at LRC gate");
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
	
	
	@GET
	@Path("getCheckout")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<BookBean> getCheckout( @QueryParam("username") String username)
	{
		ArrayList<BookBean> res=new ArrayList<>();
		try {
			
			con = DriverManager.getConnection(dbUrl, props);
	        s = con.createStatement(); 
	        Calendar cal = Calendar.getInstance();
        	cal.setTime(new Date());
        	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        	String from = dateFormat.format(cal.getTime());
        	 String query = "select book_id from issue_books where username='"+username+"'and from_date='"+from+"' and checkout=0;";
        	 rs=s.executeQuery(query );
        	 
        	 while(rs.next())
        	 {
        		 BookBean book = new BookBean();
        		 book.setBook_id(String.valueOf(rs.getInt(1)));
        		 res.add(book);
        	 }
        	 
        	 for(int i=0;i<res.size();i++)
        	 {
        		 String id =res.get(i).getBook_id();
        		 query="select name,author,genre from books where book_id='"+id+"';";
        		 rs=s.executeQuery(query);
        		 if(rs.next())
        		 {
        			 res.get(i).setBook_name(rs.getString(1));
        			 res.get(i).setAuthor(rs.getString(2));
        			 res.get(i).setGenre(rs.getString(3));
        		 }
        		 else {
        			 con.close();
        			 return null;
        		 }
        	 }
        	 
        	 con.close();
        	
		}catch(Exception e)
		{}
	
		return res;
	}
	
	@PUT
	@Path("checkout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseBean checkout(ArrayList<BookBean> books, @QueryParam("username") String username )
	{
		ResponseBean res = new ResponseBean();
		try {
			
			con = DriverManager.getConnection(dbUrl, props);
			con.setAutoCommit(false);
	        s = con.createStatement(); 
	        Calendar cal = Calendar.getInstance();
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
        	String from=  dateFormat.format(cal.getTime());
        	String query="";
	        for(int i=0;i<books.size();i++)
	        {
	        	query="update issue_books set checkout = 1 where username= '"+ username +"' and book_id='"+books.get(i).getBook_id()+"' and from_date='"+
	        			from+"';";
	        	s.addBatch(query);
	        }
	        
	        int a[] = s.executeBatch();
	        
	        if(a.length != books.size())
	        {
	        	res.setMessage("Sorry. Network Falied");
	        	res.setStatus(400);
	        	con.close();
	        	return res;
	        }
	        con.commit();
	        con.close();
	        res.setMessage("Successfully checked out");
	        res.setStatus(200);
	        
		}
		catch(Exception e) {
			res.setMessage(e.getMessage());
        	res.setStatus(400);
		}
		
		return res;
	}
	

	@GET
	@Path("returnBookDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<ReturnBean> returnBookDetails( @QueryParam("username") String username)
	{
		ArrayList<ReturnBean> res=new ArrayList<>();
		try {
			
			con = DriverManager.getConnection(dbUrl, props);
	        s = con.createStatement(); 
	        String query="select * from issue_books where return=0 and checkout=1 and username='"+username+"';";
	        
	        rs=s.executeQuery(query);
	        while(rs.next())
	        {
	        	ReturnBean returnBean = new ReturnBean();
	        	returnBean.setBook_id(rs.getString(4));
	        	returnBean.setReturnDate(rs.getString(3));
	        	returnBean.setIssueDate(rs.getString(2));
	        	
	        	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy");
	        	
	    		
	    			Date returndate = dateFormat.parse(rs.getString(3));
	    			int diffInDays = (int) ((returndate.getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
	    			int fine=0;
	    			if(diffInDays < 0) {
	    				fine= diffInDays * 10 * -1;
	    			}
	    			returnBean.setFine(fine);
	    		
	        }
	        
	        for(int i=0;i<res.size();i++)
	        {
	        	query="select name from books where book_id='"+res.get(i).getBook_id()+"';";
	        	res.get(i).setBook_name(rs.getString(1));
	        }
	        
	        query="select fine from users where username='"+username+"';";
	        rs=s.executeQuery(query);
	        
	        if(rs.next())
	        {
	        	for(int i=0;i<res.size();i++)
	        		res.get(i).setPreviousFine(rs.getInt(1));
	        }
	        s.close();
	        con.close();
		}catch(Exception e) {
			res.clear();
		}
		return res;
	}
	
	@PUT
	@Path("returnBooks")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseBean returnBook(ArrayList<ReturnBean> books, @QueryParam("username") String username)
	{
		ResponseBean res=new ResponseBean();
		try {
			con = DriverManager.getConnection(dbUrl, props);
			con.setAutoCommit(false);
	        s = con.createStatement(); 
	        String query="";
	        
	        if(books.get(0).isFineRecieved())
	        {
	        	query="update users set fine=0 where username='"+username+"';";
	        	s.addBatch(query);
	        }
	        for(int i=0;i<books.size();i++)
	        {
	        	query="update issue_books set return=1 where username='"+ username+"' and book_id='"+books.get(i).getBook_id()
	        			+"' ";
	        	s.addBatch(query);
	        	query="update books set copies = copies + 1 where book_id='"+books.get(i).getBook_id()+"'";
	        	s.addBatch(query);
	        }
	        
	        int a[] = s.executeBatch();
	        if(books.get(0).isFineRecieved())
	        {
	        	if(a.length < 2*books.size() +1)
	        	{
	        		res.setMessage("Error");
	        		res.setStatus(400);
	        		con.close();
	        	}
	        }
	        res.setMessage("Return successful");
	        res.setStatus(200);
	        con.close();
		}
		catch(Exception e)
		{}
		
		return res;
	}

}

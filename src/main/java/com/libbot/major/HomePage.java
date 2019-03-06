package com.libbot.major;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HomePage {
	
	 @GET
	 @Produces(MediaType.TEXT_HTML)
	 public String sayHtmlHello()
	 {
		 return "<html>" + 
		 		"<head>" + 
		 		"<title>Library bot's Api</title>" + 
		 		"</head>" + 
		 		"<Body>" + 
		 		"<h1>Welcome to Library Bot's Api</h1><br>" + 
		 		"<h2>Designed and maintained by: Rishika Raghuvansi & Shweta Sharma</h2>"+
		 		"</Body>" + 
		 		"</html>";
	 }

}

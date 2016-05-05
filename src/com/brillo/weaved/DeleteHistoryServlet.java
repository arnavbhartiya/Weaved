package com.brillo.weaved;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteHistoryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//**
	//@see HttpServlet#HttpServlet()
	//*
	public DeleteHistoryServlet() {
		super();
		// TODO Auto-generated constructor stubksonFactory;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//String userId=WeavedServlet.userId;
		 
		String userId = request.getParameter("userId"); 
		String eventId = request.getParameter("eventId"); 
		System.out.println(eventId);
		SQLConnector sql = new SQLConnector();
		Connection connection=null;
		try {  
			connection = sql.SQLConnectorURLgenerator("Credentials");
			System.out.println(eventId);
			deleteCommandsHistory(connection, response, userId, eventId);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void deleteCommandsHistory(Connection connection, HttpServletResponse response, String userId, String eventId) throws SQLException, IOException{
		String query = "SELECT EXISTS(SELECT * FROM USERSQL WHERE EntryID=\""+eventId+"\" and UserId=\""+userId+"\")";
		System.out.println(query);
		Statement mystmt = connection.createStatement();
		ResultSet myResult=mystmt.executeQuery(query);
		response.setContentType("text/html");  
		PrintWriter out = response.getWriter();
		myResult.next();
		int eventExists;
		eventExists = Integer.parseInt(myResult.getString(1));
		if(eventExists == 0){
			System.out.println("In event does not Exists");
			out.println("<html>");
			out.println("<body>");
			out.println("<p>Invalid Entry Id. Please make sure you enter the valid Entry Id. You can find your Entry Id by clicking the above \"RETRIEVE COMMAND HISTORY\" Button.</p>");
			out.println("</body>");
			out.println("</html>");
			
		}else{
			String deleteQuery = "DELETE FROM USERSQL WHERE EntryID=\""+eventId+"\" and UserId=\""+userId+"\"";
			System.out.println(deleteQuery);
			int res  = mystmt.executeUpdate(deleteQuery);
			System.out.println("Result" +res);
			out.println("<html>");
			out.println("<body>");
			out.println("<p>The Command History corresponding to "+eventId+" has been deleted. You can verify this by clicking the above \"RETRIEVE COMMAND HISTORY\"</p>");
			out.println("</body>");
			out.println("</html>");
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	
}

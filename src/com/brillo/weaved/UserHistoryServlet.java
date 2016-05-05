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


//**
//Servlet implementation class myServlet
//*

public class UserHistoryServlet extends WeavedServlet {

	private static final long serialVersionUID = 1L;
	//**
	//@see HttpServlet#HttpServlet()
	//*
	public UserHistoryServlet() {
		super();
		// TODO Auto-generated constructor stubksonFactory;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//String userId=WeavedServlet.userId;
		 
		String userId = request.getParameter("userId"); 
		SQLConnector sql = new SQLConnector();
		Connection connection=null;
		try {  
			connection = sql.SQLConnectorURLgenerator("Credentials");
			retrieveUserHistory(connection, response, userId);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void retrieveUserHistory(Connection connection, HttpServletResponse response, String userId) throws SQLException, IOException{
		String query = "SELECT * FROM USERSQL WHERE UserId=\""+userId+"\"";
		System.out.println(query);
		Statement mystmt = connection.createStatement();
		ResultSet myResult=mystmt.executeQuery(query);
		response.setContentType("text/html");  
		PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<body>");
			//out.println("<p> Hello from UserHistoryServlet</p>");
			int i=0;
			while( myResult.next()){
				i++;
				int EntryID=Integer.parseInt(myResult.getString("EntryID"));
				String EventDisp= myResult.getString("DevID");
				String DeviceName=myResult.getString("DevName");
				String Condition= myResult.getString("Conditions");
				String Action= myResult.getString("Actions");	
				out.println("<p>");
				out.println("Entry ID : " + EntryID);
				out.print("<br>");
				out.println("Device name : "+DeviceName);
				out.print("<br>");
				out.println("Device ID : "+EventDisp);
				out.print("<br>");
				out.println("Condition : "+Condition);
				out.print("<br>");
				out.println("Action JSON : "+Action);
				out.print("<br>");
				out.println("</p>");
			}
			if(i==0){
				out.println("<p>You do not have any commands saved! You can create commands in the \"Editor\" Section</p>");
			}
			out.println("</body>");
			out.println("</html>");
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}

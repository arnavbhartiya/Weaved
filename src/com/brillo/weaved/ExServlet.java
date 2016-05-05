package com.brillo.weaved;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class myServlet
 */
public class ExServlet extends WeavedServlet {

	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExServlet() {
		super();
		// TODO Auto-generated constructor stubksonFactory;
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String jsonScript = request.getParameter("jsonScript");
		String userId = request.getParameter("userId"); 
		System.out.println(userId);
		//response.getWriter().println(jsonScript);
		JSONParser jsonParse = new JSONParser();
		try {
			JSONObject jsonObj = (JSONObject) jsonParse.parse(jsonScript);
			try {
				JSONExtracter(jsonObj,userId,response);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private void JSONExtracter(JSONObject jsonObj,String userId,HttpServletResponse response) throws ClassNotFoundException, SQLException, ParseException, IOException{
		String deviceId;
		String condition;
		String action;
		String eventType;
		String event =  jsonObj.get("Event").toString();
		JSONParser jsonParse = new JSONParser();
		JSONObject ev = (JSONObject)jsonParse.parse(event);
		String deviceName = ev.get("DeviceName").toString(); 
		deviceId=ev.get("DeviceId").toString();
		eventType = ev.get("Type").toString();
		if(eventType.equalsIgnoreCase("Condition")){
			condition=jsonObj.get("Condition").toString();
			action=jsonObj.get("Action").toString();
			SQLinserter(deviceId, deviceName, userId, condition, action,response);
		}else if(eventType.equalsIgnoreCase("Update")){
			String updateString = jsonObj.get("Update").toString();
			JSONObject update=(JSONObject)jsonParse.parse(updateString);
			if(!update.isEmpty()){
				deviceUpdate(deviceId,update,deviceName,userId,response);
			}
		}
	}


	private void deviceUpdate(String deviceId, JSONObject update, String deviceName,String userId, HttpServletResponse response ) throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub
		if(deviceName.equalsIgnoreCase("LED")){
			LED led= new LED();
			led.LEDupdater(deviceId,update,deviceName,userId);
			PrintWriter out = response.getWriter();
			
			System.out.println("Success Message");
			out.println("<html>");
			out.println("<body>");
			out.println("<p>Success!! Device State Updated.</p>");
			out.println("</body>");
			out.println("</html>");
		}
	}
	
	private void SQLinserter(String deviceId, String deviceName, String UserId, String condition,String action,HttpServletResponse response) throws ClassNotFoundException, SQLException, ParseException, IOException{
		SQLConnector sql = new SQLConnector();
		Connection connection;
		connection = sql.SQLConnectorURLgenerator("Credentials");
		String query = "INSERT INTO USERSQL(DevID,DevName,UserId,Conditions,Actions) VALUES(\'"+deviceId+"\',\'"+deviceName+"\',\'"+UserId+"\',\'"+condition+"\',\'"+action+"\')";
		System.out.println(query);
		Statement mystmt = connection.createStatement();
		mystmt.executeUpdate(query);
		
		DeviceStatus ds =new DeviceStatus();
		
		try {
			ds.SQLFetchUser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		System.out.println("Success Message");
		out.println("<html>");
		out.println("<body>");
		out.println("<p>Success!! Your commands are up and running. You can check it in the COMMANDS HISTORY section</p>");
		out.println("</body>");
		out.println("</html>");
	}

}

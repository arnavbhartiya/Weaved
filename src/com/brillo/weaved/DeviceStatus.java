package com.brillo.weaved;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.clouddevices.CloudDevices;
import com.google.api.services.clouddevices.model.Command;
import com.google.api.services.clouddevices.model.Device;
import com.google.api.services.clouddevices.model.DevicesListResponse;


public class DeviceStatus implements Runnable{
	private static final String API_KEY = "AIzaSyDaR-PSdRK0psv1ZyDcaY2n2UFpZWui2UE";
	static CloudDevices apiClient;
	static JacksonFactory jsonFactory = WeavedServlet.jsonFactory;
	
	public void SQLFetchUser() throws ClassNotFoundException, SQLException, IOException, ParseException{
		CredentialBuilder credential = new CredentialBuilder();
		String databaseName = "Credentials";
		SQLConnector sql = new SQLConnector();
		Connection conn;
		conn = sql.SQLConnectorURLgenerator(databaseName);
		Statement stmt  = conn.createStatement();
		ResultSet myRes = stmt.executeQuery("SELECT UserId FROM "+databaseName);
		GoogleCredential cred;
		while(myRes.next()){
			cred = credential.returnCredentials(myRes.getString("UserId"));
			apiClient = getApiClient(cred);
			receiveDeviceList(apiClient);
		}
	}
	
	
	private CloudDevices getApiClient(GoogleCredential cred) throws IOException {
		
		return new CloudDevices.Builder(WeavedServlet.httpTransport, WeavedServlet.jsonFactory, cred)
				.setApplicationName("Weave Sample")
				.setServicePath("clouddevices/v1")
				.setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer(API_KEY))
				.build();
	}
	
	
	public void receiveDeviceList(CloudDevices apiClient) throws IOException, SQLException, ParseException{
		DevicesListResponse devicesListResponse;
		devicesListResponse = apiClient.devices().list().execute();
		List<Device> devices = devicesListResponse.getDevices();
		for(Device dev : devices){
			System.out.println( dev.getId());
		}
		new WeaveQuerier(devices);
		
		
		
	}


	protected static void sendCommand(Command command){
		System.out.println("Sending a new command to the device");
		System.out.println(command);
		try {
			command = apiClient.commands().insert(command).execute();
		} catch (IOException e) { throw new RuntimeException("Could not insert command", e); }

		// The state of the command will be "queued". In normal situation a client may request
		// command again via commands.get API method to get command execution results, but our fake
		// device does not actually receive any commands, so it will never be executed.
		try {
			System.out.println("Sent command to the device:\n" + jsonFactory.toPrettyString(command));
		} catch (Exception e) { throw new RuntimeException(e); }
	}

	public void run(){
		System.out.println("In device status");
		try {
			SQLFetchUser();
		} catch (ClassNotFoundException | SQLException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new Thread(new DeviceStatus()).start();
		Thread.sleep(10000);
	}
}

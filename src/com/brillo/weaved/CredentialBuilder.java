package com.brillo.weaved;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.brillo.weaved.SQLConnector;
import com.brillo.weaved.WeavedServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class CredentialBuilder {
	private static final String CLIENT_ID = "309435708548-1gdb132v9q00p53kbs7eot6i7abtt0n1.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "kemQzrbauxkeLV_fSGWivKgW";
	private static String accessToken = null;
	
	
	public GoogleCredential returnCredentials(String user_id) throws ClassNotFoundException, SQLException{
		GoogleCredential credential = null;
		String databaseName = "Credentials";
		SQLConnector sql = new SQLConnector();
		Connection conn;
		conn = sql.SQLConnectorURLgenerator(databaseName);
		String query = "SELECT * FROM "+databaseName+" WHERE UserId=\'"+user_id+"\'";
		System.out.println(query);
	    Statement mystmt = conn.createStatement();
		ResultSet myRes=mystmt.executeQuery(query);
		while(myRes.next())
			accessToken = myRes.getString("access_token");
		credential = new GoogleCredential.Builder()
				.setJsonFactory(WeavedServlet.jsonFactory)
				.setTransport(WeavedServlet.httpTransport)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET)
				.build()
				.setAccessToken(accessToken);
		return credential;
	}
}

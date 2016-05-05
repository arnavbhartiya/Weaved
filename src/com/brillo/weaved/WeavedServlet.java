package com.brillo.weaved;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.clouddevices.CloudDevices;
import com.google.api.services.clouddevices.model.Device;
import com.google.api.services.clouddevices.model.DevicesListResponse;
import com.mysql.jdbc.PreparedStatement;


/**
 * Servlet implementation class myServlet
 */

public class WeavedServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	// See https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing#setup
	// on how to set up your project and obtain client ID, client secret and API key.
	private static final String CLIENT_ID = "309435708548-1gdb132v9q00p53kbs7eot6i7abtt0n1.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "kemQzrbauxkeLV_fSGWivKgW";
	private static final String API_KEY = "AIzaSyDaR-PSdRK0psv1ZyDcaY2n2UFpZWui2UE";
	
	// Redirect URL for client side installed apps.
	private static final String REDIRECT_URL = "http://localhost:8888";

	private static String Access_Token;
	private static String Auth_Code;
	private static String Refresh_Token;
	private static String Id_Token;
	public  static String userId;
	private static String userEmail;
	
	public static final NetHttpTransport httpTransport = new NetHttpTransport();
	public static final JacksonFactory jsonFactory = new JacksonFactory();
	
	CloudDevices apiClient;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WeavedServlet() {
		super();
		// TODO Auto-generated constructor stubksonFactory;
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		//response.getWriter().println("in servlet");
		String BrowserQuery;

		BrowserQuery =  request.getReader().readLine();

		String[] BrowserQueryResponse = BrowserQuery.split("#");
		//response.getWriter().println(authCode);
		//System.out.println("Authcode:"+authCode);

		userEmail = BrowserQueryResponse[0];
		Auth_Code = BrowserQueryResponse[1];

		System.out.println(userEmail);
		apiClient = getApiClient(Auth_Code);
		try {
			userId = IdVerifier();
			credentialsStore();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//receiveDeviceList();
		
		//	    response.sendRedirect("http://localhost:8888");

		//WeaveQuerier query = new WeaveQuerier();
		
		/*DeviceStatus ds = new DeviceStatus();
		try {
			ds.SQLFetchUser();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	private CloudDevices getApiClient(String Access_Token) throws IOException {
		// Try to load cached credentials.
		GoogleCredential credential=null;
		if (credential == null) {
			System.out.println("Did not find cached credentials");
			credential = authorize(Auth_Code);
		}
		return new CloudDevices.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName("Weave Sample")
				.setServicePath("clouddevices/v1")
				.setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer(API_KEY))
				.build();
	}

	/**
	 * Goes through Google OAuth2 authorization flow. See more details:
	 * https://developers.google.com/weave/v1/dev-guides/getting-started/authorizing
	 * @return 
	 */
	private GoogleCredential authorize(String Auth_Code) throws IOException {
		String authorizationCode = Auth_Code;
		System.out.println("test");
		// Use the authorization code to get an access token and a refresh token.
		GoogleAuthorizationCodeTokenRequest request = new GoogleAuthorizationCodeTokenRequest(
				httpTransport, JacksonFactory.getDefaultInstance(), CLIENT_ID, CLIENT_SECRET, authorizationCode,
				REDIRECT_URL);
		//System.out.println(request);
		GoogleTokenResponse response = request.execute();
		//System.out.println(response);
		Access_Token=response.getAccessToken();
		Refresh_Token=response.getRefreshToken();
		Id_Token=response.getIdToken();

		System.out.println("Refresh TOken:"+response.getRefreshToken());
		System.out.println("Id TOken:"+response.getIdToken());
		// Use the access and refresh tokens to set up credentials.
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(jsonFactory)
				.setTransport(httpTransport)
				.setClientSecrets(CLIENT_ID, CLIENT_SECRET)
				.build()
				.setFromTokenResponse(response);
		return credential;

		/*GoogleCredential credential = new GoogleCredential();
		  credential.setAccessToken(Access_Token);
		  return credential;*/
	}

	
	private String IdVerifier() throws GeneralSecurityException, IOException{
		String userid = null;
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
				.setAudience(Arrays.asList(CLIENT_ID))
				// If you retrieved the token on Android using the Play Services 8.3 API or newer, set
				// the issuer to "https://accounts.google.com". Otherwise, set the issuer to
				// "accounts.google.com". If you need to verify tokens from multiple sources, build
				// a GoogleIdTokenVerifier for each issuer and try them both.
				.setIssuer("accounts.google.com")
				.build();

		// (Receive idTokenString by HTTPS POST)

		GoogleIdToken idToken;
		idToken = verifier.verify(Id_Token);

		if (idToken != null) {
			Payload payload = idToken.getPayload();
			// Print user identifier
			userid = payload.getSubject();
			System.out.println("User ID: " + userid);

			// Get profile information from payload
			/*String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");*/

			// Use or store profile information
			// ...

		} else {
			System.out.println("Invalid ID token.");
		}
		return userid;
	}

	
	private void credentialsStore() throws SQLException, ClassNotFoundException{
		String databaseName = "Credentials";
		String user_type = "Authorized_User";
		SQLConnector sql = new SQLConnector();
		Connection conn;

		conn = sql.SQLConnectorURLgenerator(databaseName);
		String statement = "INSERT INTO Credentials ( UserId, access_token, refresh_token, id_token, user_type ) VALUES(?,?,?,?,?) "
				+ "ON DUPLICATE KEY UPDATE access_token = ?";
		System.out.println(statement);
		PreparedStatement stmt =  (PreparedStatement) conn.prepareStatement(statement);
		stmt.setString(1, userId);
		stmt.setString(2, Access_Token);
		stmt.setString(3, Refresh_Token);
		stmt.setString(4, Id_Token);
		stmt.setString(5, user_type);
		stmt.setString(6, Access_Token);
		int success = 2;
		success = stmt.executeUpdate();
		if (success == 1) {
			System.out.println("Success! Database written");
		} else if (success == 0) {
			System.out.println("Failure! Database not written");
		}
	}
	
	public List<Device> receiveDeviceList() throws IOException{
		DevicesListResponse devicesListResponse;
		devicesListResponse = apiClient.devices().list().execute();
		List<Device> devices = devicesListResponse.getDevices();

		int i = 0;
		for(Device dev : devices){
			System.out.println("Available device "+ ++i+": " + dev.getId());
		}
		
		return devices;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

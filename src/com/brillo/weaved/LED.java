package com.brillo.weaved;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.brillo.weaved.DeviceStatus;
import com.brillo.weaved.WeavedServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.services.clouddevices.CloudDevices;
import com.google.api.services.clouddevices.model.Command;
public class LED {
    CloudDevices apiClient;
    private static final String API_KEY = "AIzaSyDaR-PSdRK0psv1ZyDcaY2n2UFpZWui2UE";
LED(){
    
}
protected JSONObject LEDeventHandler(Map<String, Object> map,String devId) throws SQLException, ParseException
{
    System.out.println("in ledeventhandler");
    Connection myConn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Credentials","root","root");
    Statement mystmt = myConn.createStatement();
    System.out.println("SELECT Conditions,Actions FROM USERSQL WHERE DevID=\""+devId+"\"");
    ResultSet myRes=mystmt.executeQuery("SELECT Conditions,Actions FROM USERSQL WHERE DevID=\""+devId+"\"");
    while(myRes.next()){
        System.out.println("in while");
        Object ob= map.get("_ledflasher");
        @SuppressWarnings("unchecked")
        Map<String,Object> subMap= (Map<String,Object>)ob;
        System.out.println(subMap.get("_leds"));
        System.out.println(myRes.getString("Conditions"));
        System.out.println(myRes.getString("Actions"));
        String ledStatus = subMap.get("_leds").toString();
        String condition = myRes.getString("Conditions");
        if(ledStatus.replaceAll("\\s+","").equalsIgnoreCase(condition.replaceAll("\\s+",""))){
            System.out.println("inside if");
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject)jsonParser.parse(myRes.getString("Actions"));
            return obj;
        }
    }
    System.out.println("out");
    return null;
}
protected void LEDactionHandler(JSONObject actions ) throws IOException, SQLException{
    Map<String, Object> parameters = new HashMap<String, Object>();
    String name=null;
    String devId = (String)actions.get("DeviceId");
    System.out.println("just outside if action  "+(String)actions.get("Command")+"   "+((String)actions.get("Command")).equalsIgnoreCase("set"));
    if(((String)actions.get("Command")).equalsIgnoreCase("Set")){
        parameters.put("_led",Integer.parseInt((String)actions.get("LED")));
        parameters.put("_on",Boolean.parseBoolean((String)actions.get("State")));
         name = "_ledflasher._set";
    }else if(((String)actions.get("Command")).equalsIgnoreCase("Toggle")){
        parameters.put("_led",(String)actions.get("LED"));
         name = "_ledflasher._toggle";
    }else if(((String)actions.get("Command")).equalsIgnoreCase("UpdateDeviceInfo")){
        parameters.put("description",(String)actions.get("LED"));
         name = "base.updateDeviceInfo";
    }
    Command command = new Command()
              .setName(name) 
              .setParameters(parameters)  
              .setDeviceId(devId); 
    
    DeviceStatus.sendCommand(command);
//  command = apiClient.commands().insert(command).execute();
    
}
protected void LEDupdater(String deviceId, JSONObject update, String deviceName,String userId) throws ClassNotFoundException, SQLException, IOException {
    // TODO Auto-generated method stub
    CredentialBuilder cred= new CredentialBuilder();
    GoogleCredential credential=cred.returnCredentials(userId);
    apiClient = getApiClient(credential);
    Map<String, Object> parameters = new HashMap<String, Object>();
    String updateValue= update.get("LEDnumber").toString();
    String stateValue=update.get("State").toString();
    String name="_ledflasher._set";
    parameters.put("_led",Integer.parseInt(updateValue));
    parameters.put("_on", Boolean.parseBoolean(stateValue));
    Command command = new Command()
              .setName(name) 
              .setParameters(parameters)  
              .setDeviceId(deviceId);
    System.out.println("Sending a new command to the device");
    System.out.println(command);
    try {
        command = apiClient.commands().insert(command).execute();
    } catch (IOException e) { throw new RuntimeException("Could not insert command", e); }
    // The state of the command will be "queued". In normal situation a client may request
    // command again via commands.get API method to get command execution results, but our fake
    // device does not actually receive any commands, so it will never be executed.
    try {
        System.out.println("Sent command to the device:\n" +command);
    } catch (Exception e) { throw new RuntimeException(e); }
    
}
private CloudDevices getApiClient(GoogleCredential cred) throws IOException {
    
    return new CloudDevices.Builder(WeavedServlet.httpTransport, WeavedServlet.jsonFactory, cred)
            .setApplicationName("Weave Sample")
            .setServicePath("clouddevices/v1")
            .setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer(API_KEY))
            .build();
}
}

package com.brillo.weaved;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.api.services.clouddevices.model.Device;

public class WeaveQuerier {
	
public WeaveQuerier(List<Device> devices) throws SQLException, ParseException, IOException {
	eventEvoker(devices);
}
	public static void eventEvoker(List<Device> devices) throws SQLException, ParseException, IOException{
		for(Device dev:devices){
			checkEvent(dev);
		}
	}
	
	public static void checkEvent(Device dev) throws SQLException, ParseException, IOException{

		if(dev.getUnknownKeys().get("name").toString().contentEquals("weave_daemon_ledflasher")){
			
			LED led = new LED();
			JSONObject action= led.LEDeventHandler(dev.getState(),dev.getId());
			System.out.println(action);
			if(action!=null){
			String actionDev=(String)action.get("DeviceName");
			System.out.println(actionDev);
			checkAction(actionDev,action);
			}
		}
		else if(dev.getUnknownKeys().get("name").toString().contentEquals("weave_daemon_speaker")){
			
		}
	}
	
	
	public static void checkAction(String actionDev,JSONObject action) throws IOException, SQLException{
		if(actionDev.contentEquals("LED")){
			LED led = new LED();
			led.LEDactionHandler(action);
			
		}
		else if(actionDev.contentEquals("Speaker")){
			
		}
	}

}

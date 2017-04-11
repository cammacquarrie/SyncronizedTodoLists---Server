package edu.carleton.comp2601Final;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import edu.carleton.COMP2601.communication.Event;
import edu.carleton.COMP2601.communication.EventHandler;
import edu.carleton.COMP2601.communication.Fields;
import com.google.gson.*;

public class LoginHandler implements EventHandler{

	@Override
	public void handleEvent(Event event) {
		//verify user
		String username = (String) event.get(Fields.USERNAME);
		String password = (String) event.get(Fields.PASSWORD);
		User user = Server.verifyUser(username, password);
		//if verified, add to active client list and send response
		if(user != null){
			Server.clients.put(event.getSource(), user);
			HashMap<String, Serializable> map = new HashMap<String, Serializable>();
			map.put(Fields.TYPE, Fields.LOGIN_RES);
			map.put(Fields.USER, user);
			map.put(Fields.VALUE, true);
			Event evt = new Event(event.getSource(), map);
			try {
				event.putEvent(evt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//if not verified send negative response and close connection
		else{
			HashMap<String, Serializable> map = new HashMap<String, Serializable>();
			map.put(Fields.TYPE, Fields.LOGIN_RES);
			map.put(Fields.VALUE, false);
			Event evt = new Event(event.getSource(), map);
			try {
				event.putEvent(evt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			event.close();
		}
	}

}

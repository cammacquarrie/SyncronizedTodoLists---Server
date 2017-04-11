package edu.carleton.comp2601Final;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import edu.carleton.COMP2601.communication.Event;
import edu.carleton.COMP2601.communication.EventHandler;
import edu.carleton.COMP2601.communication.Fields;

public class RegisterHandler implements EventHandler{

	@Override
	public void handleEvent(Event event) {
		String username = (String) event.get(Fields.USERNAME);
		String password = (String) event.get(Fields.PASSWORD);
		String display = (String) event.get(Fields.DISPLAY);
		if(display.isEmpty()){
			display = "-";
		}

		boolean added = Server.addUser(username, display, password);
		if(added){
			User user = Server.verifyUser(username, password);
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
		else{
			HashMap<String, Serializable> map = new HashMap<String, Serializable>();
			map.put(Fields.TYPE, Fields.REGISTER);
			map.put(Fields.VALUE, false);
			map.put(Fields.REASON, "That username is already taken");
			Event evt = new Event(event.getSource(), map);
			try {
				event.putEvent(evt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

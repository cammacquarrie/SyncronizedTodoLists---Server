package edu.carleton.comp2601Final;

import com.google.gson.Gson;

import edu.carleton.COMP2601.communication.Event;
import edu.carleton.COMP2601.communication.EventHandler;
import edu.carleton.COMP2601.communication.Fields;

public class NewListHandler implements EventHandler{

	@Override
	public void handleEvent(Event event) {
		Gson gson = new Gson();
		int listid = event.get(Fields.LIST_ID);
		String sender = event.get(Fields.SENDER);
		String receiver = event.get(Fields.RECIEVER);
		Server.addInvite(listid, sender, reciever);
	}
}

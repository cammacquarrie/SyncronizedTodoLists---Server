package edu.carleton.comp2601Final;

import com.google.gson.Gson;

import edu.carleton.COMP2601.communication.Event;
import edu.carleton.COMP2601.communication.EventHandler;
import edu.carleton.COMP2601.communication.Fields;

public class NewItemHandler implements EventHandler{

	@Override
	public void handleEvent(Event event) {
		Gson gson = new Gson();
		String itemStr = event.get(Fields.ITEM).toString();
		System.out.print(itemStr);
		Item item = gson.fromJson(itemStr, Item.class);
		Server.addItem(item);
	}

}

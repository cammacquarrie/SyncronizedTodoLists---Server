package edu.carleton.comp2601Final;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.carleton.COMP2601.communication.EventHandler;
import edu.carleton.COMP2601.communication.EventSource;
import edu.carleton.COMP2601.communication.Fields;
import edu.carleton.COMP2601.communication.JSONEventSource;
import edu.carleton.COMP2601.communication.Reactor;
import edu.carleton.COMP2601.communication.ThreadWithReactor;

public class Server {
	private final static int PORT = 7001;
	public static HashMap<EventSource, User> clients = new HashMap<EventSource, User>();
	private static DataBase db = new DataBase();

	public static void main(String[] args) {
		try {
			run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void run() throws IOException {
		System.out.println("started");
		ServerSocket listener = new ServerSocket(PORT);
		while (true) {
			Socket socket = listener.accept();
			Reactor reactor = new Reactor();
			System.out.println("Client connection accepted");
			JSONEventSource source = new JSONEventSource(socket);
			clients.put(source, null);
			// register handlers with reactor here
			reactor.register(Fields.LOGIN, new LoginHandler());
			reactor.register(Fields.REGISTER, new RegisterHandler());
			reactor.register(Fields.NEW_LIST, new NewListHandler());
			reactor.register(Fields.NEW_ITEM, new NewItemHandler());
			//////
			
			ThreadWithReactor thread = new ThreadWithReactor(source, reactor);
			thread.start();
		}
	}
	
	public static User verifyUser(String username, String password){
		User user = null;
		try {
			user = db.queryVeryifyUser(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public static void addList(List list){
		try {
			db.insertLidst(list.getName(), list.getAdmin());
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public static void addItem(Item item){
		try {
			db.insertItem(item.getName(), item.getCreatedBy(), item.getListID());
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	public static boolean  addUser(String username, String display, String password){
		try {
			db.insertUser(username, display, password);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public void print(String str) {
		System.out.println(str);
	}
}

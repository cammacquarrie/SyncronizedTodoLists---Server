package edu.carleton.comp2601Final;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBase {
	private static final String INSERT_ITEM = "INSERT INTO  items (title, description, created_by, complete, listid, points) VALUES ";
	private static final String INSERT_USER = "INSERT INTO  users (username, displayname, password) VALUES ";
	private static final String INSERT_LIST = "INSERT INTO  lists (name, admin) VALUES ";
	private static final String INSERT_ASSIGNED = "INSERT INTO  assigned (user, itemid) VALUES";
	private static final String INSERT_USER_LIST = "INSERT INTO  user_list (listid, user) VALUES";
	private static final String INSERT_INVITE = "INSERT INTO  invites (listid, sender, receiver) VALUES";
	private static final String QUERY_USER = "SELECT * FROM users ";
	private static final String QUERY_LIST = "SELECT * FROM lists ";
	private static final String QUERY_ITEM = "SELECT * FROM items ";
	private static final String UPDATE_ITEM_POINT = "UPDATE items SET points = points +";
	private static final String UPDATE_ITEM_COMPLETE = "UPDATE items SET complete = 1 WHERE ";

	private Connection connection;

	public DataBase() {
		try {
			connection = DriverManager.getConnection(Config.DB_LOC);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * USERS
	 */
	// queries db for the username and returns the user if validated
	// throws sqlexception if user does not exist
	// returns null if verification failed
	public User queryVeryifyUser(String username, String password) throws SQLException {
		String query = " WHERE username = \"" + username + "\"";
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_USER + query);
		results.next();
		String uName = results.getString("username");
		String display = results.getString("displayname");
		String pwrd = results.getString("password");

		System.out.println(display);
		// validate
		boolean validated = PasswordHash.validate(password, pwrd);
		User user = null;
		if (validated) {
			System.out.println("validated");
			user = new User(uName, display);
			ArrayList<List> lists = queryListsForUser(uName);
			ArrayList<Item> items = queryItemsForUser(uName);
			user.setLists(lists);
			user.setItems(items);
		} else {
			System.out.println("validationfailed");
		}
		return user;
	}

	// query for user by username
	// throws sqlexception if user doesnt exist
	public User queryUser(String username) throws SQLException {
		String query = " WHERE username = \"" + username + "\"";
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_USER + query);
		results.next();
		String uName = results.getString("username");
		String display = results.getString("displayname");
		return new User(uName, display);
	}

	// insert a new user into the database
	// throws sqlexception if user already exists
	public void insertUser(String username, String displayname, String password) throws SQLException {
		String pswrd = PasswordHash.getHash(password);
		String update = "(\"" + username + "\",\"" + displayname + "\",\"" + pswrd + "\")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_USER + update);
		System.out.println("user inserted");
	}
	
	/*
	 * Lists
	 */
	//insert list into db
	public void insertList(String name, String admin) throws SQLException{
		String update = "(\"" + name + "\",\"" + admin + "\")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_LIST + update);
		System.out.println("list inserted");
		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		int id = rs.getInt(1);
		addUserToList(id, admin);
	}

	/*
	 * Lists - User
	 */
	//query for lists that a user belongs to
	public ArrayList<List> queryListsForUser(String username) throws SQLException {
		String query = "l JOIN user_list u ON l.id = u.listid WHERE u.user = \"" + username + "\"";
		Statement statement = connection.createStatement();
		System.out.print(QUERY_LIST + query);
		ResultSet results = statement.executeQuery(QUERY_LIST + query);
		ArrayList<List> lists = new ArrayList<List>();
		while (results.next()) {
			System.out.println("found list for user");
			int id = results.getInt("id");
			String name = results.getString("name");
			String admin = results.getString("admin");
			List list = new List(name, id);
			list.setAdmin(queryUser(admin).getUserName());
			list.setItems(queryItemsForList(list.getId()));
			list.setUsers(queryUsersForList(id));
			lists.add(list);
		}
		return lists;
	}
	//query for users belonging to a list
	public ArrayList<User> queryUsersForList(int id) throws SQLException{
		String query = " u JOIN user_list l ON u.username = l.user WHERE l.listid = "+ id;
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_USER + query);
		ArrayList<User> users = new ArrayList<User>();
		while (results.next()) {
			String uName = results.getString("username");
			String display = results.getString("displayname");
			users.add(new User(uName, display));
		}
		return users;
	}
	//add to invite item reperesenting list invites
	public void insertInvite(int listid, String sender, String receiver) throws SQLException{
		String update = "(" + listid + ",\"" + sender + "\",\"" + receiver + "\")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_INVITE + update);
		System.out.println("invite inserted");
	}
	//add user to a list (When accepting invite)
	public void addUserToList(int listid, String user) throws SQLException{
		String update = "(" + listid + ",\"" + user + "\")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_USER_LIST + update);
		System.out.println("user inserted");
	}
	
	/*
	 * Item - User
	 */
	//query for lists that a user is assigned to and not completed
	public ArrayList<Item> queryItemsForUser(String username) throws SQLException{
		String query = " i JOIN assigned a ON i.id = a.itemid WHERE a.user = \"" + username + "\"";
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_ITEM + query);
		ArrayList<Item> items = new ArrayList<Item>();
		while (results.next()) {
			int id = results.getInt("id");
			String title = results.getString("title");
			String created = results.getString("created_by");
			String desc = results.getString("description");
			int list = results.getInt("listid");
			items.add(new Item(id, title, desc, created, list));
		}
		return items;
	}
	//query for users assigned to an item
	public ArrayList<User> queryUsersForItem(int id) throws SQLException{
		String query = " u JOIN assigned a ON u.username = a.user WHERE a.itemid = " + id;
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_USER + query);
		ArrayList<User> users = new ArrayList<User>();
		while (results.next()) {
			String uName = results.getString("username");
			String display = results.getString("displayname");
			users.add(new User(uName, display));
		}
		return users;
	}
	//add an assigned item for a user
	public void insertAssigned(int itemid, String user) throws SQLException{
		String update = "(" + itemid + ",\"" + user + "\")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_ASSIGNED + update);
		System.out.println("user inserted");
	}
	
	/*
	 * Item - List
	 */
	//query for items that are in a list
	public ArrayList<Item> queryItemsForList(int id) throws SQLException{
		String query = " WHERE listid = " + id;
		ArrayList<Item> items = new ArrayList<Item>();
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(QUERY_ITEM + query);
		while (results.next()) {
			int ID = results.getInt("id");
			String title = results.getString("title");
			String created = results.getString("created_by");
			String desc = results.getString("description");
			int list = results.getInt("listid");
			int points = results.getInt("points");
			items.add(new Item(ID, title, desc, created, list, points));
		}
		return items;
	}
	public void insertItem(Item item) throws SQLException{
		insertItem(item.getTitle(), item.getDescription(), item.getCreatedBy(), item.isCompleted(), item.getListID());
	}
	public void insertItem(String title, String description, String created_by, boolean complete, int listid) throws SQLException{
		String update = "(\"" + title + "\",\"" + description + "\",\"" + created_by + "\",\"" + complete + "\"," + listid + "," + 0+")";
		Statement statement = connection.createStatement();
		statement.executeUpdate(INSERT_ITEM + update);
		System.out.println("user inserted");
	}
	public void itemVote(int itemid, int upDown) throws SQLException{
		String update = "(" + upDown + ") WHERE id = " + itemid;
		Statement statement = connection.createStatement();
		statement.executeUpdate(UPDATE_ITEM_POINT+ update);
		System.out.println("vote inserted");
	}
	public void itemComplete(int itemid) throws SQLException{
		String update = "id = " + itemid;
		Statement statement = connection.createStatement();
		statement.executeUpdate(UPDATE_ITEM_COMPLETE + update);
		System.out.println("complete inserted");
	}
	
}

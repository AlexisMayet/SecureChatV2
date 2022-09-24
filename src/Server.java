import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

	//run: run as java aplication

	private static HashMap<String, Client> clients = new HashMap<String, Client>(13);
	
	//private static LinkedList<ClientThread> threads = new LinkedList<>();

	private static ArrayList<User> users = new ArrayList<>();

	public static void generate_users() throws FileNotFoundException
	{
		JsonParser jsonParser = new JsonParser();
		FileReader reader =  new FileReader("resources/users.json");
		JsonArray users_object = (JsonArray) jsonParser.parse(reader);

		int users_s = users_object.size();

		for (int i = 0; i < users_s; i++) {
			User user = new User(
					users_object.get(i).getAsJsonObject().get("id").getAsString(),
					users_object.get(i).getAsJsonObject().get("password").getAsString(),
					users_object.get(i).getAsJsonObject().get("secret-key").getAsString());
			users.add(user);
		}

	}

	private static String generateNum(Client client) {
		String secretKey = "";
		for(User u : users)
		{
			if(Objects.equals(u.getId(), client.getID()))
				secretKey = u.getKey();
		}
		String lastCode = null;
		while (true) {
			Base32 base32 = new Base32();
			byte[] bytes = base32.decode(secretKey);
			String hexKey = Hex.encodeHexString(bytes);
			String code = TOTP.getOTP(hexKey);
			if (!code.equals(lastCode)) {
				System.out.println("CODE--- " + code);
				return code;
			}
			lastCode = code;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {};
		}
	}

	private static boolean check_id(Client client)
	{
		boolean found = false;
		for (User u : users)
		{
			if(Objects.equals(client.getID(), u.getId()))
				found = true;

		}
		return found;
	}

	private static boolean check_password(Client client)
	{
		User user = null;
		for (User u : users)
			if(Objects.equals(u.getId(), client.getID()))
				user = u;

		return Objects.equals(user.getPassword(), client.getPassword());
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		try {
			generate_users();
		}catch (FileNotFoundException e) {
			System.out.println("Failed to read users.\nShutting down server.");
			System.exit(0);
		}

		ServerSocket ss = new ServerSocket(1234);

		System.out.println("Server awaiting connections...");
			
		while(true)
		{
			Socket socket = ss.accept();
		
			System.out.println("Connection from " + socket + "!");

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

			Client client = (Client) objectInputStream.readObject();

			if(!check_id(client))
			{
				objectOutputStream.writeObject(new Output(Status.OFFLINE,"User not found."));
				System.out.println("Closing socket for user: " + socket + "\nReason: User not found.");

			}
			else if(!check_password(client))
			{
				objectOutputStream.writeObject(new Output(Status.OFFLINE,"Password do not match."));
				System.out.println("Closing socket for user: " + socket + "\nReason: Password do not match.");

				//ClientThread ct = new ClientThread( client );
				//ct.start();
			}
			else {
				objectOutputStream.writeObject(new Output(Status.IDLE,"Correct password."));
				if(Objects.equals(objectInputStream.readObject(),generateNum(client)))
					objectOutputStream.writeObject(new Output(Status.IDLE,"Login successful.\nProceed with the action."));
				else
					objectOutputStream.writeObject(new Output(Status.OFFLINE,"Login unsuccessful."));



				if (!clients.containsKey(client.getID())) {
					clients.put(client.getID(), client);
				} else {
					System.err.println("That client is on the server already.");
				}

			System.out.println("Number of clients: " + clients.size() );
			
			for( Client c : clients.values() )
			{
				System.out.println( c );
			}
		}
	}
}

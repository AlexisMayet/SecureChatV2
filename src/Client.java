import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class Client implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private int delay;
	private String actions;

	@Override
	public String toString() {
		return "Client [username=" + username + ", password=" + password + ", delay=" + delay + ", actions=" + actions
				+ "]";
	}

	public Client(String username, String password, int delay, String actions) {

		this.username = username;
		this.password = password;
		this.delay = delay;
		this.actions = actions;
	}

	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

		try {
			if (bufferedReader != null) {
				bufferedReader.close();
			}

			if (bufferedWriter != null) {
				bufferedWriter.close();
			}

			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		// Previous
		// -------------------------------------------------
		// Socket socket = new Socket("localhost", 1234);
		// Client client = new Client(socket, "Pepe");
		// -------------------------------------------------

		String filename = args[0];

		JsonParser jsonParser = new JsonParser();

		try (FileReader reader = new FileReader(filename)) {

			JsonObject clienteObjeto = (JsonObject) jsonParser.parse(reader);

			// data
			String id = clienteObjeto.get("id").getAsString();
			// data
			String pass = clienteObjeto.get("password").getAsString();

			JsonObject server = clienteObjeto.getAsJsonObject("server");

			// data
			String ipServer = server.get("ip").getAsString();
			// data
			int portServer = Integer.parseInt(server.get("port").getAsString());

			JsonObject jsoActions = clienteObjeto.getAsJsonObject("actions");

			// data
			String sDelay = jsoActions.get("delay").getAsString();

			int delay = Integer.parseInt(sDelay);

			// data
			JsonArray jsaSteps = jsoActions.getAsJsonArray("steps");

			String actions = "";

			for (JsonElement e : jsaSteps) {

				String opAmount = e.getAsString();

				StringTokenizer st = new StringTokenizer(opAmount, " ");

				String op = st.nextToken();

				int amount = Integer.parseInt(st.nextToken());

				String a = op + " " + amount;

				actions += a + ",";
			}

			// donde dice localhost es la IP: localhost es 127.0.0.1 y 1234 es el port
			Socket socket = new Socket(ipServer, portServer);
			// Para no usar metodos publicos al crear el cliente, se crea con toda la info
			// del json
			Client client = new Client(id, pass, delay, actions);

			// Grabar un objeto en el socket
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(client);

			counter_method();
			// client.listenForMessage();
			// client.sendMessage();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
	}

	public String getID() {

		return username;
	}
	public static void counter_method(){
		Scanner value = new Scanner(System.in);
		while (true) {
			boolean name = test();
			if(name) {
				System.out.println("How much do you want to increase ");
				double value_1 = value.nextDouble();
				Server.counter += value_1;
				System.out.println("The counter:" + Server.counter);
			}
			else {
				System.out.println("How much do you want to decrease ");
				double value_1 = value.nextDouble();
				Server.counter = Server.counter - value_1;
				System.out.println("The counter is equal to : " + Server.counter);
			}

			if(test2())
				return;
		}
	}
	public static  boolean test(){
		while(true) {
			Scanner increase_or_decrease = new Scanner(System.in);
			System.out.println("The counter is equal to :" + Server.counter);
			System.out.println("Do you want to : increase or decrease");
			String userName = increase_or_decrease.nextLine();
			if ("increase".equalsIgnoreCase(userName))
				return true;
			else if ("decrease".equalsIgnoreCase(userName))
				return false;
			else
				System.out.println("Try again");
		}
	}
	public static boolean test2(){
		while(true) {
			Scanner increase_or_decrease = new Scanner(System.in);
			System.out.println("Do you want to finish ? no/yes");
			String userName_2 = increase_or_decrease.nextLine();
			if ("yes".equalsIgnoreCase(userName_2))
				return true;
			else if ("no".equalsIgnoreCase(userName_2))
				return false;
			else
				System.out.println("Try again");
		}

	}
}
/*
 *
 * public static void main(String[] args) throws IOException { // need host and
 * port, we want to connect to the ServerSocket at port 7777 Socket socket = new
 * Socket("localhost", 7777); System.out.println("Connected!");
 *
 * // get the output stream from the socket. OutputStream outputStream =
 * socket.getOutputStream(); // create an object output stream from the output
 * stream so we can send an // object through it ObjectOutputStream
 * objectOutputStream = new ObjectOutputStream(outputStream);
 *
 * // make a bunch of messages to send. List<Message> messages = new
 * ArrayList<>(); messages.add(new Message("Hello from the other side!"));
 * messages.add(new Message("How are you doing?")); messages.add(new
 * Message("What time is it?")); messages.add(new Message("Hi hi hi hi."));
 *
 * System.out.println("Sending messages to the ServerSocket");
 * objectOutputStream.writeObject(messages);
 *
 * System.out.println("Closing socket and terminating program.");
 * socket.close(); }
 *
 * }
 */

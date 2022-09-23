import java.io.*;
import java.net.Socket;
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
	private String id;
	private String password;
	private int delay;
	private String actions;
	private int counter;

	public String getActions()
	{
		return actions;
	}
	
	public Client(String username, String password, int delay, String actions) {

		this.id = username;
		this.password = password;
		this.delay = delay;
		this.actions = actions;
		this.counter = 0;
	}

	@Override
	public String toString() {
		return "Client [username=" + id + ", password=" + password + ", delay=" + delay + ", actions=" + actions
				+ "]";
	}

	public int getDelay()
	{
		return delay;
	}
	
	public int getCounter() {
		return counter;
	}

	public void increaseCounter(int amount) {
		counter += amount;
	}

	public void decreaseCounter(int amount) {
		counter -= amount;
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

		String filename = args[0];

		JsonParser jsonParser = new JsonParser();

		try (FileReader reader = new FileReader(filename)) {

			JsonObject clienteObjeto = (JsonObject) jsonParser.parse(reader);
			String id = clienteObjeto.get("id").getAsString();
			String pass = clienteObjeto.get("password").getAsString();
			JsonObject server = clienteObjeto.getAsJsonObject("server");
			String ipServer = server.get("ip").getAsString();
			int portServer = Integer.parseInt(server.get("port").getAsString());

			JsonObject jsoActions = clienteObjeto.getAsJsonObject("actions");
			String sDelay = jsoActions.get("delay").getAsString();
			int delay = Integer.parseInt(sDelay);
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

			Socket socket = new Socket(ipServer, portServer);
			Client client = new Client(id, pass, delay, actions);

			// Grabar un objeto en el socket
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(client);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

	}

	public String getID() {

		return id;
	}

}


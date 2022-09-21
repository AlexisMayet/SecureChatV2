import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Server {

	private static HashMap<String, Client> clients = new HashMap<String, Client>(13);
	
	//private static LinkedList<ClientThread> threads = new LinkedList<>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		ServerSocket ss = new ServerSocket(1234);

		System.out.println("ServerSocket awaiting connections...");
			
		while( !ss.isClosed() )
		{
			Socket socket = ss.accept();
		
			System.out.println("Connection from " + socket + "!");

			InputStream inputStream = socket.getInputStream();
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

			Client client = (Client) objectInputStream.readObject();
						
			if( !clients.containsKey(client.getID() ) )
			{
				clients.put( client.getID(), client);
			}
			else
			{
				System.err.println("That client is on the server already.");
			}

			System.out.println("Clientes: " + clients.size() );
			
			for( Client c : clients.values() )
			{
				System.out.println( c );
			}
		}

		// System.out.println("Closing sockets.");
		// ss.close();
		// socket.close();
	}
}
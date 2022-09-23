import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Server {

	//run: run as java aplication
	
	private static HashMap<String, Client> clients = new HashMap<String, Client>(13);
	
	//private static LinkedList<ClientThread> threads = new LinkedList<>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		ServerSocket ss = new ServerSocket(1234);

		System.out.println("Server awaiting connections...");
			
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
				
				//here
				//create this ClientThread
				
				ClientThread ct = new ClientThread( client );
				ct.start();
				
			}
			else
			{
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

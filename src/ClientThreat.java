import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class ClientThread extends Thread {

	private Client c;

	private Queue<String> actionsQueue = new LinkedList<String>();

	public ClientThread(Client c) {

		this.c = c;

		// cargar las actions en la cola de acciones

		StringTokenizer st = new StringTokenizer(c.getActions(), ",");

		while (st.hasMoreElements()) {
			String elem = st.nextToken();

			actionsQueue.add(elem);
		}

	}

	@Override
	public void run() {

		while (true) {

			while (!actionsQueue.isEmpty()) {

				//previous delay
				try {
					Thread.sleep(c.getDelay());
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				
				String elem = actionsQueue.poll();

				StringTokenizer st2 = new StringTokenizer(elem, " ");

				String action = st2.nextToken();
				int amount = Integer.parseInt(st2.nextToken());

				if (action.equalsIgnoreCase("INCREASE")) {
					c.increaseCounter( amount );
				} else {
					c.decreaseCounter( amount );
				}

				System.out.println("ID:" + c.getID() + " - " + c.getCounter());

				/*
				try {
					Thread.sleep(c.getDelay());
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				*/
			}
		}
	}
}

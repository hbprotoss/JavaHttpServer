package engine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import Server.Server;

/**
 * Connector accepts connection from clients and initialize two thread pools,
 * one is for network controller and the other one is for timeout controller.
 * The accepted connection will be submitted to network controller to execute.
 * 
 * @author Jiaqi LI
 */

public class Engine {

	private boolean shuttingdown = false;
	private ServerSocket serverSocket;
	private ExecutorService requestExecutor;
	private ExecutorService timeoutExecutor;

	/**
	 * Initialize the ServerSockt by host name and port number and initialize
	 * the request thread pool and timeout thread pool and then wait for
	 * connection.
	 * 
	 * @param host
	 *            the host that the connector is binded to.
	 * @param port
	 *            the port that the connector will listen.
	 */
	public Engine(String host, int port) {
		// initialize server socket.
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(host, port));
		} catch (IOException ioe) {
			System.out.println("Can not listen on port: " + port
					+ "\nServer will stop.");
			System.exit(-1);
		}
		
		// initialize thread pool.
		requestExecutor = Executors.newCachedThreadPool();
		timeoutExecutor = Executors.newCachedThreadPool();

		// waiting for incoming connection
		waitingConnection();
	}

	/**
	 * Waiting and accepting request. Once a request is accepted, a new
	 * NetworkController task will be submitted to the request ExecutorService,
	 * and a corresponding TimeoutControl task will be added to timeout
	 * ExecutorService. This method will not stop until the server begin to
	 * shutdown.
	 */
	public void waitingConnection() {
		while (!shuttingdown) {
			try {
				Socket socket = serverSocket.accept();
				Future<Void> connection = requestExecutor
						.submit(new NetworkController(socket));
				timeoutExecutor.execute(new TimeoutController(connection));
			} catch (IOException ioe) {
				System.out.println("Accept connection failed on "
						+ Server.getPort());
			}
		}
	}

	/**
	 * Stop accepting new tasks and wait until all running tasks to finish.
	 * 
	 * @param timeout
	 *            in seconds.
	 * @return true if all threads terminated before timeout, false otherwise or
	 *         interrupted.
	 */
	public boolean shutdown(int timeout) {
		shuttingdown = true;
		requestExecutor.shutdown();
		timeoutExecutor.shutdown();
		try {
			return requestExecutor.awaitTermination(timeout, TimeUnit.SECONDS)
					&& timeoutExecutor.awaitTermination(timeout,
							TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * Attempt to stop all running tasks immediately.
	 */
	public void shutdownNow() {
		shuttingdown = true;
		requestExecutor.shutdownNow();
		timeoutExecutor.shutdownNow();
	}
}

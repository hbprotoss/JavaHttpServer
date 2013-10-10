package Server;

import engine.*;

/**
 * This is the entry of the server, including the main function and start and
 * stop method.
 * 
 * @author Jiaqi LI
 */
public class Server {

	public final static String STATUS_LINE_200 = "HTTP/1.0 200 OK\r\n";
	public final static String STATUS_LINE_201 = "HTTP/1.0 201 Created\r\n";
	public final static String STATUS_LINE_304 = "HTTP/1.0 304 Not Modified\r\n";
	public final static String STATUS_LINE_400 = "HTTP/1.0 400 Bad Request\r\n";
	public final static String STATUS_LINE_403 = "HTTP/1.0 403 Foribidden\r\n";
	public final static String STATUS_LINE_404 = "HTTP/1.0 404 Not Found\r\n";
	public final static String STATUS_LINE_500 = "HTTP/1.0 500 Internal Server Error\r\n";
	public final static String STATUS_LINE_501 = "HTTP/1.0 501 Not Implemented\r\n";
	public final static int DEFAULT_PORT = 4444;
	public final static String DEFAULT_HOST = "localhost";
	public final static String DEFAULT_DOCUMENT_ROOT = "www";
	public final static String DEFAULT_NAME_OF_INDEX = "index.html";
	private static String documentRoot;
	private static String nameOfIndex;
	private static int port;
	private static String host;
	private Engine engine;

	/**
	 * Start connector with default host(localhost) and default port(4444).
	 */
	public void start() {
		start(DEFAULT_HOST, DEFAULT_PORT);
	}

	public void start(int p) {
		start(DEFAULT_HOST, p);
	}

	/**
	 * Start connector with specified host and port.
	 * 
	 * @param host
	 *            the host name that the server will be binded to.
	 * @param port
	 *            the port number that the server will listening.
	 */
	public void start(String h, int p) {
		host = h;
		port = p;
		engine = new Engine(host, port);
	}

	/**
	 * Stop the server to accept new connection and wait all activated tasks to
	 * complete until 10s elapsed, if timeout elapsed before termination, this
	 * method will attempt to stop all tasks immediately, and then stop the
	 * server.
	 */
	public void stop() {
		System.out.println("Waiting for all threads to exit...");
		if (!engine.shutdown(10)) {
			engine.shutdownNow();
		}
		System.exit(0);
	}

	/**
	 * Get the port number that the server is listening.
	 * 
	 * @return the port number
	 */
	public static int getPort() {
		return port;
	}

	/**
	 * Get the host name that the server is binded to.
	 * 
	 * @return the host name
	 */
	public static String getHost() {
		return host;
	}
}

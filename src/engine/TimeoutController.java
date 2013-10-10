package engine;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class implement the timeout mechanism of the server. The Connector will
 * pass a Future of a NetwoekController to construct this TimeoutController,
 * then the TimeoutController wait for 5s by calling Future.get. If the timeout
 * reaches before the NetworkController finish it's task, then the
 * TinmeoutrController will attempt to cancel the task.
 * 
 * @author Jiaqi LI
 */
public class TimeoutController implements Runnable {

	private final int DEFALUT_TIME_OUT = 5;
	private final Future<Void> connection;

	/**
	 * Constructs a TimeoutController.
	 * 
	 * @param connection
	 *            the Future of a NetworkController.
	 */
	public TimeoutController(Future<Void> connection) {
		this.connection = connection;
	}

	/**
	 * Wait until connection finished or time out. If the timeout elapsed before
	 * the connection finish, this TimeoutController will cancel the connection.
	 */
	public void run() {
		try {
			connection.get(DEFALUT_TIME_OUT, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			connection.cancel(true);
		} catch (InterruptedException | ExecutionException e) {
			connection.cancel(true);
			System.out.println("Connection failed : "
					+ e.getCause().getMessage());
		}
	}

}

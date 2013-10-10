package engine;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

import datamodel.RequestModel;

/**
 * The network controller deal with a given socket. It reads bytes from the
 * socket's input stream and pass the bytes to request interpreter, then get the
 * constructed request model from the request interpreter and pass it to the
 * request handler, finally get response model from request handler and send it
 * back to the client.
 * 
 * @author Jiaqi LI
 */
public class NetworkController implements Callable<Void> {

	private final Socket socket;
	private RequestModel request;
	private byte[] response;

	/**
	 * Constructs a network controller which read request from client and write
	 * response to the client.
	 * 
	 * @param socket
	 *            the socket that used to interact with client.
	 */
	public NetworkController(Socket socket) {
		this.socket = socket;
	}

	/**
	 * The entire process of handling a request and writing response.
	 */
	@Override
	public Void call() throws Exception {

		InputStream requestStream = new BufferedInputStream(
				socket.getInputStream());
		OutputStream responseStream = new BufferedOutputStream(
				socket.getOutputStream());
		BufferedReader requestReader = new BufferedReader(
				new InputStreamReader(requestStream));

		try {
			// process request - request line.
			RequestInterpreter requestInterpreter = new RequestInterpreter(
					requestReader.readLine());
			// process request - headers
			String header = new String();
			while ((header = requestReader.readLine()) != null) {
				if (!requestInterpreter.processHeaders(header)) {
					break;
				}
			}

			// process request - body
			char[] body = new char[128];
			while (requestInterpreter.getBodyLength() < requestInterpreter
					.getContentLenght()) {
				if (requestReader.read(body, 0, 128) == -1)
					break;
				requestInterpreter.processBody(body);
			}
			request = requestInterpreter.getRequestModel();

			// handle request
			RequestHandler requestHandler = new RequestHandler(request);
			response = requestHandler.getResponse();
		
			// response to client
			responseStream.write(response);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occured when processing the request.\n");
		} finally {
			responseStream.close();
		}
		return null;
	}
}

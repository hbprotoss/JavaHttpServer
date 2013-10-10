package engine;

import java.util.StringTokenizer;
import datamodel.RequestModel;

/**
 * This request interpreter accept string or byte array from network controller
 * and build a request model. This interpreter can understand both HTTP/1.0 and
 * HTTP/0.9
 * 
 * @author Jiaqi LI
 */
public class RequestInterpreter {
	private final RequestModel request;

	/**
	 * return true if the request model has not been initialized.
	 * 
	 * @return true if the request model has not been initialized.
	 */
	public boolean isBadRequest() {
		return request == null;
	}

	/**
	 * Add request line to the request model, if request line is not understood,
	 * then request model will be leave null which indicates the bad request.
	 * 
	 * @param requestLine
	 *            request line from network controller
	 */
	public RequestInterpreter(String requestLine) {
		if (requestLine != null) {
			StringTokenizer tokens = new StringTokenizer(requestLine);
			if (tokens.countTokens() == 3 || tokens.countTokens() == 2) {
				String method = tokens.nextToken().trim();
				String path = tokens.nextToken().trim();
				String version = (tokens.hasMoreTokens()) ? tokens.nextToken()
						.trim() : "HTTP/0.9";
				request = new RequestModel(method, path, version);
			} else {
				request = null;
			}
		} else {
			request = null;
		}
	}

	/**
	 * Add a header to request model if the input string is not empty and the
	 * request line has been understood and the version of HTTP is not 0.9.
	 * 
	 * @param header
	 *            HTTP header
	 * @return true if successfully add a new header to the request model, false
	 *         otherwise.
	 */
	public boolean processHeaders(String header) {
		// Bad request or HTTP/0.9 request do not need headers,
		// CRLF(empty line) indicates the end of headers.
		if (isBadRequest()
				|| header.isEmpty()
				|| request.getRequestLine().getVersion().compareTo("HTTP/0.9") == 0) {
			return false;
		} else {
			int index = header.indexOf(":");
			String key = header.substring(0, index).trim();
			String value = header.substring(index + 1, header.length()).trim();
			request.addHeader(key, value);
			return true;
		}
	}

	/**
	 * Get the entity body length from the header "Content-Length", 0 means the
	 * request is either not understood or not a post request.
	 * 
	 * @return the value of Content-Length header
	 */
	public long getContentLenght() {
		if (isBadRequest()
				|| request.getRequestLine().getMethod().compareTo("POST") != 0) {
			return 0;
		} else {
			return Long.valueOf(request.getHeader("content-length"));
		}
	}

	/**
	 * Get the length of received entity body, 0 means the request is either not
	 * understood or not a post method.
	 * 
	 * @return the length of received entity body
	 */
	public long getBodyLength() {
		if (isBadRequest()
				|| request.getRequestLine().getMethod().compareTo("POST") != 0) {
			return 0;
		} else {
			return request.getBody().length();
		}
	}

	/**
	 * Append char[] to the request entity body.
	 * 
	 * @param input
	 *            the char[] that to append to the body.
	 */
	public void processBody(char[] input) {
		String body = (new String(input)).trim();
		request.getBody().append(body);
	}

	/**
	 * Return the request model that RequestInterpreter constructed.
	 * 
	 * @return Return the request model that RequestInterpreter constructed.
	 */
	public RequestModel getRequestModel() {
		return request;
	}
}

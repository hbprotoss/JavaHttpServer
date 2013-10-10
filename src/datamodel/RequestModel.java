package datamodel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Request data model of a HTTP request. It consists of a RequestLine, headers
 * and entity body.
 * 
 * @author Jiaqi LI
 * 
 */
public class RequestModel {

	private RequestLine reqLine;
	private HashMap<String, String> headers;
	private StringBuilder body;

	public RequestModel(String m, String p) {
		this(m, p, "HTTP/0.9");
	}

	public RequestModel(String m, String p, String v) {
		reqLine = new RequestLine(m, p, v);
		headers = new HashMap<String, String>();
		body = new StringBuilder("");
	}

	public void addHeader(String key, String value) {
		headers.put(key.toLowerCase(), value.toLowerCase());
	}

	public RequestLine getRequestLine() {
		return reqLine;
	}

	public String getHeader(String key) {
		return headers.get(key);
	}

	public String getBodyString() {
		return body.toString();
	}

	public StringBuilder getBody() {
		return body;
	}

	@Override
	public String toString() {
		return toString("http");
	}

	public String toString(String format) {
		String newLine = (format.compareTo("html") == 0) ? "<br />" : "\r\n";

		if (reqLine.getVersion().compareTo("HTTP/0.9") == 0) {
			return reqLine.toString();
		} else {
			StringBuilder request = new StringBuilder();
			request.append(reqLine.toString());
			if (headers != null) {
				Iterator iter = headers.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					request.append(entry.getKey() + ": " + entry.getValue()
							+ newLine);
				}
				request.append(newLine);
			}

			if (body != null) {
				request.append(body.toString());
			}
			return request.toString();
		}
	}
}


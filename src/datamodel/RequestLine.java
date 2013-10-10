package datamodel;

/**
 * Request line of a HTTP request.
 * 
 * @author Jiaqi LI
 */
public class RequestLine {
	private final String method;
	private final String path;
	private final String version;

	public RequestLine(String m, String p, String v) {
		this.method = m.toUpperCase();
		this.path = p;
		this.version = v.toUpperCase();
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		if (version.compareTo("HTTP/0.9") == 0) {
			return method + " " + path + " \r\n";
		} else {
			return method + " " + path + " " + version + " \r\n";
		}
	}
}

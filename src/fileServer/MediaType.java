package fileServer;

/**
 * This is a help class to get Internet media type according to file name.
 * 
 * @author Jiaqi LI
 * @since 25/11/2012
 * @version 1
 */
public class MediaType {

	/**
	 * This static method map the suffix to the Internet media type;
	 * 
	 * @param name
	 *            the path of name of the file.
	 * @return the corresponding Internet media type.
	 */
	public static String getContentTypeFromName(String name) {
		if (name.endsWith(".html") || name.endsWith(".htm")
				|| name.endsWith("/") || name.endsWith(".php")) {
			return "text/html";
		} else if (name.endsWith(".txt")) {
			return "text/plain";
		} else if (name.endsWith(".css")) {
			return "text/css";
		} else if (name.endsWith(".js")) {
			return "text/js";
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (name.endsWith(".gif")) {
			return "image/gif";
		} else if (name.endsWith(".png")) {
			return "image/png";
		} else {
			return "text/plain";
		}
	}
}

package datamodel;

import Server.*;

/**
 * Generate an error page based on status code and error description.
 * 
 * @author Jiaqi LI
 */
public class ErrorPage {
	private final int code;
	private final String title;
	private final String desc;
	private String page;

	/**
	 * Generate an HTML error page.
	 */
	private void generatePage() {
		StringBuilder page = new StringBuilder();
		page.append("<html>\r\n");
		page.append("<head><TITLE>" + code + " " + title
				+ "</title></head>\r\n");
		page.append("<body>\r\n");
		page.append("<h1>" + title + "</h1>");
		page.append(desc + "\r\n");
		page.append("<hr />\r\n");
		page.append("<em>G52APR Web Server (jxl12u) at " + Server.getHost()
				+ " Port " + Server.getPort() + "</em>");
		page.append("</BODY></HTML>\r\n");
		this.page = page.toString();
	}

	/**
	 * Constructs a new error page by given http error code, the error meanning
	 * and detail description of the error.
	 * 
	 * @param code
	 *            HTTP status code
	 * @param title
	 *            the meaning of the status code
	 * @param desc
	 *            the detail description of the status code
	 */
	public ErrorPage(int code, String title, String desc) {
		this.code = code;
		this.title = title;
		this.desc = desc;
		generatePage();
	}

	/**
	 * Get error page as a string.
	 * 
	 * @return error page as string.
	 */
	public String getPage() {
		return page;
	}
}

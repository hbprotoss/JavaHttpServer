package fileServer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Server.Server;

/**
 * This file server implements IServer interface and handle Get, head Post
 * request.
 * 
 * @author Jiaqi LI
 * @since 25/11/2012
 * @version 1
 * 
 */
public class FileServer implements IServe {

	private String documentRoot;
	private String requestURI;
	private String httpVersion;
	private boolean executable;

	/**
	 * Construct a file server by given root of server, requested URI and http
	 * version.
	 * 
	 * @param httpVersion
	 *            the HTTP version could be HTTP/0.9 or HTTP/1.0.
	 * @param documentRoot
	 *            the document root of the server.
	 * @param requestURI
	 *            the request file.
	 */
	public FileServer(String httpVersion, String documentRoot, String requestURI) {
		this.documentRoot = documentRoot;
		this.requestURI = requestURI;
		this.httpVersion = httpVersion;
		this.executable = requestURI.endsWith(".php");
	}

	@Override
	public byte[] httpHEAD(String requestURI) throws HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		// Although body is not returned, the length of body is needed.
		byte[] body = (executable) ? (new CGI()).executePHP(documentRoot, requestURI)
				: FileStore.getFileContent(documentRoot, requestURI);

		String statusLine = Server.STATUS_LINE_200;
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = "Date: " + dateFormatGmt.format(new Date()) + " GMT\r\n";
		String lastModified = "Last-Modified: "
				+ dateFormatGmt.format(new File(documentRoot + requestURI)
						.lastModified()) + " GMT\r\n";
		String contentType = "Content-Type: "
				+ MediaType.getContentTypeFromName(requestURI) + "\r\n";
		String contentLength = "Content-Length:" + String.valueOf(body.length)
				+ "\r\n";
		String newLine = "\r\n";
		try {
			return ((statusLine + date + lastModified + contentLength
					+ contentType + newLine).getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			return ((statusLine + date + lastModified + contentLength
					+ contentType + newLine).getBytes());
		}
	}

	@Override
	public byte[] httpGet(String requestURI) throws HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		byte[] body = (executable) ? (new CGI()).executePHP(documentRoot, requestURI)
				: FileStore.getFileContent(documentRoot, requestURI);

		if (httpVersion.compareTo("HTTP/0.9") == 0) {
			return body;
		} else {
			ByteArrayOutputStream response = new ByteArrayOutputStream();
			try {
				response.write(Server.STATUS_LINE_200.getBytes("US-ASCII"));
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				response.write(("Date: " + dateFormatGmt.format(new Date()) + " GMT\r\n")
						.getBytes("US-ASCII"));
				response.write(("Last-Modified: "
						+ dateFormatGmt.format(new File(documentRoot
								+ requestURI).lastModified()) + " GMT\r\n")
						.getBytes("US-ASCII"));
				response.write(("Content-Type: "
						+ MediaType.getContentTypeFromName(requestURI) + "\r\n")
						.getBytes("US-ASCII"));
				response.write(("Content-Length:" + String.valueOf(body.length) + "\r\n")
						.getBytes("US-ASCII"));
				if(!executable){
					// The php-cgi will automaticly add new line.
					response.write("\r\n".getBytes("US-ASCII"));
				}
				response.write(body);
				return response.toByteArray();
			} catch (Exception e) {
				throw new HTTPRuntimeException(
						"The server encountered an internal error and was unable to complete your request.",
						e);
			}
		}
	}

	@Override
	public byte[] httpGETconditional(String requestURI, Date ifModifiedSince)
			throws HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {
		Date serverModifiedDate = HttpDateParser
				.parseHttpDate(new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss", Locale.US)
						.format(new File(documentRoot + requestURI)
								.lastModified())
						+ " GMT");

		if (ifModifiedSince == null || serverModifiedDate == null
				|| serverModifiedDate.after(ifModifiedSince)) {
			return httpGet(requestURI);
		} else {
			String statusLine = Server.STATUS_LINE_304;
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			// Only date header is needed for not modified response.
			String date = "Date: " + dateFormatGmt.format(new Date())
					+ " GMT\r\n";
			String newLine = "\r\n";
			try {
				return ((statusLine + date + newLine).getBytes("US-ASCII"));
			} catch (UnsupportedEncodingException e) {
				return ((statusLine + date + newLine).getBytes());
			}
		}
	}

	@Override
	public byte[] httpPOST(String requestURI, byte[] postData)
			throws HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {
		File requestFile = new File(documentRoot + requestURI);

		if (!requestFile.exists())
			throw new HTTPFileNotFoundException("The request URL " + requestURI
					+ " was not found on this server.");

		if(executable){
			byte[] body = (new CGI(new String(postData))).executePHP(documentRoot, requestURI);
			ByteArrayOutputStream response = new ByteArrayOutputStream();
			
			try{
				response.write(Server.STATUS_LINE_200.getBytes("US-ASCII"));
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				response.write(("Date: " + dateFormatGmt.format(new Date()) + " GMT\r\n")
						.getBytes("US-ASCII"));
				response.write(("Last-Modified: "
						+ dateFormatGmt.format(new File(documentRoot
								+ requestURI).lastModified()) + " GMT\r\n")
						.getBytes("US-ASCII"));
				response.write(("Content-Length:" + String.valueOf(body.length) + "\r\n")
						.getBytes("US-ASCII"));
				response.write(body);
				return response.toByteArray();
			}catch(Exception e){
				throw new HTTPRuntimeException("The server encountered an internal error and was unable to complete your request.");
			}
		}else{
			return httpGet(requestURI);
		}

		

	}

	/**
	 * This method is called if the POST request encountered a HTTPFileNotFound
	 * error. This method will create the file (and any folder if nessary) with
	 * the post data.
	 * 
	 * @param postData
	 *            the body of POST request
	 * @return
	 * @throws HTTPRuntimeException
	 */
	public byte[] createFile(byte[] postData) throws HTTPRuntimeException {
		File requestFile = new File(documentRoot + requestURI);
		try {
			if (requestURI.lastIndexOf("/") != -1) {
				File pathFile = new File(documentRoot
						+ requestURI.substring(0, requestURI.lastIndexOf("/")));
				pathFile.mkdirs();
			}
			requestFile.createNewFile();
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(requestFile)));
			dos.write(postData);
			dos.close();

			String statusLine = Server.STATUS_LINE_201;
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = "Date: " + dateFormatGmt.format(new Date())
					+ " GMT\r\n";
			String lastModified = "Last-Modified: "
					+ dateFormatGmt.format(new Date()) + " GMT\r\n";
			String contentType = "Content-Type: "
					+ MediaType.getContentTypeFromName(requestURI) + "\r\n";
			String contentLength = "Content-Length:"
					+ String.valueOf((documentRoot + requestURI).length())
					+ "\r\n";
			String location = "Location: " + documentRoot + requestURI + "\r\n";
			String newLine = "\r\n";
			String bodyString = documentRoot + requestURI;

			return (statusLine + date + lastModified + contentType
					+ contentLength + location + newLine + bodyString)
					.getBytes("US-ASCII");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new HTTPRuntimeException(
					"the server encountered an internal error and was unable to complete your request.",
					ex);
		}
	}

}

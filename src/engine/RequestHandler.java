 package engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import Server.Server;

import datamodel.ErrorPage;
import datamodel.RequestModel;
import fileServer.*;

/**
 * This request handler takes a request model to construct a byte array of the response.
 * 
 * @author Jiaqi LI
 * 
 */

public class RequestHandler {

	private String indexFile = Server.DEFAULT_NAME_OF_INDEX;
	private String documentRoot = Server.DEFAULT_DOCUMENT_ROOT;
	private final String version;
	private final RequestModel request;
	private byte[] response;

	

	/**
	 * Constructs a RequestHandler with a request model.
	 * 
	 * @param r
	 *            request model
	 */
	public RequestHandler(RequestModel r) {

		request = r;
		if (request == null) {
			// request can not be understood
			version = "HTTP/1.0";
			response = doError(400, "Bad Request",
					"The request could not be understood by the server.", true);
		} else {
			version = request.getRequestLine().getVersion();
			if (r.getRequestLine().getMethod().compareTo("GET") == 0) {
				response = doGet();
			} else if (r.getRequestLine().getMethod().compareTo("HEAD") == 0) {
				response = doHead();
			} else if (r.getRequestLine().getMethod().compareTo("POST") == 0) {
				response = doPost();
			} else {
				response = doError(400, "Bad Request",
						"The request could not be understood by the server.", true);
			}
		}
	}

	/**
	 * Get the response model of a handled request.
	 * 
	 * @return response model for a handled request
	 */
	public byte[] getResponse() {
		return response;
	}

	/**
	 * Perform GET method and return the result as a byte array.
	 */
	public byte[] doGet() {

		try{
			String requestURI = request.getRequestLine().getPath();
			if(requestURI.endsWith("/")){
				requestURI += indexFile;
			}
			Date ifModifiedSince = HttpDateParser.parseHttpDate(request
                    .getHeader("if-modified-since"));
			if(ifModifiedSince == null){
				return new FileServer(version, documentRoot, requestURI).httpGet(requestURI);
			}else{
				return new FileServer(version, documentRoot, requestURI).httpGETconditional(requestURI, ifModifiedSince);
			}
		} catch (HTTPFileNotFoundException e) {
			return doError(404, "Not Found", e.getMessage(), true);
		} catch (HTTPPermissionDeniedException e) {
			return doError(403, "Forbidden", e.getMessage(), true);
		} catch (Exception e) {
			return doError(500, "Internal Server Error", e.getMessage(), true);
		}
	}

	/**
	 * Perform HEAD method and return the result as a byte array.
	 */
	public byte[] doHead() {
		try{
			String requestURI = request.getRequestLine().getPath();
			if(requestURI.endsWith("/")){
				requestURI += indexFile;
			}
			return new FileServer(version, documentRoot, requestURI).httpHEAD(requestURI);
		} catch (HTTPFileNotFoundException e) {
			return doError(404, "Not Found", e.getMessage(), false);
		} catch (HTTPPermissionDeniedException e) {
			return doError(403, "Forbidden", e.getMessage(), false);
		} catch (Exception e) {
			e.printStackTrace();
			return doError(500, "Internal Server Error", e.getMessage(), false);
		}
	}

	/**
	 * Perform POST method and return the result as a byte array.
	 */
	public byte[] doPost() {
		
		String requestURI = request.getRequestLine().getPath();
		if(requestURI.endsWith("/")){
			requestURI += indexFile;
		}
		FileServer fileServer = new FileServer(version, documentRoot, requestURI);
		
		try{
			return fileServer.httpPOST(requestURI, request.getBody().toString().getBytes("US-ASCII"));
		}catch (HTTPFileNotFoundException e) {
			// create the file is it does not exist.
			try{
				return fileServer.createFile(request.getBody().toString().trim().getBytes("US-ASCII"));
			}catch(Exception ex){
				return doError(500, "Internal Server Error", ex.getMessage(), true);
			}
		} catch (HTTPPermissionDeniedException e) {
			return doError(403, "Forbidden", e.getMessage(), true);
		} catch (Exception e) {
			return doError(500, "Internal Server Error", e.getMessage(), true);
		}
	}

	/**
	 * Constructs an error page
	 * 
	 * @param code
	 *            HTTP status code
	 * @param title
	 *            the meaning of the status code
	 * @param desc
	 *            the detail description of the error
	 */
	public byte[] doError(int code, String title, String desc, boolean containBody) {

		try{
		if (version.compareTo("HTTP/0.9") == 0) {
			return new String("HTTP/0.9 " + code + " " + title + "\r\n").getBytes("US-ASCII");
		} else {
			ByteArrayOutputStream response = new ByteArrayOutputStream();
			response.write((version.toUpperCase() + " " + code + " " + title + "\r\n").getBytes("US-ASCII"));
			if(containBody){
				ErrorPage ep = new ErrorPage(code, title, desc);
				SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				response.write(("Date: " + dateFormatGmt.format(new Date())
						+ " GMT\r\n").getBytes("US-ASCII"));
				response.write("Content-Type: text/html\r\n".getBytes("US-ASCII"));
				response.write(("Content-Length: " + Integer.toString(ep.getPage().getBytes("US-ASCII").length) + "\r\n\r\n").getBytes("US-ASCII"));
				response.write(ep.getPage().getBytes("US-ASCII"));
			}
			return response.toByteArray();
		}
		}catch(UnsupportedEncodingException e){
			return (Server.STATUS_LINE_500).getBytes();
		}catch (IOException e) {
			return (Server.STATUS_LINE_500).getBytes();
		}
		
	}
}

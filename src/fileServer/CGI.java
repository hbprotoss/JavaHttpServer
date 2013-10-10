package fileServer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Execute php file and get the output.
 * 
 * @author Jiaqi LI
 * @since 25/11/2012
 * @version 1
 * 
 */
public class CGI {

	private boolean isPost = false;
	private String post;
	
	public CGI(String post){
		this.isPost = true;
		this.post = post;
	}
	
	public CGI(){
		
	}
	/**
	 * Execute the php file and return the output as a byte array.
	 * 
	 * @param documentRoot
	 *            the root of the server.
	 * @param requestURI
	 *            the requested file.
	 * @return the output of the php file as a byte array.
	 * @throws HTTPPermissionDeniedException
	 *             if IOExceprion occur.
	 * @throws HTTPRuntimeException
	 *             if interruptedException occur.
	 * @throws HTTPFileNotFoundException
	 *             if file not found.
	 */
	public byte[] executePHP(String documentRoot, String requestURI)
			throws HTTPPermissionDeniedException, HTTPRuntimeException,
			HTTPFileNotFoundException {

		File phpFile = new File(documentRoot + requestURI);
		if (!phpFile.exists()) {
			throw new HTTPFileNotFoundException("The request URL " + requestURI
					+ " wat not found on this server.");
		}

		Runtime runtime = Runtime.getRuntime();
		Process p;

		try {
			if(isPost){
				System.out.print(System.getProperty("os.name"));
				StringBuilder command = new StringBuilder();
				
				command.append("export REDIRECT_STATUS=\"yes\";");
				command.append("export REQUEST_METHOD=POST;");
				command.append("export SCRIPT_FILENAME=" + documentRoot + requestURI + ";");
				command.append("export CONTENT_TYPE=application/x-www-form-urlencoded; ");
				command.append("export CONTENT_LENGTH=" + post.length() + ";");
				command.append("echo " + post);
				command.append(" | php-cgi");
				p = runtime.exec(command.toString());

				/*runtime.exec("set REDIRECT_STATUS=\"yes\";");
				runtime.exec("set REQUEST_METHOD=POST;");
				runtime.exec("set SCRIPT_FILENAME=" + documentRoot + requestURI + ";");
				runtime.exec("set CONTENT_TYPE=application/x-www-form-urlencoded; ");
				runtime.exec("set CONTENT_LENGTH=" + post.length() + ";");
				runtime.exec("echo " + post);*/
				p = runtime.exec("php-cgi");
			}else{
				p = runtime.exec("php-cgi " + documentRoot + requestURI);
			}

			InputStream returnStreamFromFile = p.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(returnStreamFromFile));
			ByteArrayOutputStream returnByteArray = new ByteArrayOutputStream();
			String line;
			while ((line = reader.readLine()) != null) {
				returnByteArray.write((line + "\r\n").getBytes("US-ASCII"));
			}
			
			p.waitFor();
			
			if(p.exitValue() != 0){
				throw new HTTPPermissionDeniedException("You don't have permission to access " + requestURI + " on this server.");
			}else{
				return returnByteArray.toByteArray();
			}
			
		} catch (IOException e) {
			throw new HTTPRuntimeException(
					"The server encountered an internal error and was unable to complete your request.\n" + 
					"Possible cause",
					e);
		} catch(InterruptedException e){
			throw new HTTPRuntimeException(
					"The server encountered an internal error and was unable to complete your request.",
					e);
		}
	}
}

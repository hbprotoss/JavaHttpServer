package fileServer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Get the file as byte array on the local disk.
 * 
 * @author Jiaqi LI
 * @since 25/11/2012
 * @version 1
 * 
 */
public class FileStore {

	/**
	 * Get file as a byte array from local disk.
	 * 
	 * @param documentRoot
	 *            the root of the server
	 * @param requestURI
	 *            the request file path
	 * @return return the content of the file as byte array.
	 * @throws HTTPFileNotFoundException
	 *             throw the exception if the file not exist.
	 * @throws HTTPPermissionDeniedException
	 *             throw the exception if an IOException occur.
	 */
	public static byte[] getFileContent(String documentRoot, String requestURI)
			throws HTTPFileNotFoundException, HTTPPermissionDeniedException {
		File requestFile = new File(documentRoot + requestURI);

		try {
			byte[] data = new byte[(int) requestFile.length()];
			DataInputStream fis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(requestFile)));
			fis.read(data, 0, data.length);

			fis.close();
			return data;
		} catch (FileNotFoundException e) {
			throw new HTTPFileNotFoundException("The request URL " + requestURI
					+ " was not found on this server.", e);
		} catch (IOException e) {
			throw new HTTPPermissionDeniedException(
					"You don't have permission to access " + requestURI
							+ " on this server.", e);
		}
	}
}

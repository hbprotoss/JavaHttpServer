package Server;

public class Main {
	/**
	 * This is the main function of the server. Server accepts 0 or 1 argument
	 * from the terminal. If start with no argument, then the default value of
	 * host and port which are localhost and 4444 respectively will be applied.
	 * If start with 1 argument, it can be either hostname:portnumber which
	 * specify both host name and port number or just portnumber.
	 * 
	 * @param args
	 *            Usage: [[<hostname>:]<port_number>]
	 */
	public static void main(String[] args) {
		Server server = new Server();
		if (args.length >= 1) {
			String cmd = args[0];
			try {
				int idx = cmd.indexOf(":");
				if (idx > 0) {
					server.start(cmd.substring(0, idx), Integer.parseInt(cmd.substring(idx + 1)));
				} else {
					server.start(Integer.parseInt(cmd.substring(idx + 1)));
				}
			} catch (Exception e) {
				System.out
						.println("Usage: java Server [[<hostname>:]<port_number>]");
				return;
			}
		} else {
			server.start();
		}
	}
}

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import junit.framework.TestCase;

import org.uptecs.email.Mail;


public class TimeoutTest extends TestCase {
	
	private static final int port = 9091;

	public void testTestConnectFail() {
		Mail mail = new Mail("127.0.0.1", port);
		mail.setTimeout(1000);
		int result = mail.testConnection();
		assertNotSame(0, result);
		assertEquals("Unable to connect to mail server", mail.getError());
	}

	public void testTestTimeout() {
		Mail mail = new Mail("1.1.1.1", port);
		mail.setTimeout(1000);
		int result = mail.testConnection();
		assertNotSame(0, result);
		assertEquals("Timeout while connecting to mail server", mail.getError());
	}

	public void testTestReadTimeout() throws IOException {
		UnresponsiveServer server = new UnresponsiveServer(port);
		server.start();

		Mail mail = new Mail("127.0.0.1", port);
		mail.setTimeout(1000);
		int result = mail.testConnection();
		assertNotSame(0, result);
		assertEquals("Timeout while waiting for response from to mail server", mail.getError());

		server.cancel();
	}
	
	/**
	 * Helper class that simulates a mail server that is not returning any data.
	 */
	public class UnresponsiveServer extends Thread {

		private ServerSocket s;

		public UnresponsiveServer(int port) throws IOException {
			super("Unresponsive server");

			s = new ServerSocket();
			s.setReuseAddress(true);
			s.bind(new InetSocketAddress("127.0.0.1", port));
		}

		public void run() {
			try {
				Thread.sleep(100000000);
			} catch (InterruptedException e1) {
			}
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void cancel() {
			this.interrupt();
		}

	}

}

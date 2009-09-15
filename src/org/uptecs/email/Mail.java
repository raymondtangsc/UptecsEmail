/*
 * Uptecs Jakar standard library.
 *
 * Copyright(c)2006-2007, Uptecs.
 */
package org.uptecs.email;

import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.lang.Math;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basc class to allow sending of mail. Note this feature is custom written
 * because this facility does not always work out of the box on all systems.
 * This sender is based upon/compliant with RFC 821 and RFC 2822.
 */
public class Mail {

	private String host=null;
	private int port=25;
	private String error="";
	private static Random generator=null;
	static { generator=new Random(); }

	public Mail(String host,int port) {
		this.host=host;
		this.port=port;
	}

	public Mail(String host) {
		this.host=host;
	}

	String auth_username=null;
	String auth_password=null;
	boolean auth_enabled=false;

	/*
	 * Attempt to authenticate to the mail server, primarily used to enable
	 * relaying of email where required.
	 *
	 * @param username Provide account username
	 * @param password Specify account password
	 */
	public void enableAuthentication(String username, String password) {
		auth_username=username;
		auth_password=password;
		auth_enabled=true;
	}

	/*
	 * Disable attempting to authenticate with mail server.
	 */
	public void disableAuthentication() {
		auth_username=null;
		auth_password=null;
		auth_enabled=false;
	}

	/*
	 * Used to retrive the error message related generated due
	 * to failure of sendMail() function.
	 *
	 * @return Text string containing english description of problem.
	 */
	public String getError() {
		String e2=error;
		error="";
		return e2;
	}

	/*
	 * Send and email to multiple addresses in plain text format, no html or mime
	 * encoding. Specify multiple addresses in the to field separated by a comma.
	 *
	 * @param from Sender email address.
	 * @param to Destination email addresses separated by a comma.
	 * @param subject Subject of email message.
	 * @param content content of email message to be sent.
	 *
	 * @return 0 if successful, otherwise error number returned.
	 */
	public int sendMails(String from, String to, String subject,
			String content) {
		int retval=0;

		String[] a=to.split(" *, *");
		for(int i=0;i<a.length;i++) {
			int status=sendMail(from,a[i],subject,content,(List<String>)null,"");
			if(status>0) {
				error="Problem occured while sending one of the messages.";
				retval=1;
			}
		}

		return retval;
	}

	public int sendHtmlMail(String from, String to, String subject,
			String content) {
		List<String> headers=new ArrayList<String>();
		headers.add("Content-type: text/html; charset=UTF-8");
		headers.add("Mime-Version: 1.0");
		return sendMail(from,to,subject,content,headers,"");
	}

	public int sendHtmlMail(String from, String to, String subject,
			String content, String alt) {
		return sendMail(from,to,subject,content,null,alt);
	}

	public int sendHtmlMail(String from, String to, String subject,
			String content, String alt,List<String> headers) {
		return sendMail(from,to,subject,content,headers,alt);
	}

	/*
	 * Send a message in plain text format, no html or mime encoding
	 *
	 * @param from Sender email address.
	 * @param to Destination email address.
	 * @param subject Subject of email message.
	 * @param content content of email message to be sent.
	 *
	 * @return 0 if successful, otherwise error number returned.
	 */
	public int sendMail(String from, String to, String subject,
			String content) {
		return sendMail(from,to,subject,content,(List<String>)null,"");
	}

	/*
	 * Send a message in plain text format, no html or mime encoding
	 *
	 * @param from Sender email address.
	 * @param to Destination email address.
	 * @param subject Subject of email message.
	 * @param content content of email message to be sent.
	 *
	 * @return 0 if successful, otherwise error number returned.
	 */
	public int sendMail(String from, String to, String subject,
			String content,List<String> headers) {
		return sendMail(from,to,subject,content,headers,"");

	}

	/*
	 * Send a message in plain text format, no html or mime encoding
	 *
	 * @param from Sender email address.
	 * @param to Destination email address.
	 * @param subject Subject of email message.
	 * @param content content of email message to be sent.
	 * @param headers Extra header lines to insert, this may be null.
	 *
	 * @return 0 if successful, otherwise error number returned.
	 */
	public int sendMail(String from, String to, String subject,
			String content, List<String> headers, String alt) {
		Socket socket=null;
		String response=null;
		String datestamp="";

		Format dformat=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
		datestamp=dformat.format(new Date());

		Pattern p=Pattern.compile("[\"']*([^\"'<]*)[\"']* *<([^>]*)>");

		String from_email=from;
		String from_name="";
		if(from.indexOf('<')>0) {
			Matcher m=p.matcher(from);
			if(m.matches()) {
				from_name=m.group(1);
				from_email=m.group(2);
			}
		}

		String to_email=to;
		String to_name="";
		if(to.indexOf('<')>0) {
			Matcher m=p.matcher(to);
			if(m.matches()) {
				to_name=m.group(1);
				to_email=m.group(2);
			}
		}

		EmailAddress etest=new EmailAddress();
		if(!etest.isValid(from_email)) { error=etest.getError(); return 1; }
		if(!etest.isValid(to_email)) { error=etest.getError(); return 1; }

		try {
			socket = new Socket(host, port);
		}
		catch(Exception e) {
			error="Cannot connect to specified mail server";
			return 1;
		}
		try {
			InetAddress lina=InetAddress.getLocalHost();
			PrintStream ps=new PrintStream(socket.getOutputStream());
			InputStreamReader dd=new InputStreamReader(socket.getInputStream());
			BufferedReader dis=new BufferedReader(dd);

			response=dis.readLine();
			if(response.indexOf("220 ")!=0) { error="Mail did not respond like a mail server"; return 1; }

			ps.println("HELO " + lina.toString()); ps.flush();
			response=dis.readLine();
			if(response.indexOf("250 ")!=0) { error=response; return 1; }

			if(auth_enabled) {
				ps.println("AUTH LOGIN"); ps.flush();
				response=dis.readLine();
				if(response.indexOf("334 ")!=0) {
					error=response;
					return 1;
				}
				ps.println(Base64Encode.encode(auth_username)); ps.flush();
				response=dis.readLine();
				if(response.indexOf("334 ")!=0) {
					error=response;
					return 1;
				}
				ps.println(Base64Encode.encode(auth_password)); ps.flush();
				response=dis.readLine();
				if(response.indexOf("235 ")!=0) {
					error=response;
					return 1;
				}
			}

			ps.println("MAIL FROM:" + from_email); ps.flush();
			response=dis.readLine();
			if(response.indexOf("250 ")!=0) { error=response; return 1; }

			ps.println("RCPT TO:" + to_email); ps.flush();
			response=dis.readLine();
			if(response.indexOf("250 ")!=0) { error=response; return 1; }

			ps.println("DATA");
			response=dis.readLine();
			if(response.indexOf("354 ")!=0) { error=response; return 1; }

			ps.println("Date: "+datestamp);
			if(from_name.length()>0) {
				ps.println("From: \""+from_name+"\" <"+from_email+">");
			}
			else {
				ps.println("From: "+from_email);
			}
			ps.println("Subject: "+subject);
			if(to_name.length()>0) {
				ps.println("To: \""+to_name+"\" <"+to_email+">");
			}
			else {
				ps.println("To: "+to_email);
			}
			ps.println("Message-ID: <"+Math.abs(generator.nextLong())+"@"+lina.toString()+">");
			if(headers!=null) {
				for(String header : headers) {
					ps.println(header);	
				}
			}

			if(alt.length()>0) {
				String boundary=String.valueOf(Math.abs(generator.nextLong()));
				ps.println("Content-type: multipart/alternative; charset=UTF-8;  boundary="+boundary);
				ps.println("Mime-Version: 1.0");
				ps.println("");
				ps.println("--"+boundary);
				ps.println("Content-Type: text/plain; charset=utf-8; format=flowed");
				ps.println("Content-Transfer-Encoding: 8bit");
				ps.println("Content-Disposition: inline");
				ps.println("");
				ps.println(alt);
				ps.println("");
				ps.println("--"+boundary);
				ps.println("Content-Type: text/html; charset=utf-8");
				ps.println("Content-Transfer-Encoding: 8bit");
				ps.println("Content-Disposition: inline");
				ps.println("");
				ps.println(content);
				ps.println("");
				ps.println("--"+boundary+"--");
			} else {
				ps.println("");
				ps.println(content);
				ps.println("");
			}
			

			ps.println("."); ps.flush();
			response=dis.readLine();
			if(response.indexOf("250 ")!=0) { error=response; return 1; }

			ps.println("QUIT"); ps.flush();
			response=dis.readLine();
			if(response.indexOf("221 ")!=0) { error=response; return 1; }

		}
		catch (Exception ex) {
			error="Problem communicating with mail server";
			try{socket.close();}catch(Exception e) {}
			return 2;
		}

		return 0;
	}

	/*
	 * Used to test connection and communication with the specified mail server.
	 * Will report if a connection can not be established with the mail server
	 * or if the mailserver does not reply with content a mail server should reply
	 * with, or finally if there are network difficulties at the time of the test.
	 *
	 * @return 0 if successful, >0 if not successful. Use getError() to read the problem.
	 */
	public int testConnection() {
		Socket socket=null;
		String response=null;

		try {
			socket = new Socket(host, port);
		}
		catch(Exception e) {
			error="Cannot connect to specified mail server";
			return 1;
		}
		try {
			PrintStream ps=new PrintStream(socket.getOutputStream());
			InputStreamReader dd=new InputStreamReader(socket.getInputStream());
			BufferedReader dis=new BufferedReader(dd);

			response=dis.readLine();
			if(response.indexOf("220 ")!=0) { error="Mail did not respond like a mail server"; return 1; }

			ps.println("QUIT"); ps.flush();
			response=dis.readLine();
			if(response.indexOf("221 ")!=0) { error=response; return 1; }

		}
		catch (Exception ex) {
			error="Problem communicating with mail server";
			try{socket.close();}catch(Exception e) {}
			return 2;
		}

		return 0;
	}

}

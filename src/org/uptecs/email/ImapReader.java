/*
 * Uptecs Jakar standard library.
 *
 * Copyright(c)2006-2007, Uptecs.
 */
package org.uptecs.email;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.*;
import javax.net.ssl.*;

/**
 * Provides basic access to an IMAP account, without
 * a huge chunky library. Example usage:
 * <pre>
 * ImapReader i=new ImapReader();
 * i.setHostname("mailserver.com");
 * i.setUsername("user@mailserver.com");
 * i.setPassword("password");
 * try {
 *   i.connect();
 *   int count=i.readMailbox("INBOX");
 *   System.out.println("Found "+count+" emails.");
 *   for(int x=1;x<=count;x++) {
 *     i.readMail(x);
 *   }
 *   i.logout();
 * } catch(IOException e) {
 *   System.out.println("Problem accessing server. "+e);
 * }
 * </pre>
 */
public class ImapReader {
    static Socket imap=null;
    static OutputStreamWriter outStream=null;
    static InputStream inStream=null;
    static BufferedReader reader=null;
    
    private String host="";
    private int port=143;       // USe 993 for SSL;
    private String username="";
    private String password="";
    private int command=1;
    private boolean ssh=false;
    
    /**
     * Specify the host name to connect to.
     */
    public void setHostname(String h) {
        host=h;
    }
    
    /**
     * Specify the user name to be used for this transaction.
     *
     * @param u Username to provide to IMAP server.
     */
    public void setUsername(String u) {
        username=u;
    }
    
    /**
     * Specify the password to be used with this transaction.
     * @param p The password to use for this connection.
     */
    public void setPassword(String p) {
        password=p;
    }
    
    /**
     * Initiate connection to the server
     */
    public int connect() throws IOException {
        SSLContext sc=null;
        String line=null;
        Random r=new Random();
        command=Math.abs(r.nextInt()%1000000);
        
        try {
            if(ssh==true) {
            /*
             * Create a trust manager that does not validate certificate chains
             */
                TrustManager[] trustAllCerts=new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[]
                                getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String
                                authType) { }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String
                                authType) { }
                    }
                };
                
                // Install the all-trusting trust manager
                try {
                    sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new
                            java.security.SecureRandom());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // connect with the IMAP server
                SocketFactory socketFactory = sc.getSocketFactory();
                imap=socketFactory.createSocket(host, port);
            } else {
                imap=new Socket(host,143);
            }
            
            outStream=new OutputStreamWriter(imap.getOutputStream());
            inStream = imap.getInputStream();
            reader = new BufferedReader(new
                    java.io.InputStreamReader(inStream));
            
            line=readLine();
            if(!line.substring(0,4).equalsIgnoreCase("* OK") ) { return 2; }
            write("LOGIN "+username+" "+password);
            line=readLine();
            if(!line.substring(0,5).equalsIgnoreCase("J1 OK") ) { return 4; }
        } catch(UnknownHostException e) {
            System.out.println("UnknownHostException has been caught.");
            throw new IOException("Unable to resolve hostname.");
        }
        return 0;
    }
    
    /**
     *  Marks a message as deleted/ready for deletion.
     *
     *  @param id Id of the mail as provided by the readMail function.
     */
    public void deleteMail(int id) throws IOException {
        write("store "+id+" +FLAGS (\\Deleted)");
        String line=readLine();
        while(line.charAt(0)=='*') {
            line=readLine();
        }
    }
    
    /**
     * Flushes all email messages that are marked for deletion.
     */
    public void emptyTrash() throws IOException {
        write("expunge");
        String line=readLine();
        while(line.charAt(0)=='*') {
            line=readLine();
        }
    }
    
    private Pattern endcheck=Pattern.compile("J(\\d+) OK.*");
    
    /**
     * Read contents of a particular email. May only be called after
     * first using the readMailbox function to select which mail folder
     * we which to read.
     *
     * @param id of email
     * @return Contents of email.
     */
    public EmailMessage readMail(int id) throws IOException {
        StringBuffer content=new StringBuffer();
        
        write("fetch "+id+" body[header]");
        String line=readLine();
        EmailMessage e=new EmailMessage();
        while(true) {
            Matcher m=endcheck.matcher(line);
            if(m.matches()) {
                break;
            }
            if(line.length()>0 && (line.charAt(0)==' ' || line.charAt(0)=='\t')) {
                content.append(line);
            } else {
                content.append("\n");
                content.append(line);
            }
            line=readLine();
        }
        e.setHeaders(content.toString());
        
        content=new StringBuffer();
        write("fetch "+id+" body[text]");
        line=readLine(); //Skip first line, its just the command response
        line=readLine();
        while(true) {
            Matcher m=endcheck.matcher(line);
            if(m.matches()) {
                break;
            }
            content.append(line);
            content.append("\n");
            line=readLine();
        }
        String data=content.toString();
        data=data.substring(0,data.lastIndexOf(')'));
        e.setBody(data);
        
        return e;
    }
    
    
    /**
     * Download the exact content of the full email message.
     *
     * @param id of email
     * @return String containing the entire message content
     */
    public String downloadMessage(int id) throws IOException {
        StringBuffer content=new StringBuffer();
        String line;
        
        write("fetch "+id+" body[header]");
        line=readLine(); //TODO: We currently ignore response line!
        line=readLine();
        while(true) {
            Matcher m=endcheck.matcher(line);
            if(m.matches()) break;
            content.append(line);
            content.append("\n");
            line=readLine();
        }
        content.append("\n");
        String data=content.toString();
        data=data.substring(0,data.lastIndexOf(')'));
        content=new StringBuffer();
        write("fetch "+id+" body[text]");
        line=readLine(); //TODO: We currently ignore response line!
        line=readLine();
        while(true) {
            Matcher m=endcheck.matcher(line);
            if(m.matches()) break;
            content.append(line);
            content.append("\n");
            line=readLine();
        }
        data=data+content.toString();
        data=data.substring(0,data.lastIndexOf(')'));

        return data;
    }
    
    private Pattern existcheck=Pattern.compile(".*\\* (\\d+) EXIST.*");
    
    /**
     * Select a particular mail box for reading. Once selected, use the
     * readMail() command to read the messages in this folder.
     *
     * @return Total number of email's in this mail box.
     */
    public int readMailbox(String email) throws IOException {
        write("select "+email);
        String line=readLine();
        int count=0;
        
        //write("fetch "+1+" body[text]");
        //line=readLine();
        while(line.charAt(0)=='*') {
            Matcher m=existcheck.matcher(line);
            if(m.matches()) {
                count=Integer.parseInt(m.group(1));
            }
            line=readLine();
        }
        
        return count;
    }
    
    /**
     * Once all actions are completed, logout is called to disconnect from
     * the server.
     */
    public void logout() {
        try {
            write("logout");
            readLine();
        } catch(IOException e) {
        }
        disconnect();
    }
    
    /**
     * Cleanup all connections and data when complete
     */
    private void disconnect() {
        try {
            if(inStream!=null) { inStream.close(); inStream=null; }
            if(outStream!=null) { outStream.close(); outStream=null; }
            if(reader!=null) { reader.close(); reader=null; }
            if(imap!=null) { imap.close(); imap=null; }
            
        } catch(IOException e) { }
    }
    
    private String lastCommand="";
    
    /**
     * Sent a command to the server
     */
    private void write(String cmd) throws IOException {
        command=command+1;
        lastCommand=cmd;
        try {
            outStream.write("J"+command+" "+cmd+"\n");
            outStream.flush();
        } catch(IOException e) {
            disconnect();
            throw e;
        }
        //System.out.println("--> J"+command+" "+cmd);
    }
    
    /**
     * Read response from the server
     */
    private String readLine() throws IOException{
        try {
            String line=reader.readLine();
            //System.out.println("<-- "+line);
            if(line.indexOf("J"+command+" BAD")==0) {
                throw new IOException("Imap server responded 'BAD' to "+lastCommand);
            }
            if(line.indexOf("J"+command+" NO")==0) {
                throw new IOException("Imap server responded 'NO' to "+lastCommand);
            }
            return line;
        } catch(IOException e) {
            disconnect();
            throw e;
        }
    }
    
}

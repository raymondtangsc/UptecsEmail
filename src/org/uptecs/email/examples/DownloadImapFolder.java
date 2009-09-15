/*
 * DownloadImapFolder.java
 *
 * This demonstrates using the ImapReader class to download all
 * mail in your inbox into a text file. To execute type:
 *
 * java org.uptecs.email.examples.DownloadImapFolder <hostname> <username> <password>
 */

package org.uptecs.email.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.uptecs.email.ImapReader;

/**
 *
 * @author Jacob
 */
public class DownloadImapFolder {
    
    private ImapReader imap;
    
    /** Creates a new instance of DownloadImapFolder */
    public DownloadImapFolder() {
    }
    
    public static void main(String[] arg) {
        DownloadImapFolder d=new DownloadImapFolder();
        d.setParameters(arg[0], arg[1], arg[2]);
        d.download("mail.txt");
    }
    
    public void setParameters(String ihost, String iuser, String ipassword) {
        imap=new ImapReader();
        imap.setHostname(ihost);
        imap.setUsername(iuser);
        imap.setPassword(ipassword);
    }
    
    public void download(String filename) {
        try {
            File f=new File(filename);
            FileWriter fw=new FileWriter(f);
            PrintWriter out=new PrintWriter(fw);

            imap.connect();
            int count=imap.readMailbox("INBOX");
            System.out.println("Found "+count+" emails.");
            for(int x=1;x<=count;x++) {
                String m=imap.downloadMessage(x);
                out.print(m);
                out.print("****$$$$****\n");
            }
            imap.logout();
            out.flush();
            out.close();
            fw.close();
        } catch(IOException e) {
            System.out.println("Problem accessing server. "+e);
        }
    }
}

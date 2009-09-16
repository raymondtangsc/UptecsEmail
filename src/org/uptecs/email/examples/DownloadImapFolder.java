/*
Copyright (c) 2005, Uptecs. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided
   with the distribution.
 * Neither the name of the Uptecs nor the names of its
   contributors may be used to endorse or promote products
   derived from this software without specific prior written
   permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.uptecs.email.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.uptecs.email.ImapReader;

/**
 * This demonstrates using the ImapReader class to download all
 * mail in your inbox into a text file. To execute type:
 *
 * java org.uptecs.email.examples.DownloadImapFolder <hostname> <username> <password>
 *
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

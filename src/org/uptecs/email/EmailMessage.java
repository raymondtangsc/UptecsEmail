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
package org.uptecs.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds contents of an email message.
 */
public class EmailMessage {

    private String from="";
    private String to="";
    private String content="";
    private String subject="";

    /**
     * Creates a new instance of EmailMessage
     */
    public EmailMessage() {
    }

    public void setFrom(String email) {
        from=email;
    }

    public String getFrom() {
        return from;
    }

    public void setSubject(String s) {
        subject=s;
    }

    public String getSubject() {
        return subject;
    }

    public void setTo(String email) {
        to=email;
    }

    public String getTo() {
        return to;
    }

    public void setBody(String body) {
        content=body;
    }

    private Pattern hmatch=Pattern.compile("([a-zA-Z\\-]+):[ \\t]*(.*)");
    public void setHeaders(String headers) {
        String t[]=headers.split("[\n\r]");
        for(int x=0;x<t.length;x++) {
            Matcher m=hmatch.matcher(t[x]);
            if(m.matches()) {
                if(m.group(1).equalsIgnoreCase("From")) {
                    setFrom(m.group(2));
                }
                if(m.group(1).equalsIgnoreCase("To")) {
                    setTo(m.group(2));
                }
                if(m.group(1).equalsIgnoreCase("Subject")) {
                    setSubject(m.group(2));
                }
            }
        }
    }

    public String getBody() {
        return content;
    }
}

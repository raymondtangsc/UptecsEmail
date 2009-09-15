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

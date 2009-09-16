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

import java.util.regex.*;
import java.util.*;

public class EmailAddressParse {

	public EmailAddress[] parse(String line) {

		List<String> parts = new ArrayList<String>();
		String part = "";
		int mode = 0;
		char escapeType = 0;
		for(int i=0;i<line.length();i++) {
			char c = line.charAt(i);
			switch(mode) {
				case 0 :
					if(c==' ' || c=='\t' || c=='\n' || c==',') {
						if(part.length()>0)
							parts.add(part);
						part = "";
						continue;
					}
					if(c=='<' || c=='"' || c=='\'') {
						if(c=='<' && part.length()>0) {
							parts.add(part);
							part = "";
						}
						escapeType=c;
						if(c=='<') escapeType='>';
						mode=1;
						continue;
					}
					part=part+c;
					break;
				case 1 :
					if(c==escapeType) {
						if(c=='>' && part.length()>0) {
							parts.add(part);
							part = "";
						}
						mode = 0;
						continue;
					}
					part=part+c;
					break;
			}
		}
		if(part.length()>0)
			parts.add(part);

		List<EmailAddress> addresses = new ArrayList<EmailAddress>();
		String name = "";
		for(String bit : parts) {
			if(bit.contains("@")) {
				if(name == "")
					addresses.add(new EmailAddress(bit));
				else
					addresses.add(new EmailAddress(name,bit)); 
				name = "";
			} else {
				if(name == "")
					name = bit;
				else
					name = name + " " + bit;
			}
		}
		return addresses.toArray(new EmailAddress[0]);
	}

	public static void main(String[] arg) {

		EmailAddressParse parser = new EmailAddressParse();
		EmailAddress[] list = parser.parse(
				"bob@home.com, "+
				"\"Jane Smith\" <jane@home.com>, "+
				"\"John O'hare\" <john@home.com>,"+
				"Clara Rhoden <clara@home.com>,"+
				"Bob, Pty ltd <bob@bob.com>"+
				"\"Jane, Pty ltd\" <jane@jane.com>"+
				"Clara@rhoden.com,"+
				"'bob smith' bob@home.com"+
				"");
		for(EmailAddress item : list)
			System.out.println(" - "+item.toString());

	}

  /*
   * Here are the precompiled regular expressoins used to aide
   * validation of email addresses.
   */
  private static Pattern charCheck;
  private static Pattern partCheck;
  private static Pattern numberCheck;
  static {
    charCheck=Pattern.compile(".*([\\\\\\/\\*\\&\\(\\)\\!\\#\\$\\%\\^\\~\\`\\{\\}\\;\\:\\\"\\'\\,\\<\\>\\?\\[\\]\\=\\|]).*");
    partCheck=Pattern.compile("([^@]+)@([^@]+)");
    numberCheck=Pattern.compile(".*\\d.*");
  }

  /*
   * Contains an error if a problem occured with the most recent
   * call to the email validating function isValid().
   */
  private String error=null;

  /*
   * Returns the problem that occured during email address validation
   * if a problem did occur during validation. If a problem didn ot occur
   * or this function was already called, null will be returned.
   * @return String containing the error message
   */
  public String getError() {
    String e2=error;
    error=null;
    return e2;
  }

  /*
   * Tests the validity of an email address, returning true if
   * the email address is valid. You can follow this call with a call
   * to getError() to discover the problem with the email address.
   * @param email Email address to check
   * @return True if valid, false if invalid
   */
  public boolean isValid(String email) {

  if(email==null) {
    error="Email address not provided.";
    return false;
    }

  if(email.length()<6) {
    error="This is not long enough to be a valid e-mail address.";
    return false;
    }

  Matcher m=charCheck.matcher(email);
  if(m.matches()) {
    error="The "+m.group(1)+" character may not be used in an email address.\n";
    return false;
    }

  if(email.indexOf("@")<0) {
    error="Email address must contain the @ symbol.";
    return false;
    }

  if(email.indexOf(" ")>=0) {
    error="Email address should not contain a space.";
    return false;
    }

  m=partCheck.matcher(email);
  if(!m.matches()) {
    error="Email address must contain a single @ in the middle";
    return false;
    }
  //String upart=m.group(1);
  String mpart=m.group(2);
  String[] mparts=mpart.split("\\.");

  if(mparts.length<=1) {
    error="Your email domain is incomplete.\n";
    return false;
    }

  if(mparts[mparts.length-1].length()<2) {
    error="Email domain appears to be invalid.";
    return false;
    }

  if(mparts[mparts.length-1].length()>4) {
    error="Email domain appears to be invalid.";
    return false;
    }

  m=numberCheck.matcher(mparts[mparts.length-1]);
  if(m.matches()) {
    error="Email domain appears to be invalid.";
    return false;
    }

    return true;
   }
}

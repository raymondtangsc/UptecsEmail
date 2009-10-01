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
package org.uptecs.email.encoding;

public class PlainTextEncoding extends Encoding{

	/**
	 * Count how frequently a particular substring appears within a string.
	 * 
	 * @param content Text in which to scan.
	 * @param substring Substring to scan for.
	 * @return Number of times a substring appears in the content
	 */
	private int countSubstring(String content, String substring) {
		int index = -1;
		int total = 0;
		while(true) {
			if(index == -1)
				index = content.indexOf(substring);
			else
				index = content.indexOf(substring, index);
			if(index == -1)
				break;
			total++;
			index += substring.length();
		}
		return total;
	}

	/**
	 * Remove CR and LF characters from the start and end of a string.
	 * 
	 * @param line String to trim
	 * @return String with all CR and LF's removed from the start and end
	 */
	private String trimCrlf(String line) {
		while(line.endsWith("\n") || line.endsWith("\r"))
			line = line.substring(0,line.length()-1);
		while(line.startsWith("\n") || line.startsWith("\r"))
			line = line.substring(1,line.length()-1);
		return line;
	}

	/**
	 * Ensure all lines consistently end with CRLF, and ensure the full
	 * stop (period) character is escaped properly.
	 */
	public String encode(String message) {
		if(message == null || message == "")
			return message;

		int crlfCount = countSubstring(message, "\r\n");
		int crCount = countSubstring(message, "\r");
		int lfCount = countSubstring(message, "\n");
		String splitBy = "\n";

		if(crlfCount > crCount && crlfCount > lfCount)
			splitBy = "\r\n";
		if(crCount > crlfCount && crCount > lfCount)
			splitBy = "\r";
		if(lfCount > crCount && lfCount > crlfCount)
			splitBy = "\n";

		StringBuffer result = new StringBuffer();
		for(String line : message.split(splitBy)) {
			line = trimCrlf(line);
			if(line.matches("^\\.+$")) line = line + ".";
			result.append(line);
			result.append("\r\n");
		}

		return result.toString();
	}

}

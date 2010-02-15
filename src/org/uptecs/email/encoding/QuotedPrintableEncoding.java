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

public class QuotedPrintableEncoding extends Encoding{

	/**
	 * Break all lines greater than 80 characters long, Ensure
	 * all lines consistently end with CRLF, and ensure the full
	 * stop (period) character is escaped properly.
	 */
	public String encode(String message) {
		if(message == null || message == "")
			return message;

		Encoding plain = new PlainTextEncoding();
		message = plain.encode(message);

		int lineLength = 0;
		StringBuilder body = new StringBuilder();
		for(int i=0;i<message.length();i++) {
			char c = message.charAt(i);
			if(c==13 || c==10) lineLength=0;
			else lineLength++;
			if((c>127 && c<256)||c<10||c=='=') {
				if(lineLength>74) {
					body.append("=\r\n");
					lineLength = 0;
				}
				if(c>15)
					body.append("="+Integer.toHexString(c).toUpperCase());
				else {
					body.append("=0"+Integer.toHexString(c).toUpperCase());
				}
				lineLength+=2;
			} else {
				if(lineLength>76) {
					body.append("=\r\n");
					lineLength = 0;
				}
				body.append(c);
			}

		}

		return body.toString();
	}

}

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

public class MailTest {

  public static void main(String[] a) {
    Mail m=new Mail("smtp.example.com");
    int result;

    System.out.println("Test started");

    if(m.testConnection()==0) {
        System.out.println("Connection to mailserver available");
    }
    else {
        System.out.println("Problem with specified mailserver: "+m.getError());
    }

    m.enableAuthentication("jacob@example.com","password");
    result=m.sendMail("bob@example.com","jane@example.com","My subject","content2");
    if(result>0) {
        System.err.println(m.getError());
    }

    result=m.sendMail("xiongmao@example.com","gou@example.com","Another subject","content2");
    if(result>0) {
        System.err.println(m.getError());
    }

    result=m.sendMails("bob@example.com","person1@example.com,person2@example.com","test1","content");
    if(result>0) {
        System.err.println(m.getError());
    }

    System.out.println("Test complete");

    }

}

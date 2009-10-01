package org.uptecs.email.examples;

import org.uptecs.email.Mail;

public class SendEmail {

	public static void main(String arg[]) {

		Mail mail = new Mail("localhost",4020);
		int result = mail.sendHtmlMail(
				"from@example.com",
				"to@example.com",
				"Sample test message",
				"This <a name=\"Also link\">is also</a> a very simple <b>test</b> message. It has some more slightly <a href=\"http://complicated.com/\">complicated</a> html in it It should also be very long and have not many breaks in it.\n\n<br/>Best regards,\nJacob\n\n\n=-==============-=========-===================-========-===============-============-========-=========-=========",
				"This is a very simple sample\ntest email message.\n\nBest regards,\nJacob"
				);

		if(result!=0)
			System.err.println(mail.getError());
	}

}

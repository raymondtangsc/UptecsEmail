import junit.framework.TestCase;

import org.uptecs.email.EmailAddress;
import org.uptecs.email.EmailAddressParse;

/*
 * Copyright (c)2009-2010 The University of Melbourne. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of The
 * University of Melbourne. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms and conditions of contractual and/or employment
 * policy of the university.
 *
 * THE UNIVERSITY OF MELBOURNE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE UNIVERSITY OF MELBOURNE SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

public class EmailAddressParseTest extends TestCase {

	public void testValidation() {

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

		assertEquals(8, list.length);
		assertEquals("bob@home.com", list[0].toString());
		assertEquals("\"Jane Smith\" <jane@home.com>", list[1].toString());
		assertEquals("\"John O'hare\" <john@home.com>", list[2].toString());
		assertEquals("\"Clara Rhoden\" <clara@home.com>", list[3].toString());
		assertEquals("\"Bob Pty ltd\" <bob@bob.com>", list[4].toString());
		assertEquals("\"Jane, Pty ltd\" <jane@jane.com>", list[5].toString());
		assertEquals("Clara@rhoden.com", list[6].toString());
		assertEquals("\"bob smith\" <bob@home.com>", list[7].toString());
	}

	public void testParser() {
		EmailAddressParse parser = new EmailAddressParse();
		assertTrue(parser.isValid("bob@home.com"));
		assertTrue(parser.isValid("bob's@home.com"));
		assertTrue(parser.isValid("bob@home.com.au"));

		assertFalse(parser.isValid("bob@a.a"));
		assertFalse(parser.isValid("bob@home's.com"));
		assertFalse(parser.isValid("a@a.a"));
		assertFalse(parser.isValid("aaa!@aaa.aa"));

	}

}

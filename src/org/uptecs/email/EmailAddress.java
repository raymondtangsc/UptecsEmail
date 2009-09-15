/*
 * Uptecs Jakar standard library.
 *
 * Copyright(c)2006-2007, Uptecs.
 */
package org.uptecs.email;
import java.util.regex.*;

/**
 * Used to verify the validity of an email address. Provides the ability
 * to retrieve an informative message describing the problem with the
 * email address. Currenlty only supports english language messages.
 */
public class EmailAddress {

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

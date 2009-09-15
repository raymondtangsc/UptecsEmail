package org.uptecs.email;

public class MailTest {

  public static void main(String[] a) {
    Mail m=new Mail("rhoden.id.au");
    int result;

    System.out.println("Test started");
    
    if(m.testConnection()==0) {
        System.out.println("Connection to mailserver available");
    }
    else {
        System.out.println("Problem with specified mailserver: "+m.getError());
    }

    m.enableAuthentication("jacob@rhoden.id.au","gsmarti");
    result=m.sendMail("jrhoden@unimelb.edu.au","jrhoden@unimelb.edu.au","test2","content2");
    if(result>0) {
        System.err.println(m.getError());
    }

    result=m.sendMail("jrhodenunimelb.edu.au","jrhoden@unimelb.edu.au","test2","content2");
    if(result>0) {
        System.err.println(m.getError());
    }
    
    result=m.sendMails("bob@unimelb.edu.au","jrhoden@unimelb.edu.au,ea-architecture@unimelb.edu.au","test1","content");
    if(result>0) {
        System.err.println(m.getError());
    }

    System.out.println("Test complete");

    }

  }
